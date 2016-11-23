package controllers

import api.ApiError._
import api.JsonCombinators._
import models.{ ApiToken, User }
import models.db.Anwender
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import api.jwt.JwtUtil
import play.api.i18n.MessagesApi
import play.core.j.JavaWebSocket
import play.api.Configuration
import org.joda.time.DateTime
import api.jwt.TokenPayload

class Auth @Inject() (val messagesApi: MessagesApi, system: ActorSystem, val config: Configuration) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled
  )

  def signIn = ApiAction { implicit request =>
    ok(JwtUtil.signJwtPayload(new TokenPayload(1l, new DateTime()))) //@TODO nur test
    /*readFromRequest[Tuple2[String, String]] {
      case (email, pwd) =>
        User.findByEmail(email).flatMap {
          case None => errorUserNotFound
          case Some(user) => {
            if (user.password != pwd) errorUserNotFound
            else if (!user.emailConfirmed) errorUserEmailUnconfirmed
            else if (!user.active) errorUserInactive
            else ApiToken.create(request.apiKeyOpt.get, user.id).flatMap { token =>
              ok(Json.obj(
                "token" -> token,
                "minutes" -> 10
              ))
            }
          }
        }
    }*/
  }

  def testSignedIn = SecuredApiAction { implicit request =>
    ok("You are logged in")
  }
  /*
  def signOut = SecuredApiAction { implicit request =>
    ApiToken.delete(request.token).flatMap { _ =>
      noContent()
    }
  }

  implicit val signUpInfoReads: Reads[Tuple3[String, String, User]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String](Reads.minLength[String](6)) and
      (__ \ "user").read[User] tupled
  )
  */

  def signUp = ApiActionWithBody { implicit request =>

    readFromRequest[Anwender] {
      case anw: Anwender =>
        db.run(dal.insert(Anwender(anw.nutzerEmail, anw.password, anw.nutzerName) //Anwender("hans@gmail.com", "test", "hans", Some(PK[Adresse](2L)))
        )) flatMap {
          ok(_)
        }
      case all => ok("didn't work :" + all)

      /*
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
        */

    }
  }

}