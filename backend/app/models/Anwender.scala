package models

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

//@todo add return types to methods when implemented (should all return futures)
class Anwender(val anwender: Future[AnwenderEntity]) extends UnregistrierterAnwender {

  /**
   * Adresse of Anwender with lazy initialization
   */
  lazy val adresse: Future[AdresseEntity] =
    for {
      anw <- anwender
      adr <- db.run(dal.getAdresseById(anw.adresseId.get)) map { adresse => adresse }
    } yield (adr)

  //@todo implement lazy val mitarbeiterVon wich is a Future of a Sequence of MitarbeiterEntities

  //@todo implement lazy val leiterVon wich is a Future of a Sequence of LeiterEntities

  //@todo implement lazy val warteSchlangenPlaetze wich is a Future of a Sequence of WartesSchlangenPlatzEntities

  def abmelden() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def accountLoeschen() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def profilBearbeiten(nutzerName: Option[String], nutzerEmail: Option[String], adress: Option[Option[AdresseEntity]]) = {
    for {
      anw <- anwender
      adr:Option[Option[PK[AdresseEntity]]] <- if(adress.isEmpty) None else if(adress.get.isEmpty) Some(None) else db.run(dal.findOrInsert(adress.get.get)).map(_.id)
      updated <-  db.run(dal.update(
        new AnwenderEntity(
          nutzerEmail.getOrElse(anw.nutzerEmail),
          anw.password,
          nutzerName.getOrElse(anw.nutzerName),
          adr.getOrElse(anw.adresseId),
          anw.id)))
    } yield(updated)
  }


  def passwordAendern(password: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsBeitreten(dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsFuerMitarbeiterBeitreten(mitarbeiterPrimaryKey: PK[MitarbeiterEntity], dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me Future[WarteschlangenPlatzEntity]
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsVerlassen(wsp: PK[WarteSchlangenPlatzEntity]) = {
    //@todo implement me and return Future[Boolean]
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def betriebBewerten(betriebPrimaryKey: PK[BetriebEntity], bewertung: Int) = {
    //@todo maybe implement me and return Future[Boolean]
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def betriebErstellen(adresse: AdresseEntity, name: String, tel: String, oeffnungszeiten: String, kontaktEmail: String) = {
    //@todo implement me and return Future[(BetriebEntity, LeiterEntity)]
    throw new NotImplementedError("Not implemented yet, implement it")
  }

}
