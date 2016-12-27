package models

import java.sql.Timestamp

import models.db._
import slick.dbio.{ DBIO, DBIOAction }

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by David on 29.11.16.
 */
class Leiter(val leiterAction: DBIO[(LeiterEntity, BetriebEntity, Anwender)]) extends Base {

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
    for {
      betriebAndDLT <- Future.sequence(Seq(betrieb, db.run(dal.getDlTById(dienstleistungstypPK))))
      dienstleistung <- db.run(dal.insert(DienstleistungEntity(
        kommentar,
        "",
        "",
        betriebAndDLT(0).asInstanceOf[BetriebEntity].id.get,
        betriebAndDLT(1).asInstanceOf[DienstleistungsTypEntity].id.get
      )))
    } yield (dienstleistung)
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
