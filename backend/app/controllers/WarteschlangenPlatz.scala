package controllers

import javax.inject.Inject
import javax.security.auth.login.CredentialException

import api.{ ApiError, Credentials }
import api.JsonCombinators._
import models.DB
import models.db.{ BetriebEntity, MitarbeiterEntity, PK, WarteschlangenPlatzEntity }
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 09.01.2017.
 */

class WarteschlangenPlatz @Inject() (val dbD: DB, val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  val dlUndMitarbeiterReads = (
    (__ \ "dienstleistung").read[Long] and
    (__ \ "mitarbeiter").read[Long]
  )((dlId, mitarbeiterId) => (dlId, mitarbeiterId))

  def create = SecuredApiActionWithBody { implicit request =>
    readFromRequest[(Long, Long)] {
      case (dlId, mitarbeiterId) =>
        request.anwender.wsFuerBestimmtenMitarbeiterBeitreten(dlId, mitarbeiterId) flatMap {
          wsp =>
            request.anwender.wspAnzeigen() flatMap {
              fin => ok(fin)
            }
        }
    }(request, dlUndMitarbeiterReads, request.request) //request and req.req are the vals that would have also been taken if they hadn't been declared
  }

  def verlassen = SecuredApiAction { implicit request =>
    request.anwender.wsVerlassen() flatMap {
      del =>
        if (del) {
          noContent()
        } else {
          ApiError.errorItemNotFound
        }
    }
  }

  def getWarteSchlangeOfMitarbeiter(betriebId: Long) = SecuredMitarbeiterApiAction(PK[BetriebEntity](betriebId)) {
    implicit request =>
      request.mitarbeiter.warteSchlangeAnzeigen() flatMap {
        warteschlange => ok(warteschlange)
      }
  }

  def getWarteSchlangenPlatzOfAnwender = SecuredApiAction {
    implicit request =>
      request.anwender.wspAnzeigen() flatMap {
        platz => ok(platz)
      }
  }

  def getNextSlots(betriebId: Long) = SecuredApiAction {
    implicit request =>
      request.anwender.getNextTimeSlotsForBetrieb(betriebId) flatMap {
        list => ok(list)
      }
  }

  def startWorkOn(betriebId: Long, wspId: Long) = SecuredMitarbeiterApiAction(PK[BetriebEntity](betriebId)) {
    implicit request =>
      request.mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](wspId)) flatMap {
        _ => accepted()
      }
  }

  //val wspBearbeitungBeendenReads = ((__ \ "warteschlangenplatzId").read[Long])

  def finishWorkOn(betriebId: Long, wid: Long) = SecuredMitarbeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    //readFromRequest[Long] {
    //case wspId =>
    request.mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](wid)) flatMap {
      _ => accepted()
    }
    //}(request, wspBearbeitungBeendenReads, request.request) //request and req.req are the vals that would have also been taken if they hadn't been declared

  }
}
