/*
package models

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import models.Page._
import java.text.SimpleDateFormat
import scala.collection.mutable.Map

object BaseDB {

  val memos = BaseTable(TableQuery[Memos])

  case class BaseTable[A](var table: TableQuery[A]) {
    def nextId: Long = {
      if (!table.contains(incr))
        incr
      else {
        incr += 1
        nextId
      }
    }
    def get(id: Long): Option[A] = {
      //table.filter{_.id === id }.firstOption
      None
    }

    def find(p: A => Boolean): Option[A] = {
      None
      //table.values.find(p)
    }
    def insert(a: Long => A): (Long, A) = {
      val id = nextId
      val tuple = (id -> a(id))
      table += tuple
      incr += 1
      tuple
    }
    def update(id: Long)(f: A => A): Boolean = {
      get(id).map { a =>
        table += (id -> f(a))
        true
      }.getOrElse(false)
    }
    def delete(id: Long): Unit = table -= id
    def delete(p: A => Boolean): Unit = table = table.filterNot { case (id, a) => p(a) }
    def values: List[A] = table.values.toList
    def map[B](f: A => B): List[B] = values.map(f)
    def filter(p: A => Boolean): List[A] = values.filter(p)
    def exists(p: A => Boolean): Boolean = values.exists(p)
    def count(p: A => Boolean): Int = values.count(p)
    def size: Int = table.size
    def isEmpty: Boolean = size == 0
    def page(p: Int, s: Int)(filterFunc: A => Boolean)(sortFuncs: ((A, A) => Boolean)*): Page[A] = {
      val items = filter(filterFunc)
      val sorted = sortFuncs.foldRight(items)((f, items) => items.sortWith(f))
      Page(
        items = table.take(5),
        page = p,
        size = s,
        total = table.size
      )
    }
  }
  object BaseTable {
    def apply[A](elements: (Long, A)*): BaseTable[A] = apply(Map(elements: _*), elements.size + 1)
  }

}

*/
