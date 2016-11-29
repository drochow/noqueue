package models.db

trait DienstleistungsTypComponent {

  this: DriverComponent with DienstleistungComponent =>
  import driver.api._

  class DienstleistungsTypTable(tag: Tag) extends Table[DienstleistungsTypEntity](tag, "DIENSTLEISTUNGSTYP") {

    def name = column[String]("NAME")
    def id = column[Option[PK[DienstleistungsTypEntity]]]("DLT_ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id) <> (DienstleistungsTypEntity.tupled, DienstleistungsTypEntity.unapply)
  }

  val dienstleistungsTypen = TableQuery[DienstleistungsTypTable]

  def dienstleistungsTypAutoInc = dienstleistungsTypen returning dienstleistungsTypen.map(_.id)
}
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.TableQuery
//
//case class DienstleistungsTypEntity(id: Option[Long], name: String)
//
//class DienstleistungsTypen(tag: Tag) extends Table[DienstleistungsTypEntity](tag, "DIENSTLEISTUNGSTYP") {
//  def id = column[Long]("DLT_ID", O.PrimaryKey, O.AutoInc)
//  def name = column[String]("NAME")
//
//  def * = (id.?, name) <> (DienstleistungsTypEntity.tupled, DienstleistungsTypEntity.unapply)
//}
//
//object dienstleistungsTypen extends TableQuery(new DienstleistungsTypen(_)) {
//  //DOA code here
//}
