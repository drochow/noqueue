package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

case class Tagg(id: Option[Long], mitarbeiterID: Long, dienstleistungsID: Long, beschreibung: String)

class Taggs(tag: Tag) extends Table[Tagg](tag, "TAG") {
  def id = column[Long]("TAG_ID", O.PrimaryKey, O.AutoInc)
  def anbieterID = column[Long]("ANB_ID")
  def dienstleistungsID = column[Long]("DL_ID")
  def name = column[String]("NAME")

  def * = (id, anbieterID, dienstleistungsID, name) <> (Tagg.tupled, Tagg.unapply)

  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
  def dienstleistung: ForeignKeyQuery[Dienstleistungs, Dienstleistung] =
    foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungs])(_.id)
  def uniqueTag = index("A_has_Tag", (anbieterID, dienstleistungsID), unique = true)
}