package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait LeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent =>
  import driver.api._

  class LeiterTable(tag: Tag) extends Table[LeiterEntity](tag, "LEITER") {

    def id = column[PK[LeiterEntity]]("LEI_ID", O.PrimaryKey, O.AutoInc)
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def betriebId = column[PK[BetriebEntity]]("ANB_ID")

    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)
    def betriebeFK = foreignKey("BTR_FK", betriebId, betriebe)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (anwenderId, betriebId, id.?) <> (LeiterEntity.tupled, LeiterEntity.unapply)
  }

  def leiters = TableQuery[LeiterTable]

  def leitersAutoInc = leiters returning leiters.map(_.id)

  /**
   * Inserts "LeiterEntity" to Database
   *
   * @param leiter
   * @return
   */
  def insert(leiter: LeiterEntity) = (leitersAutoInc += leiter).map(id => leiter.copy(id = Option(id)))

  def getLeiterById(id: PK[LeiterEntity]) = leiters.filter(_.id === id).result

  def getLeiterOf(betriebId: PK[BetriebEntity], anwender: AnwenderEntity): DBIO[(LeiterEntity, BetriebEntity, AnwenderEntity)] = {

    val query: DBIO[(LeiterEntity, BetriebEntity)] = (leiters join betriebe on (
      (ltd: LeiterTable, btr: BetriebTable) => ltd.betriebId === btr.id
    )).filter {
        case (ltd: LeiterTable, btr: BetriebTable) => ltd.anwenderId === anwender.id.get && ltd.betriebId === betriebId
      }.result.head

    //Add the given Anwender tot he DBIO result and transform result to single tuple
    (query zip (DBIO.successful(anwender))).map(tuple => (tuple._1._1, tuple._1._2, tuple._2))
  }

  /*def getBetriebListByAnwender(anwenderId: PK[AnwenderEntity]) = {
    (leiters joinLeft betriebe on (_.betriebId === _.id)).filter { case (leiter, betrieb) => leiter.anwenderId === anwenderId }.result.head.nonFusedEquivalentAction
  }*/

  def getLeiterByAnwenderIDAndBetriebId(anwenderId: PK[AnwenderEntity], betriebId: PK[BetriebEntity]): DBIO[LeiterEntity] = {
    leiters.filter(leiter => leiter.anwenderId === anwenderId && leiter.betriebId === betriebId).result.head
  }

}