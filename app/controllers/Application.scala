package controllers

import play.api.mvc._
import _root_.helper.i18n

object Application extends Controller {
  def index = Action {
    val lang: String = "de"

    i18n.setLang(lang)

    Ok(views.html.index())
  }
}