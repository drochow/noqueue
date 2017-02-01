import java.io.File
import java.sql.Timestamp

import models._
import models.db.{ DienstleistungEntity, _ }
import org.scalatest.Matchers._
import org.scalatest._
import play.api.Mode
import play.api.inject.guice.GuiceApplicationBuilder
import utils._

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

/**
 * Tests for the LeiterEntity
 */
class MitarbeiterTest extends AsyncWordSpec {

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

  val uaMitarbeiter = new Mitarbeiter(db.dal.getMitarbeiterOfById(PK[BetriebEntity](11L), PK[AnwenderEntity](1L)), db)
  val mitarbeiter = new Mitarbeiter(db.dal.getMitarbeiterOfById(PK[BetriebEntity](11L), PK[AnwenderEntity](4L)), db)
  val anw1 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](13L)), db)
  val anw2 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](14L)), db)
  val anw3 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](15L)), db)
  val anw4 = new Anwender(db.dal.getAnwenderWithAdress(PK[AnwenderEntity](16L)), db)

  "An unauthorized Mitarbeiter" should {
    "not be able to call wspBearbeitungBeginnen" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaMitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](5L)), awaitDuration)
      }
    }
    "not be able to call wspBearbeitungBeenden" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaMitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](5L)), awaitDuration)
      }
    }
    "not be able to call warteSchlangeAnzeigen" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaMitarbeiter.warteSchlangeAnzeigen(), awaitDuration)
      }
    }
    "not be able to call mitarbeiterAnwesenheitVeraendern" in {
      assertThrows[UnauthorizedException] {
        Await.result(uaMitarbeiter.mitarbeiterAnwesenheitVeraendern(false), awaitDuration)
      }
    }
  }
  "An authorized Mitarbeiter" can {
    "access betrieb and" should {
      "get the correct betrieb returned" in {
        mitarbeiter.betrieb map {
          b => b.id.get should equal(PK[BetriebEntity](11L))
        }
      }
    }
    "access anwender and" should {
      "get the correct anwender returned" in {
        mitarbeiter.anwender map {
          a => a.id.get should equal(PK[AnwenderEntity](4L))
        }
      }
    }
    "access leiter and" should {
      "get the correct mitarbeiter returned" in {
        mitarbeiter.mitarbeiter map {
          m => m.id.get should equal(PK[LeiterEntity](9L))
        }
      }
    }
    "call wspBearbeitungBeginnen and" should {
      "not be able to start a not existing WSP" in {
        mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](100L)) map {
          r => r should be(false)
        }
      }
      "not be able to start a not owned WSP" in {
        mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](5L)) map {
          r => r should be(false)
        }
      }
      "not be able to start a WSP that already has started" in {
        for {
          //          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          b1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L)) map {
            r => r should be(true)
          }
          b2 <- assertThrows[AlreadWorkingOnAWspException] {
            Await.result(mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L)), awaitDuration)
          }
        } yield b2
      }
      "not be able to start a WSP that is not the first one" in {
        for {
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          b1 <- assertThrows[NotFirstWspException] {
            Await.result(mitarbeiter.wspBearbeitungBeginnen(wsp3.id.get), awaitDuration)
          }
        } yield b1
      }
      "be able to start the first WSP if it has not started yet" in {
        mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L)) map {
          r => r should be(true)
        }
      }
    }
    "call wspBearbeitungBeenden and" should {
      "not be able to finish a WSP that has not started yet" in {
        assertThrows[NotWorkingOnTisWSPException] {
          Await.result(mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](13L)), awaitDuration)
        }
      }
      "not be able to finish a WSP that he does not own" in {
        assertThrows[NotWorkingOnTisWSPException] {
          Await.result(mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](5L)), awaitDuration)
        }
      }
      "be able to finish a WSP that has started" in {
        for {
          b1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          b2 <- mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](13L)) map {
            r => r should be(true)
          }
        } yield b2

      }
    }
    "call mitarbeiterAnwesenheitVeraendern and" should {
      "be able to change Attendance" in {
        mitarbeiter.mitarbeiterAnwesenheitVeraendern(false) map {
          r => r should be(true)
        }
      }
      "be able to change Attendance multiple times" in {
        for {
          a1 <- mitarbeiter.mitarbeiterAnwesenheitVeraendern(false)
          a2 <- mitarbeiter.mitarbeiterAnwesenheitVeraendern(false)
          a3 <- mitarbeiter.mitarbeiterAnwesenheitVeraendern(true)
          a4 <- mitarbeiter.mitarbeiterAnwesenheitVeraendern(false)
        } yield (a1 && a2 && a3 && a4 should be(true))
      }
    }
    "call warteSchlangeAnzeigen and" should {
      "be able to show with 1 entry the correct time before it has begun" in {
        for {
          now <- Future.successful(System.currentTimeMillis() / 1000 + 40L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
          //adding 1 second
          res <- Future.successful(list._2.getTime == now || list._2.getTime == now + 1)
        } yield res should be(true)
      }
      "be able to show with 1 entry the correct time after it has begun" in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          now <- Future.successful(System.currentTimeMillis() / 1000 + 40L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
          res <- Future.successful(list._2.getTime == now || list._2.getTime == now + 1)
        } yield res should be(true)
      }
      "be able to show with 0 entries the correct time" in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          a2 <- mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](13L))
          expectedTime <- Future.successful(System.currentTimeMillis() / 1000)
          list <- mitarbeiter.warteSchlangeAnzeigen()
          res <- Future.successful(list._2.getTime == expectedTime || list._2.getTime == expectedTime + 1)
        } yield res should be(true)
      }
      "be able to show with 5 entries the correct time after one has begun" in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          expectedTime <- Future.successful(System.currentTimeMillis() / 1000 + 40L + 50L + 30L + 40L + 40L)
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
          res <- Future.successful(list._2.getTime == expectedTime || list._2.getTime == expectedTime + 1)
        } yield res should be(true)
      }
      "be able to show with 5 entries the correct time " in {
        for {
          expectedTime <- Future.successful(System.currentTimeMillis() / 1000 + 40L + 50L + 30L + 40L + 40L)
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
          res <- Future.successful(list._2.getTime == expectedTime || list._2.getTime == expectedTime + 1)
        } yield res should be(true)
      }
      "be able to return a sequence with 0 entries " in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          a2 <- mitarbeiter.wspBearbeitungBeenden(PK[WarteschlangenPlatzEntity](13L))
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1.length shouldBe (0)
      }
      "be able to return a sequence with 1 entry " in {
        mitarbeiter.warteSchlangeAnzeigen() map {
          r => r._1.length should be(1)
        }
      }
      "be able to return a sequence with 5 entries " in {
        for {
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1.length shouldBe (5)
      }
      "be able to return the correct order with 5 entries(0)" in {
        for {
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1(0)._1 shouldBe (PK[WarteschlangenPlatzEntity](13L))
      }
      "be able to return the correct order with 5 entries(1)" in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1(1)._1 shouldBe (wsp1.id.get)
      }
      "be able to return the correct order with 5 entries(2)" in {
        for {
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1(2)._1 shouldBe (wsp2.id.get)
      }
      "be able to return the correct order with 5 entries(3)" in {
        for {
          a1 <- mitarbeiter.wspBearbeitungBeginnen(PK[WarteschlangenPlatzEntity](13L))
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1(3)._1 shouldBe (wsp3.id.get)
      }
      "be able to return the correct order with 5 entries(4)" in {
        for {
          wsp1 <- anw1.wsFuerBestimmtenMitarbeiterBeitreten(4L, 9L)
          wsp2 <- anw2.wsFuerBestimmtenMitarbeiterBeitreten(5L, 9L)
          wsp3 <- anw3.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          wsp4 <- anw4.wsFuerBestimmtenMitarbeiterBeitreten(6L, 9L)
          list <- mitarbeiter.warteSchlangeAnzeigen()
        } yield list._1(4)._1 shouldBe (wsp4.id.get)
      }
    }
  }
}