import java.io.File

import models.{ DB, H2DB, Leiter, UnregistrierterAnwender }
import org.scalatest.AsyncWordSpec
import play.api.inject.{ ApplicationLifecycle, bind }
import play.api.{ Environment, Mode }
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * Created by David on 25.01.17.
 */
class LeiterTest extends AsyncWordSpec {
  override def withFixture(test: NoArgAsyncTest) = { // Define a shared fixture
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      // Shared cleanup (run at end of each test)
    }
  }

  val application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build

  val db: DB = application.injector.instanceOf[DB]
  val al: ApplicationLifecycle = application.injector.instanceOf[ApplicationLifecycle]
  val ua = new UnregistrierterAnwender(al, db);

  //  val injector = new GuiceApplicationBuilder()
  //    .load(
  //      new play.api.inject.BuiltinModule,
  //      bind[DB].to[H2DB]
  //    ).injector
  //
  //  val db: DB = injector.instanceOf[DB]
}