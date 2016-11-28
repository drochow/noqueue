package models.db

import models.DienstleistungsTyp

trait DienstleistungsTypComponent {

  this: DriverComponent with DienstleistungComponent =>
  import driver.api._

  class DienstleistungsTypTable(tag: Tag) extends Table[DienstleistungsTyp](tag, "DIENSTLEISTUNGSTYP") {

    def name = column[String]("NAME")
    def id = column[Option[PK[DienstleistungsTyp]]]("DLT_ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id) <> (DienstleistungsTyp.tupled, DienstleistungsTyp.unapply)
  }

  val dienstleistungsTypen = TableQuery[DienstleistungsTypTable]

  def dienstleistungsTypAutoInc = dienstleistungsTypen returning dienstleistungsTypen.map(_.id)
}
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.TableQuery
//
//case class DienstleistungsTyp(id: Option[Long], name: String)
//
//class DienstleistungsTypen(tag: Tag) extends Table[DienstleistungsTyp](tag, "DIENSTLEISTUNGSTYP") {
//  def id = column[Long]("DLT_ID", O.PrimaryKey, O.AutoInc)
//  def name = column[String]("NAME")
//
//  def * = (id.?, name) <> (DienstleistungsTyp.tupled, DienstleistungsTyp.unapply)
//}
//
//object dienstleistungsTypen extends TableQuery(new DienstleistungsTypen(_)) {
//  //DOA code here
//}
