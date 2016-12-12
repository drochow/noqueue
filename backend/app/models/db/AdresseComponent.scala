package models.db

import scala.concurrent.ExecutionContext.Implicits.global

/** AdresseComponent provides database definitions for AdresseEntity objects */
trait AdresseComponent {
  this: DriverComponent =>

  import driver.api._

  class AdresseTable(tag: Tag) extends Table[AdresseEntity](tag, "ADRESSE") {

    def id = column[PK[AdresseEntity]]("EmployeeId", O.AutoInc, O.PrimaryKey)
    def strasse = column[String]("STRASSE")
    def hausNummer = column[String]("HAUSNUMMER")
    def plz = column[String]("PLZ")
    def stadt = column[String]("STADT")


    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (strasse, hausNummer, plz, stadt, id.?) <> (AdresseEntity.tupled, AdresseEntity.unapply)
  }

  val adresses = TableQuery[AdresseTable]

  private val adressesAutoInc = adresses returning adresses.map(_.id.?)

  def insert(adresse: AdresseEntity): DBIO[AdresseEntity] = (for {
    //only create adresse if adresse with same straße/hausnummer/zip/stadt does not already exists
    adresseFound: Option[AdresseEntity] <- adresses
      .filter(_.strasse === adresse.strasse)
      .filter(_.hausNummer === adresse.hausNummer)
      .filter(_.plz === adresse.plz)
      .filter(_.stadt === adresse.stadt)
      .result.headOption
    adresse <- if (adresseFound.isEmpty) (adressesAutoInc += adresse).map(id => adresse.copy(id = id))
    else DBIO.successful(adresseFound.get)
  } yield (adresse))

  def getAdresseById(id: PK[AdresseEntity]): DBIO[AdresseEntity] = adresses.filter(_.id === id).result.head
}
