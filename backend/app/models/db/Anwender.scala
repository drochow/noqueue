//package models.db
//
//import scala.concurrent._
//import slick.profile.RelationalTableComponent
//import models.db.connections.{ DBComponent, PostgresDBComponent }
//import slick.lifted._
//
//trait AnwenderRespostiory extends BaseRepository[AnwenderTable, Anwender](TableQuery[AnwenderTable]) {
//  //  def createWithAdresse(anwender: Anwender, adresse: Adresse): Future[Long] = {
//  //
//  //  val dbAction = (
//  //      for {
//  //        adrId <- { adresseTableQuery returning adresseTableQuery.map(_.id) += adresse }
//  //        anwenderId <- { anwenderTableQuery returning anwenderTableQuery.map(_.id) += Anwender(anwender.nutzerEmail, anwender.password, anwender.nutzerName, Option(adrId)) }
//  //      } yield anwenderId
//  //    ).transactionally
//  //    db.run(dbAction);
//  //  }
//  def findByEmail(email: String): Future[Anwender] = { Future.successful(Anwender(email, "something", "Max Mustermann", 1, 12L)) }
//  def setup(): Future[Any] = { db.run(DBIO.seq(query.schema.create)) }
//
//}
//
//case class Anwender(
//    nutzerEmail: String,
//    password: String,
//    nutzerName: String,
//    adresseId: Option[Long] = None,
//    id: Option[Long] = None
//) {
//}
//
//class AnwenderTable(tag: Tag) extends Table[Anwender](tag, "ANWENDER") {
//
//  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
//  def adresseID = column[Long]("ADRESSE_ID")
//  def nutzerEmail = column[String]("NUTZEREMAIL")
//  def password = column[String]("PASSWORD")
//  def nutzerName = column[String]("NUTZERNAME")
//
//  def * = (nutzerEmail, password, nutzerName, adresseID.?, id.?) <> (Anwender.tupled, Anwender.unapply)
//
//  def adresse = foreignKey("ADRESSE_FK", adresseID, TableQuery[AdresseTable])(_.id)
//}
//
//object AnwenderRepository extends AnwenderRespostory with PostgresDBComponent;