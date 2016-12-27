package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait DienstleistungComponent {
  this: DriverComponent with BetriebComponent with MitarbeiterComponent with DienstleistungsTypComponent =>
  import driver.api._

  class DienstleistungTable(tag: Tag) extends Table[DienstleistungEntity](tag, "DIENSTLEISTUNG") {

    def id = column[PK[DienstleistungEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def dlTypId = column[PK[DienstleistungsTypEntity]]("DLT_ID")
    def mitarbeiterId = column[PK[MitarbeiterEntity]]("MITARBEITER_ID")
    def kommentar = column[String]("KOMMENTAR")
    def aktion = column[String]("AKTION")
    def dienstleistungsTyp = foreignKey("DLT_FK", dlTypId, dienstleistungsTypen)(_.id.get)
    def betrieb = foreignKey("MITARBEITER_FK", mitarbeiterId, mitarbeiters)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (kommentar, aktion, mitarbeiterId, dlTypId, id.?) <> (DienstleistungEntity.tupled, DienstleistungEntity.unapply)
  }

  val dienstleistungen = TableQuery[DienstleistungTable]

  private val dienstleistungenAutoInc = dienstleistungen returning dienstleistungen.map(_.id)

  def insert(dl: DienstleistungEntity): DBIO[DienstleistungEntity] = (dienstleistungenAutoInc += dl).map(id => dl.copy(id = Option(id)))

  def getDienstleistungById(id: PK[DienstleistungEntity]): DBIO[DienstleistungEntity] = dienstleistungen.filter(_.id === id).result.head

  def deleteDienstleistung(dienstleistungId: PK[DienstleistungEntity]): DBIO[Int] = dienstleistungen.filter(_.id === dienstleistungId).delete
}