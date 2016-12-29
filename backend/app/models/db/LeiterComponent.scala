package models.db

import utils.OneLeiterRequiredException

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

  def deleteLeiter(leiterId: PK[LeiterEntity], betriebId: PK[BetriebEntity]): DBIO[Int] =
    (for {
      currentLeiterCount <- (leiters filter (_.betriebId === betriebId)).length.result
      affectedRows <- if (currentLeiterCount < 2) throw new OneLeiterRequiredException else leiters.filter(_.id === leiterId).filter(_.betriebId === betriebId).delete
    } yield affectedRows)

  def getLeiterById(id: PK[LeiterEntity]) = leiters.filter(_.id === id).result

  def listLeiterOf(betriebId: PK[BetriebEntity], page: Int, size: Int): DBIO[Seq[AnwenderEntity]] =
    (for {
      (leiter, anwender) <- (leiters join anwenders on (_.anwenderId === _.id)).filter {
        case (mitarbeiter, anwender) => mitarbeiter.betriebId === betriebId
      }.drop(page * size).take(size)
    } yield anwender).result

  //  def getLeiterOf(betriebId: PK[BetriebEntity], anwender: AnwenderEntity): DBIO[(BetriebEntity, AnwenderEntity, LeiterEntity)] = {
  //
  //    val query: DBIO[(LeiterEntity, BetriebEntity)] = (leiters join betriebe on (
  //      (ltd: LeiterTable, btr: BetriebTable) => ltd.betriebId === btr.id
  //    )).filter {
  //        case (ltd: LeiterTable, btr: BetriebTable) => ltd.anwenderId === anwender.id.get && ltd.betriebId === betriebId
  //      }.result.head
  //
  //    //Add the given Anwender tot he DBIO result and transform result to single tuple
  //    (query zip (DBIO.successful(anwender))).map(tuple => (tuple._1._1, tuple._1._2, tuple._2))
  //  }

  def getLeiterOfById(betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity]): DBIO[(BetriebEntity, AnwenderEntity, LeiterEntity)] = {
    (for {
      ((betrieb, anwender), leiter) <- (betriebe join anwenders join leiters on {
        case ((betrieb: BetriebTable, anwender: AnwenderTable), leiter: LeiterTable) =>
          betrieb.id === leiter.betriebId && anwender.id === leiter.anwenderId
      })
        .filter {
          case ((betrieb, anwender), leiter) => anwender.id === anwenderId
        }
        .filter {
          case ((betrieb, anwender), leiter) => betrieb.id === betriebId
        }
    } yield (betrieb, anwender, leiter)).result.head.nonFusedEquivalentAction
  }

  /*def getBetriebListByAnwender(anwenderId: PK[AnwenderEntity]) = {
    (leiters joinLeft betriebe on (_.betriebId === _.id)).filter { case (leiter, betrieb) => leiter.anwenderId === anwenderId }.result.head.nonFusedEquivalentAction
  }*/

  def getLeiterByAnwenderIDAndBetriebId(anwenderId: PK[AnwenderEntity], betriebId: PK[BetriebEntity]): DBIO[LeiterEntity] = {
    leiters.filter(leiter => leiter.anwenderId === anwenderId && leiter.betriebId === betriebId).result.head
  }

}