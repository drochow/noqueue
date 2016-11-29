package models

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
case class Base(database: DB = new PostgresDB) {
  val db = database.db;
  val dal = database.dal;
}
