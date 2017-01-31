import java.io.File
import javax.security.auth.login.CredentialException

import api.jwt.{ JwtUtil, TokenExpiration, TokenPayload }
import models._
import models.db._
import org.apache.http.auth.InvalidCredentialsException
import org.joda.time.DateTime
import org.scalatest.Matchers._
import org.scalatest._
import play.api.Mode
import play.api.inject.guice.GuiceApplicationBuilder
import utils._

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

/**
 * Tests for the LeiterEntity
 */
class UnregistrierterAnwenderTest extends AsyncWordSpec {

  val awaitDuration: Duration = 1 seconds

  override def withFixture(test: NoArgAsyncTest) = { // Define a shared fixture
    try {
      // Shared setup (run at beginning of each test)
      val fill = new File("./test/fill.sql")
      //Awaiting  to ensure that db is fully cleaned up and filled  before test is started
      Await.result(db.db.run(db.dal.dropAllObjectsForTestDB()), awaitDuration)
      Await.result(db.db.run(db.dal.create), awaitDuration)
      Await.result(db.db.run(db.dal.runScript(fill.getAbsolutePath)), awaitDuration)
      test()
    } finally {
      // Shared cleanup (run at end of each test)
    }
  }

  val application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build

  val db: DB = application.injector.instanceOf[DB]

  val ua = new UnregistrierterAnwender(db);
  "An UnregistrierterAnwender" can {
    "call registireren and" should {
      "not be able to register with an email that does not already exist" in {
        assertThrows[EmailAlreadyInUseException] {
          Await.result(ua.registrieren(AnwenderEntity("dummy1@gmail.com", "Myt3stpw!", "mytest1")), awaitDuration)
        }
      }
      "not be able to register with an nutzerName that does not already exist" in {
        assertThrows[NutzerNameAlreadyInUseException] {
          Await.result(ua.registrieren(AnwenderEntity("dummy200@gmail.com", "Myt3stpw!", "dummy1")), awaitDuration)
        }
      }
      "be able to register with valid data" in {
        ua.registrieren(AnwenderEntity("dummy200@gmail.com", "Myt3stpw!", "dummy200")) map {
          a => a.id.get should be(PK[AnwenderEntity](20L))
        }
      }
    }
    "call betriebAnzeigen and" should {
      "not be able to call a not existing Betrieb" in {
        assertThrows[NoSuchElementException] {
          Await.result(ua.betriebAnzeigen(PK[BetriebEntity](100L)), awaitDuration)
        }
      }
      "be able to call a existing Betrieb" in {
        ua.betriebAnzeigen(PK[BetriebEntity](1L)) map {
          bua =>
            bua._1 should equal(BetriebEntity("Drugstore 3", "+49 17623373437532", "Mo-Fr 10:00-22:00 Uhr",
              "weddingerbude3@something.de", PK[AdresseEntity](1L), Some(PK[BetriebEntity](1L))))
        }

      }
    }
    "call mitarbeiterAnzeigen and" should {
      "be able to get a empty list of Mitarbeiters from a not existing Betrieb" in {
        ua.mitarbeiterAnzeigen(PK[BetriebEntity](100L), 0, 10) map {
          r => r.length should be(0)
        }
      }
      "be able to get a list of Mitarbeiters from a not existing Betrieb with correct count" in {
        ua.mitarbeiterAnzeigen(PK[BetriebEntity](8L), 0, 10) map {
          r => r.length should be(3)
        }
      }
      "be able to get paged a list of Mitarbeiters from a not existing Betrieb with correct count(0)" in {
        ua.mitarbeiterAnzeigen(PK[BetriebEntity](8L), 0, 2) map {
          r => r.length should be(2)
        }
      }
      "be able to get paged a list of Mitarbeiters from a not existing Betrieb with correct count(1)" in {
        ua.mitarbeiterAnzeigen(PK[BetriebEntity](8L), 1, 2) map {
          r => r.length should be(1)
        }
      }
      "be able to get paged a list of Mitarbeiters from a not existing Betrieb with correct count(3)" in {
        ua.mitarbeiterAnzeigen(PK[BetriebEntity](8L), 2, 2) map {
          r => r.length should be(0)
        }
      }
    }
    "call anmelden and" should {
      "not be able to signin with invalid password" in {
        assertThrows[CredentialException] {
          Await.result(ua.anmelden("dummy1", "falsch"), awaitDuration)
        }
      }
      "not be able to signin with invalid nutzerName" in {
        assertThrows[CredentialException] {
          Await.result(ua.anmelden("test", "richtig"), awaitDuration)
        }
      }
      "be able to signin with valid credentials" in {
        for {
          reg <- ua.registrieren(AnwenderEntity("dummy200@gmail.com", "Myt3stpw!", "dummy200"))
          res <- ua.anmelden("dummy200", "Myt3stpw!") map {
            anw => anw.id.get should be(reg.id.get)
          }
        } yield res
      }
    }
    "call dienstleistungAnzeigen and" should {
      "be able to get a empty list for a not existing Betrieb" in {
        ua.dienstleistungAnzeigen(100L, 0, 10) map {
          r => r.length should be(0)
        }
      }
      "be able to get a empty list for a existing Betrieb without any Service" in {
        ua.dienstleistungAnzeigen(5L, 0, 10) map {
          r => r.length should be(0)
        }
      }
      "be able to get a list for a existing Betrieb witch correct length" in {
        ua.dienstleistungAnzeigen(11L, 0, 10) map {
          r => r.length should be(3)
        }
      }
      "be able to get a paged list for a existing Betrieb witch correct length(0)" in {
        ua.dienstleistungAnzeigen(11L, 0, 2) map {
          r => r.length should be(2)
        }
      }
      "be able to get a paged list for a existing Betrieb witch correct length(1)" in {
        ua.dienstleistungAnzeigen(11L, 1, 2) map {
          r => r.length should be(1)
        }
      }
      "be able to get a paged list for a existing Betrieb witch correct length(2)" in {
        ua.dienstleistungAnzeigen(11L, 2, 2) map {
          r => r.length should be(0)
        }
      }
    }
    "call anmeldenMitPayload and" should {
      "not be able to authenticate with a payload of a deleted user" in {
        assertThrows[NoSuchElementException] {
          Await.result(ua.anmeldenMitPayload(TokenPayload(100L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1))).anwender, awaitDuration)
        }
      }
      "not be able to authenticate with a expired payload" in {
        assertThrows[TokenExpiredException] {
          ua.anmeldenMitPayload(TokenPayload(1L, DateTime.now().withDurationAdded(-1200L, 1)))
        }
      }
      "be able to authenticate with a valid payload" in {
        ua.anmeldenMitPayload(TokenPayload(1L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1))).anwender map {
          a => a.id.get.value should be(1L)
        }
      }
    }
    "call anmeldenMitPayloadAlsMitarbeiterVon and" should {
      "not be able to authenticate with a payload of a deleted user" in {
        assertThrows[NoSuchElementException] {
          Await.result(
            ua.anmeldenMitPayloadAlsMitarbeiterVon(
            TokenPayload(100L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)
          ).anwender,
            awaitDuration
          )
        }
      }
      "not be able to authenticate with a expired payload" in {
        assertThrows[TokenExpiredException] {
          ua.anmeldenMitPayloadAlsMitarbeiterVon(TokenPayload(4L, DateTime.now().withDurationAdded(-1200L, 1)), PK[BetriebEntity](11L))
        }
      }
      "not be able to authenticate with a payload of a non employee user" in {
        assertThrows[NoSuchElementException] {
          Await.result(
            ua.anmeldenMitPayloadAlsMitarbeiterVon(
            TokenPayload(1L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)
          ).mitarbeiter,
            awaitDuration
          )
        }
      }
      "be able to authenticate with a valid payload" in {
        ua.anmeldenMitPayloadAlsMitarbeiterVon(TokenPayload(4L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)).mitarbeiter map {
          m => m.anwenderId.value should be(4L)
        }
      }
    }
    "call anmeldenMitPayloadAlsLeiterVon and" should {
      "not be able to authenticate with a payload of a deleted user" in {
        assertThrows[NoSuchElementException] {
          Await.result(
            ua.anmeldenMitPayloadAlsLeiterVon(
            TokenPayload(100L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)
          ).anwender,
            awaitDuration
          )
        }
      }
      "not be able to authenticate with a expired payload" in {
        assertThrows[TokenExpiredException] {
          ua.anmeldenMitPayloadAlsLeiterVon(TokenPayload(4L, DateTime.now().withDurationAdded(-1200L, 1)), PK[BetriebEntity](11L))
        }
      }
      "not be able to authenticate with a payload of a non employee user" in {
        assertThrows[NoSuchElementException] {
          Await.result(
            ua.anmeldenMitPayloadAlsLeiterVon(
            TokenPayload(1L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)
          ).leiter,
            awaitDuration
          )
        }
      }
      "be able to authenticate with a valid payload" in {
        ua.anmeldenMitPayloadAlsLeiterVon(TokenPayload(4L, DateTime.now().withDurationAdded(TokenExpiration.expirationDuration, 1)), PK[BetriebEntity](11L)).leiter map {
          m => m.anwenderId.value should be(4L)
        }
      }
    }
  }

}