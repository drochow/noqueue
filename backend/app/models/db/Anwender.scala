package models.db

import scala.concurrent._
import models.db.connections.{ DBComponent, PostgresDBComponent }

trait AnwenderRepository extends AnwenderTable { this: DBComponent =>
  import driver.api._
  import ExecutionContext.Implicits.global

  /**
   * Create a new anwender
   *
   * @param anwender
   */
  def create(anwender: Anwender): Future[Long] = db.run { anwenderAutoInc += anwender }

  def createWithAdresse(anwender: Anwender, adresse: Adresse): Future[Long] = {

    val dbAction = (
      for {
        adrId <- { adresseTableQuery returning adresseTableQuery.map(_.id) += adresse }
        anwenderId <- { anwenderTableQuery returning anwenderTableQuery.map(_.id) += Anwender(anwender.nutzerEmail, anwender.password, anwender.nutzerName, Option(adrId)) }
      } yield anwenderId
    ).transactionally

    db.run(dbAction);
  }

  /**
   * Update a anwender
   * @param anwender
   */
  def update(anwender: Anwender): Future[Unit] = {
    db.run(DBIO.seq(anwenderTableQuery.filter(_.id === anwender.id.get).update(anwender)))
  }

  /**
   * Get anwender by id
   *
   * @param id
   */
  def getById(id: Long): Future[Option[Anwender]] = db.run { anwenderTableQuery.filter(_.id === id).result.headOption }

  /**
   * get all anwender
   *
   * @return
   */
  def getAll(): Future[List[Anwender]] = db.run { anwenderTableQuery.to[List].result }

  /**
   * delete anwender by id
   *
   * @param id
   */
  def delete(id: Long): Future[Unit] = { db.run(DBIO.seq(anwenderTableQuery.filter(_.id === id).delete)) }

  def findByEmail(email: String):Future[Anwender] = { Future.successful(Anwender(email, "something", "Max Mustermann", 1, 12L))}

  def setup(): Future[Any] = { db.run(DBIO.seq(anwenderTableQuery.schema.create)) }
}

private[db] trait AnwenderTable extends AdresseTable { this: DBComponent =>

  import driver.api._

  private[AnwenderTable] class AnwenderTable(tag: Tag) extends Table[Anwender](tag, "ANWENDER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def adresseID = column[Long]("ADRESSE_ID")
    def nutzerEmail = column[String]("NUTZEREMAIL")
    def password = column[String]("PASSWORD")
    def nutzerName = column[String]("NUTZERNAME")

    def * = (nutzerEmail, password, nutzerName, adresseID.?, id.?) <> (Anwender.tupled, Anwender.unapply)

    def adresse = foreignKey("ADRESSE_FK", adresseID, adresseTableQuery)(_.id)

  }

  protected val anwenderTableQuery = TableQuery[AnwenderTable]

  protected def anwenderAutoInc = anwenderTableQuery returning anwenderTableQuery.map(_.id)
}

object AnwenderRepository extends AnwenderRepository with PostgresDBComponent

case class Anwender(
    nutzerEmail: String,
    password: String,
    nutzerName: String,
    adresseId: Option[Long] = None,
    id: Option[Long] = None
) {
}
