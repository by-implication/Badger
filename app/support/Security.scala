package budget.support

import play.api.mvc.BodyParsers._
import play.api.mvc.Results.Redirect
import play.api.mvc.{Action, BodyParser, Controller, Request, RequestHeader, Result}
import scala.concurrent.duration._
import budget.models._

trait Secured {
  self: Controller =>

  import Secured.currentUser

  def UserAction[A](parser: BodyParser[A] = parse.anyContent)(f: User => Request[A] => Result): Action[A] = {
    Action(parser){ implicit req =>
      f(currentUser)(req)
    }
  }

}

object Secured {

  def currentUser(implicit request: RequestHeader) = {
    request.session.get("user_id").flatMap(id => {
      User.findById(id.toInt)
    }).getOrElse(User.ANON)
  }

}
