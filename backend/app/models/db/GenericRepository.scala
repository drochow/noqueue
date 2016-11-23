//package models.db
//
//import models.db.connections.DBComponent
//import slick.lifted.TableQuery
//import slick.model.Table
//
//import scala.concurrent.Future
//
///**
// * Created by anwender on 10.11.2016.
// */
//
//class GenericRepository[T <: TTable[_]] { this: DBComponent =>
//  import driver.api._
//
//  /**
//   * Create a new t
//   *
//   * @param t
//   */
//  def create(t: T): Future[Long] = db.run { tAutoInc += t }
//
//  /**
//   * Update a t
//   * @param t
//   */
//  def update(t: T): Future[Unit] = {
//    db.run(DBIO.seq(tTableQuery.filter(_.id === t.id.get).update(t)))
//  }
//
//  /**
//   * Get t by id
//   *
//   * @param id
//   */
//  def getById(id: Long): Future[Option[T]] = db.run { tTableQuery.filter(_.id === id).result.headOption }
//
//  /**
//   * get all t
//   *
//   * @return
//   */
//  def getAll(): Future[List[T]] = db.run { tTableQuery.to[List].result }
//
//  /**
//    * @param id
//    * @return Affected Rows
//    */
//  def delete(id: Long): Future[Int] = { db.run(DBIO.seq(tTableQuery.filter(_.id === id).delete)) }
//}
//
//private[db] trait TTable[T] { this: DBComponent =>
//  val id: Long
//  import driver.api._
//
//  protected val query = TableQuery[T]
//}
