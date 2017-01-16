package models

import api.ApiError
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * Created by David on 29.11.16.
 */
case class Base() {
  val db = H2DB.db;
  val dal = H2DB.dal;

  def exec[T](dbio: DBIO[T]): Future[T] = db.run(dbio)

  def setupDB = db.run(dal.create)
}
