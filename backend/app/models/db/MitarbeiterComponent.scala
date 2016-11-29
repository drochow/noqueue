package models.db

trait MitarbeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class MitarbeiterTable(tag: Tag) extends Table[MitarbeiterEntity](tag, "MITARBEITER") {
    def id = column[PK[MitarbeiterEntity]]("MIT_ID", O.PrimaryKey, O.AutoInc)
    def betriebId = column[PK[BetriebEntity]]("BETR_ID")
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def anwesend = column[Boolean]("ANWESEND")

    def * = (anwesend, betriebId, anwenderId, id.?) <> (MitarbeiterEntity.tupled, MitarbeiterEntity.unapply)

    def anbieter = foreignKey("BETR_FK", betriebId, betriebe)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
  }

  val mitarbeiters = TableQuery[MitarbeiterTable]

  def mitarbeitersAutoInc = mitarbeiters returning mitarbeiters.map(_.id)
}