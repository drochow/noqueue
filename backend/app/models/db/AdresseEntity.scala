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
    latitude: Option[Double],
    longitude: Option[Double],
    id: Option[PK[AdresseEntity]] = None
) {
}
