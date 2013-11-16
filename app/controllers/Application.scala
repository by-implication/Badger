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

object Application extends Controller with Secured {
  
  def landing = UserAction(){ user => request =>
    Ok(views.html.landing(user))
  }

  def app = UserAction(){ user => request =>
    Ok(views.html.app(user))
  }

  def meta(kind: String, id: Int) = UserAction(){ implicit user => request =>
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

  def comments(id: Int) = UserAction(){ user => request =>
    Ok(Json.toJson(Comment.getForLeaf(id).map(_.toJson)))
  }

  //////////////// rating & commenting /////////////////

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

  //////////////// login & signup ///////////////
  
  val loginForm = Form(
    mapping(
      "hp" -> tuple(
        "handle" -> nonEmptyText,
        "password" -> nonEmptyText
      ).verifying(
        "Invalid credentials!",
        _ match {
          case (handle, password) => User.authenticate(handle, password).isDefined
        }
      )
    )
    (hp => User.authenticate(hp._1, hp._2).get)
    ((user: User) => Some((user.handle, "")))
  )

  val signupForm = Form(
    mapping(
      "handle" -> nonEmptyText,
      "email" -> email,
      "passwords" -> tuple(
        "first" -> nonEmptyText,
        "second" -> nonEmptyText
      ).verifying(
        "Passwords do not match!",
        _ match { case (p1, p2) => (p1 == p2) }
      )
    )
    ((handle, email, passwords) => {
      User(NA, handle, email, passwords._1).create().get
    })
    ((user: User) => Some(user.handle, user.email, ("", "")))
  )
  
  
  def account = UserAction(){ user => request =>
    if(user.isAnonymous){
      Ok(views.html.account(signupForm, loginForm, user))
    } else {
      Redirect(routes.Application.app)
    }
  }

  def signup() = UserAction(){ user => implicit request =>
    if(user.isAnonymous){
      signupForm.bindFromRequest.fold(
        errors => {
          play.Logger.info(errors.toString)
          BadRequest(views.html.account(errors, loginForm, user))
        },
        newUser => {
          Redirect(routes.Application.app)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.app)
    }
  }

  def login() = UserAction(){ user => implicit request =>
    if(user.isAnonymous){
      loginForm.bindFromRequest.fold(
        errors => BadRequest(views.html.account(signupForm, errors, user)),
        newUser => {
          Redirect(routes.Application.app)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.app)
    }
  }

  def logout() = UserAction(){ user => request =>
    if(user.isAnonymous){
      Redirect(routes.Application.account)
    } else {
      Redirect(routes.Application.landing).withNewSession
    }
  }

  private def getTotalPs(location: Location): Int = {
    val (locs, leaves) = location.children
    val r = locs.map(getTotalPs(_)).sum + leaves.map(_.ps.getOrElse(0)).sum
    play.Logger.info(location.id.get + "\t" + location.name + "\t" + r)
    r
  }

  private def getTotalMooe(location: Location): Int = {
    val (locs, leaves) = location.children
    val r = locs.map(getTotalMooe(_)).sum + leaves.map(_.mooe.getOrElse(0)).sum
    play.Logger.info(location.id.get + "\t" + location.name + "\t" + r)
    r
  }

  private def getTotalCo(location: Location): Int = {
    val (locs, leaves) = location.children
    val r = locs.map(getTotalCo(_)).sum + leaves.map(_.co.getOrElse(0)).sum
    play.Logger.info(location.id.get + "\t" + location.name + "\t" + r)
    r
  }

  def calculateTotals = UserAction(){ user => request =>

    val l = Location.findById(0).get
    
    play.Logger.info("ps totals:")
    getTotalPs(l)
    play.Logger.info("mooe totals:")
    getTotalMooe(l)
    play.Logger.info("co totals:")
    getTotalCo(l)

    Ok("done!")

  }

  def sampleData = UserAction(){ user => request =>

    // todo: generate sample ratings, maybe comments

    Redirect(routes.Application.app)

  }

}