package modules

import com.google.inject.AbstractModule
import osm.{ AdressService, OSMAdressService }
import play.api.{ Configuration, Environment }

/**
 * Created by David on 09.01.17.
 */
class NoQueueModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[AdressService]).to(classOf[OSMAdressService])
  }
}
