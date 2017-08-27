package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import skinny.orm.SkinnyCRUDMapper
import org.joda.time.DateTime

case class Memo(id: Long, title: String, content: String, createdAt: DateTime)

object Memo extends SkinnyCRUDMapper[Memo] {
  override def defaultAlias = createAlias("m")
  override def tableName = "memos"
  override def extract(rs: WrappedResultSet, n: ResultName[Memo]) = new Memo(
    id = rs.long(n.id),
    title = rs.string(n.title),
    content = rs.string(n.content),
    createdAt = rs.jodaDateTime(n.createdAt)
  )
}

