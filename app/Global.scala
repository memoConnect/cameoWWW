/**
 * User: Björn Reimer
 * Date: 5/25/13
 * Time: 4:27 PM
 */

import play.api.mvc.{WithFilters, EssentialAction, EssentialFilter}
import play.modules.statsd.api.Statsd
import play.api.{Logger, Play}

object Global extends WithFilters(new play.modules.statsd.api.StatsdFilter()) {

  override def onStart(app: play.api.Application) = {
    Logger.info("Start up app")
  }

  override def onStop(app: play.api.Application) = {
    Logger.info("Shutting down app")

  }
}

