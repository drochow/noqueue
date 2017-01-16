package models.db

import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent with BetriebComponent =>
  import driver.api._

  class WarteSchlangenPlatzTable(tag: Tag) extends Table[WarteschlangenPlatzEntity](tag, "WARTESCHLANGENPLATZ") {
    def id = column[PK[WarteschlangenPlatzEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def folgePlatzId = column[Option[PK[WarteschlangenPlatzEntity]]]("NEXT_ID");
    def dienstleistungsId = column[PK[DienstleistungEntity]]("DLT_ID")
    def mitarbeiterId = column[PK[MitarbeiterEntity]]("MIT_ID")
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def beginnZeitpunkt = column[Option[Timestamp]]("BEGINNZEIT", SqlType("timestamp"));
    def dienstleistung = foreignKey("DL_FK", dienstleistungsId, dienstleistungen)(_.id)
    def mitarbeiter = foreignKey("MIT_FK", mitarbeiterId, mitarbeiters)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    //def folgePlatz = foreignKey("NEXT_FK", folgePlatzId, warteschlangenplaetze)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (beginnZeitpunkt, anwenderId, mitarbeiterId, dienstleistungsId, folgePlatzId, id.?) <> (WarteschlangenPlatzEntity.tupled, WarteschlangenPlatzEntity.unapply)

  }

  val warteschlangenplaetze = TableQuery[WarteSchlangenPlatzTable]

  val warteschlangenplaetzeAutoInc = warteschlangenplaetze returning warteschlangenplaetze.map(_.id)

  def insert(wsp: WarteschlangenPlatzEntity) = {
    for {
      mitarbeiterAndDl <- (mitarbeiters.filter(_.id === wsp.mitarbeiterId)
        join dienstleistungen.filter(_.id === wsp.dienstLeistungId) on ((mitarbeiter, dl) => mitarbeiter.betriebId === dl.betriebId)
        joinLeft warteschlangenplaetze on { case ((mitarbeiter: MitarbeiterTable, dl: DienstleistungTable), wspOfMitarbeiter: WarteSchlangenPlatzTable) => mitarbeiter.id === wspOfMitarbeiter.mitarbeiterId }).filter {
          case ((mitarbeiter, dl), wspOfMitarbeiter) => {
            System.out.print(wspOfMitarbeiter.isEmpty)
            wspOfMitarbeiter.map(_.folgePlatzId).isEmpty
          }
        }.filter {
          case (((mitarbeiter, dl)), wspOfMitarbeiter) => mitarbeiter.anwesend === true
        }
        .result.head.nonFusedEquivalentAction
      //.head fails if empty
      /*anwesend <- mitarbeiterAndDl match {
        case ((mitarbeiter: MitarbeiterEntity, dl: DienstleistungEntity), wspOfMitarbeiter: WarteschlangenPlatzEntity) => wspOfMitarbeiter.folgeNummer.isEmpty
        //case _ => False
      }*/
      persistedWsp <- (warteschlangenplaetzeAutoInc += wsp).map(id => {
        // sets FolgePlatzId of earlier WarteschlangenPlatzEntity to persistedWsp.id
        wsp.copy(id = Some(id))
      })
      /*worked <- if (!mitarbeiterAndDl._2.isEmpty) {
        warteschlangenplaetze.filter(_.id === mitarbeiterAndDl._2.map(_.id.get)).map(_.folgePlatzId).update(persistedWsp.id)
      } else {
        DBIO.successful(0);
      }*/
    } yield persistedWsp
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