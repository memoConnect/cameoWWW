package models

import play.api.mvc.{AnyContent, Request}
import play.api.libs.Crypto

/**
 * Created by Empujador on 21.05.2014.
 */
case class Antibot(name: String, value: String)

object Antibot {

  def createName[A](request: Request[A]) : String = {
    val ip : String = request.remoteAddress

    Crypto.encryptAES(ip)
  }

  def createValue[A](request: Request[A]) : String  = {
    val ip : String = request.remoteAddress
    val ua : String = request.headers.get("User-Agent") match {
      case None => ""
      case Some (ua: String) => ua
    }

    Crypto.encryptAES(ip+":"+ua)
  }


  def create[A](request: Request[A]) : Antibot = {
    val name : String = createName(request)
    val value: String = createValue(request)

    new Antibot(name, value)
  }
}