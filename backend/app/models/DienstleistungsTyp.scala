package models

import models.db.PK

/**
 * Created by anwender on 25.11.2016.
 */
case class DienstleistungsTyp(
    val name: String,
    val id: Option[PK[DienstleistungsTyp]] = None
) {

}
