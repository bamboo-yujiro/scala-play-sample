package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MemosController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  //def index(folderId: Long, q: Option[String], done: Option[Boolean], sort: Option[String], p: Int, s: Int) = Action.async {
  def index() = Action {
    val memos: List[Memo] = Memo.findAll()
    Ok(views.html.memos.index(memos))
  }

}
