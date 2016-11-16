//package models.db
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.{ ForeignKeyQuery, TableQuery }
//
//case class Bewertung(id: Option[Long], anbieterID: Long, anwenderID: Long, wert: Int)
//
//class Bewertungen(tag: Tag) extends Table[Bewertung](tag, "BEWERTUNG") {
//  def id = column[Long]("BEW_ID", O.PrimaryKey, O.AutoInc)
//  def anbieterID = column[Long]("ANB_ID")
//  def anwenderID = column[Long]("ANW_ID")
//  def wert = column[Int]("WERT")
//
//  def * = (id.?, anbieterID, anwenderID, wert) <> (Bewertung.tupled, Bewertung.unapply)
//
//  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
//    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
//
//  def anwender: ForeignKeyQuery[Anwenders, Anwender] =
//    foreignKey("ANW_FK", anwenderID, TableQuery[Anwenders])(_.id)
//}
//
//class bewertungen extends TableQuery(new Bewertungen(_)) {
//  //DOA code here
//}