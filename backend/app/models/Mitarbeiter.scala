package models

import java.sql.Timestamp
import java.util.NoSuchElementException

import models.db._
import slick.dbio.DBIO
import utils.UnauthorizedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * The Mitarbeiter role is a usal Employee that is able to mange  @WarteschlangenPlatzEntity as well as
 * showing a list of @WarteschlangenPlatzEntity wich together are his personal queue and to change his attendance status
 *
 * @param mitarbeiterAction action that resolves aligned information of this @Mitarbeiter
 * @param dbD database drivers used to perform queries
 */
class Mitarbeiter(
    mitarbeiterAction: DBIO[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)],
    dbD: DB
) extends Base(dbD) {

  lazy val betrieb: Future[BetriebEntity] = mitarbeiterComposition map (_._1)

  lazy val anwender: Future[AnwenderEntity] = mitarbeiterComposition map (_._2)

  lazy val mitarbeiter: Future[MitarbeiterEntity] = mitarbeiterComposition map (_._3)

  lazy val mitarbeiterComposition: Future[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)] = db.run(mitarbeiterAction)

  /**
   * Ensures that the mitarbeiterAction is performed before calling the provided action
   *
   * Background:
   *
   * Since the @Mitarbeiter is created without the need of performing the "mitarbeiterAction" given in the constructor
   * we need to ensure the authorization of all actions by calling the mitarbeiterAction and pass necessarry informations
   * to the provided action
   *
   * @param action action to perform
   * @tparam T return type of  action
   * @return the result of the given action
   */
  private def authorizedAction[T](action: (BetriebEntity, AnwenderEntity, MitarbeiterEntity) => Future[T]): Future[T] =
    mitarbeiterComposition flatMap {
      case (betrieb, anwender, mitarbeiter) => action(betrieb, anwender, mitarbeiter)
    } recover {
      case nse: NoSuchElementException => throw new UnauthorizedException
    }

  /**
   * Starts working on a @WarteschlangenPlatzEntity if it does not has started yet and is the first WSP in the line
   *
   * @todo may remove the ID and just pick the first WSP if it was not selected yet
   *
   * @param wspId the primary key of the @WarteschlangenPlatzEntity to start
   * @return boolean value indicating success or failure of operation
   */
  def wspBearbeitungBeginnen(wspId: PK[WarteschlangenPlatzEntity]): Future[Boolean] =
    authorizedAction((b, a, m) => db.run(dal.startWorkOn(wspId, m.id.get)).map(_ == 1))

  /**
   * Finishes working on a @WarteschlangenPlatzEntity if it got started before
   *
   * @param wspId the primary key of the @WarteschlangenPlatzEntity to finish
   * @return boolean value indicating success or failure of operation
   */
  def wspBearbeitungBeenden(wspId: PK[WarteschlangenPlatzEntity]): Future[Boolean] =
    authorizedAction((b, a, m) => db.run(dal.finishWorkOn(wspId, m.id.get)).map(_ == 1))

  /**
   * Searches for all @WarteschlangenPlatzEntity that are connected to the @MitarbeiterEntity of this @Mitarbeiter
   * and returns them ordered by the folgePlatzId of the @WarteschlangenPlatzEntity all entities who have a
   * Beginnzeitpunkt set get filtered out expect the one with the largest time.
   * This is done to ensure that we have only one "inProgress" @WarteschlangenPlatzEntity at a time.
   * In addition all Duration will be agregated to calculate time when we expect all @WarteschlangenPlatzEntity to be done.
   *
   * @returna tuple wich contains a Sequence of @WarteschlangenPlatzEntity relevant information and the time when all entites should be finished
   */
  def warteSchlangeAnzeigen(): Future[(Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)] =
    authorizedAction((b, a, m) =>
      for {
        list <- db.run(dal.getWarteschlangenPlaetzeOfMitarbeiter(m.id.get))
        res <- {
          //split wsps that already has begun and wsps that did not
          val (done, notDone) = list.sortWith(_._3.getOrElse(PK[WarteschlangenPlatzEntity](0L)) == _._1).partition(!_._2.isEmpty)
          var fullList = Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])]()
          var agreggatedTime = new Timestamp(System.currentTimeMillis / 1000)
          if (!done.isEmpty || !notDone.isEmpty) {
            //get the last done wsp
            val lastDone = if (!done.isEmpty) done.maxBy(_._2.get.getTime()) else notDone(0)
            val lastTime = if (!done.isEmpty) lastDone._2.get.getTime() else System.currentTimeMillis / 1000;
            val lastDuration = if (!done.isEmpty) lastDone._5 else 0;

            fullList = if (!done.isEmpty) lastDone +: notDone else notDone;

            agreggatedTime = new Timestamp(notDone.foldLeft(0)(
              (x: Int, y: (PK[WarteschlangenPlatzEntity], Option[Timestamp], Option[PK[WarteschlangenPlatzEntity]], AnwenderEntity, Int, String, PK[DienstleistungEntity])) => x + y._5
            ) + lastTime + lastDuration)
          }
          Future.successful((fullList, agreggatedTime))
        }
      } yield res)

  /**
   * Changed the attendance status of the employee to the desired value
   *
   * true => is working
   * false => is not working
   *
   * @param anwesend new attendance status of employee
   * @return boolean indicating if operation was successfull
   */
  def mitarbeiterAnwesenheitVeraendern(anwesend: Boolean): Future[Boolean] =
    authorizedAction((b, a, m) => db.run(dal.mitarbeiterAnwesenheitVeraendern(m.id.get, anwesend)).map(_ == 1))

}
