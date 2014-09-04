package controllers

import play.api.mvc._
import _root_.helper.i18n

import models.Antibot
import play.Logger
import play.modules.statsd.Statsd

object Application extends Controller {

  def tryToTrack() = Action {
    implicit request =>
      request.getQueryString("qr") match {
        case Some(qrId) => Statsd.increment("custom.qrcodes.code-" + qrId)
          Logger.debug("QRCode tracked: " + qrId)
        case None =>
      }
      Redirect("/w/de")
  }

  def redirect(url: String) = Action {
    Redirect(url)
  }

  def index(lang: String = "de") = Action { request =>
    // set lang



    i18n.setLang(lang)

    Ok(views.html.index(Antibot.create(request)))
  }
}