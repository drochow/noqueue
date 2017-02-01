package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait DienstleistungComponent {
  this: DriverComponent with BetriebComponent with MitarbeiterComponent with DienstleistungsTypComponent =>
  import driver.api._

  class DienstleistungTable(tag: Tag) extends Table[DienstleistungEntity](tag, "DIENSTLEISTUNG") {

    def id = column[PK[DienstleistungEntity]]("DL_ID", O.PrimaryKey, O.AutoInc)
    def dlTypId = column[PK[DienstleistungsTypEntity]]("DLT_ID")
    def betriebId = column[PK[BetriebEntity]]("BTR_ID")
    def kommentar = column[String]("KOMMENTAR")
    def dauer = column[Int]("DAUER")
    def dienstleistungsTyp = foreignKey("DLT_FK", dlTypId, dienstleistungsTypen)(_.id)
    def betrieb = foreignKey("BTRDL_FK", betriebId, betriebe)(_.id)

    /**
     * Unique index to ensure uniqueness of the combination: betriebId, dlTypId, dauer, kommentar
     */
    def dlUnique = index("dlUnique", (betriebId, dlTypId, dauer, kommentar), unique = true)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (kommentar, dauer, betriebId, dlTypId, id.?) <> (DienstleistungEntity.tupled, DienstleistungEntity.unapply)
  }

  val dienstleistungen = TableQuery[DienstleistungTable]

  private val dienstleistungenAutoInc = dienstleistungen returning dienstleistungen.map(_.id)

  def insert(dl: DienstleistungEntity): DBIO[DienstleistungEntity] = (dienstleistungenAutoInc += dl).map(id => dl.copy(id = Option(id)))

  def update(dl: DienstleistungEntity): DBIO[Int] = dienstleistungen.filter(_.id === dl.id.get).filter(_.betriebId === dl.betriebId).update(dl)

  def deleteDienstleistung(dienstleistungId: PK[DienstleistungEntity], betriebId: PK[BetriebEntity]): DBIO[Int] =
    dienstleistungen.filter(_.id === dienstleistungId).filter(_.betriebId === betriebId).delete
}