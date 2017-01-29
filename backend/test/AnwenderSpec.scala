import models.db.{ AdresseEntity, AnwenderEntity }
import models.{ Anwender, DB, UnregistrierterAnwender }
import org.scalatest.{ Assertion, AsyncFlatSpec, AsyncWordSpec, FutureOutcome }
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.Mode
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future
/**
 * Created by anwender on 25.01.2017.
 */
class AnwenderSpec extends AsyncFlatSpec {
  override def withFixture(test: NoArgAsyncTest): FutureOutcome = super.withFixture(test)


  val application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build
  val db: DB = application.injector.instanceOf[DB]
  val al: ApplicationLifecycle = application.injector.instanceOf[ApplicationLifecycle]
  val uA = new UnregistrierterAnwender(al, db);
  val anwE: AnwenderEntity = AnwenderEntity("bang@example.com", "password1234", "bangNutzer")
  val addrE: AdresseEntity = AdresseEntity("", "", "", "", None, None)
  //val anw: Future[Anwender] = uA.registrieren(anwE)

  "An Anwender" should "return his profile" in {
    //anw.map(_.profilAnzeigen() should ===(anwE, None))
  }
  an[NoSuchElementException] should be thrownBy {
  }
}
