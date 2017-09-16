package controllers.components.actions

import models.User
import play.api.Play.current
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

// なぜか追加しないとコンパイル通らない
import play.api.mvc.Results.Redirect

/**
 * Created by yusuke on 2015/01/23. http://play-gf.blogspot.jp/2015/03/filter.html
 */
trait AuthTrait {

  class AuthRequest[A](val currentUser: Option[User], request: Request[A]) extends WrappedRequest[A](request)

  def redirectToLogin = Action{
    Redirect("/").flashing("success" -> Messages("ログインしてください。"))
  }

  case class Auth[A](action: Action[A], redirect: Boolean = true) extends Action[A] {
    def apply(request: Request[A]): Future[Result] = {
      val currentUser: Option[User] = request.session.get("user_id") match{
        case Some(v) => User.findById(v.toLong)
        case None => None
      }
      if (currentUser == None) {
        if(redirect){ redirectToLogin(request.asInstanceOf[Request[play.api.mvc.AnyContent]]) } else { action(new AuthRequest(None, request)) }
      }else{
        action(new AuthRequest(currentUser, request))
      }
    }
    lazy val parser = action.parser
  }

}
