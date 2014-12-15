package controllers

import play.api.Play
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

      request.acceptLanguages.headOption match {
        case Some(lang) => lang.language match {
          case "de" => Redirect("/w/de")
          case _ => Redirect("/w/en")
        }
        case None => Redirect("/w/en")
      }
  }

  def redirect(url: String) = Action {
    Redirect(url)
  }

  def index(lang: String = "en") = Action { request =>
    i18n.setLang(lang)
    Ok(views.html.index(Antibot.create(request)))
  }
}