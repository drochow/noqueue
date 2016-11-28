package models.db

import models.{ Anwender, Betrieb, Mitarbeiter }

trait MitarbeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class MitarbeiterTable(tag: Tag) extends Table[Mitarbeiter](tag, "MITARBEITER") {
    def id = column[PK[Mitarbeiter]]("MIT_ID", O.PrimaryKey, O.AutoInc)
    def betriebId = column[PK[Betrieb]]("BETR_ID")
    def anwenderId = column[PK[Anwender]]("ANW_ID")
    def anwesend = column[Boolean]("ANWESEND")

    def * = (anwesend, betriebId, anwenderId, id.?) <> (Mitarbeiter.tupled, Mitarbeiter.unapply)

    def anbieter = foreignKey("BETR_FK", betriebId, betriebe)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
  }

  val mitarbeiters = TableQuery[MitarbeiterTable]

  def mitarbeitersAutoInc = mitarbeiters returning mitarbeiters.map(_.id)
}