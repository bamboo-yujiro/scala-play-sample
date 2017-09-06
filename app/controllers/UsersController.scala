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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */



@Singleton
class UsersController @Inject() extends Controller {

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

  def check(username: String, password: String) = {
    val user:Option[User] = User.where('username -> username).where('password -> password).apply().headOption
    user match {
      case Some(u) => true
      case None => false
    }
  }

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def login = Action {
    Ok(views.html.users.login(loginForm))
  }

  def result() = Action { implicit request =>
    loginForm.bindFromRequest().fold(
      errorForm => {
        Ok(views.html.users.login(errorForm))
      },
      requestForm => {
        Ok(views.html.users.result(loginForm.fill(requestForm)))
      }
    )
  }


}
