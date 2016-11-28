package models.db

import models.{ Anwender, Dienstleistung, Mitarbeiter, WarteSchlangenPlatz }
import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent =>
  import driver.api._

  class WarteSchlangenPlatzTable(tag: Tag) extends Table[WarteSchlangenPlatz](tag, "WARTESCHLANGENPLATZ") {
    def id = column[PK[WarteSchlangenPlatz]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def folgePlatzId = column[PK[WarteSchlangenPlatz]]("NEXT_ID");
    def dienstleistungsId = column[PK[Dienstleistung]]("DLT_ID")
    def mitarbeiterId = column[PK[Mitarbeiter]]("MIT_ID")
    def anwenderId = column[PK[Anwender]]("ANW_ID")
    def beginnZeitpunkt = column[Timestamp]("BEGINNZEIT", SqlType("timestamp"));

    def * = (beginnZeitpunkt, anwenderId, mitarbeiterId, dienstleistungsId, folgePlatzId.?, id.?) <> (WarteSchlangenPlatz.tupled, WarteSchlangenPlatz.unapply)

    def dienstleistung = foreignKey("DL_FK", dienstleistungsId, dienstleistungen)(_.id)
    def mitarbeiter = foreignKey("MIT_FK", mitarbeiterId, mitarbeiters)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    def folgePlatz = foreignKey("NEXT_FK", folgePlatzId, warteschlangenplaetze)(_.id)
  }

  val warteschlangenplaetze = TableQuery[WarteSchlangenPlatzTable]

  val warteschlangenplaetzeAutoInc = warteschlangenplaetze returning warteschlangenplaetze.map(_.id)
}