package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

case class DienstleistungsTyp(id: Option[Long], name: String)

class DienstleistungsTyps(tag: Tag) extends Table[DienstleistungsTyp](tag, "DIENSTLEISTUNGSTYP") {
  def id = column[Long]("DLT_ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def * = (id.?, name) <> (DienstleistungsTyp.tupled, DienstleistungsTyp.unapply)
}