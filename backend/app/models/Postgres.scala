package models

import models.db.DAL
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

/**
 * Created by David on 18.11.16.
 */
object PostgresDB {
  val db: Database = Database.forConfig("postgres")
  val dal: DAL = new DAL(PostgresDriver)
}
