/*
package models.db


import models.db.connections.DBComponent
import slick.lifted.AbstractTable
import slick.model.Table

import scala.concurrent.Future

/**
 * Created by anwender on 10.11.2016.
 */

class GenericRepository[TTable <: AbstractTable[_] with WithId] { this: DBComponent =>
  import driver.api._

  val tableQuery = slick.lifted.TableQuery[TTable]

  def tAutoInc = tableQuery returning tableQuery.map(_.id)

  type T = TTable#TableElementType with WithId

  /**
   * Create a new t
   *
   * @param t
   */
  def create(t: T): Future[Long] = db.run { tAutoInc += t }

  /**
   * Update a t
   * @param t
   */
  def update(t: T): Future[Unit] = {
    db.run(DBIO.seq(tableQuery.filter(_.id == t.id).update(t)))
  }

  /**
   * Get t by id
   *
   * @param id
   */
  def getById(id: Long): Future[Option[T]] = db.run { tableQuery.filter(_.id == id).result.headOption }

  /**
   * get all t
   *
   * @return
   */
  def getAll(): Future[List[T]] = db.run { tableQuery.to[List].result }

  /**
   * delete t by id
   *
   * @param id
   */
  def delete(id: Long): Future[Unit] = { db.run(DBIO.seq(tableQuery.filter(_.id === id).delete)) }
}

trait WithId {
  val id: Long
}
*/ 