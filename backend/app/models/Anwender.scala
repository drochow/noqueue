package models

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

//@todo add return types to methods when implemented (should all return futures)
class Anwender(val anwenderAction: DBIO[AnwenderEntity]) extends UnregistrierterAnwender {

  def this(jwtPayload: TokenPayload) = {
    this(PostgresDB.dal.getAnwenderById(PK[AnwenderEntity](jwtPayload.userId)))
  }

  lazy val anwender: Future[AnwenderEntity] = db.run(anwenderAction)
  ////  try {
  ////    db.run(anwenderAction)
  ////  } finally {
  ////    db.close()
  //  }
  //  /**
  //   * Adresse of Anwender with lazy initialization
  //   */
  //  lazy val adresse: Future[AdresseEntity] = try {
  //    for {
  //      anw <- anwender
  //      adr <- db.run(dal.getAdresseById(anw.adresseId.get)) map { adresse => adresse }
  //    } yield (adr)
  //  } finally {
  //    db.close()
  //  }

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

  def profilBearbeiten(nutzerName: Option[String], nutzerEmail: Option[String], adress: Option[AdresseEntity]): Unit = {
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

  def betriebErstellen(adresse: AdresseEntity,name: String,  tel: String, oeffnungszeiten: String, kontaktEmail: String) = {
    //@todo implement me and return Future[(BetriebEntity, LeiterEntity)]
    throw new NotImplementedError("Not implemented yet, implement it")
  }

}
