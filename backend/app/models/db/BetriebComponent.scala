package models.db

trait BetriebComponent {
  this: DriverComponent with AdresseComponent =>
  import driver.api._

  class BetriebTable(tag: Tag) extends Table[BetriebEntity](tag, "ANBIETER") {

    def id = column[PK[BetriebEntity]]("ID", O.PrimaryKey, O.AutoInc)
    def adresseId = column[PK[AdresseEntity]]("ADRESSE_ID")
    def tel = column[String]("TEL")
    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")
    def kontaktEmail = column[String]("KONTAKTEMAIL")
    def wsOffen = column[Boolean]("WSOFFEN")
    def adresse = foreignKey("ADR_FK", adresseId, adresses)(_.id)


    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (tel, oeffnungszeiten, kontaktEmail, wsOffen, adresseId, id.?) <> (BetriebEntity.tupled, BetriebEntity.unapply)
  }

  val betriebe = TableQuery[BetriebTable]

  val betriebeAutoInc = betriebe returning betriebe.map(_.id)

  def getBetriebById(id: PK[BetriebEntity]): DBIO[BetriebEntity] = betriebe.filter(_.id === id).result.head
}