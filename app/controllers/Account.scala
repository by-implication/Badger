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

object Account extends Controller with Secured {
  
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
  
  
  def index = UserAction(){ user => request =>
    if(user.isAnonymous){
      Ok(views.html.account(signupForm, loginForm, user))
    } else {
      Redirect(routes.Application.landing)
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
          Redirect(routes.Application.landing)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.landing)
    }
  }

  def login() = UserAction(){ user => implicit request =>
    if(user.isAnonymous){
      loginForm.bindFromRequest.fold(
        errors => BadRequest(views.html.account(signupForm, errors, user)),
        newUser => {
          Redirect(routes.Application.landing)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.landing)
    }
  }

  def logout() = UserAction(){ user => request =>
    if(user.isAnonymous){
      Redirect(routes.Account.index)
    } else {
      Redirect(routes.Application.landing).withNewSession
    }
  }

}
