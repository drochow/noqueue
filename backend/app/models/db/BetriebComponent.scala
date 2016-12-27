package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait BetriebComponent {
  this: DriverComponent with AdresseComponent with LeiterComponent with MitarbeiterComponent =>
  import driver.api._

  class BetriebTable(tag: Tag) extends Table[BetriebEntity](tag, "ANBIETER") {

    def id = column[PK[BetriebEntity]]("ID", O.PrimaryKey, O.AutoInc)
    def adresseId = column[PK[AdresseEntity]]("ADRESSE_ID")
    def name = column[String]("NAME")
    def tel = column[String]("TEL")
    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")
    def kontaktEmail = column[String]("KONTAKTEMAIL")
    def adresse = foreignKey("ADR_FK", adresseId, adresses)(_.id)

    def betriebsNameUnique = index("betriebsNameUnique", name, unique = true)
    def betriebsTelUnique = index("betriebsTelUnique", tel, unique = true)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (name, tel, oeffnungszeiten, kontaktEmail, adresseId, id.?) <> (BetriebEntity.tupled, BetriebEntity.unapply)
  }

  val betriebe = TableQuery[BetriebTable]

  val betriebeAutoInc = betriebe returning betriebe.map(_.id)

  /**
   * Creates within a transaction a new BetriebEntity with the corresponding AdresseEntity and LeiterEntity
   *
   * @param betrieb   The BetriebEntity to create ( The adresseId can be set to any value since it will be taken from the parameter adresse)
   * @param adresse   The corresponding AdresseEntity
   * @param anwender  The anwender wich takes the "Leiter" position for the Betrieb
   * @return
   */
  def insert(betrieb: BetriebEntity, adresse: AdresseEntity, anwender: DBIO[AnwenderEntity]): DBIO[(BetriebEntity, AdresseEntity)] =
    (for {
      adr: AdresseEntity <- findOrInsert(adresse)
      btr: BetriebEntity <- (betriebeAutoInc += betrieb.copy(adresseId = adr.id.get))
        .map(id => betrieb.copy(id = Option(id), adresseId = adr.id.get))
      anw: AnwenderEntity <- anwender
      leiter: LeiterEntity <- insert(LeiterEntity(anwenderId = anw.id.get, betriebId = btr.id.get))
    } yield ((btr, adr))).transactionally

  def getBetriebById(id: PK[BetriebEntity]): DBIO[BetriebEntity] = betriebe.filter(_.id === id).result.head

  def getBetriebWithAdresseById(id: PK[BetriebEntity]): DBIO[(BetriebEntity, AdresseEntity)] =
    (betriebe join adresses on (_.adresseId === _.id)).filter {
      case (betrieb, adresse) => betrieb.id === id
    }.result.head

  def addMitarbeiter(betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity]): DBIO[MitarbeiterEntity] =
    insert(MitarbeiterEntity(anwesend = false, betriebId = betriebId, anwenderId = anwenderId))

}