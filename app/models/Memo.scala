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

case class Memo(id: Long, title: String, content: String)

class Memos(tag: Tag) extends Table[Memo](tag, "memos") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def content = column[String]("content")
  override def * = (id, title, content) <> (Memo.tupled, Memo.unapply _)
  //def ins = title ~ text returning id
}

object Memos { self =>

  //import models.BaseDB.memos
  var memos = TableQuery[Memos]
  var query:slick.lifted.Query[Memos,Memo,Seq] = _

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile]

  def add(memo: Memo): Future[String] = {
    dbConfig.db.run(memos += memo).map(res => "memo successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def listAll(): Future[Seq[Memo]] = dbConfig.db.run(memos.result)


  def content_s(): self.type = {
    query = memos.filter(_.title === "ishibasi")
    this
  }

  def paged(): self.type = {
    //memos.drop(1).take(5)
    this
  }

  def get(): Future[Seq[Memo]] = {
    dbConfig.db.run(query.result)
  }
/*
  def page(folderId: Long, query: Option[String], done: Option[Boolean], sortingFields: Seq[(String)], p: Int, s: Int): Future[Page[Memo]] = Future.successful {
    val filterFunc: Memos => Boolean = { memo =>
      true
    }
    memos.page(p, s)(filterFunc)(sortingFields.map(sortingFunc): _*)
  }
  // List with all the available sorting fields.
  val sortingFields = Seq("id", "title", "content")
  // Defines a sorting function for the pair (field, order)
  def sortingFunc(fieldsWithOrder: (String)): (Task, Task) => Boolean = fieldsWithOrder match {
    case ("id", ASC) => _.id < _.id
    case ("id", DESC) => _.id > _.id
    case ("title", ASC) => _.title < _.title
    case ("title", DESC) => _.title > _.title
    case ("content", ASC) => _.content > _.content
    case ("content", DESC) => _.content < _.content
    case _ => (_, _) => false
  }
*/
 /**
   * Construct the Map[String,String] needed to fill a select options set
   */

  /**
  def options(implicit s: Session): Seq[(String, String)] = {
    val query = (for {
      memo <- memos
    } yield (memo.id, memo.title)).sortBy(_._2)

    query.list.map(row => (row._1.toString, row._2))
  }

   * Insert a new memo
   * @param memo
  def insert(memo: Memo)(implicit s: Session) {
    memos.insert(memo)
  }

  def count(implicit s: Session): Int =
    Query(memos.length).first
   */
}
