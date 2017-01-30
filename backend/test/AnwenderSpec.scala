import models.db.{ AdresseEntity, AnwenderEntity }
import models.{ Anwender, DB, UnregistrierterAnwender }
import org.scalatest._
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.Mode
import play.api.inject.ApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future
/**
 * Created by anwender on 25.01.2017.
 */
class AnwenderSpec extends AsyncFlatSpec with ParallelTestExecution {
  override def withFixture(test: NoArgAsyncTest): FutureOutcome = super.withFixture(test)

  val application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build
  val db: DB = application.injector.instanceOf[DB]
  //db-setup
  var test = "before creation of the DAL"
  //initilisation of our dal, if we're using a h2-DB in Memory we don't need to set that up
  db.db.run(db.dal.create) map {
    _ => { test = "after successful creation of the DAL" }
  } recover {
    case err: Throwable => test = "after failed creation of the DAL"
  }
  //val al: ApplicationLifecycle = application.injector.instanceOf[ApplicationLifecycle]
  //val addrE: AdresseEntity = AdresseEntity("", "", "", "", None, None)

  def anwenderize(x: Any) = {
    AnwenderEntity(x + "@example.com", "password" + x, "User" + x)
  }
  val anwenderEs = (1 to 10).toList.map(anwenderize)

  val uA = new UnregistrierterAnwender(db);
  def persist(anwenderEntity: AnwenderEntity) = {
    uA.registrieren(anwenderEntity)
  }
  val persAnwenders = anwenderEs.map(persist)

  def modelFromPersistedAnw(pAnw: Future[AnwenderEntity]) = {
    pAnw map {
      case anwE => new Anwender(db.dal.getAnwenderWithAdress(anwE.id.get), db)
    }
  }
  val anwenderModels = persAnwenders.map(modelFromPersistedAnw)

  "An Anwender" should "return his profile" in {
    /*for {
      profil <- anwenderModels(0).map(_.profilAnzeigen())
      persistedAnwender <- persAnwenders(0)
    } yield (profil should ===(persistedAnwender, None))*/
    anwenderModels map (_ map (_.profilAnzeigen().map(println(_))))
    //persAnwenders map (_ map println(_))
    succeed
  }
  it should "permit full-on-changing but still respect uniqueness" in {
    val s = "Update010"
    val anwenderEntity = anwenderize(s)
    for {
      anwender <- anwenderModels(1)
      updated <- anwender.anwenderInformationenAustauschen(anwenderEntity, None)
      test <- Future.successful(if (!updated) Failed)
      profil <- anwender.profilAnzeigen()
      updatedAnw <- Future.successful {
        profil match {
          case (anw: AnwenderEntity, _) => anw
        }
      }
    } yield (updatedAnw.nutzerName should ===(anwenderEntity.nutzerName))
    succeed
  }
  /*an [NoSuchElementException] should be thrownBy {
  }*/
}
