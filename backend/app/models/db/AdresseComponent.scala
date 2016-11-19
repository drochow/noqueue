package models.db

import scala.concurrent.ExecutionContext.Implicits.global

case class Adresse(straße: String, hausNummer: String, plz: String, stadt: String, id: Option[PK[Adresse]] = None)

/** AdresseComponent provides database definitions for Adresse objects */
trait AdresseComponent {
  this: DriverComponent =>
  import driver.api._

  class AdresseTable(tag: Tag) extends Table[Adresse](tag, "ADRESSE") {

    val id = column[PK[Adresse]]("EmployeeId", O.AutoInc, O.PrimaryKey)
    def strasse = column[String]("STRASSE")
    def hausnummer = column[String]("HAUSNUMMER")
    def plz = column[String]("PLZ")
    def stadt = column[String]("STADT")
    def * = (strasse, hausnummer, plz, stadt, id.?) <> (Adresse.tupled, Adresse.unapply)
  }

  val adresses = TableQuery[AdresseTable]

  private val adressesAutoInc = adresses returning adresses.map(_.id.?)

  def insert(adresse: Adresse): DBIO[Adresse] = (adressesAutoInc += adresse).map(id => adresse.copy(id = id))

  def getAdresseById(id: PK[Adresse]): DBIO[Adresse] = adresses.filter(_.id === id).result.head
}