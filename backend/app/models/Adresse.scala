package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
case class Adresse(strasse: String, hausNummer: String, plz: String, stadt: String, id: Option[PK[Adresse]] = None) {

}
