package controllers

import java.sql.SQLException
import javax.inject.Inject

import api.ApiError
import api.JsonCombinators._
import models._
import models.db.{ AnwenderEntity, PK }
import org.postgresql.util.PSQLException
import play.api.Configuration
import play.api.i18n.MessagesApi
import models.{ Anwender => AnwenderModel }
import api.jwt.{ JwtUtil, TokenPayload }
import org.joda.time.DateTime
import javax.security.auth.login.CredentialException

import api.auth.Credentials

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 06.11.2016.
 */
class Anwender @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create = ApiActionWithBody { implicit request =>
    readFromRequest[AnwenderEntity] {
      case anw: AnwenderEntity => {
        val uAnwender = new UnregistrierterAnwender()
        uAnwender.registrieren(anw) flatMap {
          //success
          anw: AnwenderEntity => ok(JwtUtil.signJwtPayload(TokenPayload(anw.id.get.value, DateTime.now().withDurationAdded(1200L, 1))));
        } recover {
          //failure

          case psqlE: PSQLException => {
            if (psqlE.getMessage.contains("emailUnique")) {
              ApiError.errorBadRequest("Diese Email wird bereits verwendet bereits!")
            }
            if (psqlE.getMessage.contains("nameUnique")) {
              ApiError.errorBadRequest("Dieser nutzerName existiert bereits!")
            } else
              ApiError.errorBadRequest(psqlE.getMessage)
          }
          case e: Exception => {
            e.printStackTrace()
            ApiError.errorBadRequest("Invalid data..")
          }
        }
      }
    }
  }

  def auth = ApiActionWithBody { implicit request =>
    readFromRequest[Credentials] {
      case credentials: Credentials => {
        val uAnwender = new UnregistrierterAnwender
        uAnwender.anmelden(credentials.nutzerName, credentials.password) flatMap {
          case anw: AnwenderEntity => ok(JwtUtil.signJwtPayload(TokenPayload(anw.id.get.value, DateTime.now().withDurationAdded(1200L, 1))))
        } recover {
          case e: CredentialException => ApiError.errorBadRequest("Invalid Credentials.")
          case ex: SQLException => ApiError.errorInternal("Databse not reachable...")
        }
      }
    }

  }

  def profil = SecuredApiAction { implicit request =>
    request.anwender.profil flatMap {
      case (anwender: AnwenderEntity, adresse: Option[AdresseEntity]) => ok((anwender, adresse))
    } recover {
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorInternal("Something went wrong!")
      }
    }
  }
}
