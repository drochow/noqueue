package controllers

import java.sql.SQLException
import javax.inject.Inject
import javax.security.auth.login.CredentialException

import api.ApiError
import api.JsonCombinators._
import api.auth.Credentials
import api.jwt.{ JwtUtil, TokenPayload }
import models.db.{ AdresseEntity, AnwenderEntity, PK }
import models.{ Anwender => AnwenderModel, _ }
import org.joda.time.DateTime
import org.postgresql.util.PSQLException
import osm.{ AdressNotFoundException, AdressService, GeoCoords, InvalidGeoCoordsException }
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.inject.ApplicationLifecycle
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by anwender on 06.11.2016.
 */
class Anwender @Inject() (val applicationLifecycle: ApplicationLifecycle, val as: AdressService, val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create = ApiActionWithBody { implicit request =>
    readFromRequest[AnwenderEntity] {
      case anw: AnwenderEntity => {
        val uAnwender = new UnregistrierterAnwender(applicationLifecycle)
        uAnwender.registrieren(anw) flatMap {
          //success
          anw: AnwenderEntity => ok(JwtUtil.signJwtPayload(TokenPayload(anw.id.get.value, DateTime.now().withDurationAdded(1200L, 1))));
        }
      }
    }
  }

  def auth = ApiActionWithBody { implicit request =>
    readFromRequest[Credentials] {
      case credentials: Credentials => {
        val uAnwender = new UnregistrierterAnwender(applicationLifecycle)
        uAnwender.anmelden(credentials.nutzerName, credentials.password) flatMap {
          case anw: AnwenderEntity => ok(JwtUtil.signJwtPayload(TokenPayload(anw.id.get.value, DateTime.now().withDurationAdded(1200L, 1))))
        }
      }
    }
  }

  def profil = SecuredApiAction { implicit request =>
    request.anwender.profilAnzeigen() flatMap {
      case anwender: (AnwenderEntity, Option[AdresseEntity]) => ok(anwender)
    }
  }

  private def addCoordinatesIfPresent(adr: Option[AdresseEntity], geo: GeoCoords): Option[AdresseEntity] =
    if (adr.isEmpty) adr else Some(adr.get.copy(latitude = Some(geo.latitude), longitude = Some(geo.longitude)))

  def profilAustauschen = SecuredApiActionWithBody { implicit request =>
    readFromRequest[(AnwenderEntity, Option[AdresseEntity])] {
      case (anw: AnwenderEntity, adrO: Option[AdresseEntity]) => {
        (for {
          geo: GeoCoords <- if (adrO.isEmpty) Future.successful[GeoCoords](GeoCoords(0.00, 0.00)) else as.getCoordsOfAdress(adrO.get)
          updateSuccessfull <- request.anwender.anwenderInformationenAustauschen(anw, addCoordinatesIfPresent(adrO, geo))
        } yield (updateSuccessfull)) flatMap {
          bool =>
            if (bool) {
              accepted("Your Input was Accepted")
            } else {
              //@todo B Schurian check if necessary ?! should break before since user is not found
              ApiError.errorInternal("Unable to save provided Data...")
            }
        }
      }
    }
  }

  def profilBearbeiten = SecuredApiActionWithBody { implicit request =>
    readFromRequest[(Option[String], Option[String], Option[Option[AdresseEntity]])] {
      case (nutzerName: Option[String], nutzerEmail: Option[String], adresse: Option[Option[AdresseEntity]]) =>
        request.anwender.anwenderInformationenVeraendern(nutzerName, nutzerEmail, adresse) flatMap {
          updated =>
            if (updated) {
              accepted("Your Input was Accepted")
            } else {
              //@todo B Schurian check if necessary ?! should break before since user is not found
              ApiError.errorInternal("Could not Update with given parameters")
            }
        }
      //@todo B Schurian check if necessary ?!
      case _ => throw new Exception("no case matched in profilbearbeiten")
    }
  }

  val oldPwAndNewPwReads = ((__ \ "oldPassword").read[String] and
    (__ \ "newPassword").read[String])((oldPassword, newPassword) => (oldPassword, newPassword))

  def pwAendern = SecuredApiActionWithBody { implicit request =>
    readFromRequest[(String, String)] {
      case passwords =>
        request.anwender.passwordVeraendern(passwords._1, passwords._2) flatMap {
          updated =>
            if (updated) {
              accepted("Your password was changed")
            } else {
              //@todo B Schurian check if necessary ?! should break before since user is not found
              ApiError.errorInternal("Could not Change Password with given parameters")
            }
        }
      //@todo B Schurian check if necessary ?!
      case _ => throw new Exception("no case matched in profilbearbeiten")
    }(request, oldPwAndNewPwReads, request.request) //request and req.req are the vals that would have also been taken if they hadn't been declared
  }

  def search(q: Option[String], page: Int, size: Int) = SecuredApiAction { implicit request =>
    request.anwender.anwenderSuchen(q, page, size) flatMap {
      listOfAnwender => ok(listOfAnwender)
    }
  }

  def show(id: Long) = SecuredApiAction { implicit request =>
    request.anwender.anwenderAnzeigen(PK[AnwenderEntity](id)) flatMap {
      anwender => ok(anwender)
    }
  }

  def myBetriebe = SecuredApiAction { implicit request =>
    request.anwender.meineBetriebe() flatMap {
      betriebe => ok(betriebe)
    }
  }
}
