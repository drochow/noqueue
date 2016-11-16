package api

import models.db.{ Adresse, Anwender }
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ }
import play.api.libs.json._

/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

  implicit val adresseWrites = new Writes[Adresse] {
    def writes(a: Adresse) = Json.obj(
      "id" -> a.id,
      "straße" -> a.straße,
      "hausNummer" -> a.hausNummer,
      "plz" -> a.plz,
      "stadt" -> a.stadt
    )
  }
  implicit val addresseReads: Reads[Adresse] =
    (__ \ "straße").read[String](minLength[String](1)).map(straße => Adresse(straße, null, null, null, Option(0L)))

  implicit val anwenderWrites = new Writes[Anwender] {
    def writes(a: Anwender) = Json.obj(
      "id" -> a.id,
      "password" -> a.password,
      "nutzerEmail" -> a.nutzerEmail,
      "nutzerName" -> a.nutzerName
    )
  }
  implicit val anwenderReads: Reads[Anwender] =
    (__ \ "nutzerName").read[String](minLength[String](1)).map(nutzerName => Anwender(null, null, nutzerName, Option(0L), Option(0L)))

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
  //  implicit val taskReads: Reads[Task] = (
  //    (__ \ "text").read[String](minLength[String](1)) and
  //    (__ \ "deadline").readNullable[Date]
  //  )((text, deadline) => Task(0L, 0L, 0, text, null, deadline, false))
  //
}