package models

import models.db.PK

class UnregistrierterAnwender {

}

/**
 * Anwender Representation
 *
 * @param nutzerEmail
 * @param password
 * @param nutzerName
 * @param adresseId
 * @param id
 */
case class Anwender(
    nutzerEmail: String,
    password: String,
    nutzerName: String,
    adresseId: Option[PK[Adresse]] = None,
    id: Option[PK[Anwender]] = None
) extends UnregistrierterAnwender {

}
