package models

import models.db.PK

case class Dienstleistung(
    // schaetzDauer: ,
    kommentar: String,
    aktion: String,
    tags: String,
    betrieb: PK[Betrieb],
    dienstLeistungsTyp: PK[DienstleistungsTyp],
    id: Option[PK[Dienstleistung]] = None
) {

}
