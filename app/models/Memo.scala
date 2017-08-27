package models

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.driver.JdbcProfile
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import models.Page._

/**
 * Helper for pagination.
 */

abstract class BaseModel[C <: Table[B], B] { self =>
  val items: TableQuery[C]
  var query: slick.lifted.Query[C,B,Seq]
  var perpage:Int = 20
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile]

  def page(p: Int): self.type = {
    val pp = (p - 1) * perpage + 1
    query = query.drop(pp).take(perpage)
    this
  }

  def per(p: Int): self.type = {
    perpage = p
    this
  }

  def get(): Future[Seq[B]] = {
    dbConfig.db.run(query.result)
  }

}

case class Memo(id: Long, title: String, content: String)

class Memos(tag: Tag) extends Table[Memo](tag, "memos") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def content = column[String]("content")
  override def * = (id, title, content) <> (Memo.tupled, Memo.unapply _)
  //def ins = title ~ text returning id
}

object Memos extends BaseModel[Memos,Memo]{ self =>
  val items = TableQuery[Memos]
  var query:slick.lifted.Query[Memos,Memo,Seq] = {
    if(query == null) items else query
  }
  /*
  var perpage:Int = 20
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile]
  def add(memo: Memo): Future[String] = {
    dbConfig.db.run(items += item).map(res => "memo successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }
  def listAll(): Future[Seq[Memo]] = dbConfig.db.run(memos.result)

*/
  def content_s(): self.type = {
    query = query.filter(_.content like s"%A%")
    this
  }

  def title_s(): self.type = {
    query = query.filter(_.title like s"%taka%")
    this
  }

}


