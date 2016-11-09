//package models.db
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.TableQuery
//
//case class DienstleistungsTyp(id: Option[Long], name: String)
//
//class DienstleistungsTypen(tag: Tag) extends Table[DienstleistungsTyp](tag, "DIENSTLEISTUNGSTYP") {
//  def id = column[Long]("DLT_ID", O.PrimaryKey, O.AutoInc)
//  def name = column[String]("NAME")
//
//  def * = (id.?, name) <> (DienstleistungsTyp.tupled, DienstleistungsTyp.unapply)
//}
//
//object dienstleistungsTypen extends TableQuery(new DienstleistungsTypen(_)) {
//  //DOA code here
//}
