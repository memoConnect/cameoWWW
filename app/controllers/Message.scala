package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Forms._
import play.api.data.Form


object Message extends Controller {

  val messageForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "subject" -> text,
      "message" -> text
    )
  )

  def sendMessage = Action {

    implicit request =>
      messageForm.bindFromRequest.fold(
        error =>
          BadRequest(error.errorsAsJson),
        success =>
          Ok(success.toString())
      )

//      Logger.debug(res.toString())
//      Ok("Message:sendMessage")
  }
}
