package models

import models.db.PK

case class Mitarbeiter(anwesend: Boolean, betriebId: PK[Betrieb], anwenderId: PK[Anwender], id: Option[PK[Mitarbeiter]] = None) {

}
