package models.db

/**
 * DienstleistungsTypEntity Representation
 *
 * @param name
 * @param id
 */
case class DienstleistungsTypEntity(
    name: String,
    id: Option[PK[DienstleistungsTypEntity]] = None
) {
}
