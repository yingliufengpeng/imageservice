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
      val jobId = (jsonBody \ "jobId").as[String] // 返回的是jobId
      //      (bodyText must be).equalTo("")
      (jobId must be) mustNotEqual ""  // 返回的job_id一定有值
    }
  }
  val act = ActorSystem("test")
  val config = Configuration.load(Environment.simple())
  val controller             = new RestfulImageController(act, Helpers.stubControllerComponents())

  "返回当前实例所有的已经上传好的urls" should {
    "是空的" in {
      val result  = controller.images("")(FakeRequest())
      val bodyText: String       = contentAsString(result)
      val jsonBody = Json.parse(bodyText)
      val urls = (jsonBody \ "urls").as[Array[String]] // 返回的是jobId
//      println(s"kk, ${RestfulImageController.job2urls}")
       (urls.length must be) equalTo 0  // 返回的images一定为空
    }
  }



  "返回当前实例某个job所有的已经上传的或者正在上传的urls" should {
    "可能会有值" in {
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
      val jobId = (jsonBody \ "jobId").as[String] // 返回的是jobId
//      println(s"jobId is $jobId")
//      println(s"kk, ${RestfulImageController.job2urls}")
      // 获取当前的job所对应的urls列表
      val result2  = controller.images(jobId)(FakeRequest())
      val bodyText2: String       = contentAsString(result2)
      val jsonBody2 = Json.parse(bodyText2)
      val res = (jsonBody2).as[Array[JsValue]] // 返回的是数组
      val successurls = (res(0) \ "urls").as[Array[String]]
      val pendingurls = (res(1) \ "urls").as[Array[String]]
      val len = successurls.length + pendingurls.length
      (len must be) equalTo 2//
    }
  }

}