package models.db

/**
 *
 * BetriebEntity Representation
 *
 * @param name
 * @param tel
 * @param oeffnungszeiten
 * @param kontaktEmail
 * @param adresseId
 * @param id
 */
case class BetriebEntity(
    name: String,
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String,
    adresseId: PK[AdresseEntity],
    id: Option[PK[BetriebEntity]] = None
) {
}

case class BetriebAndAdresse(betriebEntity: BetriebEntity, adresseEntity: AdresseEntity)
