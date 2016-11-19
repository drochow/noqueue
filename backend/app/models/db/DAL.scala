package models.db

import slick.driver.JdbcProfile

/** The Data Access Layer contains all components and a driver */
class DAL(val driver: JdbcProfile)
    extends AnwenderComponent with AdresseComponent with DriverComponent {
  import driver.api._

  def create =
    (anwenders.schema ++ adresses.schema).create
}
