package helper

import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html


object i18n {
  var lang: String = "en"

  def setLang(locale: String) = {
    lang = locale
  }

  def get(index: String) = {
    Html(HtmlEntities(Messages(index)(Lang(lang))))
  }
}