import models.db.{Anwender, DAL, PK}
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatestplus.play._
import slick.driver.H2Driver
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by anwender on 19.11.2016.
 */

class AccountSpec extends PlaySpec {
  val bspAnwenders = List(
    Anwender("1@example.com", "Uno", "Nomber1"),
    Anwender("2@example.com", "Beta", "BetaM"),
    Anwender("3@example.com", "Three", "Tri")
  )
  val db = H2DB.db
  val dal = H2DB.dal
  //initilisation of our dal, if we're using a h2-DB in Memory we don't need to set that up
  db.run(dal.create) onComplete {
    case Success(_) => "wuhu"
    case Failure(e) => fail(e)
  }
  //print(x)
  "Our DAL" must {
    "insert items" in {
      db.run(dal.insert(bspAnwenders(0))) onComplete {
        case Success(anw) =>
          anw.nutzerName should be(bspAnwenders(0).nutzerName)
          anw.id should be(Some(PK(1)))
        case Failure(e) => fail("can't read " + e)
      }
    }
    "not give the same id to later inserted items" in {
      db.run(dal.insert(bspAnwenders(1))) onComplete {
        case Success(anw) =>
          anw.nutzerName should be(bspAnwenders(1).nutzerName)
          anw.id should not be (Some(PK(1)))
        case Failure(e) => fail("can't read " + e)
      }
    }
    "isert items so that they are unique" in {
      db.run(dal.insert(bspAnwenders(0))) onComplete {
        case Success(anw) => fail("this should not have happened")
        //this is what we want
        //case Failure(e) => if(e.contains("correctExc")) succeed //@todo intecept the correct Exc
      }
    }
    "get all items" in {
      //@todo implement this test
    }
    "update whole items" in {
      //@todo implement this test
    }
    "update items partially" in {
      //@todo implement this test
    }
    "delete items" in {
      //@todo implement this test
    }
  }
}

object H2DB {
  val db: Database = Database.forConfig("h2")
  val dal: DAL = new DAL(H2Driver)
}