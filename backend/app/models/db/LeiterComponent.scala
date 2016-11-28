package models.db

import models.{ Anwender, Betrieb, Leiter }

trait LeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class LeiterTable(tag: Tag) extends Table[Leiter](tag, "LEITER") {
    def id = column[PK[Leiter]]("LEI_ID", O.PrimaryKey, O.AutoInc)
    def anwenderId = column[PK[Anwender]]("ANW_ID")
    def betriebId = column[PK[Betrieb]]("ANB_ID")

    def * = (anwenderId, betriebId, id.?) <> (Leiter.tupled, Leiter.unapply)

    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)

    def anbieter = foreignKey("ANB_FK", betriebId, betriebe)(_.id)
  }

  def leiters = TableQuery[LeiterTable]

  def leitersAutoInc = leiters returning leiters.map(_.id.?)

}