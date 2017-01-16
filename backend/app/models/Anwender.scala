package models

import java.sql.Timestamp

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import org.mindrot.jbcrypt.BCrypt
import slick.dbio.{ DBIO, DBIOAction }
import utils.WspDoesNotExistException

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

  def wsBeitreten(dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsFuerMitarbeiterBeitreten(mitarbeiterPrimaryKey: PK[MitarbeiterEntity], dlPrimaryKey: PK[DienstleistungEntity]) = {
    //@todo maybe implement me Future[WarteschlangenPlatzEntity]
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wsVerlassen(wsp: PK[WarteschlangenPlatzEntity]) = {
    for {
      anw <- anwender
      del <- db.run(dal.delete(wsp, anw.id.get))
    } yield del
  }

  def meineBetriebe(): Future[Seq[(BetriebAndAdresse, Boolean, Boolean)]] = {
    for {
      anw <- anwender
      leiter <- db.run(dal.getBetriebeWhereAnwenderIsLeiter(anw.id.get)) map {
        case (ll: Seq[(BetriebEntity, AdresseEntity, LeiterEntity)]) => ll.map(
          (l: (BetriebEntity, AdresseEntity, LeiterEntity)) => {
            System.out.println(l._1.name)
            (BetriebAndAdresse(l._1, l._2), true, false)
          }
        )
      } recover {
        case nse: NoSuchElementException => Seq.empty
      }
      mitarbeiter <- db.run(dal.getBetriebeWhereAnwenderIsMitarbeiter(anw.id.get)) map {
        case (ml: Seq[(BetriebEntity, AdresseEntity, MitarbeiterEntity)]) => ml.map(
          (m: (BetriebEntity, AdresseEntity, MitarbeiterEntity)) => (BetriebAndAdresse(m._1, m._2), false, m._3.anwesend)
        )
      } recover {
        case nse: NoSuchElementException => Seq.empty
      }
    } yield (leiter ++ mitarbeiter)
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

  def wspAnzeigen(): Future[(PK[WarteschlangenPlatzEntity], String, String, PK[DienstleistungEntity], Int, String, Timestamp)] = {
    for {
      anw <- anwender
      //wsp of anwender
      wsp <- db.run(dal.getWarteschlangenPlatzOfAnwender(anw.id.get))
      //previous wsps
      prev <- if (wsp.isEmpty) throw new WspDoesNotExistException else db.run(dal.getPrevWarteschlangenplaetze(wsp.get._2, wsp.get._1))
      res <- {
        //split wsps that already has begun and wsps that did not
        val doneAndNotDone = prev.sortWith(_._2 == _._1).partition(!_._3.isEmpty)
        //get the last done wsp
        val lastDone = doneAndNotDone._1.maxBy(_._3.get.getTime())
        //aggregate all the done
        Future.successful(new Timestamp(doneAndNotDone._2.foldLeft(0)(
          (x: Int, y: (PK[WarteschlangenPlatzEntity], PK[WarteschlangenPlatzEntity], Option[Timestamp], Int)) => x + y._4
        )))
      }
    } yield (wsp.get._1, wsp.get._3, wsp.get._4, wsp.get._5, wsp.get._6, wsp.get._7, res)
    // wspId,  mitarbeiterName, BetriebName, dlId, dldauer, dlname, schaetzZeitpunkt
  }

}
