package models

import java.sql.Timestamp

import models.db._
import slick.dbio.DBIO
import utils.UnauthorizedException

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Mitarbeiter(mitarbeiterAction: DBIO[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)]) extends Base {

  lazy val betrieb: Future[BetriebEntity] = mitarbeiterComposition map (_._1)

  lazy val anwender: Future[AnwenderEntity] = mitarbeiterComposition map (_._2)

  lazy val mitarbeiter: Future[MitarbeiterEntity] = mitarbeiterComposition map (_._3)

  lazy val mitarbeiterComposition: Future[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)] = db.run(mitarbeiterAction)

  def wsSchließen(nachricht: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeginnen(wsp: Future[WarteschlangenPlatzEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeginnen(wspPrimaryKey: PK[WarteschlangenPlatzEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wspBearbeitungBeenden(wsp: Future[WarteschlangenPlatzEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeenden(wspPrimaryKey: PK[WarteschlangenPlatzEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def warteSchlangeAnzeigen(): Future[(Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)] = {
    for {
      m <- mitarbeiter
      list <- db.run(dal.getWarteschlangenPlaetzeOfMitarbeiter(m.id.get))
      res <- {
        //split wsps that already has begun and wsps that did not
        val doneAndNotDone = list.sortWith(_._3.get == _._1).partition(!_._2.isEmpty)
        //get the last done wsp
        val lastDone = doneAndNotDone._1.maxBy(_._2.get.getTime())
        //aggregate all the done
        val agregattedTime = new Timestamp(doneAndNotDone._2.foldLeft(0)(
          (x: Int, y: (PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])) => x + y._5
        ) + lastDone._2.get.getTime())
        //prepend lastDone to List
        Future.successful((doneAndNotDone._2 :+ lastDone, agregattedTime))
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
