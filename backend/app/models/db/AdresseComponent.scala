package models.db

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

case class Adresse(strasse: String, hausNummer: String, plz: String, stadt: String, id: Option[PK[Adresse]] = None)

/** AdresseComponent provides database definitions for Adresse objects */
trait AdresseComponent {
  this: DriverComponent =>
  import driver.api._

  class AdresseTable(tag: Tag) extends Table[Adresse](tag, "ADRESSE") {

    val id = column[PK[Adresse]]("EmployeeId", O.AutoInc, O.PrimaryKey)
    def strasse = column[String]("STRASSE")
    def hausNummer = column[String]("HAUSNUMMER")
    def plz = column[String]("PLZ")
    def stadt = column[String]("STADT")
    def * = (strasse, hausNummer, plz, stadt, id.?) <> (Adresse.tupled, Adresse.unapply)
  }

  val adresses = TableQuery[AdresseTable]

  private val adressesAutoInc = adresses returning adresses.map(_.id.?)

  def insert(adresse: Adresse): DBIO[Adresse] = (for {
    //only create adresse if adresse with same straße/hausnummer/zip/stadt does not already exists
    adresseFound: Option[Adresse] <- adresses
      .filter(_.strasse === adresse.strasse)
      .filter(_.hausNummer === adresse.hausNummer)
      .filter(_.plz === adresse.plz)
      .filter(_.stadt === adresse.stadt)
      .result.headOption
    adresse <- if (adresseFound.isEmpty) (adressesAutoInc += adresse).map(id => adresse.copy(id = id))
    else DBIO.successful(adresseFound.get)
  } yield (adresse))

  def getAdresseById(id: PK[Adresse]): DBIO[Adresse] = adresses.filter(_.id === id).result.head
}