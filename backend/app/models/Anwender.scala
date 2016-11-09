package models

import slick.lifted.TableQuery

import scala.concurrent.Future

/**
 * Created by anwender on 02.11.2016.
 */
case class Anwender(
    anwenderId: Long,
    nutzerEmail: String,
    password: String,
    nutzerName: String,
    adresseId: Long //@todo Adresse || Long
) {
}
//
object AnwenderDAOFakeDB extends FakeDB.FakeTable[Anwender](FakeDB.anwenders.table, FakeDB.anwenders.incr) { // extends TableQuery(Anwender) {

  import FakeDB.anwenders

  //def getByNutzerEmail(): Anwender
  //def getById(): Anwender
  //def getByNutzerName(): Anwender
  def insert(
    nutzerEmail: String,
    password: String,
    nutzerName: String,
    straße: String,
    hausNummer: String,
    plz: String,
    stadt: String
  ): Future[(Long, Anwender)] = Future.successful {
    val newAdresseId = AdresseD.insertOrFind(straße, hausNummer, plz, stadt).value.get.get._1
    anwenders.insert(Anwender(_, nutzerEmail, password, nutzerName, newAdresseId))
  }
  //def save(anwender: Anwender)
  //def delete(anwender: Anwender)
}