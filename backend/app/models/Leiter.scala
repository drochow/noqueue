package models

import java.sql.Timestamp
import java.util.NoSuchElementException

import models.db._
import play.api.inject.ApplicationLifecycle
import slick.dbio.{ DBIO, DBIOAction }
import utils.UnauthorizedException

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by David on 29.11.16.
 */
class Leiter(val leiterAction: DBIO[(BetriebEntity, AnwenderEntity, LeiterEntity)], applicationLifecycle: ApplicationLifecycle, dbD: DB) extends Base(applicationLifecycle, dbD) {

  lazy val betrieb: Future[BetriebEntity] = leiterComposition map (_._1)

  lazy val anwender: Future[AnwenderEntity] = leiterComposition map (_._2)

  lazy val leiter: Future[LeiterEntity] = leiterComposition map (_._3)

  lazy private val leiterComposition: Future[(BetriebEntity, AnwenderEntity, LeiterEntity)] = db.run(leiterAction)

  /**
   * Ensures that the leiterAction is performed before calling the provided action
   *
   * Background:
   *
   * Since the LeiterModel is created without the need of performing the "leiterAction" given in the constructor
   * we need to ensure the authorization of all actions by calling leiterAction
   *
   * @param action database action to perform
   * @param betriebId the id of the Betrieb that is affected
   * @tparam T return type of database action
   * @return the result of the given action
   */
  private def authorizedAction[T](action: () => Future[T], betriebId: PK[BetriebEntity]): Future[T] =
    leiterComposition flatMap {
      case (betrieb, anwender, leiter) => if (betrieb.id.get == betriebId) action() else throw new UnauthorizedException
    } recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  def betriebsInformationenVeraendern(id: PK[BetriebEntity], betrieb: BetriebEntity, adresse: AdresseEntity) =
    authorizedAction(() => db.run(dal.update(id, betrieb, adresse)), id)

  def mitarbeiterAnstellen(mitarbeiter: MitarbeiterEntity): Future[MitarbeiterEntity] =
    authorizedAction(() => db.run(dal.insert(mitarbeiter)), mitarbeiter.betriebId)

  def mitarbeiterEntlassen(mitarbeiterPK: PK[MitarbeiterEntity]): Future[Boolean] =
    betrieb flatMap {
      case betriebE => authorizedAction(() => db.run(dal.deleteMitarbeiter(mitarbeiterPK)).map(_ == 1), betriebE.id.get)
    }

  def mitarbeiterAnzeigen(page: Int, size: Int): Future[Seq[(MitarbeiterEntity, AnwenderEntity)]] =
    betrieb flatMap {
      case betrieb => db.run(dal.listMitarbeiterOf(betrieb.id.get, page, size))
    } recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  def leiterEinstellen(leiterEntity: LeiterEntity, betriebId: PK[BetriebEntity]): Future[LeiterEntity] =
    authorizedAction(() => db.run(dal.insert(leiterEntity)), betriebId)

  def leiterEntlassen(leiterId: PK[LeiterEntity], betriebId: PK[BetriebEntity]): Future[Int] =
    authorizedAction(() => db.run(dal.deleteLeiter(leiterId, betriebId)), betriebId)

  def leiterAnzeigen(page: Int, size: Int): Future[Seq[(LeiterEntity, AnwenderEntity)]] =
    betrieb flatMap {
      case betrieb => db.run(dal.listLeiterOf(betrieb.id.get, page, size))
    } recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  def dienstleistungAnbieten(
    name: String,
    dauer: Int,
    kommentar: String
  ): Future[DienstleistungEntity] =
    (for {
      betrieb <- betrieb
      dlt <- db.run(dal.findOrInsert(DienstleistungsTypEntity(name = name)))
      dl <- db.run(dal.insert(
        DienstleistungEntity(
          kommentar,
          dauer,
          betrieb.id.get,
          dlt.id.get
        )
      ))
    } yield dl)

  def dienstleistungsInformationVeraendern(diensleistungPK: PK[DienstleistungEntity], name: String, dauer: Int, kommentar: String): Future[Int] =
    (for {
      betrieb <- betrieb
      dlt <- db.run(dal.findOrInsert(DienstleistungsTypEntity(name = name)))
      affectedRows <- db.run(dal.update(
        DienstleistungEntity(
          kommentar,
          dauer,
          betrieb.id.get,
          dlt.id.get,
          Option(diensleistungPK)
        )
      ))
    } yield affectedRows) recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  def dienstleistungEntfernen(dienstleistungPK: PK[DienstleistungEntity]): Future[Boolean] =
    (for {
      betrieb <- betrieb
      affectedRows <- db.run(dal.deleteDienstleistung(dienstleistungPK, betrieb.id.get))
    } yield affectedRows == 1) recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }
}
