package controllers

import play.api.mvc._
import _root_.helper.i18n

import models.Antibot
import play.Logger

object Application extends Controller {
  def redirect(url: String) = Action {
    Redirect(url)
  }

  def index(lang: String = "de") = Action { request =>
    // set lang
    i18n.setLang(lang)

    Ok(views.html.index(Antibot.create(request)))
  }
}