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

object Feedback extends Controller with Secured {

  def rate(id: Int, stars: Int) = UserAction(){ user => request =>
    if(!user.isAnonymous){
      Leaf.findById(id).map { l =>
        if(user.rate(l, stars)){
          Ok("successfully rated")
        } else {
          InternalServerError("failed to rate")
        }
      }.getOrElse(BadRequest("no such leaf"))
    } else {
      Unauthorized("unauthorized")
    }
  }

  val commentForm = Form("comment" -> nonEmptyText)

  def comment(id: Int) = UserAction(){ user => implicit request =>
    if(!user.isAnonymous){
      Leaf.findById(id).map { leaf =>
        commentForm.bindFromRequest.fold(
          errors => BadRequest("grabe lalagyan mo na nga lang ng kahit ano hindi mo pa nagawa"),
          comment => Comment(NA, user.id, leaf.id, comment).create().map(
            c => Ok(Json.toJson(c.timestamp))
          ).getOrElse(InternalServerError("failed to save comment"))
        )
      }.getOrElse(BadRequest("no such leaf"))
    } else {
      Unauthorized("unauthorized")
    }
  }

  def click(leafId: Int, lat: Double, lng: Double) = UserAction(){ user => request =>
    if(!user.isAnonymous){
      Leaf.findById(leafId).map { leaf =>
        if(user.click(leaf, lat, lng)){
          Ok("successfully clicked")
        } else {
          InternalServerError("failed to rate")
        }
      }.getOrElse(BadRequest("no such leaf"))
    } else {
      Unauthorized("unauthorized")
    }
  }

}
