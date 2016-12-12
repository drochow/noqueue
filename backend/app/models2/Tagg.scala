package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

case class Tagg(id: Option[Long], mitarbeiterID: Long, dienstleistungsID: Long, beschreibung: String)

class Faehigkeits(tag: Tag) extends Table[Faehigkeit](tag, "FAEHIGKEIT") {
  def id = column[Long]("FHK_ID", O.PrimaryKey, O.AutoInc)
  def mitarbeiterID = column[Long]("MIT_ID")
  def dienstleistungsID = column[Long]("DLT_ID")
  def beschreibung = column[String]("BESCHREIBUNG")

  def * = (id, anbieterID, dienstleistungsID, name) <> (Faehigkeit.tupled, Faehigkeit.unapply)



  def anbieter: ForeignKeyQuery[Anbieters, (Long, Long, String, String, String, Boolean, Int)] =
    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
  def dienstleistung: ForeignKeyQuery[Dienstleistungs, (Long, Long, Long, String, String)] =
    foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungs])(_.id)
  def uniqueTag = index("A_has_Tag", (anbieterID, dienstleistungsID), unique = true)
}