package controllers

import play.api.mvc._
import javax.inject.Inject

import play.api.Configuration
import play.api.i18n.MessagesApi

class Application @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def test = ApiAction { implicit request =>
    ok("The API is ready")
  }

}
