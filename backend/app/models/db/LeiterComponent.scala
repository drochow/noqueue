package models.db

trait LeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class LeiterTable(tag: Tag) extends Table[LeiterEntity](tag, "LEITER") {
    def id = column[PK[LeiterEntity]]("LEI_ID", O.PrimaryKey, O.AutoInc)
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def betriebId = column[PK[BetriebEntity]]("ANB_ID")

    def * = (anwenderId, betriebId, id.?) <> (LeiterEntity.tupled, LeiterEntity.unapply)

    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)

    def anbieter = foreignKey("ANB_FK", betriebId, betriebe)(_.id)
  }

  def leiters = TableQuery[LeiterTable]

  def leitersAutoInc = leiters returning leiters.map(_.id.?)

}