//package models.db
//
//import scala.concurrent.Future
//import models.db.connections.{DBComponent, PostgresDBComponent}
//import slick.lifted.{ForeignKeyQuery}
//
//trait AnbieterRepository extends AnbieterTable { this: DBComponent =>
//  import driver.api._
//
//  /**
//    * Create a new Anbieter
//    *
//    * @param anbieter
//    */
//  def create(anbieter: Anbieter): Future[Long] = db.run { anbieterTableAutoInc += anbieter }
//
//  /**
//    * Update a anbieter
//    *
//    * @param anbieter
//    */
//  def update(anbieter: Anbieter): Future[Long] = db.run { anbieterTableQuery.filter(_.id === anbieter.id.get).update(anbieter) }
//
//  /**
//    * Get anbieter by id
//    *
//    * @param id
//    */
//  def getById(id: Int): Future[Option[Anbieter]] = db.run { anbieterTableQuery.filter(_.id === id).result.headOption }
//
//
//  /**
//    * get all anbiters
//    *
//    * @return
//    */
//  def getAll(): Future[List[Anbieter]] = db.run { anbieterTableQuery.to[List].result }
//
//  /**
//    * delete anbieter by id
//    *
//    * @param id
//    */
//  def delete(id: Long): Future[Long] = db.run { anbieterTableQuery.filter(_.id === id).delete }
//
//}
//
//private[db] trait AnbieterTable { this: DBComponent =>
//
//  import driver.api._
//
//  private[AnbieterTable] class AnbieterTable(tag: Tag) extends Table[Anbieter](tag, "ANBIETER") {
//    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
//    def adresseID = column[Long]("ADRESSE_ID")
//    def tel = column[String]("TEL")
//    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")
//    def kontaktEmail = column[String]("KONTAKTEMAIL")
//    def wsOffen = column[Boolean]("WSOFFEN")
//    def bewertung = column[Int]("BEWERTUNG")
//
//    def * = (id.?, adresseID, tel, oeffnungszeiten, kontaktEmail, wsOffen, bewertung) <> (Anbieter.tupled, Anbieter.unapply)
//
//    def adresse: ForeignKeyQuery[AdresseTable, Adresse] = foreignKey("ADR_FK", adresseID, TableQuery[AdresseTable])(_.id)
//  }
//
//  protected val anbieterTableQuery = TableQuery[AnbieterTable]
//
//  protected def anbieterTableAutoInc = anbieterTableQuery returning anbieterTableQuery.map(_.id)
//}
//
///**
//  * Singleton instace with Postgres Drivers
//  */
//object AnbieterRepository extends AnbieterRepository with PostgresDBComponent
//
//case class Anbieter(anwenderID: Long, anbieterID: Long, id: Option[Long] = None)