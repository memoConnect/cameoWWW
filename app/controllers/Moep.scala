package controllers

import play.api.mvc._

object Moep extends Controller {
  def moep = Action {
    Ok("moep")
  }
}
