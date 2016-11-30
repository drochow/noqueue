package controllers

import javax.inject.Inject

import api.JsonCombinators._
import models._
import models.db.{ AnwenderEntity, PK }
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 06.11.2016.
 */
class AnwenderC @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create = ApiActionWithBody { implicit request =>
    readFromRequest[AnwenderEntity] {
      case anw: AnwenderEntity =>
        val unregAnw = new UnregistrierterAnwender
        unregAnw.registrieren(anw.nutzerEmail, anw.password, anw.nutzerName) flatMap { /*("hans@gmail.com", "test", "hans"))//Test @TODO entfernen*/
          ok(_)
        }
      case all => ok("didn't work :" + all)
      /*readFromRequest[AdresseEntity] {
          adresse => created("okey")*/
      //            AnwenderRepository.createWithAdresse(anwender, adresse).flatMap {
      //              case newAnwenderId => created(newAnwenderId)
      //              case _ => ApiError.errorInternal("Unable to create User")
      //            }
      //}
    }
  }
  def get(anwenderId: Long) = ApiAction { implicit request =>
    //    db.run(dal.getAnwenderById(PK(anwenderId))).flatMap {
    //      case x: AnwenderEntity => ok(x)
    //      case _ => ok("nope")
    //
    //    }
    ok("Success")
  }
}
