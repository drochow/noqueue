package models.db

import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent with DienstleistungsTypComponent with BetriebComponent =>

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
    //def folgePlatz = foreignKey("NEXT_FK", folgePlatzId, warteschlangenplaetze)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (beginnZeitpunkt.?, anwenderId, mitarbeiterId, dienstleistungsId, folgePlatzId.?, id.?) <> (WarteschlangenPlatzEntity.tupled, WarteschlangenPlatzEntity.unapply)

  }

  val warteschlangenplaetze = TableQuery[WarteSchlangenPlatzTable]

  val warteschlangenplaetzeAutoInc = warteschlangenplaetze returning warteschlangenplaetze.map(_.id)

  //    def insert(wsp: WarteschlangenPlatzEntity) = {
  //      for {
  //        mitarbeiterAndDl <- (mitarbeiters.filter(_.id === wsp.mitarbeiterId)
  //          join dienstleistungen.filter(_.id === wsp.dienstLeistungId) on ((mitarbeiter, dl) => mitarbeiter.betriebId === dl.betriebId)
  //          join warteschlangenplaetze on { case ((mitarbeiter: MitarbeiterTable, dl: DienstleistungTable), wspOfMitarbeiter: WarteSchlangenPlatzTable) => mitarbeiter.id === wspOfMitarbeiter.mitarbeiterId }).filter {
  //            case ((dl, mitarbeiter), wspOfMitarbeiter) => wspOfMitarbeiter.folgePlatzId.isEmpty
  //          }.result.head.nonFusedEquivalentAction
  //        //.head fails if empty
  //        anwesend <- Future.successful(mitarbeiterAndDl match {
  //          case ((mitarbeiter: MitarbeiterEntity, dl: DienstleistungEntity), wspOfMitarbeiter: WarteschlangenPlatzEntity) => wspOfMitarbeiter.folgeNummer.isEmpty
  //          //case _ => False
  //        })
  //        persistedWsp <- if (anwesend) (warteschlangenplaetzeAutoInc += wsp).map(id => {
  //          warteschlangenplaetze.filter(_.id === mitarbeiterAndDl._2.id.get).map(_.folgePlatzId).update(Some(id))
  //          // sets FolgePlatzId of earlier WarteschlangenPlatzEntity to persistedWsp.id
  //          wsp.copy(id = Some(id))
  //        })
  //        else DBIO.failed(new Throwable("anwesend: " + anwesend))
  //      } yield persistedWsp
  //    }

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

  /**
   *
   * @param mitarbeiterId
   * @return (Wsp_ID, BeginnZeitpunkt, Folge_Wsp_ID, Anwender, Dauer, DienstleistungsName)
   */
  def getWarteschlangenPlaetzeOfMitarbeiter(mitarbeiterId: PK[MitarbeiterEntity]): DBIO[Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])]] =
    (for {
      wsps <- warteschlangenplaetze join anwenders on {
        case (wsp: WarteSchlangenPlatzTable, anw: AnwenderTable) => wsp.anwenderId === anw.id
      } join dienstleistungen on {
        case ((wsp: WarteSchlangenPlatzTable, anw: AnwenderTable), dl: DienstleistungTable) => wsp.dienstleistungsId === dl.id
      } join dienstleistungsTypen on {
        case (((wsp: WarteSchlangenPlatzTable, anw: AnwenderTable), dl: DienstleistungTable), dlt: DienstleistungsTypTable) => dlt.id === dl.dlTypId
      } filter {
        case (((wsp: WarteSchlangenPlatzTable, anw: AnwenderTable), dl: DienstleistungTable), dlt: DienstleistungsTypTable) =>
          wsp.mitarbeiterId === mitarbeiterId
      }
    } yield (wsps._1._1._1.id, wsps._1._1._1.beginnZeitpunkt.?, wsps._1._1._1.folgePlatzId, wsps._1._1._2, wsps._1._2.dauer, wsps._2.name, wsps._1._2.id)).result.nonFusedEquivalentAction
}