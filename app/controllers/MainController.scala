package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import controllers.components.actions.AuthTrait

import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

@Singleton
class MainController @Inject()(val messagesApi: MessagesApi) extends Controller with AuthTrait {

  def index = Auth ({
    Action { implicit request =>
      val user = request.asInstanceOf[AuthRequest[AnyContent]].currentUser
      Ok(views.html._main.index(user))
    }
  }, false)

}
