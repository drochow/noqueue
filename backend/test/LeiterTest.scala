import java.io.{ BufferedReader, File, FileReader }

import models.db.{ DienstleistungEntity, _ }
import models.{ DB, H2DB, Leiter, UnregistrierterAnwender }
import org.h2.jdbc.JdbcSQLException
import org.scalatest.Matchers._
import org.scalatest._
import Assertions._

import scala.concurrent.{ Await, Future }
import play.api.{ Environment, Mode }
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnauthorizedException

import scala.concurrent.duration._

/**
 * Tests for the LeiterEntity
 */
class LeiterTest extends AsyncWordSpec {

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

  val uaLeiter = new Leiter(db.dal.getLeiterOfById(PK[BetriebEntity](11L), PK[AnwenderEntity](1L)), db)
  val leiter = new Leiter(db.dal.getLeiterOfById(PK[BetriebEntity](11L), PK[AnwenderEntity](4L)), db)

  "An unauthorized Leiter" should {
    "not be able to call dienstleistungAnbieten" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaLeiter.dienstleistungAnbieten("Haare Waschen", 3600, "Saubere Haare"), 2 seconds)
      }
    }
    "not be able to call dienstLeistungsInformationVeraendern" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaLeiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](10L), "Haare Waschen", 40, "Haare Waschen"), 2 seconds)
      }
    }
    "not be able to call betriebsInformationenVeraendern" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaLeiter.betriebsInformationenVeraendern(
          BetriebEntity("Test", "0162 123 231 0", "Mo-Fr 10-16", "test@test.com", PK[AdresseEntity](1L), Some(PK[BetriebEntity](11L))),
          AdresseEntity("Ostender Straße", "9", "13353", "berlin", Some(52.5468305), Some(13.3529318), Some(PK[AdresseEntity](1L)))
        ), 2 seconds)
      }
    }
    "not be able to call mitarbeiterAnstellen" in {
      assertThrows[UnauthorizedException] {
        Await.result(
          uaLeiter.mitarbeiterAnstellen(
            MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](1L), None)
          ),
          2 seconds
        )
      }
    }
    "not be able to call leiterAnstellen" in {
      assertThrows[UnauthorizedException] {
        Await.result(
          uaLeiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), None)),
          2 seconds
        )
      }
    }
    "not be able to call mitarbeiterEntlassen" in {
      assertThrows[UnauthorizedException] {
        Await.result(
          uaLeiter.mitarbeiterEntlassen(
            PK[MitarbeiterEntity](9L)
          ),
          2 seconds
        )
      }
    }
    "not be able to call leiterEntlassen" in {
      assertThrows[UnauthorizedException] {
        Await.result(
          uaLeiter.leiterEntlassen(PK[LeiterEntity](5L)),
          2 seconds
        )
      }
    }
    "not be able to call leiterAnzeigen" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaLeiter.leiterAnzeigen(0, 10), 2 seconds)
      }
    }
    "not be able to call dienstleistungEntfernen" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaLeiter.dienstleistungEntfernen(PK[DienstleistungEntity](4L)), 2 seconds)
      }
    }
  }

  "An authorized Leiter" can {
    "call dienstleistungAnbieten and " should {
      "be able to create DL with an existing DLTypeEntity with same Name and get the DL returned" in {
        val expectedResult = DienstleistungEntity("Saubere Haare", 3600, PK[BetriebEntity](11L), PK[DienstleistungsTypEntity](5L), Some(PK[DienstleistungEntity](10L)))
        leiter.dienstleistungAnbieten("Haare Waschen", 3600, "Saubere Haare") map {
          res =>
            {
              res should equal(expectedResult)
            }
        }
      }
      "be able to create DL and DLT by creating a DL with a new DLT name" in {
        val expectedResult = DienstleistungEntity("Schöne Haare", 3600, PK[BetriebEntity](11L), PK[DienstleistungsTypEntity](6L), Some(PK[DienstleistungEntity](10L)))
        leiter.dienstleistungAnbieten("Haare Schneiden", 3600, "Schöne Haare") map {
          res =>
            {
              res should equal(expectedResult)
            }
        }
      }
      "not be able to create an already Existing DL" in {
        assertThrows[JdbcSQLException] {
          Await.result(leiter.dienstleistungAnbieten("Haare Waschen", 40, "Haare Waschen"), 2 seconds)
        }
      }
    }
    "call dienstLeistungsInformationVeraendern and " should {
      "not be able to modify an not owned DL" in {
        leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](1L), "Haare Waschen", 40, "Haare Waschen") map {
          affectedRows => affectedRows should be(0)
        }
      }
      "not be able to modify an not existing DL" in {
        leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](100L), "Haare Waschen", 40, "Haare Waschen") map {
          affectedRows => affectedRows should be(0)
        }
      }
    }
  }
}