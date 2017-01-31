package modules

import com.google.inject.AbstractModule
import models.{ DB, H2DB, PostgresDB }
import services.{ AdressService, GoolgeAdressService, OSMAdressService }
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Mode }

/**
 * Created by David on 09.01.17.
 */
class NoQueueModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    if (environment.mode.equals(Mode.Test)) {
      bind(classOf[AdressService]).to(classOf[GoolgeAdressService])
      bind(classOf[DB]).to(classOf[H2DB])
    } else {
      bind(classOf[AdressService]).to(classOf[OSMAdressService])
      bind(classOf[DB]).to(classOf[PostgresDB])
    }
  }
}
