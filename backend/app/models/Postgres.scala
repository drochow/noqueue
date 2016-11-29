package models

import com.google.inject.Singleton
import models.db.DAL
import slick.driver.{ H2Driver, PostgresDriver }
import slick.driver.PostgresDriver.api._

trait DB {
  val db: Database;
  val dal: DAL;
}

@Singleton
class PostgresDB extends DB {
  val db = Database.forConfig("postgres")
  val dal = new DAL(PostgresDriver)
}

@Singleton
class H2DB extends DB {
  val db = Database.forConfig("h2")
  val dal = new DAL(H2Driver)
}
