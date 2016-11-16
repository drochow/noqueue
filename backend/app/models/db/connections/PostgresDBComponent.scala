package models.db.connections

import slick.driver.PostgresDriver

/**
 * Postgtes DB Component for PostgresSQL Database
 */
trait PostgresDBComponent extends DBComponent {

  val driver = PostgresDriver

  import driver.api._

  val db: Database = PostgresDB.connectionPool

}

private[connections] object PostgresDB {

  import slick.driver.PostgresDriver.api._

  val connectionPool = Database.forConfig("postgres")

}