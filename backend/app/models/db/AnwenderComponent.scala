package models.db

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * AnwenderEntity Component Trait including Driver and AdresseEntity Component traits via cake pattern injection
 */
trait AnwenderComponent {
  this: DriverComponent with AdresseComponent =>

  import driver.api._

  /**
   * AnwenderEntity Table Schema definition
   *
   * @param tag
   */
  class AnwenderTable(tag: Tag) extends Table[AnwenderEntity](tag, "ANWENDER") {

    def id = column[PK[AnwenderEntity]]("ID", O.PrimaryKey, O.AutoInc)

    def nutzerEmail = column[String]("NUTZEREMAIL")

    def password = column[String]("PASSWORD")

    def nutzerName = column[String]("NUTZERNAME")

    def adresseId = column[Option[PK[AdresseEntity]]]("ADRESSE_ID")

    def adresse = foreignKey("fk_adresse", adresseId, adresses)(_.id.?)

    /**
     * Default Projection Mapping to case Class
     *
     * @return
     */
    def * = (nutzerEmail, password, nutzerName, adresseId, id.?) <> (AnwenderEntity.tupled, AnwenderEntity.unapply)
  }

  val anwenders = TableQuery[AnwenderTable]

  private val anwenderAutoInc = anwenders returning anwenders.map(_.id)

  def insert(anwender: AnwenderEntity): DBIO[AnwenderEntity] =
    if (anwender.adresseId.isEmpty) (anwenderAutoInc += anwender).map(id => anwender.copy(id = Option(id)))
    else (getAdresseById(anwender.adresseId.get) andThen (anwenderAutoInc += anwender).map(id => anwender.copy(id = Option(id))))

  def getAnwenderById(id: PK[AnwenderEntity]): DBIO[AnwenderEntity] = anwenders.filter(_.id === id).result.head

  def getAnwenderWithAdress(id: PK[AnwenderEntity]): DBIO[(AnwenderEntity, Option[AdresseEntity])] =
    (anwenders joinLeft adresses on (_.adresseId === _.id)).filter { case (anwender, adresse) => anwender.id === id }.result.head.nonFusedEquivalentAction
}

