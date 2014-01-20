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
    Redirect(routes.Explore.index)
  }

  def about = UserAction(){ user => request =>
    Ok(views.html.landing(user))
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
