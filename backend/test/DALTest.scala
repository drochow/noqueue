import java.sql.SQLException

import models.db.{AnwenderEntity, DAL, PK}
import org.h2.jdbc.JdbcSQLException
import org.scalatest.{AsyncFlatSpec, AsyncWordSpec}
import org.scalatest.Matchers._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatestplus.play.MixedPlaySpec
import slick.driver.H2Driver
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by anwender on 19.11.2016.
 */

class AccountSpec extends AsyncWordSpec {
  val bspAnwenders = List(
    AnwenderEntity("1@example.com", "Uno", "Nomber1"),
    AnwenderEntity("2@example.com", "Beta", "BetaM"),
    AnwenderEntity("3@example.com", "Three", "Tri")
  )
  val db = H2DB.db
  val dal = H2DB.dal

  var test = "before creation of the DAL"
  //initilisation of our dal, if we're using a h2-DB in Memory we don't need to set that up
  db.run(dal.create) map {
    _ => { test = "after successful creation of the DAL" }
  } recover {
    case err: Throwable => test = "after failed creation of the DAL"
  }
  "Our DAL " + test must {
    "insert items" in {
      db.run(dal.insert(bspAnwenders(0))) map {
        anw: AnwenderEntity =>
          anw.nutzerName should be(bspAnwenders(0).nutzerName)
          anw.id should be(Some(PK(1)))
      }
    }
    "give the a different id to later inserted items" in {
      db.run(dal.insert(bspAnwenders(1))) map {
        anw: AnwenderEntity =>
          anw.nutzerName should be(bspAnwenders(1).nutzerName)
          anw.id should not be (Some(PK(1)))
      }
    }
    "not insert items if they are not unique" in {
      db.run(dal.insert(bspAnwenders(0))) map {
        anw: AnwenderEntity =>
          fail("this should not have happened")
      } recover {
        case err: SQLException =>
          succeed //succeed //@todo intecept the correct Exc
      }
    }
    /*"get all items" in {
  1 should be(2)
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
  }*/
  }
}

object H2DB {
  val db: Database = Database.forConfig("h2")
  val dal: DAL = new DAL(H2Driver)
}