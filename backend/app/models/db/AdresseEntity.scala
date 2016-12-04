package models.db

/**
 * Created by anwender on 25.11.2016.
 */
case class AdresseEntity(strasse: String, hausNummer: String, plz: String, stadt: String, id: Option[PK[AdresseEntity]] = None) {

}
