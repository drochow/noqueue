package models.db.connections

import slick.driver.JdbcProfile

/**
 * Basic Trait to deliver Database Agnostic
 */
trait DBComponent {

  val driver: JdbcProfile

  import driver.api._

  val db: Database

}