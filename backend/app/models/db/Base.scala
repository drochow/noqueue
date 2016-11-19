//package models.db
//
//import slick.driver.JdbcDriver
//import slick.lifted.{ CanBeQueryCondition, Rep, TableQuery }
//
//import scala.concurrent.Future
//import scala.reflect._
//
//trait BaseEntity {
//  val id: Long
//}
//
//abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
//    extends Table[E](tag, schemaName, tableName) {
//  val classOfEntity = classTag[E].runtimeClass
//  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
//}
//
//trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {
//  def getById(id: Long): Future[Option[E]]
//  def getAll: Future[Seq[E]]
//  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]
//  def save(row: E): Future[E]
//  def deleteById(id: Long): Future[Int]
//  def updateById(id: Long, row: E): Future[Int]
//}
//
//trait BaseRepositoryQuery[T <: BaseTable[E], E <: BaseEntity] {
//  val query: PostgresDriver.api.type#TableQuery[T]
//
//  def getByIdQuery(id: Long) = {
//    query.filter(_.id === id)
//  }
//
//  def getAllQuery = {
//    query.to[List]
//  }
//
//  def filterQuery[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
//    query.filter(expr)
//  }
//
//  def saveQuery(row: E) = {
//    query returning query += row
//  }
//
//  def deleteByIdQuery(id: Long) = {
//    query.filter(_.id === id).delete
//  }
//
//  def updateByIdQuery(id: Long, row: E) = {
//    query.filter(_.id === id).update(row)
//  }
//
//}
//
//abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity: ClassTag](clazz: TableQuery[T]) extends BaseRepositoryQuery[T, E] with BaseRepositoryComponent[T, E] {
//  val clazzTable: TableQuery[T] = clazz
//  lazy val clazzEntity = classTag[E].runtimeClass
//  val query: PostgresDriver.api.type#TableQuery[T] = clazz
//  val db: PostgresDriver.backend.DatabaseDef = DriverHelper.db
//
//  import driver.api._
//
//  def getAll: Future[Seq[E]] = {
//    db.run(getAllQuery.result)
//  }
//
//  def getById(id: Long): Future[Option[E]] = {
//    db.run(getByIdQuery(id).result.headOption)
//  }
//
//  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
//    db.run(filterQuery(expr).result)
//  }
//
//  def save(row: E) = {
//    db.run(saveQuery(row))
//  }
//
//  def updateById(id: Long, row: E) = {
//    db.run(updateByIdQuery(id, row))
//  }
//
//  def deleteById(id: Long) = {
//    db.run(deleteByIdQuery(id))
//  }
//
//}
