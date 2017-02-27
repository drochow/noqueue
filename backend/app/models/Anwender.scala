package models

import java.sql.{ SQLException, Timestamp }
import java.util.NoSuchElementException

import akka.actor.FSM.Failure
import api.jwt.TokenPayload
import models.db._
import org.mindrot.jbcrypt.BCrypt
import play.api.inject.ApplicationLifecycle
import slick.dbio.{ DBIO, DBIOAction }
import utils.{ InvalidWspSubscribtionException, UnauthorizedException, WspDoesNotExistException }

import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

class Anwender(val anwenderAction: DBIO[(AnwenderEntity, Option[AdresseEntity])], dbD: DB) extends UnregistrierterAnwender(dbD) {

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
  def profilAnzeigen(): Future[(AnwenderEntity, Option[AdresseEntity])] = db.run(anwenderAction)

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
      updated <- db.run(dal.partialUpdate(seq(0).asInstanceOf[AnwenderEntity].id.get, nutzerName, nutzerEmail, adrIdOptOpt)) //Future.failed(new Exception("too few or too many rows where updated"))
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

  def wsVerlassen() = {
    for {
      anw <- anwender
      del <- db.run(dal.deleteWspOfAnw(anw.id.get))
    } yield (del == 1)
  }

  def meineBetriebe(): Future[Seq[(BetriebAndAdresse, Boolean, Boolean)]] = {
    for {
      anw <- anwender
      leiter <- db.run(dal.getBetriebeWhereAnwenderIsLeiter(anw.id.get)) map {
        case (ll: Seq[(BetriebEntity, AdresseEntity, LeiterEntity)]) => ll.map(
          (l: (BetriebEntity, AdresseEntity, LeiterEntity)) => {
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

  def wsFuerBestimmtenMitarbeiterBeitreten(dlId: Long, mitarbeiterId: Long): Future[WarteschlangenPlatzEntity] = {
    (for {
      anwenderId <- anwender.map(_.id)
      wsp <- db.run(dal.insert(WarteschlangenPlatzEntity(None, anwenderId.get, PK[MitarbeiterEntity](mitarbeiterId), PK[DienstleistungEntity](dlId))))
    } yield wsp) recover {
      case sqle: SQLException => if (sqle.getMessage.contains("integrity constraint violation")) throw new InvalidWspSubscribtionException else throw sqle
    }
  }

  def wspAnzeigen(): Future[(PK[WarteschlangenPlatzEntity], String, String, AdresseEntity, PK[DienstleistungEntity], Int, String, Timestamp)] = {
    for {
      anw <- anwender
      //wsp of anwender
      wsp <- db.run(dal.getWarteschlangenPlatzOfAnwender(anw.id.get))
      addr <- db.run(dal.getAdresseById(wsp.map(_._5).get))
      //previous wsps
      prev <- if (wsp.isEmpty) throw new WspDoesNotExistException else db.run(dal.getPrevWarteschlangenplaetze(wsp.get._2, wsp.get._1))
      res <- {

        //split wsps that already has begun and wsps that did not
        val doneAndNotDone = prev.sortWith(_._1 == _._2.getOrElse(PK[WarteschlangenPlatzEntity](0L))).partition(!_._3.isEmpty)

        if (doneAndNotDone._1.isEmpty && doneAndNotDone._2.isEmpty)
          Future.successful(new Timestamp(System.currentTimeMillis / 1000))

        val lastTime = if (!doneAndNotDone._1.isEmpty) doneAndNotDone._1.maxBy(_._3.get.getTime())._3.get.getTime() else System.currentTimeMillis / 1000;
        val lastDuration = if (!doneAndNotDone._1.isEmpty) doneAndNotDone._1.maxBy(_._3.get.getTime())._4 else 0;

        //aggregate all the done
        Future.successful(new Timestamp(doneAndNotDone._2.foldLeft(0)(
          (x: Int, y: (PK[WarteschlangenPlatzEntity], Option[PK[WarteschlangenPlatzEntity]], Option[Timestamp], Int)) => x + y._4
        ) + lastTime + lastDuration))
      }
    } yield (wsp.get._1, wsp.get._3, wsp.get._4, addr.get, wsp.get._6, wsp.get._7, wsp.get._8, res)
    // wspId,  mitarbeiterName, BetriebName, Adresse, dlId, dldauer, dlname, schaetzZeitpunkt
  }
  //[
  //  ...
  // (mitarbeiterID,  mitarbeiterName, schaetzZeitpunkt)
  //  ...
  //]
  def getNextTimeSlotsForBetrieb(betriebId: Long): Future[Seq[(PK[MitarbeiterEntity], String, Long)]] = {
    //@todo may optimize to one query with hand written left join query
    for {
      wsps <- db.run(dal.getWspsOfBetrieb(PK[BetriebEntity](betriebId)))
      allAvailables <- db.run(dal.getAvailableEmployees(PK[BetriebEntity](betriebId)))
      res <- Future.successful(allAvailables.map(m => (m._1, m._2)).distinct.map {
        m =>
          {
            val (done, notDone) = wsps.filter(_._1 == m._1).sortWith(_._2 == _._3.getOrElse(PK[WarteschlangenPlatzEntity](0L))).partition(!_._4.isEmpty)

            if (done.isEmpty && notDone.isEmpty)
              (m._1, m._2, System.currentTimeMillis / 1000) //return mitarbeiterId, mitarbeiterName, schaetzZeit

            else {
              val lastTime = if (!done.isEmpty)
                done.maxBy(_._4.get.getTime())._4.get.getTime()
              else
                System.currentTimeMillis / 1000;

              val lastDuration = if (!done.isEmpty) done.maxBy(_._4.get.getTime())._5 else 0;
              val time = notDone.foldLeft(0)(
                (x: Int, y: (PK[MitarbeiterEntity], PK[WarteschlangenPlatzEntity], Option[PK[WarteschlangenPlatzEntity]], Option[Timestamp], Int, String)) => x + y._5
              ) + lastTime + lastDuration

              (m._1, m._2, time) //return mitarbeiterId, mitarbeiterName, schaetzZeit
            }
          }
      })
    } yield res
  }
}