package controllers

import javax.inject.Inject

import api.JsonCombinators._
import api.ApiError
import play.api.Configuration
import play.api.i18n.MessagesApi
import models.db.{ Adresse, Anwender => AnwenderResource }

/**
 * Created by anwender on 06.11.2016.
 */
class AnwenderC @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create(nutzerName: String) = ApiActionWithBody { implicit request =>
    readFromRequest[AnwenderResource] {
      anwender =>
        readFromRequest[Adresse] {
          adresse => created("okey")
          //            AnwenderRepository.createWithAdresse(anwender, adresse).flatMap {
          //              case newAnwenderId => created(newAnwenderId)
          //              case _ => ApiError.errorInternal("Unable to create User")
          //            }
        }
    }
  }
}
