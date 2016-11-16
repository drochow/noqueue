package models.db

import scala.concurrent._
import models.db.connections.{ DBComponent, PostgresDBComponent }

class AdresseTable(tag: Tag) extends Table[Adresse](tag, "ADRESSE") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def strasse = column[String]("STRASSE")
  def hausnummer = column[String]("HAUSNUMMER")
  def plz = column[String]("PLZ")
  def stadt = column[String]("STADT")

  def * = (strasse, hausnummer, plz, stadt, id.?) <> (Adresse.tupled, Adresse.unapply)
}

trait AdresseRepository extends BaseRepository[AnwenderTable, Anwender](TableQuery[AnwenderTable)) { this: DbComponent =>

  def findByEmail(email: String): Future[Anwender] = { Future.successful(Anwender(email, "something", "Max Mustermann", 1, 12L)) }
  //def setup(): Future[Any] = { db.run(DBIO.seq(query.schema.create)) }

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
