package controllers

import java.sql.SQLException
import javax.inject.Inject

import api.ApiError
import api.JsonCombinators._
import models.UnregistrierterAnwender
import models.db._
import osm.{ AdressNotFoundException, AdressService, GeoCoords, InvalidGeoCoordsException }
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.{ OneLeiterRequiredException, UnauthorizedException }

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by anwender on 06.11.2016.
 */
class Betrieb @Inject() (val as: AdressService, val messagesApi: MessagesApi, val config: Configuration) extends api.ApiController {

  def create = SecuredApiActionWithBody { implicit request =>
    //@todo cleanup and move all recover blocks to the most outer recover to enable generic errorhandling via the template method pattern
    readFromRequest[BetriebAndAdresse] {
      case btrAndAdr: BetriebAndAdresse => {
        (for {
          geo: GeoCoords <- as.getCoordsOfAdress(btrAndAdr.adresseEntity)
          apiResult <- request.anwender.betriebErstellen(
            btrAndAdr.betriebEntity,
            btrAndAdr.adresseEntity.copy(latitude = Some(geo.latitude), longitude = Some(geo.longitude))
          ) flatMap {
              baa: (BetriebEntity, AdresseEntity) => ok(BetriebAndAdresse(betriebEntity = baa._1, adresseEntity = baa._2));
            } recover {
              //failure
              case sqlE: SQLException => {
                if (sqlE.getMessage.contains("betriebNameUnique")) {
                  ApiError.errorBadRequest("Ein Betrieb mit diesem Namen existiert bereits!")
                }
                if (sqlE.getMessage.contains("betriebTelUnique")) {
                  ApiError.errorBadRequest("Ein Betrieb mit dieser Telefonnummer existiert bereits!")
                } else
                  ApiError.errorBadRequest(sqlE.getMessage)
              }
              case e: Exception => {
                e.printStackTrace()
                ApiError.errorBadRequest("Invalid data..")
              }
            }
        } yield apiResult) recover {
          case igce: InvalidGeoCoordsException => ApiError.errorBadRequest(igce.getMessage)
          case anfe: AdressNotFoundException => ApiError.errorItemNotFound("This adress does not exist.")
        }
      }
    }
  }

  def modify(id: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](id)) { implicit request =>
    readFromRequest[BetriebAndAdresse] {
      case btrAndAdr: BetriebAndAdresse => {
        (for {
          geo: GeoCoords <- as.getCoordsOfAdress(btrAndAdr.adresseEntity)
          apiResult <- request.leiter.betriebsInformationenVeraendern(PK[BetriebEntity](id), btrAndAdr.betriebEntity,
            btrAndAdr.adresseEntity.copy(latitude = Some(geo.latitude), longitude = Some(geo.longitude)))
            .flatMap {
              case affectedRows: Int => if (affectedRows < 1) ApiError.errorItemNotFound else accepted()
            } recover {
              case ua: UnauthorizedException => ApiError.errorUnauthorized
              case e: Exception => {
                e.printStackTrace()
                ApiError.errorBadRequest("Invalid data..")
              }
            }
        } yield apiResult) recover {
          case igce: InvalidGeoCoordsException => ApiError.errorBadRequest(igce.getMessage)
          case anfe: AdressNotFoundException => ApiError.errorItemNotFound("This adress does not exist.")
        }
      }
    }
  }

  def show(id: Long) = SecuredApiAction { implicit request =>
    request.anwender.betriebAnzeigen(PK[BetriebEntity](id)) flatMap {
      btrAndAdr: (BetriebEntity, AdresseEntity) => ok(BetriebAndAdresse(betriebEntity = btrAndAdr._1, adresseEntity = btrAndAdr._2))
    } recover {
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def addMitarbeiter(betriebId: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[MitarbeiterEntity] {
      case mitarbeiter: MitarbeiterEntity => {
        request.leiter.mitarbeiterAnstellen(mitarbeiter) flatMap {
          case mitarbeiter: MitarbeiterEntity => created()
        } recover {
          case ua: UnauthorizedException => ApiError.errorUnauthorized
          case e: Exception => {
            e.printStackTrace()
            ApiError.errorInternalServer("Something went wrong")
          }
        }
      }
    }
  }

  def removeMitarbeiter(betriebId: Long, mitarbeiterId: Long) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.mitarbeiterEntlassen(PK[MitarbeiterEntity](mitarbeiterId), PK[BetriebEntity](betriebId)) flatMap {
      case count: Int => if (count < 1) ApiError.errorItemNotFound else ok("Success")
    } recover {
      case ua: UnauthorizedException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorInternalServer("Something went wrong")
      }
    }
  }

  def listMitarbeiter(betriebId: Long, page: Int, size: Int) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.mitarbeiterAnzeigen(page, size) flatMap {
      mitarbeiter => ok(mitarbeiter)
    } recover {
      case nse: UnauthorizedException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def addLeiter(betriebId: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[LeiterEntity] {
      case leiter: LeiterEntity => {
        request.leiter.leiterEinstellen(leiter, PK[BetriebEntity](betriebId)) flatMap {
          leiter => ok(leiter)
        } recover {
          case ua: UnauthorizedException => ApiError.errorUnauthorized
          case e: Exception => {
            e.printStackTrace()
            ApiError.errorBadRequest("Invalid data..")
          }
        }
      }
    }
  }

  def removeLeiter(betriebId: Long, leiterId: Long) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.leiterEntlassen(PK[LeiterEntity](leiterId), PK[BetriebEntity](betriebId)) flatMap {
      affectedRows: Int => if (affectedRows < 1) ApiError.errorItemNotFound else accepted()
    } recover {
      case ua: UnauthorizedException => ApiError.errorUnauthorized
      case nse: NoSuchElementException => ApiError.errorUnauthorized
      case olre: OneLeiterRequiredException => ApiError.errorBadRequest("Atleast 1 Leiter is required. You can not delete the last Leiter.")
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def listLeiter(betriebId: Long, page: Int, size: Int) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.leiterAnzeigen(page, size) flatMap {
      leiter => ok(leiter)
    } recover {
      case nse: UnauthorizedException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def addDienstleistung(betriebId: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[DienstleistungEntityApiRead] {
      case dlar: DienstleistungEntityApiRead => {
        request.leiter.dienstleistungAnbieten(dlar.name, dlar.dauer, dlar.kommentar) flatMap {
          dl => ok(dl)
        } recover {
          case nse: UnauthorizedException => ApiError.errorUnauthorized
          case e: Exception => {
            e.printStackTrace()
            ApiError.errorBadRequest("Invalid data..")
          }
        }
      }
    }
  }

  def updateDienstleistung(betriebId: Long, dlId: Long) = SecuredLeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[DienstleistungEntityApiRead] {
      case dlar: DienstleistungEntityApiRead => {
        request.leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](dlId), dlar.name, dlar.dauer, dlar.kommentar) flatMap {
          dl => if (dl < 1) ApiError.errorItemNotFound else accepted()
        } recover {
          case nse: UnauthorizedException => ApiError.errorUnauthorized
          case e: Exception => {
            e.printStackTrace()
            ApiError.errorBadRequest("Invalid data..")
          }
        }
      }
    }
  }

  def removeDienstleistung(betriebId: Long, dlId: Long) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.dienstleistungEntfernen(PK[DienstleistungEntity](dlId)) flatMap {
      count => if (count < 1) ApiError.errorItemNotFound else accepted()
    } recover {
      case nse: UnauthorizedException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def listDienstleistung(betriebId: Long, page: Int, size: Int) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.dienstleistungAnzeigen(page, size) flatMap {
      dientleistung => ok(dientleistung)
    } recover {
      case nse: UnauthorizedException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  //@todo may move to other controller
  def searchDLT(query: String, page: Int, size: Int) = SecuredApiAction { implicit request =>
    request.anwender.dienstleistungsTypSuchen(query, page, size) flatMap {
      dlt => ok(dlt)
    } recover {
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  ////////////////////////////////////
  /// Actions performed by Mitarbeiters
  ////////////////////////////////////

  implicit val anwesendReads = ((__ \ "anwesend").read[Boolean])

  def mitarbeiterAnwesenheitVeraendern(betriebId: Long) = SecuredMitarbeiterApiActionWithBody(PK[BetriebEntity](betriebId)) { implicit request =>
    readFromRequest[Boolean] {
      anwesend =>
        val updated = request.mitarbeiter.mitarbeiterAnwesenheitVeraendern(anwesend)
        updated.flatMap {
          if (_) {
            ok("You set Anwesenheit to " + anwesend.toString)
          } else {
            ApiError.errorInternal("could not complete anwesenheitSetting")
          }
        }
    }(request, anwesendReads, request.request) //request and req.req are the vals that would have also been taken if they hadn't been declared
  }

  /**
   * TEST METHODS  JUST FOR BOILERPLATE TESTS
   * TEST METHODS  JUST FOR BOILERPLATE TESTS
   * TEST METHODS  JUST FOR BOILERPLATE TESTS
   */

  def mitarbeiterOnlyTest(betriebId: Long) = SecuredMitarbeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.mitarbeiter.mitarbeiter flatMap {
      case _ => ok("Success")
    } recover {
      case nse: NoSuchElementException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def leiterOnlyTest(betriebId: Long) = SecuredLeiterApiAction(PK[BetriebEntity](betriebId)) { implicit request =>
    request.leiter.leiter flatMap {
      case _ => ok("Success")
    } recover {
      case nse: NoSuchElementException => ApiError.errorUnauthorized
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest("Invalid data..")
      }
    }
  }

  def search(query: String, lat: Double, long: Double, umkreis: Int, page: Int, size: Int) = ApiAction { implicit request =>
    val ua = new UnregistrierterAnwender
    ua.anbieterSuchen(suchBegriff = query, latitude = lat, longitude = long, umkreisM = umkreis, page = page, size = size) flatMap {
      case (seq: Seq[(BetriebAndAdresse, String)]) => {
        if (seq.length > 0) System.out.println(seq(0)._2)
        ok(seq)
      }
    } recover {
      case e: Exception => {
        e.printStackTrace()
        ApiError.errorBadRequest(e.getMessage)
      }
    }
  }

}
