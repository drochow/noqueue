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


class Anwenders(tag: Tag) extends Table[Anwender](tag, "ANWENDER") {
    def id = column[Long]("ANW_ID", O.PrimaryKey, O.AutoInc)  
    def adresseID = column[Long]("ADR_ID")  
    def nutzerEmail = column[String]("NUTZEREMAIL")  
    def password = column[String]("PASSWORD")  
    def nutzerName = column[String]("NUTZERNAME")  
    
    def * = (id.?, adresseID.?, nutzerEmail, password, nutzerName) <> (Anwender.tupled, Anwender.unapply)  
    
    def adresse: ForeignKeyQuery[Adresses, Adresse] = foreignKey("ADR_FK", adresseID, TableQuery[Adresses])(_.id)
}

//
object AnwenderDAO { // extends TableQuery(Anwender) {

  import FakeDB.anwenders

  //@TODO Sean

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
    val newAdresseId = AdresseDAO.insertOrFind(straße, hausNummer, plz, stadt).value.get.get._1
    anwenders.insert(Anwender(_, nutzerEmail, password, nutzerName, newAdresseId))
  }
  //def save(anwender: Anwender)
  //def delete(anwender: Anwender)
}
