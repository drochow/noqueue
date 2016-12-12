package models.db

/**
 * AdresseEntity Representation
 *
 * @param strasse
 * @param hausNummer
 * @param plz
 * @param stadt
 * @param id
 */
case class AdresseEntity(
    strasse: String,
    hausNummer: String,
    plz: String,
    stadt: String,
    id: Option[PK[AdresseEntity]] = None
) {
}
