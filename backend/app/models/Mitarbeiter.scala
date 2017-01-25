package models

import java.sql.Timestamp

import models.db._
import play.api.inject.ApplicationLifecycle
import slick.dbio.DBIO
import utils.UnauthorizedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Mitarbeiter(
    mitarbeiterAction: DBIO[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)],
    applicationLifecycle: ApplicationLifecycle, dbD: DB
) extends Base(applicationLifecycle, dbD) {

  lazy val betrieb: Future[BetriebEntity] = mitarbeiterComposition map (_._1)

  lazy val anwender: Future[AnwenderEntity] = mitarbeiterComposition map (_._2)

  lazy val mitarbeiter: Future[MitarbeiterEntity] = mitarbeiterComposition map (_._3)

  lazy val mitarbeiterComposition: Future[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)] = db.run(mitarbeiterAction)

  def wsSchließen(nachricht: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeginnen(wspId: PK[WarteschlangenPlatzEntity]) = {
    for {
      m <- mitarbeiter recover {
        case nse: NoSuchElementException => throw new UnauthorizedException
      }
      rows <- db.run(dal.startWorkOn(wspId, m.id.get))
      success <- if (rows > 0) Future.successful(true) else Future.failed(throw new NoSuchElementException)
    } yield success
  }

  def wspBearbeitungBeenden(wspId: PK[WarteschlangenPlatzEntity]) = {
    for {
      m <- mitarbeiter recover {
        case nse: NoSuchElementException => throw new UnauthorizedException
      }
      rows <- db.run(dal.finishWorkOn(wspId, m.id.get))
      success <- if (rows > 0) Future.successful(true) else Future.failed(throw new NoSuchElementException)
    } yield success
  }

  def warteSchlangeAnzeigen(): Future[(Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)] = {
    for {
      m <- mitarbeiter
      list <- db.run(dal.getWarteschlangenPlaetzeOfMitarbeiter(m.id.get))
      res <- {
        //split wsps that already has begun and wsps that did not
        val doneAndNotDone = list.sortWith(_._1 == _._3.getOrElse(PK[WarteschlangenPlatzEntity](0L))).partition(!_._2.isEmpty)

        if (doneAndNotDone._1.isEmpty && doneAndNotDone._2.isEmpty)
          Future.successful((Seq, System.currentTimeMillis / 1000))
        //get the last done wsp
        val lastDone = if (!doneAndNotDone._1.isEmpty) doneAndNotDone._1.maxBy(_._2.get.getTime()) else doneAndNotDone._2(0)
        val lastTime = if (!doneAndNotDone._1.isEmpty) lastDone._2.get.getTime() else System.currentTimeMillis / 1000;
        val fullList = if (!doneAndNotDone._1.isEmpty) doneAndNotDone._2 :+ lastDone else doneAndNotDone._2;
        val lastDuration = if (!doneAndNotDone._1.isEmpty) lastDone._5 else 0;

        val agregattedTime = new Timestamp(doneAndNotDone._2.foldLeft(0)(
          (x: Int, y: (PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])) => x + y._5
        ) + lastTime + lastDuration)
        Future.successful((fullList, agregattedTime))
      }
    } yield res
  }

  def mitarbeiterAnwesenheitVeraendern(anwesend: Boolean) = {
    for {
      m <- mitarbeiter.recover {
        case nse: NoSuchElementException => throw new UnauthorizedException
      }
      entriesChanged <- db.run(dal.mitarbeiterAnwesenheitVeraendern(m.id.get, anwesend))
    } yield entriesChanged == 1
  }

}
