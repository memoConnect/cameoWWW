package controllers

import models.Antibot
import org.apache.commons.mail.EmailException
import play.api.Play.current
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.{Logger, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FormSubmit extends Controller {

  case class MessageSubmit(name: String,
                           email: String,
                           subject: String,
                           message: String,
                           abName: String,
                           abValue: String,
                           additionalFields: Option[JsObject])

  object MessageSubmit {
    implicit val format = Json.format[MessageSubmit]
  }

  def submit = Action.async(parse.tolerantJson) {
    request =>
      request.body.validate[MessageSubmit].map {
        ms =>
          Logger.info("Received contact request: " + request.body)
          // validate antibot
          Antibot.create(request).equals(new Antibot(ms.abName, ms.abValue)) match {
            case false => Future(BadRequest("Invalid antibot values"))
            case true =>
              createTicket(ms)
            //sendMail(ms)
          }

      }.recoverTotal {
        err => Future(BadRequest(JsError.toFlatJson(err)))
      }
  }

  def createTicket(messageSubmit: MessageSubmit): Future[Result] = {

    // append additional values
    val maybeAppend = messageSubmit.additionalFields.map {
      af => af.keys.map {
        key => key + ":" + (af \ key).as[String]
      }.mkString("\n")
    }

    val messageBody = maybeAppend match {
      case None => messageSubmit.message
      case Some(text) => messageSubmit.message + "\n\nAdditional Values:\n" + text
    }

    val body =
      Seq(
        "message" -> messageBody,
        "useridentifier" -> "support@cameo.io",
        "department" -> "default",
        "subject" -> messageSubmit.subject,
        "recipient" -> messageSubmit.email,
        "recipient_name" -> messageSubmit.name,
        "apikey" -> Play.configuration.getString("liveagent.apikey").get
      )

//    val jsonBody =
//      Json.obj(
//        "message" -> messageBody,
//        "useridentifier" -> "support@cameo.io",
//        "department" -> "default",
//        "subject" -> messageSubmit.subject,
//        "recipient" -> messageSubmit.email,
//        "recipient_name" -> messageSubmit.name,
//        "apikey" -> Play.configuration.getString("liveagent.apikey").get
//      )

//    val body =
//      Map(
//        "message" -> "foo",
//        "useridentifier" -> "support",
//        "department" -> "default",
//        "subject" -> "subj",
//        "recipient" -> "foo",
//        "recipient_name" -> "name",
//        "apikey" -> Play.configuration.getString("liveagent.apikey").get
//      )

    val bodyString = body.map(map => map._1 + "=" + map._2).mkString("&")

    Logger.debug("body:" + bodyString + ":")

    val url = Play.configuration.getString("liveagent.url").get + "/api/conversations"

    val response = WS.url(url).withHeaders(("content-type","application/x-www-form-urlencoded;charset=UTF-8")).post(bodyString)

    response.map {
      response =>
        Logger.debug(response.body)
        Ok("mail send")
    }


  }

  def sendMail(messageSubmit: MessageSubmit): Result = {
    import com.typesafe.plugin._

    val mail = use[MailerPlugin].email
    mail.setSubject(messageSubmit.subject)
    mail.setRecipient("Support <support@cameo.io>") //todo: put this in config
    mail.setFrom("noreply@cameo.io")
    mail.setReplyTo(messageSubmit.name + " <" + messageSubmit.email + ">")

    // append additional values
    val maybeAppend = messageSubmit.additionalFields.map {
      af => af.keys.map {
        key => key + ":" + (af \ key).as[String]
      }.mkString("\n")
    }

    val messageBody = maybeAppend match {
      case None => messageSubmit.message
      case Some(text) => messageSubmit.message + messageSubmit.name + ", " + messageSubmit.email + "\n\nAdditional Values:\n" + text
    }

    try {
      mail.send(messageBody)
    } catch {
      case ee: EmailException => Logger.error("Error sending mail: ", ee)
    }
    Ok("mail send")
  }
}
