package models.db

import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent with DienstleistungsTypComponent with BetriebComponent =>

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

  def insertMe(wsp: WarteschlangenPlatzEntity) = {
    for {
      mitarbeiterAndDl <- (mitarbeiters.filter(_.id === wsp.mitarbeiterId)
        join dienstleistungen.filter(_.id === wsp.dienstLeistungId) on (
          (mitarbeiter, dl) => mitarbeiter.betriebId === dl.betriebId
        ) joinLeft warteschlangenplaetze on {
            case ((mitarbeiter: MitarbeiterTable, dl: DienstleistungTable), wspOfMitarbeiter: WarteSchlangenPlatzTable) =>
              mitarbeiter.id === wspOfMitarbeiter.mitarbeiterId
          })
        .filter {
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

  def insert(wsp: WarteschlangenPlatzEntity) = {
    (for {
      isValidDL <- (mitarbeiters.filter(_.id === wsp.mitarbeiterId)
        join dienstleistungen.filter(_.id === wsp.dienstLeistungId) on (
          (mitarbeiter, dl) => mitarbeiter.betriebId === dl.betriebId
        )).exists.result
      checkIsValidDL <- if (!isValidDL) throw new DLInvalidException else DBIO.successful()
      anwenderHasWsp <- warteschlangenplaetze.filter(_.anwenderId === wsp.anwenderId).exists.result
      anwenderHasWspCheck <- {
        if (anwenderHasWsp) throw new AnwenderAlreadyLinedUpException else DBIO.successful()
      }
      mitarbeiterIsAnwesend <- mitarbeiters.filter(_.id === wsp.mitarbeiterId).filter(_.anwesend === true).exists.result
      mitarbeiterIsAnwesendCheck <- {
        if (!mitarbeiterIsAnwesend) throw new MitarbeiterNotAnwesendException else DBIO.successful()
      }
      persistedWsp <- (warteschlangenplaetzeAutoInc += wsp).map(id => wsp.copy(id = Some(id)))

      prevWsp <- warteschlangenplaetze
        .filterNot(_.id === persistedWsp.id)
        .filter(_.mitarbeiterId === wsp.mitarbeiterId)
        .filter(_.folgePlatzId.isEmpty)
        .map(_.folgePlatzId)
        .update(persistedWsp.id)
    } yield persistedWsp).transactionally
  }

  def deleteWspOfAnw(anwID: PK[AnwenderEntity]) =
    for {
      wsp <- warteschlangenplaetze.filter(_.anwenderId === anwID).result.head
      prevWsp <- warteschlangenplaetze.filter(_.folgePlatzId === wsp.id).map(_.folgePlatzId).update(wsp.folgeNummer)
      del <- warteschlangenplaetze.filter(_.anwenderId === anwID).delete
    } yield del

  //  def wspsOfMitarbeiter = 1
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
  def getWarteschlangenPlaetzeOfMitarbeiter(mitarbeiterId: PK[MitarbeiterEntity]): DBIO[Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])]] =
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
    } yield (
      wsps._1._1._1.id, //wsp id
      wsps._1._1._1.beginnZeitpunkt, //wsp beginn
      wsps._1._1._1.folgePlatzId, //wsp next
      wsps._1._1._2, //anwender entity
      wsps._1._2.dauer, //dl dauer
      wsps._2.name, //dl name
      wsps._1._2.id
    ) //dl id
    ).result.nonFusedEquivalentAction

  def getWarteschlangenPlatzOfAnwender(anwenderId: PK[AnwenderEntity]): DBIO[Option[(PK[WarteschlangenPlatzEntity], PK[MitarbeiterEntity], String, String, PK[AdresseEntity], PK[DienstleistungEntity], Int, String)]] =
    (for {
      res <- warteschlangenplaetze join mitarbeiters on {
        case (wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable) => wsp.mitarbeiterId === mt.id
      } join anwenders on {
        case ((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable), mta: AnwenderTable) => mt.anwenderId === mta.id
      } join betriebe on {
        case (((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable),
          mta: AnwenderTable), bt: BetriebTable) => mt.betriebId === bt.id
      } join dienstleistungen on {
        case ((((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable),
          mta: AnwenderTable), bt: BetriebTable), dl: DienstleistungTable) => wsp.dienstleistungsId === dl.id
      } join dienstleistungsTypen on {
        case (((((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable),
          mta: AnwenderTable), bt: BetriebTable), dl: DienstleistungTable),
          dlt: DienstleistungsTypTable) => dlt.id === dl.dlTypId
      } filter {
        case (((((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable),
          mta: AnwenderTable), bt: BetriebTable), dl: DienstleistungTable),
          dlt: DienstleistungsTypTable) => wsp.anwenderId === anwenderId
      }
    } yield (
      res._1._1._1._1._1.id, //id of wsp
      res._1._1._1._1._1.mitarbeiterId, //id of mitarbeiter
      res._1._1._1._2.nutzerName, //name of mitarbeiter
      res._1._1._2.name, //name of betrieb
      res._1._1._2.adresseId, //adresse of betrieb
      res._1._2.id, //id of dl
      res._1._2.dauer, //dauer of dl
      res._2.name //name of dlt
    )).result.headOption

  def getPrevWarteschlangenplaetze(mitarbeiterId: PK[MitarbeiterEntity], wspId: PK[WarteschlangenPlatzEntity]): DBIO[Seq[(PK[WarteschlangenPlatzEntity], Option[PK[WarteschlangenPlatzEntity]], Option[Timestamp], Int)]] = {
    (for {
      res <- warteschlangenplaetze join mitarbeiters on {
        case (wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable) => wsp.mitarbeiterId === mt.id
      } join dienstleistungen on {
        case ((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable), dl: DienstleistungTable) => wsp.dienstleistungsId === dl.id
      } filter {
        case ((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable), dl: DienstleistungTable) => wsp.mitarbeiterId === mitarbeiterId
      } filter {
        case ((wsp: WarteSchlangenPlatzTable, mt: MitarbeiterTable), dl: DienstleistungTable) => wsp.id < wspId
      }
    } yield (res._1._1.id, res._1._1.folgePlatzId, res._1._1.beginnZeitpunkt, res._2.dauer)).result.nonFusedEquivalentAction
  }

  def getWspsOfBetrieb(betriebId: PK[BetriebEntity]): DBIO[Seq[(PK[MitarbeiterEntity], PK[WarteschlangenPlatzEntity], Option[PK[WarteschlangenPlatzEntity]], Option[Timestamp], Int, String)]] = {
    (for {
      res <- mitarbeiters join warteschlangenplaetze on {
        case (m: MitarbeiterTable, wsp: WarteSchlangenPlatzTable) => wsp.mitarbeiterId === m.id
      } join dienstleistungen on {
        case ((m: MitarbeiterTable, wsp: WarteSchlangenPlatzTable), dl: DienstleistungTable) => wsp.dienstleistungsId === dl.id
      } join anwenders on {
        case (((m: MitarbeiterTable, wsp: WarteSchlangenPlatzTable), dl: DienstleistungTable), an: AnwenderTable) => m.anwenderId === an.id
      } filter {
        case (((m: MitarbeiterTable, wsp: WarteSchlangenPlatzTable), dl: DienstleistungTable), an: AnwenderTable) => m.betriebId === betriebId
      }
    } yield (
      res._1._1._1.id, //mitarbeiter ID
      res._1._1._2.id, //wsp ID
      res._1._1._2.folgePlatzId, //next wsp ID
      res._1._1._2.beginnZeitpunkt, //wsp beginnZeitpunkt
      res._1._2.dauer, //dl dauer
      res._2.nutzerName //mitarbeiter Name
    )).result.nonFusedEquivalentAction
  }

  def getAvailableEmployees(betriebId: PK[BetriebEntity]): DBIO[Seq[(PK[MitarbeiterEntity], String)]] =
    (for {
      res <- mitarbeiters.filter(_.betriebId === betriebId).filter(_.anwesend === true) join anwenders on (_.anwenderId === _.id)
    } yield (
      res._1.id, //mitarbeiter ID
      res._2.nutzerName //mitarbeiter Name
    )).result

  def startWorkOn(id: PK[WarteschlangenPlatzEntity], mid: PK[MitarbeiterEntity]): DBIO[Int] = {
    for {
      isAnyInBearbeitung <- warteschlangenplaetze.filter(_.mitarbeiterId === mid).filterNot(_.beginnZeitpunkt.isEmpty).exists.result
      isAnyBefore <- warteschlangenplaetze.filter(_.folgePlatzId === id).exists.result
      res <- if (isAnyBefore) throw new NotFirstWspException else if (isAnyInBearbeitung) throw new AlreadWorkingOnAWspException
      else warteschlangenplaetze.filter(_.id === id).map(_.beginnZeitpunkt).update(Some(new Timestamp(System.currentTimeMillis() / 1000)))
    } yield res
  }

  def finishWorkOn(id: PK[WarteschlangenPlatzEntity], mid: PK[MitarbeiterEntity]): DBIO[Int] = {
    for {
      isInBearbeitung <- warteschlangenplaetze.filter(_.id === id).filter(_.mitarbeiterId === mid).filterNot(_.beginnZeitpunkt.isEmpty).exists.result
      res <- if (isInBearbeitung) {
        warteschlangenplaetze.filter(_.id === id).delete
      } else {
        throw new AlreadWorkingOnAWspException
      }
    } yield res
  }

}