package models

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import slick.dbio.{ DBIO, DBIOAction }

import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class Anwender(val anwenderAction: DBIO[(AnwenderEntity, Option[AdresseEntity])]) extends UnregistrierterAnwender {

  lazy val anwender: Future[PK[AnwenderEntity]] = profil map (_._1.id.get)
  
  lazy val adresse: Future[Option[AdresseEntity]] = profil map (_._2)

  /**
   * Adresse of Anwender with lazy initialization
   */
  lazy val profil: Future[(AnwenderEntity, Option[AdresseEntity])] = db.run(anwenderAction)

  /*for {
    anw <- anwender
    //tup <- db.run(dal.getAnwenderWithAdress(anw.id.get))  // version with join
    adrO <- if (anw.adresseId.isEmpty) Future.successful(None) else db.run(dal.getAdresseById(anw.adresseId.get))
  } yield (anw, adrO)
*/
  //@todo implement lazy val mitarbeiterVon wich is a Future of a Sequence of MitarbeiterEntities

  //@todo implement lazy val leiterVon wich is a Future of a Sequence of LeiterEntities

  //@todo implement lazy val warteSchlangenPlaetze wich is a Future of a Sequence of WartesSchlangenPlatzEntities

  def leitet(betriebId: PK[BetriebEntity]): Leiter = {
    val leiterEntityFuture = for {
      anwId <- anwender
      leiterEntity <- db.run(dal.getLeiterByAnwenderIDAndBetriebId(anwId, betriebId))
    } yield (leiterEntity)
    new Leiter(leiterEntityFuture)
  }

  def profilAnzeigen(): Future[AnwenderEntity] = {
    anwender flatMap {
      anwId => db.run(dal.getAnwenderById(anwId))
    }
  }

  def abmelden() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def accountLoeschen() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def anwenderInformationenAustauschen(anwenderEntity: AnwenderEntity): Future[Boolean] = {
    for {
      anwId <- anwender
      updated <- db.run(dal.update((anwId), anwenderEntity))
    } yield updated == 1
  }

  def anwenderInformationenVeraendern(nutzerName: Option[String], nutzerEmail: Option[String], adresse: Option[Option[AdresseEntity]]): Future[Boolean] = {
    for {
      //traverse for parallel completion of futures, since regular for{anw<-...; adr <- ...} would work sequentially
      //contains the id of this Anwender and his persisted AdresseEntity
      seq <- Future.sequence(Seq(anwender, if (!adresse.isEmpty && !adresse.get.isEmpty) {
        db.run(dal.findOrInsert(adresse.get.get))
      } else { //case adresse
        Future.successful(AdresseEntity) //you can ignore this, because we won't use it later
      }))
      adrIdOptOpt <- if (adresse.isEmpty) { //case do nothing (on no adress field provided)
        Future.successful(None)
      } else {
        if (adresse.get.isEmpty) { //case delete adresse (on adress field provided but empty)
          Future.successful(Some(None))
        } else { //case adresse
          Future.successful(Some(Some(seq(1).asInstanceOf[AdresseEntity].id.get))) //asInstance is typecasting
        }
      }
      updated <- db.run(dal.partialUpdate(seq(0).asInstanceOf[PK[AnwenderEntity]], nutzerName, nutzerEmail, adrIdOptOpt)) //Future.failed(new Exception("too few or too many rows where updated"))
      /*db.run(dal.partialUpdate(x)) == 1*/
    } yield (updated == 1)
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

  def betriebAnzeigen(id: PK[BetriebEntity]) = {
    db.run(dal.getBetriebWithAdresseById(id))
  }
  /**
   *
   * @param betriebEntity
   * @param adresseEntity
   * @return
   */
  def betriebErstellen(
    betriebEntity: BetriebEntity,
    adresseEntity: AdresseEntity
  ): Future[(BetriebEntity, AdresseEntity)] =
    db.run(dal.insert(
      betrieb = betriebEntity, adresse = adresseEntity, anwender = DBIO.from(anwender)
    ))

  def getLeiterFor(betriebId: PK[BetriebEntity]): Future[(LeiterEntity, BetriebEntity, AnwenderEntity)] =
    for {
      anw <- anwender
      leiter <- db.run(dal.getLeiterOf(betriebId, anw))
    } yield (leiter)

}
