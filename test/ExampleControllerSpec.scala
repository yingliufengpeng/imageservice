import akka.actor.ActorSystem
import akka.stream.Materializer
import controllers.{HomeController, RestfulImageController}
import play.api.{Configuration, Environment, test}
import play.api.test._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future

//import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class ExampleControllerSpec extends PlaySpecification with Results {

  "Example Page#index" should {
    "be valid" in {
      val ws = new test.WsTestClient.InternalWSClient("localhost", 9999)
      val controller             = new HomeController(ws, Helpers.stubControllerComponents())
      val result: Future[Result] = controller.index().apply(FakeRequest())
      val bodyText: String       = contentAsString(result)
//      println("error")
      (bodyText must be).equalTo("")
    }
  }

  "简单的测试" should {
    "应该是" in {
      val act = ActorSystem("test")
//      val config = Configuration.load(Environment.simple())
      val controller             = new RestfulImageController(act, Helpers.stubControllerComponents())
      val result  = controller.images("")(FakeRequest.apply())
      val bodyText: String       = contentAsString(result)
      val jsonBody = Json.parse(bodyText)
      val urls = (jsonBody \ "urls").as[Array[String]]
//      (bodyText must be).equalTo("")
      (urls must be) equalTo Array.empty[String]
    }
  }

  "上传图片测试" should {
    "返回jobID" in {
      val act = ActorSystem("test")
//      val config = Configuration.load(Environment.simple())
      val controller             = new RestfulImageController(act, Helpers.stubControllerComponents())

      val urls_str = Array(
        "https://farm3.staticflickr.com/2879/11234651086_681b3c2c00_b_d.jpg",
        "https://farm4.staticflickr.com/3790/11244125445_3c2f32cd83_k_d.jpg"
      )

      val value = Json.toJson(Map("urls" -> urls_str))
      val fakeRequest = FakeRequest(
        "POST",
        "/upload",
        Headers.create(),
        AnyContentAsJson(value),
      )
      // 图片上传
      val result  = controller.upload()(fakeRequest)
      val bodyText: String       = contentAsString(result)
      val jsonBody = Json.parse(bodyText)
      val urls = (jsonBody \ "jobId").as[String] // 返回的是jobId
      //      (bodyText must be).equalTo("")
      (urls must be) mustNotEqual ""  // 返回的job_id一定有值
    }
  }

  "返回当前实例所有的已经上传好的urls" should {
    "urls " in {
      val act = ActorSystem("test")
      val config = Configuration.load(Environment.simple())
      val controller             = new RestfulImageController(act, Helpers.stubControllerComponents())

      val urls_str = Array(
        "https://farm3.staticflickr.com/2879/11234651086_681b3c2c00_b_d.jpg",
        "https://farm4.staticflickr.com/3790/11244125445_3c2f32cd83_k_d.jpg"
      )

      val value = Json.toJson(Map("urls" -> urls_str))
      val fakeRequest = FakeRequest(
        "POST",
        "/upload",
        Headers.create(),
        AnyContentAsJson(value),
      )
      val result  = controller.images("")(fakeRequest)
      val bodyText: String       = contentAsString(result)
      val jsonBody = Json.parse(bodyText)
      val urls = (jsonBody \ "jobId").as[String] // 返回的是jobId
      //      (bodyText must be).equalTo("")
      (urls must be) mustNotEqual ""  // 返回的job_id一定有值
    }
  }

}