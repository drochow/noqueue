package controllers

import java.sql.SQLTimeoutException
import javax.inject.Inject

import api.ApiError
import api.JsonCombinators._
import models.db.{ AdresseEntity, AnwenderEntity, PK }
import play.api.Configuration
import play.api.i18n.MessagesApi

class Application @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def test = ApiAction { implicit request =>
    //    val action = for {
    //      a1 <- dal.insert(AnwenderEntity("hans@gmail.com", "test", "hans"))
    //      a2 <- dal.insert(AdresseEntity("Bollestraße", "20", "13509", "Berlin"))
    //      a3 <- dal.insert(AdresseEntity("Bollestraße", "21", "13509", "Berlin"))
    //      a4 <- dal.insert(AnwenderEntity("hans@gmail.com", "test", "hans", a3.id))
    //    } yield ()
    //
    //    db.run(action) flatMap {
    //      _ => ok("Insertion complete")
    //    } recover {
    //      case t => ApiError.errorInternal("Unable to insert:" + t.toString())
    //    }
    ok("Success")
  }

  def test2 = ApiAction { implicit request =>
    //    db.run(dal.insert(AnwenderEntity("hans@gmail.com", "test", "hans", Some(PK[AdresseEntity](2L))))) flatMap {
    //      case anwender: AnwenderEntity => ok[AnwenderEntity](anwender)
    //    } recover {
    //      case ex: SQLTimeoutException => ApiError.errorInternal("Service Unavailable: Database not reachable.")
    //      case ex: NoSuchElementException => ApiError.errorBadRequest("Provided Adress does not exist.")
    //      case _ => ApiError.errorInternal("Service Unavailabl: Unknown error occured")
    //    }
    //
    //    //    db.run(dal.insert(AnwenderEntity("hans@gmail.com", "test", "hans", Some(PK[AdresseEntity](20L)))).asTry).map {
    //    //      result =>
    //    //        result match {
    //    //          case Success(res) => res
    //    //          case Failure(e: PSQLException) => e.
    //    //        }
    //    //    } flatMap {
    //    //      case anwender => ok("Yay")
    //    //    } recover {
    //    //      case msg => ApiError.errorBadRequest("Unable to insert:" + msg)
    //    //    }
    ok("Success")
  }

  def test3 = ApiAction { implicit request =>
    //    db.run(dal.getAnwenderWithAdress(PK[AnwenderEntity](2L))) flatMap {
    //      case anwenderWithAdress: Seq[(AnwenderEntity, AdresseEntity)] => ok(anwenderWithAdress.head._1)
    //    } recover {
    //      case e => ApiError.errorInternal("Something happend: " + e.toString())
    //    }
    //    //    db.run(dal.get(PK[AdresseEntity](1L))) flatMap {
    //    //      case adresse: AdresseEntity => ok[AdresseEntity](adresse)
    //    //    } recover {
    //    //      case ex: SQLTimeoutException => ApiError.errorInternal("Service Unavailable: Database not reachable.")
    //    //      case ex: NoSuchElementException => ApiError.errorBadRequest("Provided Adress does not exist.")
    //    //      case _ => ApiError.errorInternal("Service Unavailabl: Unknown error occured")
    //    //    }
    ok("Success")
  }

  def setup = SecuredApiAction { implicit request =>
    //    db.run(dal.create) flatMap {
    //      _ => ok("Setup complete")
    //    } recover {
    //      case t => ApiError.errorInternal("Unable to setup:" + t.toString())
    //    }
    ok("Success")
  }

}
