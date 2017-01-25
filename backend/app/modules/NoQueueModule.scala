package modules

import com.google.inject.AbstractModule
import models.{ DB, H2DB, PostgresDB }
import osm.{ AdressService, GoolgeAdressService, OSMAdressService }
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment }

/**
 * Created by David on 09.01.17.
 */
class NoQueueModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[AdressService]).to(classOf[OSMAdressService])
    bind(classOf[DB]).to(classOf[PostgresDB])
  }
}

class NoQueueTestModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[AdressService]).to(classOf[GoolgeAdressService])
    bind(classOf[DB]).to(classOf[H2DB])
  }
}
