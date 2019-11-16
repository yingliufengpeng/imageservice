package controllers

import javax.inject._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.mvc.request.RequestAttrKey

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ws: WSClient, cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index: Action[AnyContent] = Action { request =>
//    val m = request.attrs.get(RequestAttrKey.Server)
    Ok("")
//    Ok(views.html.index("Your new application is ready. 映柳枫鹏"))
  }

  def hello(name: String): Action[AnyContent] = Action { request =>
      val m = request.attrs.get(RequestAttrKey.Server)
      println(s"m is $m")
      println(s"hello, world!!! $name")
      Ok("")
//      Ok(views.html.hello(name))
  }

}
