package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import skinny.orm.SkinnyCRUDMapper
import org.joda.time.DateTime
import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import skinny.orm._
import org.joda.time._

case class User(id: Long, username: String, password: String, createdAt: DateTime)

object User extends SkinnyCRUDMapper[User] { self =>
  override def defaultAlias = createAlias("u")
  override def tableName = "users"
  override def extract(rs: WrappedResultSet, n: ResultName[User]) = new User(
    id = rs.long(n.id),
    username = rs.string(n.username),
    password = rs.string(n.password),
    createdAt = rs.jodaDateTime(n.createdAt)
  )

  def authenticate(username:String, password:String): Boolean = {
    false
  }

}

