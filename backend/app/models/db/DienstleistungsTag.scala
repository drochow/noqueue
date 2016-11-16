//package models.db
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.{ ForeignKeyQuery, TableQuery }
//
//case class DienstleistungsTag(id: Option[Long], mitarbeiterID: Long, dienstleistungsID: Long, beschreibung: String)
//
//class DienstleistungsTags(tag: Tag) extends Table[DienstleistungsTag](tag, "TAG") {
//  def id = column[Long]("TAG_ID", O.PrimaryKey, O.AutoInc)
//  def anbieterID = column[Long]("ANB_ID")
//  def dienstleistungsID = column[Long]("DL_ID")
//  def name = column[String]("NAME")
//
//  def * = (id, anbieterID, dienstleistungsID, name) <> (DienstleistungsTag.tupled, DienstleistungsTag.unapply)
//
//  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
//    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
//  def dienstleistung: ForeignKeyQuery[Dienstleistungen, Dienstleistung] =
//    foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungen])(_.id)
//  def uniqueTag = index("A_has_Tag", (anbieterID, dienstleistungsID), unique = true)
//}
