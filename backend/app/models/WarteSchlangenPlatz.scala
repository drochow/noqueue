package models

import org.joda.time.DateTime

/**
 * Created by anwender on 25.11.2016.
 */
case class WarteSchlangenPlatz(
    folgeNummer: Int,
    beginnZeitpunkt: DateTime,
    schaetzZeitpunkt: DateTime,
    platzNummer: Int,
    anwender: Anwender,
    mitarbeiter: Mitarbeiter,
    dienstLeistung: DienstLeistung
) {

}
