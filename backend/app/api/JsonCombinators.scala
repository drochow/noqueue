package api

import models._
import java.util.Date

import models.db.{ AdresseEntity, AnwenderEntity, DienstleistungsTypEntity, PK }
import play.api.libs.json._
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ }
import play.api.libs.functional.syntax._

/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

  //  implicit val adresseWrites = new Writes[AdresseEntity] {
  //    def writes(a: AdresseEntity) = Json.obj(
  //      "id" -> a.id.get.value,
  //      "straße" -> a.straße,
  //      "hausNummer" -> a.hausNummer,
  //      "plz" -> a.plz,
  //      "stadt" -> a.stadt
  //    )
  //  }
  //  implicit val addresseReads: Reads[AdresseEntity] =
  //    (__ \ "straße").read[String](minLength[String](1)).map(straße => AdresseEntity(straße, null, null, null, Option(PK[AdresseEntity](0L))))

  implicit val anwenderWrites = new Writes[AnwenderEntity] {
    def writes(a: AnwenderEntity) = Json.obj(
      "id" -> a.id.getOrElse(PK[AnwenderEntity](0L)).value,
      "nutzerEmail" -> a.nutzerEmail,
      "nutzerName" -> a.nutzerName
    )
  }

  /*//implicit val pkFormat: Format[PK[_]] = Json.format[PK[_]]
  implicit val pkr: Reads[PK[_]] = Json.reads[PK[_]]
  implicit val pkw: Writes[PK[_]] = Json.writes[PK[_]]*/

  implicit val pkAdresseReads = Json.reads[PK[AdresseEntity]]
  implicit val pkAdresseWrites = Json.writes[PK[AdresseEntity]]
  implicit val pkAnwenderReads = Json.reads[PK[AnwenderEntity]]
  implicit val pkAnwenderWrites = Json.writes[PK[AnwenderEntity]]
  implicit val pkDlT = Json.format[PK[DienstleistungsTypEntity]]

  implicit val adresseFormat: Format[AdresseEntity] = Json.format[AdresseEntity]
  implicit val adresseReads = Json.reads[AdresseEntity]
  implicit val adresseWrites = Json.writes[AdresseEntity]
  implicit val dienstleistungsTypEntityFormat: Format[DienstleistungsTypEntity] = Json.format[DienstleistungsTypEntity]

  //@todo fix to do real mapping
  implicit val anwenderReads: Reads[AnwenderEntity] = (
    (__ \ "nutzerEmail").read[String](minLength[String](1)) and
    (__ \ "password").read[String](minLength[String](1)) and
    (__ \ "nutzerName").read[String](minLength[String](1))
  )((nutzerEmail, password, nutzerName) =>
      AnwenderEntity(nutzerEmail, password, nutzerName, Option(PK[AdresseEntity](0L)), Option(PK[AnwenderEntity](0L))))

  //
  //  implicit val userWrites = new Writes[User] {
  //    def writes(u: User) = Json.obj(
  //      "id" -> u.id,
  //      "email" -> u.email,
  //      "name" -> u.name
  //    )
  //  }
  //  implicit val userReads: Reads[User] =
  //    (__ \ "name").read[String](minLength[String](1)).map(name => User(0L, null, null, name, false, false))
  //
  //  implicit val folderWrites = new Writes[Folder] {
  //    def writes(f: Folder) = Json.obj(
  //      "id" -> f.id,
  //      "userId" -> f.userId,
  //      "order" -> f.order,
  //      "name" -> f.name
  //    )
  //  }
  //  implicit val folderReads: Reads[Folder] =
  //    (__ \ "name").read[String](minLength[String](1)).map(name => Folder(0L, 0L, 0, name))
  //
  //  implicit val taskWrites = new Writes[Task] {
  //    def writes(t: Task) = Json.obj(
  //      "id" -> t.id,
  //      "folderId" -> t.folderId,
  //      "order" -> t.order,
  //      "text" -> t.text,
  //      "date" -> t.date,
  //      "deadline" -> t.deadline,
  //      "done" -> t.done
  //    )
  //  }
  //    implicit val taskReads: Reads[Task] = (
  //      (__ \ "text").read[String](minLength[String](1)) and
  //      (__ \ "deadline").readNullable[Date]
  //    )((text, deadline) => Task(0L, 0L, 0, text, null, deadline, false))
  //
}