import java.io.{ BufferedReader, File, FileReader }

import models.db.{ DienstleistungEntity, _ }
import models.{ DB, Leiter, UnregistrierterAnwender }
import org.h2.jdbc.JdbcSQLException
import org.scalatest.Matchers._
import org.scalatest._
import Assertions._

import scala.concurrent.{ Await, Future }
import play.api.{ Environment, Mode }
import play.api.inject.guice.GuiceApplicationBuilder
import utils.{ OneLeiterRequiredException, UnauthorizedException }

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
      "not  be able to modify an DL to the same Data set as an other existing DL" in {
        assertThrows[JdbcSQLException] {
          Await.result(
            leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](5L), "Haare Waschen", 40, "Haare Waschen"),
            2 seconds
          )
        }
      }
      "be able to modify an DL changing Dauer and Kommentar" in {
        leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](5L), "Haare Waschen", 60, "Tolle Haare") map {
          affectedRows => affectedRows should be(1)
        }
      }
      "be able to modify an DL changing Name of Type" in {
        leiter.dienstleistungsInformationVeraendern(PK[DienstleistungEntity](5L), "Tolle nacken Massage", 40, "Haare Waschen") map {
          affectedRows => affectedRows should be(1)
        }
      }
    }

    "call mitarbeiterAnstellen and " should {
      "not be able to hire a not existing Anwender" in {
        assertThrows[JdbcSQLException] {
          Await.result(
            leiter.mitarbeiterAnstellen(MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](100L), None)),
            2 seconds
          )
        }
      }
      "not be able to hire a already employed Mitarbeiter" in {
        assertThrows[JdbcSQLException] {
          Await.result(
            leiter.mitarbeiterAnstellen(MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](4L), None)),
            2 seconds
          )
        }
      }
      "be able to hire a valid Mitarbeiter" in {
        leiter.mitarbeiterAnstellen(MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](1L), None)) map {
          m => m should equal(MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](1L), Some(PK[MitarbeiterEntity](15L))))
        }
      }
      "be able to hire a Mitarbeiter with autocorrected BetriebId" in {
        leiter.mitarbeiterAnstellen(MitarbeiterEntity(true, PK[BetriebEntity](1L), PK[AnwenderEntity](1L), None)) map {
          m => m should equal(MitarbeiterEntity(true, PK[BetriebEntity](11L), PK[AnwenderEntity](1L), Some(PK[MitarbeiterEntity](15L))))
        }
      }
    }

    "call mitarbeiterEntlassen and " should {
      "not be able to fire a not existing Mitarbeiter" in {
        leiter.mitarbeiterEntlassen(PK[MitarbeiterEntity](100L)) map {
          success => success should be(false)
        }
      }
      "not be able to fire a existing Mitarbeiter of another Betrieb" in {
        leiter.mitarbeiterEntlassen(PK[MitarbeiterEntity](1L)) map {
          success => success should be(false)
        }
      }
      "be able to fire a existing (last) Mitarbeiter of this Betrieb" in {
        leiter.mitarbeiterEntlassen(PK[MitarbeiterEntity](4L)) map {
          success => success should be(false)
        }
      }
    }
    "call leiterEntlassen and " should {
      "not be able to fire a not existing Leiter" in {
        leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), None)) flatMap {
          leiterE =>
            leiter.leiterEntlassen(PK[LeiterEntity](100L)) map {
              success => success should be(0)
            }
        }
      }
      "not be able to fire a existing Leiter of another Betrieb" in {
        leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), None)) flatMap {
          leiterE =>
            leiter.leiterEntlassen(PK[LeiterEntity](1L)) map {
              success => success should be(0)
            }
        }
      }
      "not be able to fire the last existing Leiter of this Betrieb" in {
        assertThrows[OneLeiterRequiredException] {
          Await.result(leiter.leiterEntlassen(PK[LeiterEntity](4L)), 2 seconds)
        }
      }
      "be able to fire existing Leiter of this Betrieb who is not th last leiter" in {
        leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), None)) flatMap {
          leiterE =>
            leiter.leiterEntlassen(PK[LeiterEntity](11L)) map {
              success => success should be(1)
            }
        }
      }
    }
    "call leiterEinstellen and" should {
      "not be able to hire a not existing Anwender" in {
        assertThrows[JdbcSQLException] {
          Await.result(
            leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](100L), PK[BetriebEntity](11L), None)),
            2 seconds
          )
        }
      }
      "not be able to hire a Anwender who already is a Leiter" in {
        assertThrows[JdbcSQLException] {
          Await.result(
            leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](4L), PK[BetriebEntity](11L), None)),
            2 seconds
          )
        }
      }
      "be able to hire a Anwender who is not a Leiter yet" in {
        leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), None)) map {
          leiterE => leiterE should equal(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), Some(PK[LeiterEntity](11L))))
        }
      }
      "be able to hire a Anwender who is not a Leiter yet with autocorrected BetriebID" in {
        leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](1L), None)) map {
          leiterE => leiterE should equal(LeiterEntity(PK[AnwenderEntity](1L), PK[BetriebEntity](11L), Some(PK[LeiterEntity](11L))))
        }
      }
    }
    "call leiterAnzeigen and" should {
      "be able to see 1 Leiters on page 1 with pagesize 10" in {
        leiter.leiterAnzeigen(0, 10) map {
          leiterSeq => leiterSeq.length should be(1)
        }
      }
      "be able to see 0 Leiters on page 2 with pagesize 10" in {
        leiter.leiterAnzeigen(1, 10) map {
          leiterSeq => leiterSeq.length should be(0)
        }
      }
      "be able to see 0 Leiters on page 100 with pagesieze 10" in {
        leiter.leiterAnzeigen(99, 10) map {
          leiterSeq => leiterSeq.length should be(0)
        }
      }
      "be able to see 5 Leiters on page 2 after inserting 12 with pagesize 5" in {
        for {
          l1 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](6L), PK[BetriebEntity](11L), None))
          l2 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](7L), PK[BetriebEntity](11L), None))
          l3 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](8L), PK[BetriebEntity](11L), None))
          l4 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](9L), PK[BetriebEntity](11L), None))
          l5 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](10L), PK[BetriebEntity](11L), None))
          l6 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](11L), PK[BetriebEntity](11L), None))
          l7 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](12L), PK[BetriebEntity](11L), None))
          l8 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](13L), PK[BetriebEntity](11L), None))
          l9 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](14L), PK[BetriebEntity](11L), None))
          l10 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](15L), PK[BetriebEntity](11L), None))
          l11 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](16L), PK[BetriebEntity](11L), None))
          l12 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](17L), PK[BetriebEntity](11L), None))
          res <- leiter.leiterAnzeigen(1, 5) map {
            leiterSeq => leiterSeq.length should be(5)
          }
        } yield res
      }
      "be able to see 10 Leiters on page 1 after inserting 12 with pagesize 10" in {
        for {
          l1 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](6L), PK[BetriebEntity](11L), None))
          l2 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](7L), PK[BetriebEntity](11L), None))
          l3 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](8L), PK[BetriebEntity](11L), None))
          l4 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](9L), PK[BetriebEntity](11L), None))
          l5 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](10L), PK[BetriebEntity](11L), None))
          l6 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](11L), PK[BetriebEntity](11L), None))
          l7 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](12L), PK[BetriebEntity](11L), None))
          l8 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](13L), PK[BetriebEntity](11L), None))
          l9 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](14L), PK[BetriebEntity](11L), None))
          l10 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](15L), PK[BetriebEntity](11L), None))
          l11 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](16L), PK[BetriebEntity](11L), None))
          l12 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](17L), PK[BetriebEntity](11L), None))
          res <- leiter.leiterAnzeigen(0, 10) map {
            leiterSeq => leiterSeq.length should be(10)
          }
        } yield res
      }
      "be able to see 3 Leiters on page 2 after inserting 12 with pagesize 10" in {
        for {
          l1 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](6L), PK[BetriebEntity](11L), None))
          l2 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](7L), PK[BetriebEntity](11L), None))
          l3 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](8L), PK[BetriebEntity](11L), None))
          l4 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](9L), PK[BetriebEntity](11L), None))
          l5 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](10L), PK[BetriebEntity](11L), None))
          l6 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](11L), PK[BetriebEntity](11L), None))
          l7 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](12L), PK[BetriebEntity](11L), None))
          l8 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](13L), PK[BetriebEntity](11L), None))
          l9 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](14L), PK[BetriebEntity](11L), None))
          l10 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](15L), PK[BetriebEntity](11L), None))
          l11 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](16L), PK[BetriebEntity](11L), None))
          l12 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](17L), PK[BetriebEntity](11L), None))
          res <- leiter.leiterAnzeigen(1, 10) map {
            leiterSeq => leiterSeq.length should be(3)
          }
        } yield res
      }
      "be able to see 0 Leiters on page 3 after inserting 12 with pagesize 10" in {
        for {
          l1 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](6L), PK[BetriebEntity](11L), None))
          l2 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](7L), PK[BetriebEntity](11L), None))
          l3 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](8L), PK[BetriebEntity](11L), None))
          l4 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](9L), PK[BetriebEntity](11L), None))
          l5 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](10L), PK[BetriebEntity](11L), None))
          l6 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](11L), PK[BetriebEntity](11L), None))
          l7 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](12L), PK[BetriebEntity](11L), None))
          l8 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](13L), PK[BetriebEntity](11L), None))
          l9 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](14L), PK[BetriebEntity](11L), None))
          l10 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](15L), PK[BetriebEntity](11L), None))
          l11 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](16L), PK[BetriebEntity](11L), None))
          l12 <- leiter.leiterEinstellen(LeiterEntity(PK[AnwenderEntity](17L), PK[BetriebEntity](11L), None))
          res <- leiter.leiterAnzeigen(2, 10) map {
            leiterSeq => leiterSeq.length should be(0)
          }
        } yield res
      }
    }
  }
}