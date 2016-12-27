package models.db

import scala.concurrent.ExecutionContext.Implicits.global

trait MitarbeiterComponent {
  this: DriverComponent with AnwenderComponent with BetriebComponent with DienstleistungComponent =>
  import driver.api._

  class MitarbeiterTable(tag: Tag) extends Table[MitarbeiterEntity](tag, "MITARBEITER") {

    def id = column[PK[MitarbeiterEntity]]("MIT_ID", O.PrimaryKey, O.AutoInc)
    def betriebId = column[PK[BetriebEntity]]("BETR_ID")
    def anwenderId = column[PK[AnwenderEntity]]("ANW_ID")
    def anwesend = column[Boolean]("ANWESEND")
    def anbieter = foreignKey("BETR_FK", betriebId, betriebe)(_.id)
    def anwender = foreignKey("ANW_FK", anwenderId, anwenders)(_.id)

    /**
     * Default Projection Mapping to case Class
     * @return
     */
    def * = (anwesend, betriebId, anwenderId, id.?) <> (MitarbeiterEntity.tupled, MitarbeiterEntity.unapply)
  }

  val mitarbeiters = TableQuery[MitarbeiterTable]

  def mitarbeitersAutoInc = mitarbeiters returning mitarbeiters.map(_.id)

  def getMitarbeiterById(id: PK[MitarbeiterEntity]): DBIO[MitarbeiterEntity] = mitarbeiters.filter(_.id === id).result.head

  def insert(mitarbeiter: MitarbeiterEntity): DBIO[MitarbeiterEntity] = (mitarbeitersAutoInc += mitarbeiter).map(id => mitarbeiter.copy(id = Option(id)))

  def addDienstleistung(dienstleistungEntity: DienstleistungEntity): DBIO[DienstleistungEntity] = insert(dienstleistungEntity)

}