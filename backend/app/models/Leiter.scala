package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
case class Leiter(val anwenderId: PK[Anwender], val betriebId: PK[Betrieb], val id: Option[PK[Leiter]] = None) {

}
