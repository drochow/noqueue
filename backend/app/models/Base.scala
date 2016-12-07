package models

import api.ApiError

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
case class Base(database: DB = new H2DB) {
  val db = database.db;
  val dal = database.dal;

  def setupDB = db.run(dal.create)
}
