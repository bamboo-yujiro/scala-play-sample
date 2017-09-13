package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.User

import forms.RequestForm
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.data.validation.{Constraint, Constraints, Invalid, Valid}

import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

@Singleton
class UsersController @Inject() extends Controller {

  private var _user:Option[User] = None

  val userUnique: Constraint[String] = Constraint("constraints.userIsUnique")({
    plainText =>
      val user:Option[User] = User.where('username -> plainText).apply().headOption
      user match {
        case Some(u) => Invalid("User already exists.")
        case None => Valid
      }
  })

  val createForm = Form(
    mapping (
      "username" -> nonEmptyText(minLength = 4).verifying(userUnique),
      "password" -> nonEmptyText(minLength = 8)
    )(RequestForm.apply)(RequestForm.unapply)
  )

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String):Boolean = {
    val user:Option[User] = User.where('username -> username).where('password -> password).apply().headOption
    _user = user
    user match {
      case Some(u) => true
      case None => false
    }
  }

  def _new = Action {
    Ok(views.html.users._new(createForm))
  }

  def create() = Action { implicit request =>
    createForm.bindFromRequest().fold(
      errorForm => {
        Ok(views.html.users._new(errorForm))
      },
      requestForm => {
        User.createWithAttributes('username -> requestForm.username, 'password -> requestForm.password)
        Redirect("/users/login").flashing("success" -> Messages("ユーザーを作成しました。"))
      }
    )
  }

  def login = Action { implicit request =>
    Ok(views.html.users.login(loginForm))
  }

  def logout = Action {
    Redirect("/").withNewSession.flashing("success" -> Messages("ログアウトしました。"))
  }

  def result() = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      errorForm => {
        Ok(views.html.users.login(errorForm))
      },
      requestForm => {
        Redirect("/memos").withSession("user_id" -> _user.get.id.toString)
      }
    )
  }

}
