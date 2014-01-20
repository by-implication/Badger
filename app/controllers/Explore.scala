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

object Explore extends Controller with Secured {

  def index = UserAction(){ user => request =>
    Ok(views.html.explore(user))
  }

  def meta(category: Option[Int], region: Option[Int], year: Option[Int], offset: Int, sort: String, order: String) = UserAction(){ implicit user => request =>
    val dptDscs: Seq[String] = category.map(Category.findById(_).get.subcats.list).getOrElse(Seq.empty[String])
    val areaDscs: Seq[String] = region.map(Location.findById(_).get.areas.list).getOrElse(Seq.empty[String])
    val (list, count) = Leaf.exploreQuery(dptDscs, areaDscs, year, offset, sort, order)
    Ok(Json.obj(
      "list" -> list.map(_.toJson(user)),
      "count" -> count
    ))
  }

  def focus(id: Int) = UserAction(){ user => request =>
    Ok(Json.toJson(Comment.getForLeaf(id).map(_.toJson)))
  }

}
