package modules

import com.google.inject.AbstractModule
import osm.{ AdressService, OSMAdressService, GoolgeAdressService }
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment }

/**
 * Created by David on 09.01.17.
 */
class NoQueueModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[AdressService]).to(classOf[GoolgeAdressService])
    //    bind(classOf[ApplicationLifecycle]).to(classOf[Base])
  }
}
