package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

case class Leiter(id: Option[Long], anwenderID: Long, anbieterID: Long)

class Leiters(tag: Tag) extends Table[Leiter](tag, "LEITER") {
  def id = column[Long]("LEI_ID", O.PrimaryKey, O.AutoInc)
  def anwenderID = column[Long]("ANW_ID")
  def anbieterID = column[Long]("ANB_ID")

  def * = (id.?, anwenderID, anbieterID) <> (Leiter.tupled, Leiter.unapply)

  def anwender: ForeignKeyQuery[Anwenders, Anwender] =
    foreignKey("ANW_FK", anwenderID, TableQuery[Anwenders])(_.id)

  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
}