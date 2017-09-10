package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.components.actions.AuthTrait

import forms.MemoForm

import play.api.data.Form
import play.api.data.Forms._

import scalikejdbc._
import skinny._
import skinny.orm._, feature._

import controllers.components.actions.AuthTrait

import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MemosController @Inject() extends Controller with AuthTrait {

  val createForm = Form(
    mapping (
      "title" -> nonEmptyText,
      "content" -> nonEmptyText
    )(MemoForm.apply)(MemoForm.unapply)
  )

  def index() = Auth {
    Action { implicit request =>
      val pageNum = request.queryString.map({ case(k,v) =>
        (k, v.head.toInt)
      }).getOrElse("page", 1)
      val user = request.asInstanceOf[AuthRequest[AnyContent]].currentUser
      val memoMaps: Map[String,Any] = Memo.getForTop(user.get.id, pageNum)
      val memos:List[Memo] = memoMaps.get("items").get.asInstanceOf[List[Memo]]
      val paging = memoMaps.get("paging").get
      val pagingMap = paging.asInstanceOf[Map[String, Int]]
      Ok(views.html.memos.index(memos, user, pagingMap))
    }
  }

  def _new() = Auth {
    Action { implicit request =>
      Ok(views.html.memos._new(createForm))
    }
  }

  def create() = Auth {
    Action { implicit request =>
      createForm.bindFromRequest().fold(
        errorForm => {
          Ok(views.html.memos._new(errorForm))
        },
        requestForm => {
          val user = request.asInstanceOf[AuthRequest[AnyContent]].currentUser
          Memo.createWithAttributes('title -> requestForm.title, 'content -> requestForm.content, 'user_id -> user.get.id)
          Redirect("/memos")
        }
      )
    }
  }

}
