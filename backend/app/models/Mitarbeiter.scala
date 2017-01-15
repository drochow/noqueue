package models

import models.db._
import slick.dbio.DBIO

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

  def mitarbeiterAnwesenheitVeraendern(anwesend: Boolean) = {
    for {
      m <- mitarbeiter
      entriesChanged <- db.run(dal.mitarbeiterAnwesenheitVeraendern(m.id.get, anwesend))
    } yield entriesChanged == 1
  }

}
