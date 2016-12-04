package models.db

case class MitarbeiterEntity(anwesend: Boolean, betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity], id: Option[PK[MitarbeiterEntity]] = None) {

}
