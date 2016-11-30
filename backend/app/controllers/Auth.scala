package controllers

import api.ApiError._
import api.JsonCombinators._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import api.ApiResponse
import api.jwt.{ JwtUtil, TokenPayload }
import models.db.AnwenderEntity
import models.{ Anwender, UnregistrierterAnwender }
import org.joda.time.DateTime
import play.api.Configuration
import play.api.i18n.MessagesApi

class Auth @Inject() (val messagesApi: MessagesApi, system: ActorSystem, val config: Configuration) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "nutzerName").read[String] and
      (__ \ "password").read[String] tupled
  )

  def signIn = ApiActionWithBody { implicit request =>
    readFromRequest[Tuple2[String, String]] {
      case (email, password) =>
        val unregAnw = new UnregistrierterAnwender
        lazy val anwender = new Anwender(unregAnw.anmelden(email, password))
        anwender.anwender flatMap {
          anwender: AnwenderEntity => ok(JwtUtil.signJwtPayload(new TokenPayload(anwender.id.get.value, DateTime.now().plusMinutes(120))))
        }
    }
  }

  def testSignedIn = SecuredApiAction { implicit request =>
    ok("You are logged in")
  }

  //  def signOut = SecuredApiAction { implicit request => ok(Json.obj("message" -> "Successfully logged out")) }
  //  //
  //  implicit val signUpInfoReads: Reads[Tuple3[String, String, User]] = (
  //    (__ \ "email").read[String](Reads.email) and
  //      (__ \ "password").read[String](Reads.minLength[String](6)) and
  //      (__ \ "user").read[User] tupled
  //  )
  //
  //  def signUp = ApiActionWithBody { implicit request =>
  //    readFromRequest[Tuple3[String, String, User]] {
  //      case (email, password, user) =>
  //        User.findByEmail(email).flatMap {
  //          case Some(anotherUser) => errorCustom("api.error.signup.email.exists")
  //          case None => User.insert(email, password, user.name).flatMap {
  //            case (id, user) =>
  //
  //              // Send confirmation email. You will have to catch the link and confirm the email and activate the user.
  //              // But meanwhile...
  //              system.scheduler.scheduleOnce(30 seconds) {
  //                User.confirmEmail(id)
  //              }
  //
  //              ok(user)
  //          }
  //        }
  //    }
  //  }

}