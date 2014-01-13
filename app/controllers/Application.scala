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

  def explore = UserAction(){ user => request =>
    Ok(views.html.explore(user))
  }

  def breakdown = UserAction(){ user => request =>
    Ok(views.html.breakdown(user))
  }

  def exploreMeta(category: Option[Int], region: Option[Int], year: Option[Int], offset: Int, sort: String, order: String) = UserAction(){ implicit user => request =>
    val dptDscs: Seq[String] = category.map(Category.findById(_).get.subcats.list).getOrElse(Seq.empty[String])
    val areaDscs: Seq[String] = region.map(Location.findById(_).get.areas.list).getOrElse(Seq.empty[String])
    Ok(Json.toJson(Leaf.exploreQuery(dptDscs, areaDscs, year, offset, sort, order).map(_.toJson(user))))
  }

  def breakdownMeta(kind: String, year: Int, dpt: String, owner: String, fpap: String) = UserAction(){ implicit user => request =>
    Leaf.query(kind, year, dpt, owner, fpap).map { leaf =>
      Ok("yey")
    }.getOrElse(NotFound("no such leaf"))
  }

  def comments(id: Int) = UserAction(){ user => request =>
    Ok(Json.toJson(Comment.getForLeaf(id).map(_.toJson)))
  }

  //////////////// feedback /////////////////

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
      Redirect(routes.Application.explore)
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
          Redirect(routes.Application.explore)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.explore)
    }
  }

  def login() = UserAction(){ user => implicit request =>
    if(user.isAnonymous){
      loginForm.bindFromRequest.fold(
        errors => BadRequest(views.html.account(signupForm, errors, user)),
        newUser => {
          Redirect(routes.Application.explore)
          .withSession(request.session + ("user_id" -> newUser.id.toString))
        }
      )
    } else {
      Redirect(routes.Application.explore)
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
    val (locs, leaves) = location.children(limit = 1000000)
    val r = locs.map(getTotalPs(_)).sum + leaves.map(_.ps.getOrElse(0)).sum
    play.Logger.info(location.id.get + "\t" + location.name + "\t" + r)
    r
  }

  private def getTotalMooe(location: Location): Int = {
    val (locs, leaves) = location.children(limit = 1000000)
    val r = locs.map(getTotalMooe(_)).sum + leaves.map(_.mooe.getOrElse(0)).sum
    play.Logger.info(location.id.get + "\t" + location.name + "\t" + r)
    r
  }

  private def getTotalCo(location: Location): Int = {
    val (locs, leaves) = location.children(limit = 1000000)
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

}
