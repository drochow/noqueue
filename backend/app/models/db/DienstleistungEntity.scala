package models.db

/**
 * DienstleistungEntity Representation
 *
 * @param kommentar
 * @param aktion
 * @param tags
 * @param betrieb
 * @param dienstLeistungsTyp
 * @param id
 */
case class DienstleistungEntity(
    //schaetzdauer
    kommentar: String,
    aktion: String,
    tags: String,
    betrieb: PK[BetriebEntity],
    dienstLeistungsTyp: PK[DienstleistungsTypEntity],
    id: Option[PK[DienstleistungEntity]] = None
) {
}
