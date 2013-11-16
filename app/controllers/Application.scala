package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

import budget.models._

// json
import play.api.libs.json._
import play.api.libs.json.Json

// ws
import play.api.libs.ws
import play.api.libs.ws.WS

// async
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._

// val q: Future[ws.Response] = WS.url("http://google.com").get()
// Async {
//   q.map { response =>
//     Ok(views.html.index("Async (Future), WS, and DB online."))
//   }
// }

object Application extends Controller {
  
  def landing = Action {
    Ok(views.html.landing("Landing"))
  }

  def app = Action {
    Ok(views.html.app("App"))
  }

  def account = Action {
    Ok(views.html.account("Account"))
  }

  def meta(kind: String, id: Int, comments: Boolean) = Action {
    kind match {
      case "leaf" | "loc" => {
        (kind match {
          case "leaf" => Leaf.query(id)
          case "loc" => Location.query(id)
        }).map(Ok(_)).getOrElse(NotFound("not found"))
      }
      case _ => BadRequest("invalid type")
    }
  }
  
}