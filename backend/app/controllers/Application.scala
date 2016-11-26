package controllers

import java.sql.SQLTimeoutException
import javax.inject.Inject

import api.ApiError
import api.JsonCombinators._
import models.db.PK
import models.{ Adresse, Anwender }
import play.api.Configuration
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject() (val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def test = ApiAction { implicit request =>
    val action = for {
      a1 <- dal.insert(Anwender("hans@gmail.com", "test", "hans"))
      a2 <- dal.insert(Adresse("Bollestraße", "20", "13509", "Berlin"))
      a3 <- dal.insert(Adresse("Bollestraße", "21", "13509", "Berlin"))
      a4 <- dal.insert(Anwender("hans@gmail.com", "test", "hans", a3.id))
    } yield ()

    db.run(action) flatMap {
      _ => ok("Insertion complete")
    } recover {
      case t => ApiError.errorInternal("Unable to insert:" + t.toString())
    }
  }

  def test2 = ApiAction { implicit request =>
    db.run(dal.insert(Anwender("hans@gmail.com", "test", "hans", Some(PK[Adresse](2L))))) flatMap {
      case anwender: Anwender => ok[Anwender](anwender)
    } recover {
      case ex: SQLTimeoutException => ApiError.errorInternal("Service Unavailable: Database not reachable.")
      case ex: NoSuchElementException => ApiError.errorBadRequest("Provided Adress does not exist.")
      case _ => ApiError.errorInternal("Service Unavailabl: Unknown error occured")
    }

    //    db.run(dal.insert(Anwender("hans@gmail.com", "test", "hans", Some(PK[Adresse](20L)))).asTry).map {
    //      result =>
    //        result match {
    //          case Success(res) => res
    //          case Failure(e: PSQLException) => e.
    //        }
    //    } flatMap {
    //      case anwender => ok("Yay")
    //    } recover {
    //      case msg => ApiError.errorBadRequest("Unable to insert:" + msg)
    //    }
  }

  def test3 = ApiAction { implicit request =>
    db.run(dal.getAnwenderWithAdress(PK[Anwender](2L))) flatMap {
      case anwenderWithAdress: Seq[(Anwender, Adresse)] => ok(anwenderWithAdress.head._1)
    } recover {
      case e => ApiError.errorInternal("Something happend: " + e.toString())
    }
    //    db.run(dal.get(PK[Adresse](1L))) flatMap {
    //      case adresse: Adresse => ok[Adresse](adresse)
    //    } recover {
    //      case ex: SQLTimeoutException => ApiError.errorInternal("Service Unavailable: Database not reachable.")
    //      case ex: NoSuchElementException => ApiError.errorBadRequest("Provided Adress does not exist.")
    //      case _ => ApiError.errorInternal("Service Unavailabl: Unknown error occured")
    //    }
  }

  def setup = ApiAction { implicit request =>
    db.run(dal.create) flatMap {
      _ => ok("Setup complete")
    } recover {
      case t => ApiError.errorInternal("Unable to setup:" + t.toString())
    }
  }

}
