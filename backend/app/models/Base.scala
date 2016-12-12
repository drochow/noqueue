package models

import api.ApiError

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
case class Base() {
  val db = PostgresDB.db;
  val dal = PostgresDB.dal;

  def setupDB = db.run(dal.create)
}
