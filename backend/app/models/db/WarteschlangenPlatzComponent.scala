package models.db

import java.sql.Timestamp

import slick.profile.SqlProfile.ColumnOption.SqlType

trait WarteschlangenPlatzComponent {
  this: DriverComponent with AnwenderComponent with MitarbeiterComponent with DienstleistungComponent =>
  import driver.api._

  class WarteSchlangenPlatzTable(tag: Tag) extends Table[WarteSchlangenPlatzEntity](tag, "WARTESCHLANGENPLATZ") {
    def id = column[PK[WarteSchlangenPlatzEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def folgePlatzId = column[PK[WarteSchlangenPlatzEntity]]("NEXT_ID");
    def dienstleistungsId = column[PK[DienstleistungEntity]]("DLT_ID")
    def mitarbeiterId = column[PK[MitarbeiterEntity]]("MIT_ID")
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def beginnZeitpunkt = column[Timestamp]("BEGINNZEIT", SqlType("timestamp"));
    def dienstleistung = foreignKey("DL_FK", dienstleistungsId, dienstleistungen)(_.id)
    def mitarbeiter = foreignKey("MIT_FK", mitarbeiterId, mitarbeiters)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    def folgePlatz = foreignKey("NEXT_FK", folgePlatzId, warteschlangenplaetze)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (beginnZeitpunkt, anwenderId, mitarbeiterId, dienstleistungsId, folgePlatzId.?, id.?) <> (WarteSchlangenPlatzEntity.tupled, WarteSchlangenPlatzEntity.unapply)

  }

  val warteschlangenplaetze = TableQuery[WarteSchlangenPlatzTable]

  val warteschlangenplaetzeAutoInc = warteschlangenplaetze returning warteschlangenplaetze.map(_.id)

  def getWarteschlangenPlaetzeOfMitarbeiter(mitarbeiterId: PK[MitarbeiterEntity]): DBIO[Seq[WarteSchlangenPlatzEntity]] =
    warteschlangenplaetze.filter(_.mitarbeiterId === mitarbeiterId).result
}