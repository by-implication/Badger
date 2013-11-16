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

  def meta(kind: String, id: Int) = UserAction(){ user => request =>
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

  def comment(id: Int) = UserAction(){ user => request =>
    if(!user.isAnonymous){
      Leaf.findById(id).map { l =>
        Ok("comment")
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

}