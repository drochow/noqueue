package models

import java.util.NoSuchElementException

import models.db._
import slick.dbio.DBIO
import utils.UnauthorizedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Buissness model class for all operations related to the role @Leiter
 *
 * @param leiterAction DBIO action that returnes the Entities related to this Leiter
 * @param dbD          Database drivers used to perform queries
 */
class Leiter(val leiterAction: DBIO[(BetriebEntity, AnwenderEntity, LeiterEntity)], dbD: DB) extends Base(dbD) {

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
   * we need to ensure the authorization of all actions by calling the leiterAction and pass necessarry informations
   * to the provided action
   *
   * @param action action to perform
   * @tparam T return type of  action
   * @return the result of the given action
   */
  private def authorizedAction[T](action: (BetriebEntity) => Future[T]): Future[T] =
    leiterComposition flatMap {
      case (betrieb, anwender, leiter) => action(betrieb)
    } recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  /**
   * Updates data for @BetriebEntity aligned to this @Leiter
   *
   * @param betrieb Entity of betrieb with the (may) changed data set
   * @param adresse Entity of the related (may) changed Adresse
   * @return
   */
  def betriebsInformationenVeraendern(betrieb: BetriebEntity, adresse: AdresseEntity) =
    authorizedAction((b) => db.run(dal.update(b.id.get, betrieb, adresse)))

  /**
   * Hires new @MitarbeiterEntity for the @BetriebEntity aligned with this @Leiter
   *
   * @param mitarbeiter @MitarbeiterEntity that should get employeed
   * @return The freshly created @MitarbeiterEntity
   */
  def mitarbeiterAnstellen(mitarbeiter: MitarbeiterEntity): Future[MitarbeiterEntity] =
    authorizedAction((b) => db.run(dal.insert(mitarbeiter.copy(betriebId = b.id.get))))

  /**
   * Fires  @MitarbeiterEntity with given Primary Key
   *
   * @param mitarbeiterPK Primary key of the @MitarbeiterEntity wich should get fired
   * @return boolean value representing success or failure of the action
   */
  def mitarbeiterEntlassen(mitarbeiterPK: PK[MitarbeiterEntity]): Future[Boolean] =
    authorizedAction((b: BetriebEntity) => db.run(dal.deleteMitarbeiter(mitarbeiterPK, b.id.get)).map(_ == 1))

  /**
   * Hires new @LeiterEntity for the @BetriebEntity aligned with this @Leiter
   *
   * @param leiterEntity @LeiterEntity that should get hired
   * @return The freshly hired @LeiterEntity
   */
  def leiterEinstellen(leiterEntity: LeiterEntity): Future[LeiterEntity] =
    authorizedAction((b) => db.run(dal.insert(leiterEntity.copy(betriebId = b.id.get))))

  /**
   * Fires  @MitarbeiterEntity with given Primary Key
   *
   * @todo should return true/false representing failure or success
   * @param leiterId the Primary Key of the @LeiterEntity that should ge fired
   * @return Returns affected Rows
   */
  def leiterEntlassen(leiterId: PK[LeiterEntity]): Future[Int] =
    authorizedAction((b) => db.run(dal.deleteLeiter(leiterId, b.id.get)))

  /**
   * Shows all @LeiterEntity and related @AnwenderEntity of the @BetriebEntity related to this @Leiter
   *
   * This operation supports Pagination
   *
   * @param page starting page
   * @param size size per page
   * @return Sequence of Tuples of @AnwenderEntity and @BetriebEntity
   */
  def leiterAnzeigen(page: Int, size: Int): Future[Seq[(LeiterEntity, AnwenderEntity)]] =
    authorizedAction((b) => db.run(dal.listLeiterOf(b.id.get, page, size)))

  /**
   * Creates a new @DienstleistungEntity with the provided data
   *
   * If the parameter name does not match any existing @DienstleistungsTypEntity a new one will get created.
   *
   * @param name      Name of the @DienstleistungsTypEntity that is related to this @DienstleistungEntity
   * @param dauer     Duration of Service provided in Seconds
   * @param kommentar Komment descriping the Service
   * @return The newly created @DienstleistungEntity
   */
  def dienstleistungAnbieten(
    name: String,
    dauer: Int,
    kommentar: String
  ): Future[DienstleistungEntity] =
    authorizedAction((b) =>
      for {
        dlt <- db.run(dal.findOrInsert(DienstleistungsTypEntity(name = name)))
        dl <- db.run(dal.insert(
          DienstleistungEntity(
            kommentar,
            dauer,
            b.id.get,
            dlt.id.get
          )
        ))
      } yield dl)

  /**
   * Updates @DienstleistungEntity matching the provided Primary Key with provided data
   *
   * If the parameter name does not match any existing @DienstleistungsTypEntity a new one will get created.
   * @todo shoud return true/false or the updated entity (needs further discussion with team)
   *
   * @param diensleistungPK Primary Key of the @DienstleistungEntity that should get Updated
   * @param name new name of the service
   * @param dauer new duration of the service in seconds
   * @param kommentar new comment descriping the service
   * @return by update affected rows
   */
  def dienstleistungsInformationVeraendern(diensleistungPK: PK[DienstleistungEntity], name: String, dauer: Int, kommentar: String): Future[Int] =
    authorizedAction((b) =>
      for {
        dlt <- db.run(dal.findOrInsert(DienstleistungsTypEntity(name = name)))
        affectedRows <- db.run(dal.update(
          DienstleistungEntity(
            kommentar,
            dauer,
            b.id.get,
            dlt.id.get,
            Option(diensleistungPK)
          )
        ))
      } yield affectedRows)

  /**
   * Removes @DienstleistungEntity matching the provided Primary Key
   *
   * @param dienstleistungPK Primary Key of the service to remove
   * @return boolean value representing success or failure of the action
   */
  def dienstleistungEntfernen(dienstleistungPK: PK[DienstleistungEntity]): Future[Boolean] =
    authorizedAction((b) => db.run(dal.deleteDienstleistung(dienstleistungPK, b.id.get)).map(_ == 1))
}
