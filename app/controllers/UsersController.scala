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

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class MyForm(custom:String)

@Singleton
class UsersController @Inject() extends Controller {

  val loginForm = Form(
    mapping (
      "username" -> tuple(
        "a" -> nonEmptyText(minLength = 4),
        "b" -> nonEmptyText(maxLength = 8)
      ),
      "password" -> nonEmptyText(minLength = 8)
    )(RequestForm.apply)(RequestForm.unapply)
  )

   /*
   val form = Form(
     mapping(
       "username" -> nonEmptyText(minLength = 4),
       "password" -> nonEmptyText(minLength = 8)
     )(RequestForm.apply)(RequestForm.unapply)
   )
   */
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
        println( errorForm.errors.collectFirst {
          case v if (v.key == "username.a") => Messages(v.message, v.args)
        }.getOrElse("") )
        errorForm.errors.foreach{ v =>
          println(v.message)
          println(v.args)
          //println(Messages(v.message, v.args))
        }
        Ok(views.html.users.login(errorForm))
      },
      requestForm => {
        println("OK")
        Ok(views.html.users.result(loginForm.fill(requestForm)))
      }
    )
  }


}
