package models

import scalikejdbc._
import skinny._
import skinny.orm._, feature._
import org.joda.time.DateTime
import scala.math.ceil

case class Memo(
  id: Int,
  title: String,
  content: String,
  createdAt: DateTime,
  updatedAt: DateTime,
  userId: Int,
  user: Option[User] = None,
  hogeVal: String
)

object Memo extends SkinnyCRUDMapper[Memo] with TimestampsFeature[Memo] { self =>
  override def defaultAlias = createAlias("m")
  override def tableName = "memos"
  override def extract(rs: WrappedResultSet, n: ResultName[Memo]) = {
    def hoge: String = {
      rs.jodaDateTime(n.createdAt).toString("yyyy/MM/dd HH:mm:ss")
    }
    new Memo(
      id = rs.int(n.id),
      title = rs.string(n.title),
      content = rs.string(n.content),
      createdAt = rs.jodaDateTime(n.createdAt),
      updatedAt = rs.jodaDateTime(n.updatedAt),
      userId = rs.int(n.userId),
      hogeVal = hoge
    )
  }

  val perPageNum = 5

  belongsTo[User](User, (m, u) => m.copy(user = u)).byDefault

  def getForTop(userId:Int, currentPage:Int): Map[String,Any] = {
    val items = paginate(Pagination.page(currentPage).per(perPageNum)).where('user_id -> userId).orderBy(defaultAlias.id.desc).apply()
    val count = where('user_id -> userId).count()
    val sumPageNum = ceil(count.toDouble / perPageNum.toDouble).toInt
    Map(
      "items" -> items,
      "paging" -> Map(
        "page" -> currentPage,
        "perPage" -> perPageNum,
        "count" -> count,
        "sumPageNum" -> sumPageNum
      )
    )
  }

}

