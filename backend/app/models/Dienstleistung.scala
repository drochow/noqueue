package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

  case class Dienstleistung(id: Option[Long], dlTypID: Long, anbieterID: Long, kommentar: String, aktion: String)

  class Dienstleistungs(tag: Tag) extends Table[Dienstleistung](tag, "DIENSTLEISTUNG") {
    def id = column[Long]("DL_ID", O.PrimaryKey, O.AutoInc)
    def dlTypID = column[Long]("DLT_ID")
    def anbieterID = column[Long]("ANB_ID")
    def kommentar = column[String]("KOMMENTAR")
    def aktion = column[String]("AKTION")

    def * = (id.?, dlTypID, anbieterID, kommentar, aktion) <> (Dienstleistung.tupled, Dienstleistung.unapply)

    def dienstleistungsTyp: ForeignKeyQuery[DienstleistungsTyps, DienstleistungsTyp] =
      foreignKey("DLT_FK", dlTypID, TableQuery[DienstleistungsTyps])(_.id)
    def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
      foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
  }
