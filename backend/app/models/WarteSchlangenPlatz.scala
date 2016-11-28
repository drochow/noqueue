package models

import java.sql.Timestamp

import models.db.PK
import org.joda.time.DateTime

/**
 * Created by anwender on 25.11.2016.
 */
case class WarteSchlangenPlatz(
    val beginnZeitpunkt: Timestamp,
    val anwenderId: PK[Anwender],
    val mitarbeiterId: PK[Mitarbeiter],
    val dienstLeistungId: PK[Dienstleistung],
    val folgeNummer: Option[PK[WarteSchlangenPlatz]] = None,
    val id: Option[PK[WarteSchlangenPlatz]] = None
) {

}
