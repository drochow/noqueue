package controllers

import javax.inject.Inject

import models.{ Adresse, Anwender, AnwenderDAO }
import play.api.Configuration
import play.api.i18n.MessagesApi

/**
 * Created by anwender on 06.11.2016.
 */
class Anwenders @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {
  def create(nutzerName: String) = ApiActionWithBody { implicit request =>
    readFromRequest[Anwender] {
      anwender =>
        readFromRequest[Adresse] { //@todo stil
          adresse =>
            AnwenderDAO.insert(anwender.nutzerEmail, anwender.password, anwender.nutzerName, adresse.straße, adresse.hausNummer, adresse.plz, adresse.stadt).flatMap {
              case (_, newAnwender) => created(newAnwender)
              //case _ => //@todo fail
            }
        }
    }
  }
}
