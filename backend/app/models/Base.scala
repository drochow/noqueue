package models

import api.ApiError
import slick.dbio.DBIO

import play.api.inject.ApplicationLifecycle
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
//@todo inject config and choose db
class Base(val applicationLifecycle: ApplicationLifecycle) {

  val db = PostgresDB.db;
  val dal = PostgresDB.dal;

  def exec[T](dbio: DBIO[T]): Future[T] = db.run(dbio)

  def setupDB = db.run(dal.create)

  applicationLifecycle.addStopHook { () =>
    Future.successful(db.close())
  }
}
