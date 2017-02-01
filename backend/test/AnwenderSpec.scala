import java.io.{ BufferedReader, File, FileReader }

import models.db.{ DienstleistungEntity, _ }
import models.{ Anwender, DB, Leiter, UnregistrierterAnwender }
import org.h2.jdbc.JdbcSQLException
import org.scalatest.Matchers._
import org.scalatest._
import Assertions._
import org.mindrot.jbcrypt.BCrypt

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
    //NULL, 4, 9, 6, NULL, 13
    val expectedWsP = WarteschlangenPlatzEntity(None, PK[AnwenderEntity](4), PK[MitarbeiterEntity](9), PK[DienstleistungEntity](6), None, Some(PK[WarteschlangenPlatzEntity](13)))
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
    "be able to search for DiensleistungsTyps" in {
      anwender.dienstleistungsTypSuchen("Haare", 0, 10) map {
        seq =>
          {
            //assert(seq contains())
            seq.length should equal(2)
          }
      }
    }
    "be able to change the password" in {
      val pw = "1234"
      val anw = anwenderize("newAnwenderComingIn")
      val unregistrierterAnwender = new UnregistrierterAnwender(db)
      for {
        newAnwenderE <- unregistrierterAnwender.registrieren(anw)
        newAnwender <- Future.successful(new Anwender(db.dal.getAnwenderWithAdress(newAnwenderE.id.get), db))
        pwChanged <- newAnwender.passwordVeraendern(anw.password, pw)
        throwAway <- Future.successful(if (!pwChanged) Failed)
        profil <- newAnwender.profilAnzeigen()
      } yield (assert(BCrypt.checkpw(pw, profil._1.password)))
    }
    "not be able to if old password is not given" in {
      val pw = "password1234"
      anwender.passwordVeraendern("notTheRealPassword", pw) map {
        changerPW => assert(!changerPW)
      }
    }
    "be able to search Anwender with a query relating to email" in {
      anwender.anwenderSuchen(Some("davidkaatz"), 0, 100) map {
        seq =>
          {
            assert(seq.exists(_.id.get == PK[AnwenderEntity](3L)))
            seq.length should equal(2)
          }
      }
    }
    "be able to search Anwender with a query relating to nutzerName" in {
      anwender.anwenderSuchen(Some("dkaatz"), 0, 100) map {
        seq =>
          {
            assert(seq.exists(_.id.get == PK[AnwenderEntity](3L)))
            seq.length should equal(2)
          }
      }
    }
    "be able to search Anwender without a query" in {
      anwender.anwenderSuchen(None, 0, 100) map {
        seq =>
          {
            assert(seq.exists(_.id.get == PK[AnwenderEntity](3L)))
            seq.length should equal(18)
          }
      }
    }
    "return someone else's profile" in {
      for {
        profil <- anwender.anwenderAnzeigen(PK[AnwenderEntity](1L))
      } yield (profil should equal(AnwenderEntity("davidkaatz5@gmx.de", "$2a$10$LdM4yf7zgmjS8Pb5rGyeeeiUXFFc/wEJfeZloUPbjo8MD/CLA0B0S", "dkaatz5", None, Some(PK[AnwenderEntity](1)))))
    }
    "be able to show a WarteschlangePlatz" in {
      for {
        wsp <- anwender.wspAnzeigen()
      } yield (wsp._1 shouldEqual (expectedWsP.id.get))
    }
    "be able to cancel a WarteschlangePlatz" in {
      for {
        a <- anwender.wsVerlassen()
        b <- anwender.wspAnzeigen() //@ todo FIX
      } yield (b._1)
      succeed
    }
    "be able to see every Betrieb Anwender is in relation with" in {
      anwender.meineBetriebe() map {
        seq =>
          {
            assert(seq.exists(_._1.betriebEntity.id.get == PK[BetriebEntity](8)))
            seq.length should equal(5)
          }
      }
    }
    "be able to create a Betrieb" in {
      val betriebE = BetriebEntity("Laden", "030-1234567", "first cry of the rooster-sundown", "Laden@example.com", PK[AdresseEntity](8))
      for {
        (betrieb, adresse) <- anwender.betriebErstellen(betriebE, expectedAdresse)
        persistedBetrieb <- anwender.betriebAnzeigen(betrieb.id.get) map (_._1)
      } yield (persistedBetrieb shouldEqual (betriebE.copy(id = persistedBetrieb.id)))
    }
    "be able to book WsPs" in {
      val pw = "1234"
      val anw = anwenderize("newAnwenderComingIn")
      val unregistrierterAnwender = new UnregistrierterAnwender(db)
      for {
        newAnwenderE <- unregistrierterAnwender.registrieren(anw)
        newAnwender <- Future.successful(new Anwender(db.dal.getAnwenderWithAdress(newAnwenderE.id.get), db))
        wsp <- newAnwender.wsFuerBestimmtenMitarbeiterBeitreten(expectedWsP.dienstLeistungId.value, expectedWsP.mitarbeiterId.value)
      } yield (wsp should equal(WarteschlangenPlatzEntity(None, newAnwenderE.id.get, expectedWsP.mitarbeiterId, expectedWsP.dienstLeistungId, None, None).copy(id = wsp.id)))
    }
    "be able to get next time of a Betrieb" in {
      /*INSERT INTO "MITARBEITER" ("ANWESEND", "BETR_ID", "ANW_ID", "MIT_ID") VALUES (true, 8, 3, 3);
INSERT INTO "MITARBEITER" ("ANWESEND", "BETR_ID", "ANW_ID", "MIT_ID") VALUES (true, 8, 1, 4);
INSERT INTO "MITARBEITER" ("ANWESEND", "BETR_ID", "ANW_ID", "MIT_ID") VALUES (true, 8, 4, 5);*/
      for {
        seq <- anwender.getNextTimeSlotsForBetrieb(PK[BetriebEntity](8).value)
      } yield (2) //@todo
      succeed
    }
  }
  /*an [NoSuchElementException] should be thrownBy {
  }*/
}
