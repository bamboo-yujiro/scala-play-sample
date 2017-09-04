package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.components.actions.AuthTrait

import scalikejdbc._
import skinny._
import skinny.orm._, feature._

import controllers.components.actions.AuthTrait

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MemosController @Inject() extends Controller with AuthTrait {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  //def index(folderId: Long, q: Option[String], done: Option[Boolean], sort: Option[String], p: Int, s: Int) = Action.async {
  def index() = Auth {
    Action { implicit request =>
      //val memos: List[Memo] = Memo.paginate(Pagination.page(1).per(20)).orderBy(m.id.desc).apply()
      val memos: List[Memo] = Memo.getForTop
      val user = request.asInstanceOf[AuthRequest[AnyContent]].currentUser
      //println(user)
      //Ok(views.html.memos.index(memos)).withSession("user_id" -> "1")
      Ok(views.html.memos.index(memos)).withNewSession
    }
  }

}
