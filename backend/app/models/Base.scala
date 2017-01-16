package models

import api.ApiError
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * Created by David on 29.11.16.
 */
case class Base() {
  val db = PostgresDB.db;
  val dal = PostgresDB.dal;

  def exec[T](dbio: DBIO[T]): Future[T] = db.run(dbio)

  def setupDB = db.run(dal.create)
}
