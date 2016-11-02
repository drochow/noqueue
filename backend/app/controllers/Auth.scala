package controllers

import api.ApiError._
import api.JsonCombinators._
import models.{ User, ApiToken }
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.i18n.MessagesApi

class Auth @Inject() (val messagesApi: MessagesApi, system: ActorSystem) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled
  )

  def signIn = ApiActionWithBody { implicit request =>
    readFromRequest[Tuple2[String, String]] {
      case (email, pwd) => DaAnwender.findByEmail(email).pw(pwd).flatMap{
        case None => errorAnwenderNotFound
        case Some(anwender) => {
          if anwender.active InvalidCredentialsException
          else {
            val payLoad = TokenPayload(anwender.userId, 3000000000)//#TODO realistisch
            ok(JwtUtil.signJwtPayload(Json.toJson(payLoad)))
          }
        }
      }
    }
  }

  def signOut = SecuredApiAction { implicit request =>
    ResponseToken.delete(request.token).flatMap { _ =>
      noContent()
    }
  }

  implicit val signUpInfoReads: Reads[Tuple3[String, String, User]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String](Reads.minLength[String](6)) and
      (__ \ "user").read[User] tupled
  )

  def signUp = ApiActionWithBody { implicit request =>
    readFromRequest[Tuple3[String, String, User]] {
      case (email, password, user) =>
        User.findByEmail(email).flatMap {
          case Some(anotherUser) => errorCustom("api.error.signup.email.exists")
          case None => User.insert(email, password, user.name).flatMap {
            case (id, user) =>

              // Send confirmation email. You will have to catch the link and confirm the email and activate the user.
              // But meanwhile...
              system.scheduler.scheduleOnce(30 seconds) {
                User.confirmEmail(id)
              }

              ok(user)
          }
        }
    }
  }

}

object