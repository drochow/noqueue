package models.db

import models.{ Adresse, Betrieb }

trait BetriebComponent {
  this: DriverComponent with AdresseComponent =>
  import driver.api._

  class BetriebTable(tag: Tag) extends Table[Betrieb](tag, "ANBIETER") {
    def id = column[PK[Betrieb]]("ID", O.PrimaryKey, O.AutoInc)
    def adresseId = column[PK[Adresse]]("ADRESSE_ID")
    def tel = column[String]("TEL")
    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")
    def kontaktEmail = column[String]("KONTAKTEMAIL")
    def wsOffen = column[Boolean]("WSOFFEN")

    def * = (tel, oeffnungszeiten, kontaktEmail, wsOffen, adresseId, id.?) <> (Betrieb.tupled, Betrieb.unapply)

    def adresse = foreignKey("ADR_FK", adresseId, adresses)(_.id)
  }

  val betriebe = TableQuery[BetriebTable]

  val betriebeAutoInc = betriebe returning betriebe.map(_.id)
}