package controllers

import javax.inject.Inject

import api.JsonCombinators._
import models.db.{ BetriebEntity, DienstleistungsTypEntity, PK }
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 25.12.2016.
 */
class Leiter @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {
  def dienstleistungAnbieten(betriebId: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[(PK[DienstleistungsTypEntity], String, Int, String)] {
      case (dltId: PK[DienstleistungsTypEntity], name: String, dauer: Int, kommentar: String) =>
        okF(request.leiter.dienstleistungAnbieten(dltId, name, dauer, kommentar))
    }
  }
}
