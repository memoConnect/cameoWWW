package controllers

import play.api.mvc._
import play.api.Logger

import models.Antibot

object Application extends Controller {

  def index = Action {
    val ab = new Antibot("moep", "moep-value")
    Logger.debug(ab.name)

    Ok(views.html.index())
  }

}