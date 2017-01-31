package models

import java.sql.SQLException
import java.util.NoSuchElementException
import javax.inject.Inject
import javax.security.auth.login.CredentialException

import api.jwt.TokenPayload
import models.db._
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import play.api.inject.ApplicationLifecycle
import utils.{ EmailAlreadyInUseException, NutzerNameAlreadyInUseException, TokenExpiredException, UnauthorizedException }

import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class UnregistrierterAnwender(dbD: DB) extends Base(dbD) {

  import scala.concurrent.ExecutionContext.Implicits.global

  /**
   * Signin for @Anwender
   *
   * @param nutzerName the nutzerName of the user
   * @param password the unencrypted password of the user
   * @return the @AnwenderEntity of the now signed in user
   */
  def anmelden(nutzerName: String, password: String): Future[AnwenderEntity] = {
    db.run(
      dal.getAnwenderByName(nutzerName)
    ) map {
        anw: AnwenderEntity => if (BCrypt.checkpw(password, anw.password)) anw else throw new CredentialException("Invalid credentials.(PW)" + BCrypt.checkpw(password, anw.password) + password + " -- " + anw.password)
      } recover {
        case nf: NoSuchElementException => throw new CredentialException("Invalid credentials.(nutzerName)")
      }
  }

  /**
   * Throws exception if the expiration time of the token is before the current time
   *
   * @param payload Payload of a JWToken
   */
  private def tokenExpirationCheck(payload: TokenPayload) = if (payload.expiration isBeforeNow) throw new TokenExpiredException

  /**
   * Authenticates user as a @Anwender if the token payload is valid
   *
   * @param jwtPayload Payload of a JWToken
   * @return @Anwender object for further authenticated actions
   */
  def anmeldenMitPayload(jwtPayload: TokenPayload): Anwender = {
    tokenExpirationCheck(jwtPayload)
    new Anwender(dal.getAnwenderWithAdress(new PK[AnwenderEntity](jwtPayload.anwenderId)), dbD)
  }

  /**
   * Authorizes user as a @Mitarbeiter if the token payload is valid
   *
   * @param jwtPayload Payload of a JWToken
   * @param betriebId Id of the Betrieb for wich the authorization is done
   * @return @Mitarbeiter object for further authorized actions
   */
  def anmeldenMitPayloadAlsMitarbeiterVon(jwtPayload: TokenPayload, betriebId: PK[BetriebEntity]): Mitarbeiter = {
    tokenExpirationCheck(jwtPayload)
    new Mitarbeiter(dal.getMitarbeiterOfById(betriebId, PK[AnwenderEntity](jwtPayload.anwenderId)), dbD)
  }

  /**
   * Authorizes user as a @Leiter if the token payload is valid
   *
   * @param jwtPayload Payload of a JWToken
   * @param betriebId Id of the Betrieb for wich the authorization is done
   * @return @Leiter object for further authorized actions
   */
  def anmeldenMitPayloadAlsLeiterVon(jwtPayload: TokenPayload, betriebId: PK[BetriebEntity]): Leiter = {
    tokenExpirationCheck(jwtPayload)
    new Leiter(dal.getLeiterOfById(betriebId, PK[AnwenderEntity](jwtPayload.anwenderId)), dbD)
  }

  /**
   * Performs a paged radius search of @BetriebAndAdresse within a specified @umkreisM from the location located by @longitude and @latitude
   * where the @suchBegriff matches either the kommentar of a related the @DienstleistungEntity, the name of a related the @DienstleistungsTypEntity,
   * the name of the @BetriebEntity or the name of a related @MitarbeiterEntity of a @BetriebEntity
   *
   * @param suchBegriff the query parameter wich need to match any substring
   * @param umkreisM the range in meters
   * @param longitude the longitude of the current position
   * @param latitude the latitude of the current position
   * @param page page number for pagination
   * @param size size per page for pagination
   * @return a sequence of all matching @BetriebAndAdresse and the distance
   */
  def anbieterSuchen(
    suchBegriff: String,
    umkreisM: Int,
    longitude: Double,
    latitude: Double,
    page: Int,
    size: Int
  ): Future[Seq[(BetriebAndAdresse, String)]] = {
    db.run(dal.searchBetrieb(suchBegriff, umkreisM, longitude, latitude, page, size));
  }

  /**
   * Signup as a @Anwender
   *
   * @param anwender @AnwenderEntity representing the new @Anwender
   * @return the successfully created @AnwenderEntity
   */
  def registrieren(anwender: AnwenderEntity) = {
    db.run(dal.insert(AnwenderEntity(anwender.nutzerEmail, BCrypt.hashpw(anwender.password, BCrypt.gensalt()), anwender.nutzerName))) recover {
      case sqle: SQLException => {
        if (sqle.getMessage.contains("emailUnique")) throw new EmailAlreadyInUseException
        if (sqle.getMessage.contains("nameUnique")) throw new NutzerNameAlreadyInUseException
        //this should only happen when we encounter db connection failures
        // $COVERAGE-OFF$
        throw sqle;
        // $COVERAGE-ON$
      }
    }
  }

  /**
   * Shows public details about a @BetriebEntity
   *
   * @param id primary key of the @BetriebEntity to show
   * @return a tuple with the @BetriebEntity and @AdresseEntity related to it
   */
  def betriebAnzeigen(id: PK[BetriebEntity]) = {
    db.run(dal.getBetriebWithAdresseById(id))
  }

  /**
   * Shows public information about @MitarbeiterEntity with the related @AnwenderEntity in a list
   *
   * @param betriebId primary key of the @BetriebEntity from wich we want the @MitarbeiterEntity list
   * @param page page number for pagination
   * @param size size per page for pagination
   * @return a sequence of @MitarbeiterEntity and @AnwenderEntity related to the @BetriebEntity matching the provided PK
   */
  def mitarbeiterAnzeigen(betriebId: PK[BetriebEntity], page: Int, size: Int): Future[Seq[(MitarbeiterEntity, AnwenderEntity)]] =
    db.run(dal.listMitarbeiterOf(betriebId, page, size))

  /**
   *
   * Shows sequence with public information about @DienstleistungEntity and related @DienstleistungsTypEntity of the @BetriebEntity
   * with the provided primary key
   *
   * @param betriebId primary key of the @BetriebEntity from wich we want the list
   * @param page page number for pagination
   * @param size size per page for pagination
   * @return
   */
  def dienstleistungAnzeigen(betriebId: Long, page: Int, size: Int): Future[Seq[(DienstleistungEntity, DienstleistungsTypEntity)]] =
    for {
      dls <- db.run(dal.listDienstleistungOfBetrieb(PK[BetriebEntity](betriebId), page, size))
    } yield dls

  //no idea where this goes so i'll put it here for now
  //@todo seems not be used, check if that is correct
  //  def getDienstleistungsTypen(limit: Long, offset: Long): Future[Seq[DienstleistungsTypEntity]] = {
  //    try {
  //      db.run(dal.getAllDlTs(limit, offset))
  //    } finally {
  //      db.close()
  //    }
  //  }
}
