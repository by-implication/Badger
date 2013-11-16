package budget.support

import play.api.mvc.BodyParsers._
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, BodyParser, Controller, Request, RequestHeader, Result}
import scala.concurrent.duration._
import budget.models._
import controllers.routes

trait Secured {
  self: Controller =>

  import Secured.currentUser

  def UserAction[A](parser: BodyParser[A] = parse.anyContent)(f: User => Result): Action[A] = {
    Action(parser) { implicit req =>
      val user = currentUser
      if(user.isAnonymous){
        Unauthorized("unauthorized")
      } else {
        f(user)
      }
    }
  }

}

object Secured {

  def login(user: User)(implicit request: RequestHeader) = {
    Redirect(routes.Application.app)
    .withSession(request.session + ("user_id" -> user.id.toString))
  }

  def logout()(implicit user: User, request: RequestHeader) = {
    Redirect(routes.Application.landing).withNewSession
  }

  def currentUser(implicit request: RequestHeader) = {
    request.session.get("user_id").flatMap(id => {
      User.findById(id.toInt)
    }).getOrElse(User.ANON)
  }
}
