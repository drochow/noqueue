package models.db

case class DienstleistungEntity(
    // schaetzDauer: ,
    kommentar: String,
    aktion: String,
    tags: String,
    betrieb: PK[BetriebEntity],
    dienstLeistungsTyp: PK[DienstleistungsTypEntity],
    id: Option[PK[DienstleistungEntity]] = None
) {

}
