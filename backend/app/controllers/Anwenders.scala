package controllers

import javax.inject.Inject

import io.netty.channel.AddressedEnvelope
import models.{ Adresse, Anwender, AnwenderDAOFakeDB }
import play.api.Configuration
import play.api.i18n.MessagesApi

/**
 * Created by anwender on 06.11.2016.
 */
class Anwenders @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {
  def create(nutzerEmail: String, password: String, nutzerName: String, straße: String, hausNummer: String, plz: String, stadt: String) = ApiActionWithBody { implicit request =>
    readFromRequest[Adresse] {
      adresse =>
        readFromRequest[Anwender] { //@todo stil
          anwender =>
            AnwenderDAOFakeDB.insert(anwender.nutzerEmail, anwender.password, anwender.nutzerName, adresse.straße, adresse.hausNummer, adresse.plz, adresse.stadt).flatMap {
              case (_, newAnwender) => created(newAnwender)
              //case _ => //@todo fail
            }
        }
    }
  }

  def get = SecuredApiAction { implicit request =>
    readFromRequest[Anwender] {
      anwender =>
        ok(AnwenderDAOFakeDB.get(anwender.anwenderId));
    }
  }
}
