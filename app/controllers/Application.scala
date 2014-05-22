package controllers

import play.api.mvc._
import _root_.helper.i18n

import models.Antibot
import play.Logger

object Application extends Controller {
  def redirect(url: String) = Action {
    Redirect(url)
  }

  def index(lang: String = "de") = Action {
    // set lang
    i18n.setLang(lang)

    val ab = new Antibot("moep", "moep-value")
//    Logger.debug(ab.name)

    Ok(views.html.index(ab))
  }
}