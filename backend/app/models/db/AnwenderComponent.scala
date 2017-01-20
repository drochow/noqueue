package models.db

import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.SetParameter

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * AnwenderEntity Component Trait including Driver and AdresseEntity Component traits via cake pattern injection
 */
trait AnwenderComponent {
  this: DriverComponent with AdresseComponent with BetriebComponent with MitarbeiterComponent with LeiterComponent =>

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

  def listAnwender(page: Int, size: Int): DBIO[Seq[AnwenderEntity]] = anwenders.sortBy(_.nutzerName).drop(page * size).take(size).result

  def searchAnwender(query: String, page: Int, size: Int): DBIO[Seq[AnwenderEntity]] = {
    val tokenizedQuery = Option("%" + query + "%")
    anwenders.filter {
      (a: AnwenderTable) =>
        List(
          tokenizedQuery.map(a.nutzerName.like(_)),
          tokenizedQuery.map(a.nutzerEmail.like(_))
        ).collect({ case Some(c) => c }).reduceLeftOption(_ || _).getOrElse(true: Rep[Boolean])
    }.sortBy(_.nutzerName).drop(page * size).take(size).result

  }

  /**
   * full update of an AnwenderEntity (currently only updates the nutzerName and the nutzerEmail)
   * @param id
   * @param anwenderEntity
   * @return
   */
  def update(id: PK[AnwenderEntity], anwenderEntity: AnwenderEntity): DBIO[Int] =
    anwenders.filter(_.id === id)
      .map(anw => (anw.nutzerName, anw.nutzerEmail, anw.adresseId)).update((anwenderEntity.nutzerName, anwenderEntity.nutzerEmail, anwenderEntity.adresseId))

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
    sqlu"""UPDATE ANWENDER
            SET NUTZERNAME = (CASE WHEN ${!nutzerNameOpt.isEmpty}
              THEN ${nutzerNameOpt.getOrElse("Whoops")} ELSE NUTZERNAME END),
                NUTZEREMAIL = (CASE WHEN ${!nutzerEmailOpt.isEmpty}
                 THEN ${nutzerEmailOpt.getOrElse("Whoops")} ELSE NUTZEREMAIL END),
                ADRESSE_ID = (CASE WHEN ${!adresseOpt.isEmpty}
                  THEN ${adresseOpt.getOrElse(Some(PK[AdresseEntity](9001))).get} ELSE ADRESSE_ID END)
            WHERE ID = $id"""
  }

  def passwordVeraendern(id: PK[AnwenderEntity], newPassword: String): DBIO[Int] =
    //@todo try to do this with only one Query please
    //sqlu"""UPDATE ANWENDER SET "PASSWORD" = (CASE WHEN TRUE THEN ${BCrypt.hashpw(newPassword, BCrypt.gensalt())} ELSE PASSWORD END)  WHERE ID = $id"""
    //anwenders.filter(anw => anw.id === id && LiteralColumn(BCrypt.checkpw(oldPassword, anw.password))).map(_.password).update(BCrypt.hashpw(newPassword, BCrypt.gensalt()))
    anwenders.filter(anw => anw.id === id).map(_.password).update(BCrypt.hashpw(newPassword, BCrypt.gensalt()))

  def getAnwenderWithAdress(id: PK[AnwenderEntity]): DBIO[(AnwenderEntity, Option[AdresseEntity])] =
    (anwenders joinLeft adresses on (_.adresseId === _.id)).filter { case (anwender, adresse) => anwender.id === id }.result.head.nonFusedEquivalentAction

  /**
   * Creates DBIOAction wich joins BetriebTable(by betriebID) with AnwenderTable(by anwenderID) and make a leftjoin to optionally
   * find mitarbeiter and and Leiter entities
   *
   * @param betriebId
   * @param anwenderId
   * @return
   *
   */
  def getFullRelationOf(betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity]): DBIO[(BetriebEntity, AnwenderEntity, Option[LeiterEntity], Option[MitarbeiterEntity])] = {
    (for {
      (((betrieb, anwender), leiter), mitarbeiter) <- (betriebe join anwenders joinLeft leiters on {
        case ((betrieb: BetriebTable, anwender: AnwenderTable), leiter: LeiterTable) =>
          betrieb.id === leiter.betriebId && anwender.id === leiter.anwenderId
      } joinLeft mitarbeiters on {
        case (((betrieb: BetriebTable, anwender: AnwenderTable), leiter: Rep[Option[LeiterTable]]), mitarbeiter: MitarbeiterTable) => betrieb.id === mitarbeiter.betriebId && anwender.id === mitarbeiter.anwenderId
      })
        .filter {
          case (((betrieb, anwender), leiter), mitarbeiter) => anwender.id === anwenderId
        }
        .filter {
          case (((betrieb, anwender), leiter), mitarbeiter) => betrieb.id === betriebId
        }
    } yield (betrieb, anwender, leiter, mitarbeiter)).result.head.nonFusedEquivalentAction
  }
}

