package controllers

import javax.inject.Inject

import api.JsonCombinators._
import models.db.{ Adresse, Anwender, PK }
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 06.11.2016.
 */
class AnwenderC @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create = ApiActionWithBody { implicit request =>
    readFromRequest[Anwender] {
      case anw: Anwender =>
        db.run(dal.insert(Anwender(anw.nutzerEmail, anw.password, anw.nutzerName) //Anwender("hans@gmail.com", "test", "hans", Some(PK[Adresse](2L)))
        )) flatMap {
          ok(_)
        }
      case all => ok("didn't work :" + all)
      /*readFromRequest[Adresse] {
          adresse => created("okey")*/
      //            AnwenderRepository.createWithAdresse(anwender, adresse).flatMap {
      //              case newAnwenderId => created(newAnwenderId)
      //              case _ => ApiError.errorInternal("Unable to create User")
      //            }
      //}
    }
  }
  def get(anwenderId: Long) = ApiAction { implicit request =>
    db.run(dal.getAnwenderById(PK(anwenderId))).flatMap {
      case x: Anwender => ok(x)
      case _ => ok("nope")
    }
  }
}
