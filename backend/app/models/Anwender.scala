package models

import akka.actor.FSM.Failure
import models.db._

import scala.concurrent.Future
import scala.util.Success

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by David on 29.11.16.
 */
//@todo add return types to methods when implemented (should all return futures)
class Anwender(val anwender: Future[AnwenderEntity]) extends UnregistrierterAnwender {

  /**
   * Adresse of Anwender with lazy initialization
   */
  lazy val adresse: Future[AdresseEntity] = for {
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

  def namenAendern(nutzerName: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def emailAendern(nutzerEmail: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def passwordAendern(password: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsBeitreten(dl: Future[DienstleistungEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsBeitreten(dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsFuerMitarbeiterBeitreten(mitarbeiter: Future[MitarbeiterEntity], dl: Future[DienstleistungEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsFuerMitarbeiterBeitreten(mitarbeiterPrimaryKey: PK[MitarbeiterEntity], dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsVerlassen(wsp: Future[WarteSchlangenPlatzEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def betriebBewerten(betrieb: Future[BetriebEntity], bewertung: Int) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def betriebBewerten(betriebPrimaryKey: PK[BetriebEntity], bewertung: Int) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

}
