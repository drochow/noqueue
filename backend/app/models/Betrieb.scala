package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
case class Betrieb(
    adresseId: PK[Adresse],
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String,
    wsOffen: Boolean,
    bewertung: Float,
    bewertungen: Set[Int]
) {
}
