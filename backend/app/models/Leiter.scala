package models

import models.db.{AnwenderEntity, BetriebEntity, LeiterEntity, PK}

import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class Leiter(val leiter: Future[LeiterEntity]) extends Base {
  lazy val anwender: Future[AnwenderEntity] = for {
    lt <- leiter
    anw <- db.run(dal.getAnwenderById(lt.anwenderId))
  } yield (anw)

  lazy val betrieb: Future[BetriebEntity] = for {
    lt <- leiter
    btr <- db.run(dal.getBetriebById(lt.betriebId))
  } yield (btr)

  def betriebsInformationenVeraendern(betrieb: Future[BetriebEntity]) = {

  }

  def betriebsInformationenVeraendern(betriebPrimaryKey: PK[])
}
