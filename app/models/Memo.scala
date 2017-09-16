package models

import scalikejdbc._
import skinny._
import skinny.orm._, feature._
import org.joda.time.DateTime
import scala.math.ceil

import models.MemoTag
import models.Tag

case class Memo(
  id: Int,
  title: String,
  content: String,
  createdAt: DateTime,
  updatedAt: DateTime,
  userId: Int,
  user: Option[User] = None,
  memo_tags: Seq[MemoTag] = Nil,
  tags: Seq[Tag] = Nil
) {

  def tagStr:String = {
    tags.map(v =>
      v.name
    ).mkString(",")
  }

  def hogeVal: String = {
    createdAt.toString("yyyy/MM/dd HH:mm:ss")
  }

}

object Memo extends SkinnyCRUDMapper[Memo] with TimestampsFeature[Memo] { self =>
  override def defaultAlias = createAlias("m")
  override def tableName = "memos"
  override def extract(rs: WrappedResultSet, n: ResultName[Memo]) = {
    new Memo(
      id = rs.int(n.id),
      title = rs.string(n.title),
      content = rs.string(n.content),
      createdAt = rs.jodaDateTime(n.createdAt),
      updatedAt = rs.jodaDateTime(n.updatedAt),
      userId = rs.int(n.userId)
    )
  }

  lazy val memoTagsRef = hasMany[MemoTag](
    // association's SkinnyMapper and alias
    many = MemoTag -> MemoTag.defaultAlias,
    // defines join condition by using aliases
    on = (m, mt) => sqls.eq(m.id, mt.memoId),
    // function to merge associations to main entity
    merge = (memo, memo_tags) => memo.copy(memo_tags = memo_tags)
  )

  lazy val tagsRef = hasManyThrough[Tag](
    through = MemoTag,
    many = Tag,
    merge = (memo, tags) => memo.copy(tags = tags)
  )

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

  def tagUpdate(id:Int, tagStr:String):Unit = {
    MemoTag.deleteBy(sqls.eq(MemoTag.column.memoId, id))
    tagStr.split(',').foreach({v =>
      var tag = Tag.where('name -> v).apply().headOption
      val tagId = if(tag == None){ Tag.createWithAttributes('name -> v) }else{ tag.get.id }
      MemoTag.createWithAttributes('memo_id -> id, 'tag_id -> tagId)
    })
  }

}

