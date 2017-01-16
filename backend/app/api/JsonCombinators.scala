package api

import java.sql.Timestamp

import models._
import java.util.Date

import models.db._
import play.api.libs.json._
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ }
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes.{ DefaultDateWrites => _, _ }
/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

  implicit val anwenderWrites = new Writes[AnwenderEntity] {
    def writes(a: AnwenderEntity) = Json.obj(
      "id" -> a.id.getOrElse(PK[AnwenderEntity](0L)).value,
      "nutzerEmail" -> a.nutzerEmail,
      "nutzerName" -> a.nutzerName
    )
  }

  implicit val anwenderWithAdresseReads: Reads[(AnwenderEntity, Option[AdresseEntity])] = (
    (__ \ "nutzerEmail").read[String](minLength[String](1)) and
    (__ \ "nutzerName").read[String](minLength[String](1)) and
    (__ \ "adresse").lazyReadNullable[AdresseEntity](adresseReads)
  )((nutzerEmail, nutzerName, adresse) => //the other values will be ignored anyway
      (AnwenderEntity(nutzerEmail, "", nutzerName, if (!adresse.isEmpty) adresse.get.id else None, Some(PK[AnwenderEntity](0L))), adresse))

  /**
   * @todo maybe simplify
   */
  implicit val anwenderWithADresseWrites = new Writes[(AnwenderEntity, Option[AdresseEntity])] {
    def writes(anw: (AnwenderEntity, Option[AdresseEntity])) =
      if (anw._1.adresseId.isEmpty)
        Json.obj(
          "id" -> anw._1.id.getOrElse(PK[AnwenderEntity](0L)).value,
          "nutzerEmail" -> anw._1.nutzerEmail,
          "nutzerName" -> anw._1.nutzerName,
          "adresse" ->
            Json.obj(
              "id" -> "",
              "straße" -> "",
              "hausNummer" -> "",
              "plz" -> "",
              "stadt" -> ""
            )
        )
      else
        Json.obj(
          "id" -> anw._1.id.getOrElse(PK[AnwenderEntity](0L)).value,
          "nutzerEmail" -> anw._1.nutzerEmail,
          "nutzerName" -> anw._1.nutzerName,
          "adresse" ->
            Json.obj(
              "id" -> anw._2.get.id.getOrElse(PK[AdresseEntity](0L)).value,
              "straße" -> anw._2.get.strasse,
              "hausNummer" -> anw._2.get.hausNummer,
              "plz" -> anw._2.get.plz,
              "stadt" -> anw._2.get.stadt
            )
        )
  }

  //@todo fix to do real mapping
  implicit val anwenderPOSTReads: Reads[AnwenderEntity] = (
    (__ \ "nutzerEmail").read[String](minLength[String](1)) and
    (__ \ "nutzerName").read[String](minLength[String](1)) and
    (__ \ "password").read[String](minLength[String](1)) and
    (__ \ "adresse").lazyReadNullable[AdresseEntity](adresseReads)
  )((nutzerEmail, nutzerName, password, adresse) => //the other values will be ignored anyway
      AnwenderEntity(nutzerEmail, password, nutzerName, if (!adresse.isEmpty) adresse.get.id else None, Some(PK[AnwenderEntity](0L))))

  implicit val optionalAdresseReads: Reads[Option[AdresseEntity]] = (
    (__ \ "adresse").readNullable[AdresseEntity]
  ).map(adresseOpt => adresseOpt)

  implicit val anwenderInformationenVeraendernReads: Reads[(Option[String], Option[String], Option[Option[AdresseEntity]])] = (
    (__ \ "nutzerName").readNullable[String] and
    (__ \ "nutzerEmail").readNullable[String] and
    //we either get no adress wich means that we do nothing, or a nulled adress wich means we delete it or an adress with values wich means update
    (__ \ "adresse").readNullable[Option[AdresseEntity]]
  )((nutzerEmailOpt, nutzerNameOpt, adresseEntityOptOpt) => (nutzerEmailOpt, nutzerNameOpt, adresseEntityOptOpt))

  /*//implicit val pkFormat: Format[PK[_]] = Json.format[PK[_]]
  implicit val pkr: Reads[PK[_]] = Json.reads[PK[_]]*/
  //@todo think about adding adding corresponding urls to each pk
  implicit val pkw: Writes[PK[_]] = new Writes[PK[_]] {
    override def writes(pk: PK[_]) = Json.obj {
      "id" -> pk.value
    }
  }

  //implicit val optFormat: Format[Option[_]] = Json.format[Option[_]]
  implicit val pkOptFormat: Writes[Option[PK[_]]] = (
    (__ \ "id").writeNullable[PK[_]]
  )

  implicit val pkAdresseReads = Json.reads[PK[AdresseEntity]]

  implicit val pkAdresseWrites = new Writes[PK[AdresseEntity]] {
    def writes(btr: PK[AdresseEntity]) = {
      Json.toJson(btr.value)
    }
  }

  implicit val pkAnwenderReads = Json.reads[PK[AnwenderEntity]]
  //implicit val pkAnwenderWrites = Json.writes[PK[AnwenderEntity]]
  implicit val pkDlR = Json.reads[PK[DienstleistungsTypEntity]]
  implicit val pkBetriebR = Json.reads[PK[BetriebEntity]]

  implicit val adresseReads = Json.reads[AdresseEntity]

  implicit val adresseWrites = Json.writes[AdresseEntity]

  implicit val dienstleistungsTypEntityFormat: Format[DienstleistungsTypEntity] = Json.format[DienstleistungsTypEntity]

  implicit val dienstleistungAnlegenReads: Reads[(PK[DienstleistungsTypEntity], String, Int, String)] = (
    (__ \ "dienstleistungstyp").read[PK[DienstleistungsTypEntity]] and
    (__ \ "name").read[String] and
    (__ \ "dauer").read[Int] and
    (__ \ "kommentar").read[String]
  )((dlt, name, dauer, kommentar) => (dlt, name, dauer, kommentar))

  implicit val dienstLeistungWrites: Writes[DienstleistungEntity] = Json.writes[DienstleistungEntity]

  implicit val betriebAndAdresseWrites: Writes[BetriebAndAdresse] = new Writes[BetriebAndAdresse] {
    def writes(btr: BetriebAndAdresse) =
      Json.obj(
        "id" -> btr.betriebEntity.id.getOrElse(PK[BetriebEntity](0L)).value,
        "name" -> btr.betriebEntity.name,
        "kontaktEmail" -> btr.betriebEntity.kontaktEmail,
        "tel" -> btr.betriebEntity.tel,
        "oeffnungszeiten" -> btr.betriebEntity.oeffnungszeiten,
        "adresse" -> Json.toJson(btr.adresseEntity)
      )
  }

  implicit val betriebAndAdresseWithDistanceWrites: Writes[(BetriebAndAdresse, String)] = new Writes[(BetriebAndAdresse, String)] {
    def writes(btr: (BetriebAndAdresse, String)) =
      Json.obj(
        "id" -> btr._1.betriebEntity.id.getOrElse(PK[BetriebEntity](0L)).value,
        "name" -> btr._1.betriebEntity.name,
        "kontaktEmail" -> btr._1.betriebEntity.kontaktEmail,
        "tel" -> btr._1.betriebEntity.tel,
        "oeffnungszeiten" -> btr._1.betriebEntity.oeffnungszeiten,
        "adresse" -> Json.toJson(btr._1.adresseEntity),
        "distanz" -> btr._2
      )
  }

  implicit val betriebReads: Reads[BetriebAndAdresse] = (
    (__ \ "name").read[String](minLength[String](1)) and
    (__ \ "oeffnungszeiten").read[String](minLength[String](1)) and
    (__ \ "kontaktEmail").read[String](minLength[String](1)) and
    (__ \ "tel").read[String](minLength[String](1)) and
    (__ \ "adresse").read[AdresseEntity](adresseReads)
  )((name, oeffnugszeiten, kontaktEmail, tel, adresseEntity) => (BetriebAndAdresse(BetriebEntity(name, tel, oeffnugszeiten, kontaktEmail, PK[AdresseEntity](0L)), adresseEntity)))

  implicit val mitarbeiterReads: Reads[MitarbeiterEntity] = (
    (__ \ "anwenderId").read[Long] and
    (__ \ "betriebId").read[Long] and
    (__ \ "anwesend").read[Boolean]
  )((anw, btr, anwesend) => MitarbeiterEntity(anwenderId = PK[AnwenderEntity](anw), betriebId = PK[BetriebEntity](btr), anwesend = anwesend))

  implicit val mitarbeiterWrites: Writes[MitarbeiterEntity] = new Writes[MitarbeiterEntity] {
    def writes(mitarbeiter: MitarbeiterEntity) =
      Json.obj(
        "id" -> mitarbeiter.id.getOrElse(PK[MitarbeiterEntity](0L)).value,
        "anwesend" -> mitarbeiter.anwesend,
        "anwenderId" -> mitarbeiter.anwenderId,
        "betriebId" -> mitarbeiter.betriebId
      )
  }

  implicit val mitarbeiterAndAnwenderWrites: Writes[(MitarbeiterEntity, AnwenderEntity)] = new Writes[(MitarbeiterEntity, AnwenderEntity)] {
    override def writes(mitarbAndAnw: (MitarbeiterEntity, AnwenderEntity)): JsValue =
      Json.obj(
        "mitarbeiter" -> Json.toJsFieldJsValueWrapper(mitarbAndAnw._1),
        "anwender" -> Json.toJsFieldJsValueWrapper(mitarbAndAnw._2)
      )
  }

  implicit val leiterReads: Reads[LeiterEntity] = (
    (__ \ "anwenderId").read[Long] and
    (__ \ "betriebId").read[Long]
  )((anw, btr) => LeiterEntity(anwenderId = PK[AnwenderEntity](anw), betriebId = PK[BetriebEntity](btr)))

  implicit val leiterWrites: Writes[LeiterEntity] = new Writes[LeiterEntity] {
    def writes(leiter: LeiterEntity) =
      Json.obj(
        "id" -> leiter.id.getOrElse(PK[MitarbeiterEntity](0L)).value,
        "anwenderId" -> leiter.anwenderId,
        "betriebId" -> leiter.betriebId
      )
  }

  implicit val meineBetriebeWrites: Writes[(BetriebAndAdresse, Boolean, Boolean)] = new Writes[(BetriebAndAdresse, Boolean, Boolean)] {
    override def writes(v: (BetriebAndAdresse, Boolean, Boolean)): JsValue = {
      Json.obj(
        "betrieb" -> Json.toJson(v._1),
        "isLeiter" -> v._2,
        "isAnwesend" -> v._3
      )
    }
  }

  implicit val warteSchlangeOfMitarbeiterWrites: Writes[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])] =
    new Writes[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])] {
      override def writes(v: (PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])): JsValue = {
        Json.obj(
          "id" -> v._1.value,
          "beginnZeitpunk" -> v._2.getOrElse(new Timestamp(0L)).getTime,
          "next" -> v._3.value,
          "anwender" -> Json.toJson(v._4),
          "dauer" -> v._5,
          "dlName" -> v._6,
          "dlId" -> v._7.value
        )
      }
    }

  implicit val warteSchlangeOfMitarbeiterWritesWithEstimation: Writes[(Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)] =
    new Writes[(Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)] {
      override def writes(v: (Seq[(PK[WarteschlangenPlatzEntity], Option[Timestamp], PK[WarteschlangenPlatzEntity], AnwenderEntity, Int, String, PK[DienstleistungEntity])], Timestamp)): JsValue = {
        Json.obj(
          "wsps" -> Json.toJson(v._1),
          "schaetzEnde" -> v._2.getTime
        )
      }
    }

  implicit val warteSchlangenPlatzOfAnwenderWrites: Writes[(PK[WarteschlangenPlatzEntity], String, String, PK[DienstleistungEntity], Int, String, Timestamp)] =
    new Writes[(PK[WarteschlangenPlatzEntity], String, String, PK[DienstleistungEntity], Int, String, Timestamp)] {
      override def writes(v: (PK[WarteschlangenPlatzEntity], String, String, PK[DienstleistungEntity], Int, String, Timestamp)): JsValue = {
        Json.obj(
          "id" -> v._1.value,
          "mitarbeiter" -> v._2,
          "betrieb" -> v._3,
          "dlId" -> v._4.value,
          "dlDauer" -> v._5,
          "dlName" -> v._6,
          "schaetzZeitpunkt" -> v._7.getTime()
        )
      }
    }

  implicit val wspReads: Reads[WarteschlangenPlatzEntity] = (
    (__ \ "dlId").read[Long] and
    (__ \ "mitarbeiterId").read[Long]
  )((dlId, mId) =>
      WarteschlangenPlatzEntity(None, PK[AnwenderEntity](0L), PK[MitarbeiterEntity](mId), PK[DienstleistungEntity](dlId), None, None))

  implicit val wspWrites = Json.writes[WarteschlangenPlatzEntity]

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