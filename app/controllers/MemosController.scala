package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import models._

import controllers.components.actions.AuthTrait

import forms.MemoForm

import play.api.data.Form
import play.api.data.Forms._

import scalikejdbc._
import skinny._
import skinny.orm._, feature._

import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

@Singleton
class MemosController @Inject()(val messagesApi: MessagesApi) extends Controller with AuthTrait with I18nSupport {

  val createForm = Form(
    mapping (
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "tag_str" -> nonEmptyText
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
          val id = Memo.createWithAttributes('title -> requestForm.title, 'content -> requestForm.content, 'user_id -> user.get.id)
          Memo.tagUpdate(id.toInt,requestForm.tag_str)
          Redirect("/memos/" + id.toString() + "/show").flashing("success" -> Messages("メモを追加しました。"))
        }
      )
    }
  }

  def edit(id: Int) = Auth {
    Action {
      val memo = Memo.joins(Memo.tagsRef).where('id -> id).apply().headOption.get
      val editForm:Form[MemoForm] = createForm.bind(Map("title" -> memo.title, "content" -> memo.content, "tag_str" -> memo.tagStr))
      Ok(views.html.memos.edit(editForm,id))
    }
  }

  def update(id: Int) = Auth {
    Action { implicit request =>
      createForm.bindFromRequest().fold(
        errorForm => {
          Ok(views.html.memos.edit(errorForm,id))
        },
        requestForm => {
          Memo.updateById(id).withAttributes('title -> requestForm.title, 'content -> requestForm.content)
          Memo.tagUpdate(id.toInt,requestForm.tag_str)
          Redirect("/memos/" + id.toString() + "/show").flashing("success" -> Messages("メモを編集しました。"))
        }
      )
    }
  }

  def show(id: Int) = Auth {
    Action { implicit request =>
      val memo = Memo.joins(Memo.tagsRef).where('id -> id).apply().headOption.get
      Ok(views.html.memos.show(memo))
    }
  }

  def destroy(id: Int) = Auth {
    Action {
      Memo.deleteById(id)
      Redirect("/memos").flashing("success" -> Messages("メモを削除しました。"))
    }
  }

}
