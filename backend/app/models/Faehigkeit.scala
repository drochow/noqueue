package models

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

case class Faehigkeit(id: Option[Long], mitarbeiterID: Long, dienstleistungsID: Long, beschreibung: String)

class Faehigkeits(tag: Tag) extends Table[Faehigkeit](tag, "FAEHIGKEIT") {
  def id = column[Long]("FHK_ID", O.PrimaryKey, O.AutoInc)
  def mitarbeiterID = column[Long]("MIT_ID")
  def dienstleistungsID = column[Long]("DLT_ID")
  def beschreibung = column[String]("BESCHREIBUNG")

  def * = (id.?, mitarbeiterID, dienstleistungsID, beschreibung) <> (Faehigkeit.tupled, Faehigkeit.unapply)

  def mitarbeiter: ForeignKeyQuery[Mitarbeiters, Mitarbeiter] =
    foreignKey("MIT_FK", mitarbeiterID, TableQuery[Mitarbeiters])(_.id)
  def dienstleistung: ForeignKeyQuery[Dienstleistungs, Dienstleistung] =
    foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungs])(_.id)
  def uniqueFaehigkeit = index("M_has_Faehigkeit", (mitarbeiterID, dienstleistungsID), unique = true)
}
