package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait DienstleistungsTypComponent {

  this: DriverComponent with DienstleistungComponent =>
  import driver.api._

  class DienstleistungsTypTable(tag: Tag) extends Table[DienstleistungsTypEntity](tag, "DIENSTLEISTUNGSTYP") {

    def name = column[String]("NAME")
    def id = column[PK[DienstleistungsTypEntity]]("DLT_ID", O.PrimaryKey, O.AutoInc)

    def * = (name, id.?) <> (DienstleistungsTypEntity.tupled, DienstleistungsTypEntity.unapply)
  }

  val dienstleistungsTypen = TableQuery[DienstleistungsTypTable]

  def dienstleistungsTypAutoInc = dienstleistungsTypen returning dienstleistungsTypen.map(_.id)

  def findOrInsert(dlt: DienstleistungsTypEntity): DBIO[DienstleistungsTypEntity] = (for {
    dltFound: Option[DienstleistungsTypEntity] <- dienstleistungsTypen
      .filter(_.name === dlt.name)
      .result.headOption
    dlt <- if (dltFound.isEmpty) (dienstleistungsTypAutoInc += dlt).map(id => dlt.copy(id = Option(id))) else DBIO.successful(dltFound.get)
  } yield dlt)

  def insert(dlT: DienstleistungsTypEntity): DBIO[DienstleistungsTypEntity] = (dienstleistungsTypAutoInc += dlT).map(id => dlT.copy(id = Option(id)))

  def getDlTById(id: PK[DienstleistungsTypEntity]): DBIO[DienstleistungsTypEntity] = dienstleistungsTypen.filter(_.id === id).result.head

  def searchDienstleistung(query: String, page: Int, size: Int): DBIO[Seq[DienstleistungsTypEntity]] =
    dienstleistungsTypen.filter(_.name like "%" + query + "%").drop(page * size).take(size).result

  def getByEntireName(entireName: String) = dienstleistungsTypen.filter(_.name === entireName).result.head

  def getAllDlTs(limit: Long = 20, offset: Long = 1): DBIO[Seq[DienstleistungsTypEntity]] =
    dienstleistungsTypen.filter(_.id > PK[DienstleistungsTypEntity](offset)).filter(_.id < PK[DienstleistungsTypEntity](offset + limit)).result
}
