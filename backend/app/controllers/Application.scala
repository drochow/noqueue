package controllers

import play.api.mvc._
import javax.inject.Inject

import api.{ ApiError, ApiResponse, ApiResult }
import models.db.{ AdresseRepository, AnwenderRepository }
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.util.{ Success, Failure }
import scala.concurrent.duration._

class Application @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def test = ApiAction { implicit request =>
    ok("The API is ready")
  }

  def setup = ApiAction { implicit request =>
    var apiResult: ApiResult = ApiError.errorInternalServer("Setup failed") {
      //for comprehension to combine the two futures
      for {
        adresseF <- AdresseRepository.setup()
        anwenderF <- AnwenderRepository.setup()
      } yield (adresseF, anwenderF)
    }.flatMap {
      //success block
      case _ => apiResult = ok("Successfully setup database.")
    } recover {
      //failure block
      case t => apiResult = ApiError.errorInternal("Unable to setup:" + t.toString())
    }
    apiResult
  }

}
