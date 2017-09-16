package models

import scalikejdbc._
import skinny._
import skinny.orm._, feature._
import org.joda.time.DateTime
import scala.math.ceil

case class MemoTag(
  id: Int,
  memoId: Int,
  tagId: Int,
  createdAt: DateTime,
  updatedAt: DateTime
)

object MemoTag extends SkinnyJoinTable[MemoTag] { self =>
  override def defaultAlias = createAlias("mt")
  override def tableName = "memo_tags"

}

