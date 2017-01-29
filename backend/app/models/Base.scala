package models

import slick.dbio.DBIO

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class Base(val dbD: DB) {

  val db = dbD.db;
  val dal = dbD.dal;

  def exec[T](dbio: DBIO[T]): Future[T] = db.run(dbio)

  def setupDB = db.run(dal.create)
}

