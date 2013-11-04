package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current

object Application extends Controller {
  
  def index = Action {
		DB.withConnection { implicit c =>
    	Ok(views.html.index("Your new application is ready."))
		}
  }
  
}