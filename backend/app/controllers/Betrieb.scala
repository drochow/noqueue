package controllers

import java.sql.SQLException
import javax.inject.Inject
import javax.security.auth.login.CredentialException

import api.ApiError
import api.JsonCombinators._
import api.auth.Credentials
import api.jwt.{ JwtUtil, TokenPayload }
import models._
import models.db._
import org.joda.time.DateTime
import org.postgresql.util.PSQLException
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by anwender on 06.11.2016.
 */
class Betrieb @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  //  def create = ApiAction {
  //    implicit reqjeust =>
  //      ok("Success")
  //  }
  def create = SecuredApiActionWithBody { implicit request =>
    readFromRequest[BetriebAndAdresse] {
      case btrAndAdr: BetriebAndAdresse => {
        for {
          anw <- request.anwender
          result <- anw.betriebErstellen(btrAndAdr.betriebEntity, btrAndAdr.adresseEntity) flatMap {
            baa: (BetriebEntity, AdresseEntity) => ok(BetriebAndAdresse(betriebEntity = baa._1, adresseEntity = baa._2));
          } recover {
            //failure
            case sqlE: SQLException => {
              if (sqlE.getMessage.contains("betriebNameUnique")) {
                ApiError.errorBadRequest("Ein Betrieb mit diesem Namen existiert bereits!")
              }
              if (sqlE.getMessage.contains("betriebTelUnique")) {
                ApiError.errorBadRequest("Ein Betrieb mit dieser Telefonnummer existiert bereits!")
              } else
                ApiError.errorBadRequest(sqlE.getMessage)
            }
            case e: Exception => {
              e.printStackTrace()
              ApiError.errorBadRequest("Invalid data..")
            }
          }
        } yield (result)

      }
    }
  }

  def show(id: Long) = SecuredApiAction { implicit request =>
    for {
      anw <- request.anwender
      result <- anw.betriebAnzeigen(PK[BetriebEntity](id)) flatMap {
        btrAndAdr: (BetriebEntity, AdresseEntity) => ok(BetriebAndAdresse(betriebEntity = btrAndAdr._1, adresseEntity = btrAndAdr._2))
      } recover {
        case e: Exception => {
          e.printStackTrace()
          ApiError.errorBadRequest("Invalid data..")
        }
      }
    } yield (result)
  }

  //  def addMitarbeiter(betriebId: Long, mitarbeiterId: Long) = SecuredApiActionWithBody {
  //    implicit request =>
  //      request.anwender.getLeiterFor(PK[BetriebEntity](betriebId)) flatMap {
  //        leiter: (LeiterEntity, BetriebEntity, AnwenderEntity) => {
  //          val leiterModel = new Leiter(DBIO.from(Future.successfull(leiter)))
  //          leiterModel.mitarbeiterAnstellen(PK[MitarbeiterEntity](mitarbeiterId)) flatMap {
  //            case _: ok("Successfully created Mitarbeiter")
  //          } recover {
  //            case e: Exception => {
  //              e.printStackTrace()
  //              ApiError.errorBadRequest("Invalid data..")
  //            }
  //          }
  //        }
  //      } recover {
  //        case nseE: NoSuchElementException => ApiError.errorUnauthorized
  //        case e: Exception => {
  //          e.printStackTrace()
  //          ApiError.errorBadRequest("Invalid data..")
  //        }
  //      }
  //  }
}
