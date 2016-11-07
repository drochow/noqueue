package models2

import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import slick.lifted.{ProvenShape, ForeignKeyQuery}


import scala.concurrent.Future

  case class Anbieter(id: Option[Long], anwenderID: Long, anbieterID: Long)

  class Anbieters(tag: Tag) extends Table[Anbieter](tag, "ANBIETER") {
    def id = column[Long]("LEI_ID", O.PrimaryKey, O.AutoInc)
    def adresseID = column[Long]("ADR_ID")
    def tel = column[String]("TEL")
    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")
    def kontaktEmail = column[String]("KONTAKTEMAIL")
    def wsOffen = column[Boolean]("WSOFFEN")
    def bewertung = column[Int]("BEWERTUNG")

    def * = (id.?, adresseID, tel, oeffnungszeiten, kontaktEmail, wsOffen, bewertung) <> (Anbieter.tupled, Anbieter.unapply)

    def adresse: ForeignKeyQuery[Adresses, Adresse] =
      foreignKey("ADR_FK", adresseID, TableQuery[Adresses])(_.id)
  }

