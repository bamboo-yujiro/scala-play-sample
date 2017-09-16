package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import skinny.orm.SkinnyCRUDMapper
import org.joda.time.DateTime
import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import skinny.orm._
import org.joda.time._

case class Tag(id: Int, name: String, createdAt: DateTime, updatedAt: DateTime)

object Tag extends SkinnyCRUDMapper[Tag] { self =>
  override def defaultAlias = createAlias("t")
  override def tableName = "tags"
  override def extract(rs: WrappedResultSet, n: ResultName[Tag]) = new Tag(
    id = rs.int(n.id),
    name = rs.string(n.name),
    createdAt = rs.jodaDateTime(n.createdAt),
    updatedAt = rs.jodaDateTime(n.updatedAt)
  )

}

