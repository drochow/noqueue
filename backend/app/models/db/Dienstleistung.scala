//package models.db
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.{ ForeignKeyQuery, TableQuery }
//
//case class Dienstleistung(id: Option[Long], dlTypID: Long, anbieterID: Long, kommentar: String, aktion: String)
//
//class Dienstleistungen(tag: Tag) extends Table[Dienstleistung](tag, "DIENSTLEISTUNG") {
//  def id = column[Long]("DL_ID", O.PrimaryKey, O.AutoInc)
//  def dlTypID = column[Long]("DLT_ID")
//  def anbieterID = column[Long]("ANB_ID")
//  def kommentar = column[String]("KOMMENTAR")
//  def aktion = column[String]("AKTION")
//
//  def * = (id.?, dlTypID, anbieterID, kommentar, aktion) <> (Dienstleistung.tupled, Dienstleistung.unapply)
//
//  def dienstleistungsTyp: ForeignKeyQuery[DienstleistungsTypen, DienstleistungsTyp] =
//    foreignKey("DLT_FK", dlTypID, TableQuery[DienstleistungsTypen])(_.id)
//  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
//    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
//}
//
//object dienstleistungen extends TableQuery(new Dienstleistungen(_)) {
//  //DOA code here
//}