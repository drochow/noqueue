package models.db

/**
 * Created by anwender on 25.11.2016.
 */
case class LeiterEntity(val anwenderId: PK[AnwenderEntity], val betriebId: PK[BetriebEntity], val id: Option[PK[LeiterEntity]] = None) {

}
