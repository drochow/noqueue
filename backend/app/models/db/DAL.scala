package models.db

import slick.driver.JdbcProfile

/** The Data Access Layer contains all components and a driver */
class DAL(val driver: JdbcProfile)
    extends AnwenderComponent
    with AdresseComponent
    with BetriebComponent
    with DienstleistungComponent
    with DienstleistungsTypComponent
    with LeiterComponent
    with MitarbeiterComponent
    with WarteschlangenPlatzComponent
    with DriverComponent {
  import driver.api._

  def runScript(location: String) =
    sqlu"""
       RUNSCRIPT FROM $location;
      """

  def dropAllObjectsForTestDB() =
    sqlu"""
          DROP ALL OBJECTS;
        """

  def create =
    (anwenders.schema
      ++ adresses.schema
      ++ betriebe.schema
      ++ dienstleistungen.schema
      ++ dienstleistungsTypen.schema
      ++ mitarbeiters.schema
      ++ leiters.schema
      ++ warteschlangenplaetze.schema).create.transactionally
}
