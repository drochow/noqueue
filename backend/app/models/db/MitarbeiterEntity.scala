package models.db

//@todo these should have unique names in their Betrieb so we don't have to register with "crazyFrogfan1993"
/**
 * MitarbeiterEntity Representation
 *
 * @param anwesend
 * @param betriebId
 * @param anwenderId
 * @param id
 */
case class MitarbeiterEntity(
    anwesend: Boolean,
    betriebId: PK[BetriebEntity],
    anwenderId: PK[AnwenderEntity],
    id: Option[PK[MitarbeiterEntity]] = None
) {
}
