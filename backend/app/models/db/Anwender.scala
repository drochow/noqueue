package models.db

import scala.concurrent.Future
import models.db.connections.{ DBComponent, PostgresDBComponent }

trait AnwenderRepository extends AnwenderTable { this: DBComponent =>
  import driver.api._

  /**
   * Create a new anwender
   *
   * @param anwender
   */
  def create(anwender: Anwender): Future[Long] = db.run { anwenderAutoInc += anwender }

  def createWithAdresse(anwender: Anwender, adresse: Adresse): Future[Long] = {

    val dbAction = (
      for {
        adrId <- AdresseRepository.create(adresse)
        anwenderId <- anwenderAutoInc += Anwender(anwender.nutzerEmail, anwender.password, anwender.nutzerName, adrId.result)
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
