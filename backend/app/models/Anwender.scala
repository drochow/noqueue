package models

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import slick.dbio.{ DBIO, DBIOAction }

import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class Anwender(val anwenderAction: DBIO[(AnwenderEntity, Option[AdresseEntity])]) extends UnregistrierterAnwender {

  lazy val anwender: Future[AnwenderEntity] = profil map (_._1)

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

  def leitet(betriebId: PK[BetriebEntity]): Future[Leiter] =
    anwender map ((anw: AnwenderEntity) => new Leiter(dal.getLeiterOfById(betriebId = betriebId, anwenderId = anw.id.get)))

  def profilAnzeigen(): Future[(AnwenderEntity, Option[AdresseEntity])] = db.run(anwenderAction)

  def abmelden() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def accountLoeschen() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def anwenderInformationenAustauschen(anwenderEntity: AnwenderEntity, adrO: Option[AdresseEntity]): Future[Boolean] = {
    for {
      anw <- anwender
      adr <- if (!adrO.isEmpty) db.run(dal.findOrInsert(adrO.get)).map(_.id) else Future.successful(None)
      updated <- db.run(dal.update((anw.id.get), anwenderEntity.copy(adresseId = adr)))
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

  def passwordVeraendern(oldPassword: String, newPassword: String) =
    for {
      anw <- anwender
      updated <- if (BCrypt.checkpw(oldPassword, anw.password)) {
        db.run(dal.passwordVeraendern(anw.id.get, newPassword))
      } else {
        Future.successful(0) //@todo think about throwing an Exc
      }
    } yield (updated == 1)

  def anwenderSuchen(queryString: Option[String], page: Int, size: Int): Future[Seq[AnwenderEntity]] =
    if (!queryString.isEmpty)
      db.run(dal.searchAnwender(queryString.get, page, size))
    else
      db.run(dal.listAnwender(page, size))

  def anwenderAnzeigen(id: PK[AnwenderEntity]): Future[AnwenderEntity] = db.run(dal.getAnwenderById(id))

  def dienstleistungsTypSuchen(query: String, page: Int, size: Int): Future[Seq[DienstleistungsTypEntity]] = {
    db.run(dal.searchDienstleistung(query, page, size))
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

  def wsVerlassen(wsp: PK[WarteschlangenPlatzEntity]) = {
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

  def getLeiterFor(betriebId: PK[BetriebEntity]): Future[(BetriebEntity, AnwenderEntity, LeiterEntity)] =
    for {
      anw <- anwender
      leiter <- db.run(dal.getLeiterOfById(betriebId, anw.id.get))
    } yield (leiter)

  def wsFuerBestimmtenMitarbeiterBeitreten(dlId: Long, mitarbeiterId: Long): Future[WarteschlangenPlatzEntity] = {
    for {
      anwenderId <- anwender.map(_.id)

      wsp <- db.run(dal.insert(WarteschlangenPlatzEntity(None, anwenderId.get, PK[MitarbeiterEntity](mitarbeiterId), PK[DienstleistungEntity](dlId))))
    } yield wsp
  }

}
