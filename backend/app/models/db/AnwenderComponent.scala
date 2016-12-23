package models.db

import slick.jdbc.SetParameter

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
    def nameUnique = index("nameUnique", nutzerName, unique = true)
    def emailUnique = index("emailUnique", nutzerEmail, unique = true)

    /**
     * Default Projection Mapping to case Class
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

  def getAnwenderByName(name: String): DBIO[AnwenderEntity] = anwenders.filter(_.nutzerName === name).result.head

  private def getIfSome[A](option: Option[A]) = if (option.isEmpty) "" else option.get

  /**
   * this full update currently only updates the nutzerName and the nutzerEmail of one Anwender
   * @param id
   * @param anwenderEntity
   * @return
   */
  def update(id: PK[AnwenderEntity], anwenderEntity: AnwenderEntity): DBIO[Int] =
    anwenders.filter(_.id === id).map(anw => (anw.nutzerName, anw.nutzerEmail)).update((anwenderEntity.nutzerName, anwenderEntity.nutzerEmail))

  //this helps sqlu undestand what it maps the PK[_] to
  private implicit val setPK = SetParameter[PK[_]](
    (pk, pp) => pp.setLong(pk.value)
  )
  def partialUpdate(
    id: PK[AnwenderEntity],
    nutzerNameOpt: Option[String],
    nutzerEmailOpt: Option[String],
    adresseOpt: Option[Option[PK[AdresseEntity]]]
  ): DBIO[Int] = {
    //@todo DRY, Reusable Code for other partialUpdates, Make more Readable
    sqlu"UPDATE ANWENDER SET NUTZERNAME = (CASE WHEN ${!nutzerNameOpt.isEmpty} THEN ${nutzerNameOpt.getOrElse("Whoops")} ELSE NUTZERNAME END), NUTZEREMAIL = (CASE WHEN ${!nutzerEmailOpt.isEmpty} THEN ${nutzerEmailOpt.getOrElse("Whoops")} ELSE NUTZEREMAIL END), ADRESSE_ID = (CASE WHEN ${!adresseOpt.isEmpty} THEN ${adresseOpt.getOrElse(Some(PK[AdresseEntity](9001))).get} ELSE ADRESSE_ID END) WHERE ID = $id"
  }

  def getAnwenderWithAdress(id: PK[AnwenderEntity]): DBIO[(AnwenderEntity, Option[AdresseEntity])] =
    (anwenders joinLeft adresses on (_.adresseId === _.id)).filter { case (anwender, adresse) => anwender.id === id }.result.head.nonFusedEquivalentAction
}

