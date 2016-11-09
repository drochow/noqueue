package models

import scala.collection.mutable
import scala.concurrent.Future

/**
 * Created by anwender on 02.11.2016.
 */
case class Adresse(
    AdresseId: Long,
    straße: String,
    hausNummer: String,
    plz: String,
    stadt: String
) {

}

trait GenDAO[A] {
  def nextId: Long
  def get(id: Long): Option[A]
  def find(p: A => Boolean): Option[A]
  def insert(a: Long => A): (Long, A)

  def update(id: Long)(f: A => A): Boolean
  def delete(id: Long): Unit
  def delete(p: A => Boolean): Unit

  def values: List[A]
  def map[B](f: A => B): List[B]
  def filter(p: A => Boolean): List[A]
  def exists(p: A => Boolean): Boolean
  def count(p: A => Boolean): Int
  def size: Int
  def isEmpty: Boolean

  def page(p: Int, s: Int)(filterFunc: A => Boolean)(sortFuncs: ((A, A) => Boolean)*): Any //Page[A]
}

class GenDAOSlick[A] { // extends GenDAO[A]{
}

trait AdresseDAO extends GenDAO[Adresse] {
  def insertOrFind(straße: String, hausNummer: String, plz: String, stadt: String): (Long, Adresse)
  //def findByplz //Bsp
}

class AdresseDAOSlick { //extends GenDAOSlick[Adresse] with AdresseDAO{
  def insertOrFind(straße: String, hausNummer: String, plz: String, stadt: String): Unit = None: Unit //@Todo Sean
}

object AdresseDAOFakeDB extends FakeDB.FakeTable[Adresse](FakeDB.adressen.table, FakeDB.adressen.incr) with AdresseDAO {
  import FakeDB.adressen
  override def insertOrFind(straße: String, hausNummer: String, plz: String, stadt: String) = adressen.insert(Adresse(_, straße, hausNummer, plz, stadt))
}

