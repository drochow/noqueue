package models

import javax.inject._

import models.db.DAL
import play.api.inject.ApplicationLifecycle
import slick.driver.{ H2Driver, PostgresDriver }
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import javax.inject.Inject

/**
 * Component that holds the Database Driver and the Data Access Layer
 */
trait DB {
  val applicationLifecycle: ApplicationLifecycle
  val db: Database
  val dal: DAL

  applicationLifecycle.addStopHook { () =>
    Future.successful(db.close())
  }
}

@Singleton
class PostgresDB @Inject() (val applicationLifecycle: ApplicationLifecycle) extends DB {
  val db = Database.forConfig("bonecp")
  val dal = new DAL(PostgresDriver)
}

@Singleton
class H2DB @Inject() (val applicationLifecycle: ApplicationLifecycle) extends DB {
  val db = Database.forConfig("h2")
  val dal = new DAL(H2Driver)
}
