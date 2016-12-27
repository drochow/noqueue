package models.db

/**
 * DienstleistungEntity Representation
 */
case class DienstleistungEntity(
    kommentar: String,
    aktion: String,
    mitarbeiter: PK[MitarbeiterEntity],
    dienstLeistungsTyp: PK[DienstleistungsTypEntity],
    id: Option[PK[DienstleistungEntity]] = None
) {
}
