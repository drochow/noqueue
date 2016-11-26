package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
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
) {}
