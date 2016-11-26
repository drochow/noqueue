package models

/**
 * Created by anwender on 25.11.2016.
 */
case class DienstLeistung(
    //schaetzDauer:,
    kommentar: String,
    aktion: String,
    //von:,
    //bis:,
    tags: Set[String],
    betrieb: Betrieb,
    dienstLeistungsTyp: DienstLeistungsTyp
) {

}
