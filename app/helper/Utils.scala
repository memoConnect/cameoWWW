package helper

import java.util.Calendar
import java.text.SimpleDateFormat
import controllers.routes

object utils {
  def currentYear = {
    val today = Calendar.getInstance.getTime
    val curTimeFormat = new SimpleDateFormat("YYYY")

    curTimeFormat.format(today)
  }

  def getFile(file: String) = {
    routes.Assets.at(file)
  }
}
