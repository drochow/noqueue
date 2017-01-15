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
    def anwender = foreignKey("MTA_ANW_FK", anwenderId, anwenders)(_.id)
    def relationUnique = index("mitarbeiterUnique", (betriebId, anwenderId), unique = true)

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

  def deleteMitarbeiter(id: PK[MitarbeiterEntity], betriebId: PK[BetriebEntity]): DBIO[Int] = mitarbeiters.filter(_.id === id).filter(_.betriebId === betriebId).delete

  def getMitarbeiterOfById(betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity]): DBIO[(BetriebEntity, AnwenderEntity, MitarbeiterEntity)] = {
    (for {
      ((betrieb, anwender), mitarbeiter) <- (betriebe join anwenders join mitarbeiters on {
        case ((betrieb: BetriebTable, anwender: AnwenderTable), mitarbeiter: MitarbeiterTable) =>
          betrieb.id === mitarbeiter.betriebId && anwender.id === mitarbeiter.anwenderId
      })
        .filter {
          case ((betrieb, anwender), mitarbeiter) => anwender.id === anwenderId
        }
        .filter {
          case ((betrieb, anwender), mitarbeiter) => betrieb.id === betriebId
        }
    } yield (betrieb, anwender, mitarbeiter)).result.head.nonFusedEquivalentAction
  }

  def listMitarbeiterOf(betriebId: PK[BetriebEntity], page: Int, size: Int): DBIO[Seq[(MitarbeiterEntity, AnwenderEntity)]] =
    (mitarbeiters
      .filter(_.betriebId === betriebId) join anwenders on (_.anwenderId === _.id))
      .drop(page * size).take(size)
      .result

  def addDienstleistung(dienstleistungEntity: DienstleistungEntity): DBIO[DienstleistungEntity] = insert(dienstleistungEntity)

  def mitarbeiterAnwesenheitVeraendern(id: PK[MitarbeiterEntity], anwesend: Boolean) = mitarbeiters.filter(_.id === id).map(_.anwesend).update(anwesend)
}