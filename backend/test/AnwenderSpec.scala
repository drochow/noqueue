import java.io.{ BufferedReader, File, FileReader }

import models.db.{ DienstleistungEntity, _ }
import models.{ Anwender, DB, Leiter, UnregistrierterAnwender }
import org.h2.jdbc.JdbcSQLException
import org.scalatest.Matchers._
import org.scalatest._
import Assertions._

import scala.concurrent.{ Await, Future }
import play.api.{ Environment, Mode }
import play.api.inject.guice.GuiceApplicationBuilder
import utils.{ OneLeiterRequiredException, UnauthorizedException }

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
/**
 * Created by anwender on 25.01.2017.
 */
class AnwenderSpec extends AsyncWordSpec {

  override def withFixture(test: NoArgAsyncTest) = { // Define a shared fixture
    try {
      // Shared setup (run at beginning of each test)
      val fill = new File("./test/fill.sql")
      //Awaiting  to ensure that db is fully cleaned up and filled  before test is started
      Await.result(db.db.run(db.dal.dropAllObjectsForTestDB()), 10 seconds)
      Await.result(db.db.run(db.dal.create), 10 seconds)
      Await.result(db.db.run(db.dal.runScript(fill.getAbsolutePath)), 10 seconds)
      test()
    } finally {
      // Shared cleanup (run at end of each test)
    }
  }
  val application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build
  val db: DB = application.injector.instanceOf[DB]

  /*  def anwenderize(x: Any) = {
    AnwenderEntity(x + "@example.com", "password" + x, "User" + x)
  }
  val anwenderEs = (1 to 31).toList.map(anwenderize)

  val uA = new UnregistrierterAnwender(db);
  def persist(anwenderEntity: AnwenderEntity) = {
    uA.registrieren(anwenderEntity)
  }
  val persAnwenders = anwenderEs.map(persist)

  def modelFromPersistedAnw(pAnw: Future[AnwenderEntity]): Future[Anwender] = {
    pAnw map {
      case anwE => new Anwender(db.dal.getAnwenderWithAdress(anwE.id.get), db)
    }
  }
  val anwenderModels = persAnwenders.map(modelFromPersistedAnw)
*/

  def anwenderize(x: Any) = {
    AnwenderEntity(x + "@example.com", "password" + x, "User" + x)
  }

  val anwender = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](4L)), db)

  "An Anwender" should {
    //dummy1@gmail.com', '$2a$10$p3ckLWcp7jUMaPkZP85vkOmrPunxBhjebLyDTAIeGNhQ7y4R64e.G', 'dummy1', 8, 4
    val expectedAnwender = AnwenderEntity("dummy1@gmail.com", "$2a$10$p3ckLWcp7jUMaPkZP85vkOmrPunxBhjebLyDTAIeGNhQ7y4R64e.G", "dummy1", Some(PK[AdresseEntity](8)), Some(PK[AnwenderEntity](4)))
    //Bollestraße', '1020', '13509', 'Berlin', 52.591225399999999, 13.2978073999999999, 8
    val expectedAdresse = AdresseEntity("Bollestraße", "1020", "13509", "Berlin", Some(52.591225399999999), Some(13.2978073999999999), Some(PK[AdresseEntity](8)))
    "return his profile" in {
      for {
        profil <- anwender.profilAnzeigen()
      } yield (profil should equal((expectedAnwender, Some(expectedAdresse))))
    }
    "permit full-on-changing as long as nutzerName and nutzerEmail stay unique" in {
      val updateAnwender = anwenderize(12344321)
      for {
        updated <- anwender.anwenderInformationenAustauschen(updateAnwender, None)
        throwAway <- Future.successful(if (!updated) Failed)
        profil <- anwender.profilAnzeigen()
      } yield (profil should equal((updateAnwender.copy(id = expectedAnwender.id, password = expectedAnwender.password), None)))
    }
    "forbid full-on-changing if nutzerName and nutzerEmail are not unique" in {
      val updateAnwender = anwenderize(12344321)
      for {
        updated <- anwender.anwenderInformationenAustauschen(updateAnwender, None)
        throwAway <- Future.successful(if (!updated) Failed)
        profil <- anwender.profilAnzeigen()
      } yield (profil should equal((updateAnwender.copy(id = expectedAnwender.id, password = expectedAnwender.password), None)))
      val anw2 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](1L)), db)
      recoverToSucceededIf[JdbcSQLException](
        anw2.anwenderInformationenAustauschen(updateAnwender, None) map {
          updateHappened => assert(!updateHappened)
        }
      )
    }
    "permit partial changing as long as nutzerName and nutzerEmail stay unique" in {
      val updateAnwender = anwenderize(12344321)
      for {
        updated <- anwender.anwenderInformationenVeraendern(Some(updateAnwender.nutzerName), Some(updateAnwender.nutzerEmail), Some(None))
        throwAway <- Future.successful(if (!updated) Failed)
        profil <- anwender.profilAnzeigen()
      } yield (profil should equal((updateAnwender.copy(id = expectedAnwender.id, password = expectedAnwender.password), None)))
    }
    "forbid partial changing if nutzerName and nutzerEmail are not unique" in {
      val updateAnwender = anwenderize(12344321)
      for {
        updated <- anwender.anwenderInformationenVeraendern(Some(updateAnwender.nutzerName), Some(updateAnwender.nutzerEmail), Some(None))
        throwAway <- Future.successful(if (!updated) Failed)
        profil <- anwender.profilAnzeigen()
      } yield (profil should equal((updateAnwender.copy(id = expectedAnwender.id, password = expectedAnwender.password), None)))
      val anw2 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](1L)), db)
      recoverToSucceededIf[JdbcSQLException](
        anw2.anwenderInformationenVeraendern(Some(updateAnwender.nutzerName), Some(updateAnwender.nutzerEmail), Some(None)) map {
          updateHappened => assert(!updateHappened)
        }
      )

    }
  }
  /*an [NoSuchElementException] should be thrownBy {
  }*/
}
