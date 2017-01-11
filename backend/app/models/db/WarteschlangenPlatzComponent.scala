package models.db

import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent with BetriebComponent =>
  import driver.api._

  class WarteSchlangenPlatzTable(tag: Tag) extends Table[WarteschlangenPlatzEntity](tag, "WARTESCHLANGENPLATZ") {
    def id = column[PK[WarteschlangenPlatzEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def folgePlatzId = column[PK[WarteschlangenPlatzEntity]]("NEXT_ID");
    def dienstleistungsId = column[PK[DienstleistungEntity]]("DLT_ID")
    def mitarbeiterId = column[PK[MitarbeiterEntity]]("MIT_ID")
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def beginnZeitpunkt = column[Timestamp]("BEGINNZEIT", SqlType("timestamp"));
    def dienstleistung = foreignKey("DL_FK", dienstleistungsId, dienstleistungen)(_.id)
    def mitarbeiter = foreignKey("MIT_FK", mitarbeiterId, mitarbeiters)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    def folgePlatz = foreignKey("NEXT_FK", folgePlatzId, warteschlangenplaetze)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (beginnZeitpunkt, anwenderId, mitarbeiterId, dienstleistungsId, folgePlatzId.?, id.?) <> (WarteschlangenPlatzEntity.tupled, WarteschlangenPlatzEntity.unapply)

  }

  val warteschlangenplaetze = TableQuery[WarteSchlangenPlatzTable]

  val warteschlangenplaetzeAutoInc = warteschlangenplaetze returning warteschlangenplaetze.map(_.id)

  def insert(wsp: WarteschlangenPlatzEntity) = {
    for{
      mitarbeiterAndDl <- (mitarbeiters.filter(_.id === wsp.mitarbeiterId)
        join dienstleistungen.filter(_.id === wsp.dienstLeistungId) on ((mitarbeiter, dl)=> mitarbeiter.betriebId === dl.betriebId)).result.head.nonFusedEquivalentAction
      // .head fails if empty
      anwesend <- mitarbeiterAndDl._1.anwesend
      persistedWsp <- if(anwesend)(warteschlangenplaetzeAutoInc += wsp).map(id => wsp.copy(id = Some(id)))
        else DBIO.failed(new Throwable("anwesend: "+ anwesend))
    }yield persistedWsp
  }

  def wspsOfMitarbeiter = 1
  /*
    //@todo do not ignore the newest wsp if beginnzeitpunkt is not wsp.dl.dauer ago
    val wspAndMitarbeiterAndDl = (
      dienstleistungen.filter(_.id === wsp.dienstLeistungId)
        join mitarbeiters.filter(_.id === wsp.mitarbeiterId) on ((dl, mitarbeiter) => dl.betriebId === mitarbeiter.betriebId)
        //now we know mitarbeiter offers doing dienstleistung
        join warteschlangenplaetze on {case ((dl: DienstleistungTable, mitarbeiter: MitarbeiterTable), wspOfMitarbeiter: WarteSchlangenPlatzTable) => mitarbeiter.id === wspOfMitarbeiter.mitarbeiterId}
        join dienstleistungen on {case (((dl: DienstleistungTable, mitarbeiter: MitarbeiterTable), wspOfMitarbeiter: WarteSchlangenPlatzTable), dlOfMitarbeiter: DienstleistungTable) => dlOfMitarbeiter.id === wspOfMitarbeiter.mitarbeiterId}
    )
      .filter{
        case (((dl, mitarbeiter), wspOfMitarbeiter), dlOfMitarbeiter) => wspOfMitarbeiter.beginnZeitpunkt === None
      }//filter out what is far in the past
    */


  def getWarteschlangenPlaetzeOfMitarbeiter(mitarbeiterId: PK[MitarbeiterEntity]): DBIO[Seq[WarteschlangenPlatzEntity]] =
    warteschlangenplaetze.filter(_.mitarbeiterId === mitarbeiterId).result

}