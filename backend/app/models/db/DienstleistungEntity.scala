package models.db

import api.auth.Credentials
import play.api.libs.json.{ Format, Json }

/**
 * DienstleistungEntity Representation
 */
case class DienstleistungEntity(
    kommentar: String,
    dauer: Int,
    betriebId: PK[BetriebEntity],
    dienstLeistungsTyp: PK[DienstleistungsTypEntity],
    id: Option[PK[DienstleistungEntity]] = None
) {
}

case class DienstleistungEntityApiRead(
    name: String,
    kommentar: String,
    dauer: Int
) {

}

object DienstleistungEntityApiRead {
  implicit val jsonFormat: Format[DienstleistungEntityApiRead] = Json.format[DienstleistungEntityApiRead]
}