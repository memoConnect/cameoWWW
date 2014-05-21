package helper

import play.api.i18n.Messages
import java.util.Calendar
import java.text.SimpleDateFormat

object Utils {

  def i18n(index: String) = {
    Messages(index)
  }

  def currentYear = {
    val today = Calendar.getInstance.getTime
    val curTimeFormat = new SimpleDateFormat("YYYY")

    curTimeFormat.format(today)
  }
}
