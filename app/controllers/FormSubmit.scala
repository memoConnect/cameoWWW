package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import models.Antibot
import play.api.Play.current
import org.apache.commons.mail.EmailException
import play.api.Logger

object FormSubmit extends Controller {

  case class MessageForm(name: String,
                          email: String,
                          subject: String,
                          message: String)

  val messageForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "subject" -> text,
      "message" -> text
    )(MessageForm.apply)(MessageForm.unapply)
  )

  def submit = Action {

    implicit request =>
      messageForm.bindFromRequest.fold(
        error =>
          BadRequest(error.errorsAsJson),
        messageForm =>
          validate(request, messageForm)
      )
  }

  def validate(request: Request[AnyContent], messageForm: MessageForm) : Result = {
    val ab = Antibot.create(request)

    request.body.asFormUrlEncoded.get.find(
      _._1.equals(ab.name)
    ) match {
      case None => BadRequest("key not found")
      case Some (values) => values._2.headOption match {
        case None => BadRequest("value not found")
        case Some (value) => value.equals(ab.value) match {
          case false => BadRequest("invalid value")
          case true => sendMail(messageForm)
        }
      }
    }
  }

  def sendMail(messageForm: MessageForm): Result = {
    import com.typesafe.plugin._

    val mail = use[MailerPlugin].email
    mail.setSubject("[Kontaktformular] " + messageForm.subject)
    mail.setRecipient("Support <support@cameo.io>")
    mail.setFrom("noreply@cameo.io")
    mail.setReplyTo(messageForm.name + " <"+ messageForm.email + ">")

    try {
      mail.send(messageForm.message)
    } catch {
      case ee: EmailException => Logger.error("Error sending mail: ", ee)
    }
    Ok("mail send")
  }
}
