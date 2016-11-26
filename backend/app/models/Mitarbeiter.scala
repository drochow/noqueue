package models

/**
 * Created by anwender on 25.11.2016.
 */
case class Mitarbeiter(anwender: Anwender, anwesend: Boolean, faehigkeiten: Set[DienstLeistung], betrieb: Betrieb) {

}
