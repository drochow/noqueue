package models

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
case class Base(database: DB = new H2DB) { //@TOdo change back to postgres
  val db = database.db
  val dal = database.dal
}
