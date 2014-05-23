package controllers

import play.api.mvc._
import play.api.Logger
import play.api.data.Forms._
import play.api.data.Form
import models.Antibot


object Message extends Controller {

  val messageForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "subject" -> text,
      "message" -> text
    )
  )

  def validate = Action {

    implicit request =>
      messageForm.bindFromRequest.fold(
        error =>
          BadRequest(error.errorsAsJson),
        success =>
          checkABDings(request)
//          Ok(success.toString())
      )
  }

  def checkABDings(request: Request[AnyContent]) : Result = {
    val ab = Antibot.create(request)

    request.body.asFormUrlEncoded.get.find(
      _._1.equals(ab.name)
    ) match {
      case None => BadRequest("key not found")
      case Some (values) => values._2.headOption match {
        case None => BadRequest("value not found")
        case Some (value) => value.equals(ab.value) match {
          case false => BadRequest("invalid value")
          case true => moep()
        }
      }
    }
  }

  def moep(): Result = {
    Ok("Antibot Rockz!")
  }
}
