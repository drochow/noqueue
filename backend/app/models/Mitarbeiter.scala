package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

  case class Mitarbeiter(id: Option[Long], anwenderID: Long, anbieterID: Long, anwesend: Boolean)

  class Mitarbeiters(tag: Tag) extends Table[Mitarbeiter](tag, "MITARBEITER") {
    def id = column[Long]("MIT_ID", O.PrimaryKey, O.AutoInc)
    def anbieterID = column[Long]("ANB_ID")
    def anwenderID = column[Long]("ANW_ID")
    def anwesend = column[Boolean]("ANWESEND")

    def * = (id.?, anbieterID, anwenderID, anwesend) <> (Mitarbeiter.tupled, Mitarbeiter.unapply)

    def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
      foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
    def anwender: ForeignKeyQuery[Anwenders, Anwender] =
      foreignKey("ANW_FK", anwenderID, TableQuery[Anwenders])(_.id)
  }