package osm;

import javax.inject.Inject

import models.db.{ AdresseEntity, DienstleistungsTypEntity, PK }
import play.api.Play
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.functional.syntax._

class InvalidGeoCoordsException(msg: String) extends Exception(msg: String)

class AdressNotFoundException(msg: String) extends Exception(msg: String)

case class GeoCoords(latitude: Double, longitude: Double) {
}

object GeoCoords {

  /**
   * Json Reader wich expects an JSON object like:
   *  {
   *      ..
   *      lat: "double parseable value"
   *      lon: "double parseable value"
   *      ..
   *   }
   *
   *   expected syntax for "double parseable  value": %f3.15
   *
   *  Ignoring additional values
   */
  //@todo may move to JsonCombinators
  //@todo catch possible errors in case of string values with a unexpected(not double like) syntax
  implicit val geoCoordsRead: Reads[GeoCoords] = (
    (__ \ "lat").read[String] and
    (__ \ "lon").read[String]
  )((latitude: String, longitude: String) => GeoCoords(latitude = latitude.toDouble, longitude = longitude.toDouble))
}

trait AdressService {

  def getCoordsOfAdress(adresseEntity: AdresseEntity): Future[GeoCoords]
}

class GoolgeAdressService @Inject() (ws: WSClient) extends AdressService {
  val baseURL = "https://nominatim.openstreetmap.org/search"
  override def getCoordsOfAdress(adresseEntity: AdresseEntity): Future[GeoCoords] = {
    val req: WSRequest = ws.url(s"$baseURL")
      .withQueryString(
        "format" -> "json",
        "countrycode" -> "de",
        "city" -> adresseEntity.stadt,
        "street" -> (adresseEntity.hausNummer + " " + adresseEntity.strasse),
        "postalcode" -> adresseEntity.plz
      )
    System.out.println(req.uri.toString)
    req.get() map {
      response =>

        response.json.validate[Seq[GeoCoords]] match {
          case geoCoords: JsSuccess[Seq[GeoCoords]] =>
            //@todo throw explicit error wich indicates multiple matching GeoCoordinates for the provided adress and allow user to choose the correct address
            if (geoCoords.value.length >= 1)
              geoCoords.value(0)
            else
              throw new AdressNotFoundException("No matching GeoCoords found")

          case e: JsError => {
            System.out.println(JsError.toJson(e).toString())
            throw new InvalidGeoCoordsException(JsError.toJson(e).toString())
          }
        }
    }
  }
}

/**
 * Adress Service to verify provided adresses and return its Coordinates
 * @param ws
 */
class OSMAdressService @Inject() (ws: WSClient) extends AdressService {
  val baseURL = "https://nominatim.openstreetmap.org/search"

  /**
   * Searches on OpenStreetMap for an adress with the specified parameters and returns a sequenece of all found results
   *
   * @param adresseEntity representation of an adress for wich we search the geo coordinates
   * @return
   */
  override def getCoordsOfAdress(adresseEntity: AdresseEntity): Future[GeoCoords] = {
    val req: WSRequest = ws.url(s"$baseURL")
      .withQueryString(
        "format" -> "json",
        "countrycode" -> "de",
        "city" -> adresseEntity.stadt,
        "street" -> (adresseEntity.hausNummer + " " + adresseEntity.strasse),
        "postalcode" -> adresseEntity.plz
      )
    System.out.println(req.uri.toString)
    req.get() map {
      response =>

        response.json.validate[Seq[GeoCoords]] match {
          case geoCoords: JsSuccess[Seq[GeoCoords]] =>
            //@todo throw explicit error wich indicates multiple matching GeoCoordinates for the provided adress and allow user to choose the correct address
            if (geoCoords.value.length >= 1)
              geoCoords.value(0)
            else
              throw new AdressNotFoundException("No matching GeoCoords found")

          case e: JsError => {
            System.out.println(JsError.toJson(e).toString())
            throw new InvalidGeoCoordsException(JsError.toJson(e).toString())
          }
        }
    }
  }
}
