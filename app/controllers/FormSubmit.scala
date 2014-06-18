package controllers

import models.Antibot
import org.apache.commons.mail.EmailException
import play.api.Logger
import play.api.Play.current
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.mvc._

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

  def submit = Action(parse.tolerantJson) {
    request =>
      request.body.validate[MessageSubmit].map {
        ms =>
          Logger.info("Received contact request: " + request.body)
          // validate antibot
          Antibot.create(request).equals(new Antibot(ms.abName, ms.abValue)) match {
            case false => BadRequest("Invalid antibot values")
            case true => sendMail(ms)
          }

      }.recoverTotal {
        err => BadRequest(JsError.toFlatJson(err))
      }
  }


  def sendMail(messageSubmit: MessageSubmit): Result = {
    import com.typesafe.plugin._

    val mail = use[MailerPlugin].email
    mail.setSubject("[Kontaktformular] " + messageSubmit.subject)
    mail.setRecipient("Support <support@cameo.io>") //todo: put this in config
    mail.setFrom("noreply@cameo.io")
    mail.setReplyTo(messageSubmit.name + " <" + messageSubmit.email + ">")
    
    // append additional values
    val maybeAppend = messageSubmit.additionalFields.map{
      af => af.keys.map {
        key => key + ":" + (af \ key).as[String]
      }.mkString("\n")
    }

    val messageBody = maybeAppend match {
      case None => messageSubmit.message
      case Some(text) => messageSubmit.message + "\n\nAdditional Values:\n"+ text
    }

    try {
      mail.send(messageBody)
    } catch {
      case ee: EmailException => Logger.error("Error sending mail: ", ee)
    }
    Ok("mail send")
  }
}
