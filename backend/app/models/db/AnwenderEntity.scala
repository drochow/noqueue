package models.db

/**
 * AnwenderEntity Representation
 *
 * @param nutzerEmail
 * @param password
 * @param nutzerName
 * @param adresseId
 * @param id
 */
case class AnwenderEntity(
  nutzerEmail: String,
  password: String,
  nutzerName: String,
  adresseId: Option[PK[AdresseEntity]] = None,
  id: Option[PK[AnwenderEntity]] = None
)
