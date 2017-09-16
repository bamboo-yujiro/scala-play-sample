package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import controllers.components.actions.AuthTrait

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MainController @Inject() extends Controller with AuthTrait {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Auth ({
    Action { implicit request =>
      val user = request.asInstanceOf[AuthRequest[AnyContent]].currentUser
      Ok(views.html._main.index(user))
    }
  }, false)

}
