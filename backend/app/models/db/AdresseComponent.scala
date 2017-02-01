package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait AdresseComponent {
  this: DriverComponent =>

  import driver.api._

  /**
   * Database schema definition of an @AdresseEntity
   *
   * This class is includes all details about the scheme definition for the choosen database.
   *
   * @param tag name of the table within the database
   */
  class AdresseTable(tag: Tag) extends Table[AdresseEntity](tag, "ADRESSE") {

    def id = column[PK[AdresseEntity]]("ID", O.AutoInc, O.PrimaryKey)

    def strasse = column[String]("STRASSE")

    def hausNummer = column[String]("HAUSNUMMER")

    def plz = column[String]("PLZ")

    def stadt = column[String]("STADT")

    def latitude = column[Double]("LATITUDE")

    def longitude = column[Double]("LONGITUDE")

    def adresseUnique = index("adresseUnique", (strasse, hausNummer, stadt, plz), unique = true)

    //@todo add unique index

    /**
     * projection mapping to case class
     */
    def * = (strasse, hausNummer, plz, stadt, latitude.?, longitude.?, id.?) <> (AdresseEntity.tupled, AdresseEntity.unapply)
  }

  /**
   * TableQuery representation
   */
  val adresses = TableQuery[AdresseTable]

  /**
   * primary key auto increment
   */
  private val adressesAutoInc = adresses returning adresses.map(_.id.?)

  /**
   * Creates Database action that searchs for a @AdresseEntity matching all fields except the primary key
   * or creates a new @AdresseEntity*
   * This method only creates a @AdresseEntity if adresse with same straße,hausnummerm, plz AND stadt does not exists
   *
   * @param adresse @AdresseEntity that should get matched or created
   * @return the created or matching @AdresseEntity
   */
  def findOrInsert(adresse: AdresseEntity): DBIO[AdresseEntity] = (for {
    //
    adresseFound: Option[AdresseEntity] <- adresses
      .filter(_.strasse === adresse.strasse)
      .filter(_.hausNummer === adresse.hausNummer)
      .filter(_.plz === adresse.plz)
      .filter(_.stadt === adresse.stadt)
      .result.headOption
    adresse <- if (adresseFound.isEmpty) {
      (adressesAutoInc += adresse).map(id => adresse.copy(id = id))
    } else DBIO.successful(adresseFound.get)
  } yield (adresse))

  /**
   * Creates a database action to fetch a @AdresseEntity by primary key
   *
   * @param id primary key of the requested @AdresseEntity
   * @return optional @AdresseEntity
   */
  def getAdresseById(id: PK[AdresseEntity]): DBIO[Option[AdresseEntity]] = adresses.filter(_.id === id).result.headOption
}
