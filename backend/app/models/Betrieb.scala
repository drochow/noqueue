package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
case class Betrieb(
    tel: String,
    oeffnungszeiten: String,
    kontaktEmail: String,
    wsOffen: Boolean,
    adresseId: PK[Adresse],
    id: Option[PK[Betrieb]] = None
) {
}
