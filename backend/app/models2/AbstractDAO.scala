package models2

import api.Page

trait GenDAO[A]{
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

  def page(p: Int, s: Int)(filterFunc: A => Boolean)(sortFuncs: ((A, A) => Boolean)*): Page[A]
}

