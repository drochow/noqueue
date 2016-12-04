package models.db

import java.sql.Timestamp

/**
 * Created by anwender on 25.11.2016.
 */
case class WarteSchlangenPlatzEntity(
    val beginnZeitpunkt: Timestamp,
    val anwenderId: PK[AnwenderEntity],
    val mitarbeiterId: PK[MitarbeiterEntity],
    val dienstLeistungId: PK[DienstleistungEntity],
    val folgeNummer: Option[PK[WarteSchlangenPlatzEntity]] = None,
    val id: Option[PK[WarteSchlangenPlatzEntity]] = None
) {

}
