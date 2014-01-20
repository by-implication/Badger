package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

import budget.models._
import budget.support._

// forms
import play.api.data.Form
import play.api.data.Forms._

// json
import play.api.libs.json._
import play.api.libs.json.Json

// ws
import play.api.libs.ws
import play.api.libs.ws.WS

// async
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._

object Breakdown extends Controller with Secured {

	def index = UserAction(){ user => request =>
    Ok(views.html.breakdown(user))
  }

  def meta(kind: String, year: Int, dpt: String, owner: String, fpap: String) = UserAction(){ implicit user => request =>
    Leaf.query(kind, year, dpt, owner, fpap).map { leaf =>
      Ok("yey")
    }.getOrElse(NotFound("no such leaf"))
  }

}
