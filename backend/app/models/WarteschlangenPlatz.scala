package models2

import org.joda.time.DateTime
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

  case class WarteschlagenPlatz(id: Option[Long], dienstleistungsID: Long, mitarbeiterID: Long, anwenderID: Long,
                                folgePlatzID: Long, beginnZeitpunkt: DateTime, schaetzPunkt: DateTime, platzNummer: Int)

  class WarteschlagenPlatzs(tag: Tag) extends Table[WarteschlagenPlatz](tag, "WARTESCHLANGENPLATZ") {
    def id = column[Long]("DL_ID", O.PrimaryKey, O.AutoInc)
    def dienstleistungsID = column[Long]("DLT_ID")
    def mitarbeiterID = column[Long]("MIT_ID")
    def anwenderID = column[Long]("ANW_ID")
    def folgePlatzID = column[Long]("FPL_ID")
    def beginnZeitpunkt = column[Long]("BEGINNZEIT")
    def schaetzPunkt = column[Long]("SCHAETZPUNKT")
    def platzNummer = column[Long]("PLATZNUMMER")

    def * = (id.?, dienstleistungsID, mitarbeiterID, anwenderID, folgePlatzID, beginnZeitpunkt, schaetzPunkt, platzNummer) <> (WarteschlagenPlatz.tupled, WarteschlagenPlatz.unapply)

    def dienstleistung: ForeignKeyQuery[Dienstleistungs, Dienstleistung] =
      foreignKey("DL_FK", dienstleistungsID, TableQuery[Dienstleistungs])(_.id)
    def mitarbeiter: ForeignKeyQuery[Mitarbeiters, Mitarbeiter] =
      foreignKey("MIT_FK", mitarbeiterID, TableQuery[Mitarbeiters])(_.id)
    def anwender: ForeignKeyQuery[Anwenders, Anwender] =
      foreignKey("ANW_FK", anwenderID, TableQuery[Anwenders])(_.id)
    def folgePlatz: ForeignKeyQuery[WarteschlagenPlatzs, WarteschlagenPlatz] =
      foreignKey("DL_FK", folgePlatzID, TableQuery[WarteschlagenPlatzs])(_.id)
  }




