package models

import java.sql.SQLException
import javax.security.auth.login.CredentialException

import api.jwt.TokenPayload
import models.db._
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class UnregistrierterAnwender extends Base {

  import scala.concurrent.ExecutionContext.Implicits.global

  def anmelden(nutzerName: String, password: String): Future[AnwenderEntity] = {
    db.run(
      dal.getAnwenderByName(nutzerName)
    ) map {
        anw: AnwenderEntity => if (BCrypt.checkpw(password, anw.password)) anw else throw new CredentialException("Invalid credentials.(PW)" + BCrypt.checkpw(password, anw.password) + password + " -- " + anw.password)
      } recover {
        case nf: NoSuchElementException => throw new CredentialException("Invalid credentials.(nutzerName)")
      }
  }

  def anmeldenMitPayload(jwtPayload: TokenPayload): Anwender = {
    new Anwender(dal.getAnwenderWithAdress(new PK[AnwenderEntity](jwtPayload.anwenderId)))
  }

  def anmeldenMitPayloadAlsMitarbeiterVon(jwtPayload: TokenPayload, betriebId: PK[BetriebEntity]): Mitarbeiter = {
    new Mitarbeiter(dal.getMitarbeiterOfById(betriebId, PK[AnwenderEntity](jwtPayload.anwenderId)))
  }

  def anmeldenMitPayloadAlsLeiterVon(jwtPayload: TokenPayload, betriebId: PK[BetriebEntity]): Leiter = {
    new Leiter(dal.getLeiterOfById(betriebId, PK[AnwenderEntity](jwtPayload.anwenderId)))
  }

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

  def registrieren(anwender: AnwenderEntity) = {
    db.run(dal.insert(AnwenderEntity(anwender.nutzerEmail, BCrypt.hashpw(anwender.password, BCrypt.gensalt()), anwender.nutzerName)))
  }

  //Example CallByName Parameter
  def tryAndClose[A](block: => Future[A]) = try { block } finally { db.close }

  def registrieren(nutzerEmail: String, nutzerName: String, password: String): Future[AnwenderEntity] = {
    try {
      db.run(dal.insert(AnwenderEntity(nutzerEmail, BCrypt.hashpw(password, BCrypt.gensalt()), nutzerName)))
    } finally {
      db.close()
    }
  }

  //@todo DELET This
  def testDltinserts = {
    db.run(dal.insert(DienstleistungsTypEntity("haareschneiden")) andThen (dal.insert(DienstleistungsTypEntity("Fönen"))))
  }

  //no idea where this goes so i'll put it here for now
  def getDienstleistungsTypen(limit: Long, offset: Long): Future[Seq[DienstleistungsTypEntity]] = {
    try {
      db.run(dal.getAllDlTs(limit, offset))
    } finally {
      db.close()
    }
  }

}
