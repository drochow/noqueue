package models.db

import scala.concurrent._
import models.db.connections.{ DBComponent, PostgresDBComponent }

trait AdresseRepository extends AdresseTable { this: DBComponent =>

  import driver.api._
  /*
  /**
   * Create a new Adresse
   *
   * @param adresse
   */
  def findOrCreate(adresse: Adresse): Future[Long] = {
    //db.run(adresseTableQuery += adresse)
  }
*/
  /**
   * Update a adresse
   * @param adresse
   */
  def update(adresse: Adresse): Future[Unit] = { db.run(DBIO.seq(adresseTableQuery.filter(_.id === adresse.id.get).update(adresse))) }

  /**
   * Get adresse by id
   *
   * @param id
   */
  def getById(id: Long): Future[Option[Adresse]] = db.run { adresseTableQuery.filter(_.id === id).result.headOption }

  /**
   * get all adressen
   *
   * @return
   */
  def getAll(): Future[List[Adresse]] = db.run { adresseTableQuery.to[List].result }

  /**
   * delete adresse by id
   *
   * @param id
   */
  def delete(id: Long): Future[Unit] = { db.run(DBIO.seq(adresseTableQuery.filter(_.id === id).delete)) }

  def setup(): Future[Any] = db.run { adresseTableQuery.schema.create }
  //def setup(): Future[Unit] = db.run { adresseTableQuery.schema.create }
}

private[db] trait AdresseTable { this: DBComponent =>

  import driver.api._

  private[AdresseTable] class AdresseTable(tag: Tag) extends Table[Adresse](tag, "ADRESSE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def strasse = column[String]("STRASSE")
    def hausnummer = column[String]("HAUSNUMMER")
    def plz = column[String]("PLZ")
    def stadt = column[String]("STADT")

    def * = (strasse, hausnummer, plz, stadt, id.?) <> (Adresse.tupled, Adresse.unapply)
  }

  protected val adresseTableQuery = TableQuery[AdresseTable]

  protected def adresseAutoInc = adresseTableQuery returning adresseTableQuery.map(_.id)

}

/**
 * Singleton instace with Postgres Drivers
 */
object AdresseRepository extends AdresseRepository with PostgresDBComponent

case class Adresse(
  straße: String,
  hausNummer: String,
  plz: String,
  stadt: String,
  id: Option[Long] = None
) {}
