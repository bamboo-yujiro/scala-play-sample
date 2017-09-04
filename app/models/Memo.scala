package models

import scalikejdbc._
import skinny._
import skinny.orm._, feature._
import org.joda.time.DateTime

case class Memo(
  id: Long,
  title: String,
  content: String,
  createdAt: DateTime,
  userId: Long,
  user: Option[User] = None,
  hogeVal: String
)

object Memo extends SkinnyCRUDMapper[Memo] { self =>
  override def defaultAlias = createAlias("m")
  override def tableName = "memos"
  override def extract(rs: WrappedResultSet, n: ResultName[Memo]) = {
    def hoge: String = {
      rs.jodaDateTime(n.createdAt).toString("yyyy/MM/dd HH:mm:ss")
    }
    new Memo(
      id = rs.long(n.id),
      title = rs.string(n.title),
      content = rs.string(n.content),
      createdAt = rs.jodaDateTime(n.createdAt),
      userId = rs.long(n.userId),
      hogeVal = hoge
    )
  }
  belongsTo[User](User, (m, u) => m.copy(user = u)).byDefault

  def getForTop: List[Memo] = {
    paginate(Pagination.page(1).per(20)).orderBy(defaultAlias.id.desc).apply()
  }

}

