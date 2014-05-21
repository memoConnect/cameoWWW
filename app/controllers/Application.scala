package controllers

import play.api.mvc._

object Application extends Controller {

  def redirect(url: String) = Action {
    Redirect(url)
  }

  def index = Action {
    //val lang: String = "de"
    Ok(views.html.index())
  }

}