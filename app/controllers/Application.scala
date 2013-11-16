package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

// ws
import play.api.libs.ws
import play.api.libs.ws.WS

// async
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._

object Application extends Controller {
  
  def index = Action {
		DB.withConnection { implicit c =>

      // val q: Future[ws.Response] = WS.url("http://google.com").get()
      // Async {
      //   q.map { response =>
          Ok(views.html.index("Async (Future), WS, and DB online."))
      //   }
      // }

		}
  }
  
}