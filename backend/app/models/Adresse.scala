package models

import scala.concurrent.Future

/**
 * Created by anwender on 02.11.2016.
 */
case class Adresse(
    AdresseId: Long,
    straße: String,
    hausNummer: String,
    plz: String,
    stadt: String
) {

}

object AdresseDAO{
  import FakeDB.adressen
  def insertOrFind(straße: String, hausNummer: String, plz: String, stadt: String) = Future.successful {
    adressen.insert(Adresse(_, straße, hausNummer, plz, stadt))
  }
}