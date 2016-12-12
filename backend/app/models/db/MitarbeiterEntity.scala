package models.db

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
  id: Option[PK[MitarbeiterEntity]] = None) {
}
