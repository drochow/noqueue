package models.db

import java.sql.Timestamp

/**
 *  WarteschlangenPlatzEntity Representation
  *
 * @param beginnZeitpunkt
 * @param anwenderId
 * @param mitarbeiterId
 * @param dienstLeistungId
 * @param folgeNummer
 * @param id
 */

case class WarteschlangenPlatzEntity(
    beginnZeitpunkt: Option[Timestamp],
    anwenderId: PK[AnwenderEntity],
    mitarbeiterId: PK[MitarbeiterEntity],
    dienstLeistungId: PK[DienstleistungEntity],
    folgeNummer: Option[PK[WarteschlangenPlatzEntity]] = None,
    id: Option[PK[WarteschlangenPlatzEntity]] = None
) {
}
