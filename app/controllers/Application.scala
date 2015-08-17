package controllers

import play.api._
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc._

class Application extends Controller {

  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  def index = Action { implicit request =>
    Ok(views.html.index(routes.Application.chatFeed(), routes.Application.postMessage()))
  }

  def chatFeed = Action { request =>
    println("Someone connected: "+ request.remoteAddress)
    Ok.chunked(chatOut
      &> EventSource()).as("text/event-stream")
  }

  def postMessage = Action(parse.json) {
    request => chatChannel.push(request.body);

      Ok
  }

}
