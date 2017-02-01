package controllers

import java.sql.SQLTimeoutException
import javax.inject.Inject

import api.ApiError
import api.JsonCombinators._
import models._
import models.db.{ AdresseEntity, AnwenderEntity, DienstleistungsTypEntity, PK }
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Application @Inject() (val dbD: DB, val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def setup = ApiAction { implicit request =>
    val base = new Base(dbD)
    base.setupDB flatMap {
      case _ => ok("Setup complete...")
    } recover {
      case e: IndexOutOfBoundsException => ApiError.errorInternal("Unknown error: " + e.toString)
    }
  }

  implicit val limitAndOffsetReads: Reads[(Long, Long)] = {
    (__ \ "limit").read[Long] and
      (__ \ "offset").read[Long] tupled
  }

  //please put this method where it belongs, but for now i will leave it here
  //@todo seesm to be unused in frontend so please check if that is correct
  //  def getDienstleistungsTypen(page: Int, size: Int) = ApiActionWithBody { implicit request =>
  //    okF((new UnregistrierterAnwender(dbD)).getDienstleistungsTypen(page, size))
  //  }

}
