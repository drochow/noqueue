//package models.db
//
//import models.Mitarbeiters
//import slick.driver.PostgresDriver.api._
//import slick.lifted.{ ForeignKeyQuery, TableQuery }
//
//case class Faehigkeit(id: Option[Long], mitarbeiterID: Long, dienstleistungsID: Long, beschreibung: String)
//
//class Faehigkeiten(tag: Tag) extends Table[Faehigkeit](tag, "FAEHIGKEIT") {
//  def id = column[Long]("FHK_ID", O.PrimaryKey, O.AutoInc)
//  def mitarbeiterID = column[Long]("MIT_ID")
//  def dienstleistungsID = column[Long]("DLT_ID")
//  def beschreibung = column[String]("BESCHREIBUNG")
//
//  def * = (id.?, mitarbeiterID, dienstleistungsID, beschreibung) <> (Faehigkeit.tupled, Faehigkeit.unapply)
//
//  def mitarbeiter: ForeignKeyQuery[Mitarbeiters, Mitarbeiter] =
//    foreignKey("MIT_FK", mitarbeiterID, TableQuery[Mitarbeiters])(_.id)
//  def dienstleistung: ForeignKeyQuery[Dienstleistungen, Dienstleistung] =
//    foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungen])(_.id)
//  def uniqueFaehigkeit = index("M_has_Faehigkeit", (mitarbeiterID, dienstleistungsID), unique = true)
//}
//
//object faehigkeiten extends TableQuery(new Faehigkeiten(_)) {
//  //DOA code here
//}
