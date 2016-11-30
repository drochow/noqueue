package models

import java.sql.Timestamp

import models.db._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by David on 29.11.16.
 */
class Leiter(val leiter: Future[LeiterEntity]) extends Base {

  lazy val anwender: Future[AnwenderEntity] = for {
    lt <- leiter
    anw <- db.run(dal.getAnwenderById(lt.anwenderId))
  } yield (anw)

  lazy val betrieb: Future[BetriebEntity] = for {
    lt <- leiter
    btr <- db.run(dal.getBetriebById(lt.betriebId))
  } yield (btr)

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

  def dienstleistungAnbieten(dienstleistungstypPK: PK[DienstleistungsTypEntity], dauer: Int, kommentar: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
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

  def mitarbeiterAnstellen(mitarbeiterPK: PK[MitarbeiterEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

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
