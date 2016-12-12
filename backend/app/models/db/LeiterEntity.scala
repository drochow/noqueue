package models.db

/**
 * LeiterEntity Representation
 *
 * @param anwenderId
 * @param betriebId
 * @param id
 */
case class LeiterEntity(
    anwenderId: PK[AnwenderEntity],
    betriebId: PK[BetriebEntity],
    id: Option[PK[LeiterEntity]] = None
) {
}
