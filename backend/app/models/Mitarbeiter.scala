package models

import models.db._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class Mitarbeiter(mitarbeiter: Future[MitarbeiterEntity]) extends Base {

  lazy val anwender: Future[AnwenderEntity] = for {
    mta <- mitarbeiter
    anw <- db.run(dal.getAnwenderById(mta.anwenderId))
  } yield (anw)

  lazy val betrieb: Future[BetriebEntity] = for {
    mta <- mitarbeiter
    btr <- db.run(dal.getBetriebById(mta.betriebId))
  } yield (btr)

  lazy val warteschlangenPlaetze: Future[Seq[WarteSchlangenPlatzEntity]] = for {
    mta <- mitarbeiter
    wsps <- db.run(dal.getWarteschlangenPlaetzeOfMitarbeiter(mta.id.get))
  } yield (wsps)

  def wsBeitrittOeffnen() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsBeitrittVerhindern() = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wsSchließen(nachricht: String) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeginnen(wsp: Future[WarteSchlangenPlatzEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeginnen(wspPrimaryKey: PK[WarteSchlangenPlatzEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def wspBearbeitungBeenden(wsp: Future[WarteSchlangenPlatzEntity]) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

  def wspBearbeitungBeenden(wspPrimaryKey: PK[WarteSchlangenPlatzEntity]) = {
    //@todo maybe implement me
    throw new NotImplementedError("Not implemented yet, may implement it")
  }

  def anwesenheitVeraendern(anwesend: Boolean) = {
    //@todo implement me
    throw new NotImplementedError("Not implemented yet, implement it")
  }

}
