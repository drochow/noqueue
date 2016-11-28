package models.db

import models.{ Adresse, Anwender }

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Anwender Component Trait including Driver and Adresse Component traits via cake pattern injection
 */
trait AnwenderComponent {
  this: DriverComponent with AdresseComponent =>

  import driver.api._

  /**
   * Anwender Table Schema definition
   *
   * @param tag
   */
  class AnwenderTable(tag: Tag) extends Table[Anwender](tag, "ANWENDER") {

    def id = column[PK[Anwender]]("ID", O.PrimaryKey, O.AutoInc)

    def nutzerEmail = column[String]("NUTZEREMAIL")

    def password = column[String]("PASSWORD")

    def nutzerName = column[String]("NUTZERNAME")

    def adresseId = column[Option[PK[Adresse]]]("ADRESSE_ID")

    def adresse = foreignKey("fk_adresse", adresseId, adresses)(_.id.?)

    /**
     * Default Projection Mapping to case Class
     *
     * @return
     */
    def * = (nutzerEmail, password, nutzerName, adresseId, id.?) <> (Anwender.tupled, Anwender.unapply)
  }

  val anwenders = TableQuery[AnwenderTable]

  private val anwenderAutoInc = anwenders returning anwenders.map(_.id)

  def insert(anwender: Anwender): DBIO[Anwender] =
    if (anwender.adresseId.isEmpty) (anwenderAutoInc += anwender).map(id => anwender.copy(id = Option(id)))
    else (getAdresseById(anwender.adresseId.get) andThen (anwenderAutoInc += anwender).map(id => anwender.copy(id = Option(id))))

  def getAnwenderById(id: PK[Anwender]): DBIO[Anwender] = anwenders.filter(_.id === id).result.head

  def getAnwenderWithAdress(id: PK[Anwender]): DBIO[(Anwender, Option[Adresse])] =
    (anwenders joinLeft adresses on (_.adresseId === _.id)).filter { case (anwender, adresse) => anwender.id === id }.result.head.nonFusedEquivalentAction
}

