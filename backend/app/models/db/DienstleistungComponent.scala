package models.db

trait DienstleistungComponent {
  this: DriverComponent with BetriebComponent with DienstleistungsTypComponent =>
  import driver.api._

  class DienstleistungTable(tag: Tag) extends Table[DienstleistungEntity](tag, "DIENSTLEISTUNG") {

    def id = column[PK[DienstleistungEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def dlTypID = column[PK[DienstleistungsTypEntity]]("DLT_ID")
    def betriebID = column[PK[BetriebEntity]]("BETR_ID")

    def tags = column[String]("TAGS")
    def kommentar = column[String]("KOMMENTAR")
    def aktion = column[String]("AKTION")

    def * = (kommentar, aktion, tags, betriebID, dlTypID, id.?) <> (DienstleistungEntity.tupled, DienstleistungEntity.unapply)

    def dienstleistungsTyp = foreignKey("DLT_FK", dlTypID, dienstleistungsTypen)(_.id.get)

    def betrieb = foreignKey("ANB_FK", betriebID, betriebe)(_.id)
  }

  val dienstleistungen = TableQuery[DienstleistungTable]

  val dienstleistungenAutoInc = dienstleistungen returning dienstleistungen.map(_.id)
}
//
//import slick.driver.PostgresDriver.api._
//import slick.lifted.{ ForeignKeyQuery, TableQuery }
//
//case class DienstleistungEntity(id: Option[Long], dlTypID: Long, anbieterID: Long, kommentar: String, aktion: String)
//
//class Dienstleistungen(tag: Tag) extends Table[DienstleistungEntity](tag, "DIENSTLEISTUNG") {
//  def id = column[Long]("DL_ID", O.PrimaryKey, O.AutoInc)
//  def dlTypID = column[Long]("DLT_ID")
//  def anbieterID = column[Long]("ANB_ID")
//  def kommentar = column[String]("KOMMENTAR")
//  def aktion = column[String]("AKTION")
//
//  def * = (id.?, dlTypID, anbieterID, kommentar, aktion) <> (DienstleistungEntity.tupled, DienstleistungEntity.unapply)
//
//  def dienstleistungsTyp: ForeignKeyQuery[DienstleistungsTypen, DienstleistungsTypEntity] =
//    foreignKey("DLT_FK", dlTypID, TableQuery[DienstleistungsTypen])(_.id)
//  def anbieter: ForeignKeyQuery[Anbieters, Anbieter] =
//    foreignKey("ANB_FK", anbieterID, TableQuery[Anbieters])(_.id)
//}
//
//object dienstleistungen extends TableQuery(new Dienstleistungen(_)) {
//  //DOA code here
//}