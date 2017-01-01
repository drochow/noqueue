package models

import api.ApiError

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
case class Base() {
  val db = H2DB.db;
  val dal = H2DB.dal;

  def setupDB = db.run(dal.create)
}
