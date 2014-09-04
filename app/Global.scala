/**
 * User: Björn Reimer
 * Date: 5/25/13
 * Time: 4:27 PM
 */

import play.api.mvc.{WithFilters, EssentialAction, EssentialFilter}
import play.modules.statsd.api.Statsd
import play.api.{Logger, Play}

object StatsFilter extends EssentialFilter {

  def apply(action: EssentialAction): EssentialAction = EssentialAction {
    request =>

      request.path match {
        case path if path.startsWith("/w/") =>
          Statsd.increment("custom.request.api.combined")
          request.method match {
            case method if method.equalsIgnoreCase("GET") => Statsd.increment("custom.request.web.GET")
            case method if method.equalsIgnoreCase("POST") => Statsd.increment("custom.request.web.POST")
            case method if method.equalsIgnoreCase("PUT") => Statsd.increment("custom.request.web.PUT")
            case method if method.equalsIgnoreCase("DELETE") => Statsd.increment("custom.request.web.DELETE")
            case method if method.equalsIgnoreCase("OPTIONS") => Statsd.increment("custom.request.web.OPTIONS")
          }
        case path if path.startsWith("/w") => Statsd.increment("custom.request.w")
        case _ =>
      }

      action.apply(request)
  }
}

object Global extends WithFilters(new play.modules.statsd.api.StatsdFilter(), StatsFilter) {

  override def onStart(app: play.api.Application) = {
    Logger.info("Start up app")
  }

  override def onStop(app: play.api.Application) = {
    Logger.info("Shutting down app")

  }
}

