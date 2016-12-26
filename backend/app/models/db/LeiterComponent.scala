package models.db

trait LeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class LeiterTable(tag: Tag) extends Table[LeiterEntity](tag, "LEITER") {

    def id = column[PK[LeiterEntity]]("LEI_ID", O.PrimaryKey, O.AutoInc)
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def betriebId = column[PK[BetriebEntity]]("ANB_ID")

    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    def anbieter = foreignKey("ANB_FK", betriebId, betriebe)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (anwenderId, betriebId, id.?) <> (LeiterEntity.tupled, LeiterEntity.unapply)
  }

  def leiters = TableQuery[LeiterTable]

  def leitersAutoInc = leiters returning leiters.map(_.id.?)

  def getLeiterById(id: PK[LeiterEntity]): DBIO[LeiterEntity] = leiters.filter(_.id === id).result.head

  /*def getBetriebListByAnwender(anwenderId: PK[AnwenderEntity]) = {
    (leiters joinLeft betriebe on (_.betriebId === _.id)).filter { case (leiter, betrieb) => leiter.anwenderId === anwenderId }.result.head.nonFusedEquivalentAction
  }*/

  def getLeiterByAnwenderIDAndBetriebId(anwenderId: PK[AnwenderEntity], betriebId: PK[BetriebEntity]): DBIO[LeiterEntity] = {
    leiters.filter(leiter => leiter.anwenderId === anwenderId && leiter.betriebId === betriebId).result.head
  }

}