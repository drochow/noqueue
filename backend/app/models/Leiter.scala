package models

import java.sql.Timestamp

import models.db._
import slick.dbio.{ DBIO, DBIOAction }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by David on 29.11.16.
 */
class Leiter(val leiterAction: DBIO[(LeiterEntity, BetriebEntity, AnwenderEntity)]) extends Base {

  lazy val leiter = leiterComposition map (_._1)

  lazy val betrieb = leiterComposition map (_._2)

  lazy val anwender = leiterComposition map (_._3)

  lazy private val leiterComposition = db.run(leiterAction)

  def betriebsInformationenVeraendern(betrieb: Future[BetriebEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def betriebsInformationenVeraendern(
    betriebPrimaryKey: PK[BetriebEntity],
    adressePrimaryKey: PK[AdresseEntity],
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String
  ) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def dienstleistungAnbieten(
    dienstleistungstypPK: PK[DienstleistungsTypEntity],
    name: String,
    dauer: Int,
    kommentar: String
  ): Future[DienstleistungEntity] = {
    throw new NotImplementedError("Not Implemented yet, fix todo")
    //@todo muss angepasst werden wir wollten die dienstleistungen mit mitarbeitern verknüpfen und nur indirekt
    //      über die Betriebe, es wir dalso eine Optionale MitarbeiterID übergeben und es wird die DL für den Mitarbeiter angelegt
    //      wenn keine Übergeben wird wird für jeden mitarbeiter die DL hinzugefügt.
    //
    //    for {
    //      betriebAndDLT <- Future.sequence(Seq(betrieb, db.run(dal.getDlTById(dienstleistungstypPK))))
    //      dienstleistung <- db.run(dal.insert(DienstleistungEntity(
    //        kommentar,
    //        "",
    //        betriebAndDLT(0).asInstanceOf[BetriebEntity].id.get,
    //        betriebAndDLT(1).asInstanceOf[DienstleistungsTypEntity].id.get
    //      )))
    //    } yield (dienstleistung)
  }

  def dienstleistungsInformationVeraendern(diensleistungPK: PK[DienstleistungEntity], dauer: Int, kommentar: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def dienstleistungEntfernen(dienstleistungPK: PK[DienstleistungEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def dienstleistungsTypErstellen(name: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def dlMitAktionAnbieten(dienstleistungPK: PK[DienstleistungEntity], aktion: String, von: Timestamp, bis: Timestamp) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  //  def mitarbeiterAnstellen(mitarbeiterPK: PK[MitarbeiterEntity]): Future[MitarbeiterEntity] = {
  //
  //  }

  def mitarbeiterEntlassen(mitarbeiterPK: PK[MitarbeiterEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def tagZuDLHinzufuegen(dienstleistungPK: PK[DienstleistungEntity], tag: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def tagAusDLEntfernen(dienstleistungPK: PK[DienstleistungEntity], tag: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }
}
