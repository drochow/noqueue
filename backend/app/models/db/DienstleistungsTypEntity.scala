package models.db

/**
 * Created by anwender on 25.11.2016.
 */
case class DienstleistungsTypEntity(
    val name: String,
    val id: Option[PK[DienstleistungsTypEntity]] = None
) {

}
