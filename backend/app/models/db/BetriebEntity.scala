package models.db

/**
 * BetriebEntity Representation
 *
 * @param tel
 * @param oeffnungszeiten
 * @param kontaktEmail
 * @param wsOffen
 * @param adresseId
 * @param id
 */
case class BetriebEntity(
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String,
    wsOffen: Boolean,
    adresseId: PK[AdresseEntity],
    id: Option[PK[BetriebEntity]] = None
) {
}
