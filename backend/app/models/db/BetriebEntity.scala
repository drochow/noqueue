package models.db

/**
 * Created by anwender on 25.11.2016.
 */
case class BetriebEntity(
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String,
    wsOffen: Boolean,
    adresseId: PK[AdresseEntity],
    id: Option[PK[BetriebEntity]] = None
) {
}
