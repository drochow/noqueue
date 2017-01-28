//import java.sql.SQLException
//
//import models.UnregistrierterAnwender
//import models.db.{ AnwenderEntity, DAL, PK }
//import org.h2.jdbc.JdbcSQLException
//import org.scalatest.{ Assertion, AsyncFlatSpec, AsyncWordSpec, FutureOutcome }
//import org.scalatest.Matchers._
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//import org.scalatestplus.play.MixedPlaySpec
//import slick.driver.H2Driver
//import slick.jdbc.JdbcBackend.Database
//
//import scala.concurrent.{ Await, Future }
//import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.util.{ Failure, Success }
//
///**
// * Created by anwender on 19.11.2016.
// */
//
//class AccountSpec extends AsyncWordSpec {
//  val bspAnwenders = List(
//    AnwenderEntity("1@example.com", "Uno", "Nomber1"),
//    AnwenderEntity("2@example.com", "Beta", "BetaM"),
//    AnwenderEntity("3@example.com", "Three", "Tri"),
//    AnwenderEntity("4@example.com", "Quadr", "Quad")
//  )
//
//  val h2 = new H2DB
//  val db = h2.db
//  val dal = h2.dal
//
//  //db-setup
//  var test = "before creation of the DAL"
//  //initilisation of our dal, if we're using a h2-DB in Memory we don't need to set that up
//  db.run(dal.create) map {
//    _ => { test = "after successful creation of the DAL" }
//  } recover {
//    case err: Throwable => test = "after failed creation of the DAL"
//  }
//
//  override def withFixture(test: NoArgAsyncTest): FutureOutcome = {
//    try test()
//    finally {
//      // clean all affected tables @todo not very clean at the moment
//      db.run(dal.anwenders.filter(_.id > PK[AnwenderEntity](-1)).delete)
//    }
//  }
//  "Our DAL " + test can {
//    "empty" should {
//      "insert items" in {
//        db.run(dal.insert(bspAnwenders(0))) map {
//          anw: AnwenderEntity =>
//            anw.nutzerName should be(bspAnwenders(0).nutzerName)
//            anw.id should be(Some(PK(1)))
//        }
//      }
//    }
//    def insertionFixture(test: Future[Assertion]): Future[Assertion] = {
//      db.run(dal.insert(bspAnwenders(0)))
//      test
//    }
//    "non-empty" should {
//      "give the a different id to later inserted items" ignore {
//        insertionFixture {
//          db.run(dal.insert(bspAnwenders(1))) map {
//            anw: AnwenderEntity =>
//              anw.nutzerName should be(bspAnwenders(1).nutzerName)
//              anw.id should not be (Some(PK(1)))
//              anw.id should not be (None)
//          }
//        }
//      }
//      "not insert non-unique Anwender" ignore {
//        insertionFixture {
//          db.run(dal.insert(bspAnwenders(0))) map {
//            _ => fail("this should not have happened")
//          } recover {
//            //this is what we want
//            case sqlExc: SQLException => succeed //@todo intecept the correct Exc
//          }
//        }
//      }
//      "update whole items" in {
//        insertionFixture {
//          db.run(dal.update(PK[AnwenderEntity](1), bspAnwenders(2))) map {
//            amountUpdated: Int =>
//              amountUpdated should be(1)
//          }
//        }
//      }
//      "update items partially" in {
//        insertionFixture {
//          db.run(dal.partialUpdate(PK[AnwenderEntity](1), Some("partPatch"), Some("partPatch@example.com"), None)).map {
//            amountUpdated: Int =>
//              amountUpdated should be(1)
//          }
//        }
//      }
//      /*"delete items" in {
//        //@todo implement this test
//      }*/
//    }
//  }
//}
//
//class ProfilPatchRequestSpec extends AsyncWordSpec {
//
//}
//
//class H2DB {
//  val db: Database = Database.forConfig("h2")
//  val dal: DAL = new DAL(H2Driver)
//}