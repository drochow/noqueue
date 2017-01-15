package models.db

import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global

trait BetriebComponent {
  this: DriverComponent with AdresseComponent with LeiterComponent with MitarbeiterComponent with DienstleistungComponent with DienstleistungsTypComponent =>

  import driver.api._

  class BetriebTable(tag: Tag) extends Table[BetriebEntity](tag, "ANBIETER") {

    def id = column[PK[BetriebEntity]]("ID", O.PrimaryKey, O.AutoInc)

    def adresseId = column[PK[AdresseEntity]]("ADRESSE_ID")

    def name = column[String]("NAME")

    def tel = column[String]("TEL")

    def oeffnungszeiten = column[String]("OEFFNUNGSZEITEN")

    def kontaktEmail = column[String]("KONTAKTEMAIL")

    def adresse = foreignKey("ADR_FK", adresseId, adresses)(_.id, onDelete = ForeignKeyAction.Restrict)

    def betriebsNameUnique = index("betriebsNameUnique", name, unique = true)

    def betriebsTelUnique = index("betriebsTelUnique", tel, unique = true)

    /**
     * Default Projection Mapping to case Class
     *
     * @return
     */
    def * = (name, tel, oeffnungszeiten, kontaktEmail, adresseId, id.?) <> (BetriebEntity.tupled, BetriebEntity.unapply)
  }

  implicit val getBetriebAndAdresseResult = GetResult(r => BetriebAndAdresse(
    BetriebEntity(r.nextString(), r.nextString(), r.nextString(), r.nextString(), PK[AdresseEntity](r.nextLong()), Some(PK[BetriebEntity](r.nextLong))),
    AdresseEntity(r.nextString(), r.nextString(), r.nextString(), r.nextString(), r.nextDoubleOption(), r.nextDoubleOption(), Some(PK[AdresseEntity](r.nextLong())))
  ))

  val betriebe = TableQuery[BetriebTable]

  val betriebeAutoInc = betriebe returning betriebe.map(_.id)

  /**
   * Creates within a transaction a new BetriebEntity with the corresponding AdresseEntity and LeiterEntity
   *
   * @param betrieb  The BetriebEntity to create ( The adresseId can be set to any value since it will be taken from the parameter adresse)
   * @param adresse  The corresponding AdresseEntity
   * @param anwender The anwender wich takes the "Leiter" position for the Betrieb
   * @return
   */
  def insert(betrieb: BetriebEntity, adresse: AdresseEntity, anwender: DBIO[AnwenderEntity]): DBIO[(BetriebEntity, AdresseEntity)] =
    (for {
      adr: AdresseEntity <- findOrInsert(adresse)
      btr: BetriebEntity <- (betriebeAutoInc += betrieb.copy(adresseId = adr.id.get))
        .map(id => betrieb.copy(id = Option(id), adresseId = adr.id.get))
      anw: AnwenderEntity <- anwender
      leiter: LeiterEntity <- insert(LeiterEntity(anwenderId = anw.id.get, betriebId = btr.id.get))
    } yield (btr, adr)).transactionally

  def getBetriebById(id: PK[BetriebEntity]): DBIO[BetriebEntity] = betriebe.filter(_.id === id).result.head

  def getBetriebWithAdresseById(id: PK[BetriebEntity]): DBIO[(BetriebEntity, AdresseEntity)] =
    (betriebe join adresses on (_.adresseId === _.id)).filter {
      case (betrieb, adresse) => betrieb.id === id
    }.result.head

  def update(id: PK[BetriebEntity], betrieb: BetriebEntity, adresse: AdresseEntity): DBIO[Int] =
    (for {
      adr: AdresseEntity <- findOrInsert(adresse)
      count: Int <- betriebe.filter(_.id === id).update(betrieb.copy(adresseId = adr.id.get, id = Option(id)))
    } yield count).transactionally

  def addMitarbeiter(betriebId: PK[BetriebEntity], anwenderId: PK[AnwenderEntity]): DBIO[MitarbeiterEntity] =
    insert(MitarbeiterEntity(anwesend = false, betriebId = betriebId, anwenderId = anwenderId))

  def listDienstleistungOfBetrieb(betriebId: PK[BetriebEntity], page: Int, size: Int): DBIO[Seq[DienstleistungEntity]] =
    (for {
      (betrieb, dienstleistung) <- (betriebe.filter(_.id === betriebId) join dienstleistungen on (_.id === _.betriebId)).drop(page * size).take(size)
    } yield dienstleistung).result

  /**
   *
   * @param suchBegriff substring wich hast to be present in BetriebEntity.name or DL.kommentar or DLT.name
   * @param umkreisM    the maximum BetriebEntity distance in meters
   * @param longitude   current position longitude coordinate
   * @param latitude    current position latitude coordinate
   * @param page        page filter
   * @param size        size per page filter
   * @return
   */
  def searchBetrieb(
    suchBegriff: String,
    umkreisM: Int,
    longitude: Double,
    latitude: Double,
    page: Int,
    size: Int
  ): DBIO[Seq[(BetriebAndAdresse, String)]] = {
    val umkreisDouble = umkreisM.toDouble
    val q = "%" + suchBegriff + "%"
    val offset = page * size;
    val result = sql"""
         SELECT btr.*, adr.*, (sqrt(power(abs((adr."LONGITUDE"-$longitude)*cos($latitude)), 2) + power(abs(adr."LATITUDE"-$latitude), 2))*6371000) as "DISTANCE"
         FROM "ANBIETER" as btr, "ADRESSE" as adr, "DIENSTLEISTUNG" as dl, "DIENSTLEISTUNGSTYP" as dlt
          WHERE btr."ADRESSE_ID" = adr."ID"
          AND dl."BTR_ID" = btr."ID"
          AND dl."DLT_ID" = dlt."DLT_ID"
          AND ( dlt."NAME" LIKE $q
            OR btr."NAME" LIKE $q
            OR btr."KONTAKTEMAIL" LIKE $q
            OR dl."KOMMENTAR" LIKE $q
          )
          AND (sqrt(power(abs((adr."LONGITUDE"-$longitude)*cos($latitude)), 2) + power(abs(adr."LATITUDE"-$latitude), 2))*6371000) <= $umkreisDouble
          ORDER BY (sqrt(power(abs((adr."LONGITUDE"-$longitude)*cos($latitude)), 2) + power(abs(adr."LATITUDE"-$latitude), 2))*6371000) ASC
          LIMIT $size
          OFFSET $offset
          """.as[(BetriebAndAdresse, String)]
    //    System.out.println(result.statements)
    //    System.out.println(result.statements)
    //    System.out.println("Query: " + q);
    //    System.out.println("latitude: " + latitude);
    //    System.out.println("longitude: " + longitude);
    //    System.out.println("UmkreisDouble: " + umkreisDouble);
    //    System.out.println("offset: " + offset);
    //    System.out.println("limit: " + size);
    result
  }

}