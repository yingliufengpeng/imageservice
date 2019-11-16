package controllers

import java.io.{ByteArrayInputStream, File, FileOutputStream, OutputStream, PrintWriter}
import java.util.UUID

import akka.actor.ActorSystem
import com.aliyun.oss.model.OSSObjectSummary
import com.aliyun.oss.{OSS, OSSClientBuilder}
import javax.inject._
import play.api.{Configuration, Environment}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import scalaj.http.Http
import services.{Counter, Job}

import scala.concurrent.duration._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class RestfulImageController @Inject()(actorSystem: ActorSystem, cc: ControllerComponents)
                                      (implicit e: ExecutionContext) extends AbstractController(cc) {

  import RestfulImageController._


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  //  def process(urls: Array[String])(implicit e: ExecutionContext): (String, Array[Future[String]]) = {
  def process(urls: Array[String]): String = {
    //    val jobId = counter.nextCount()
    val jobId = UUID.randomUUID().toString

    actorSystem.scheduler.scheduleOnce(1.micros) {
      val urls_dis = urls.distinct
      val maps = urls_dis.map(url => {
        val uuid = UUID.randomUUID().toString
        val suffix = url.substring(url.lastIndexOf(".") + 1);
        val filename = uuid + "-img." + suffix
        val f = s"$prefix$filename"
        (url -> f)
      }).toMap


      job2urls(jobId) = Job(jobId, urls_dis, maps, urls_dis.map(url => Future {
        val r = Http(url).asString
        val filename = maps(url)
//        Thread.sleep(5000);
        // 创建OSSClient实例。
        val ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
        ossClient.putObject(bucketName, s"$prefix$filename", new ByteArrayInputStream(r.body.getBytes))
        // 关闭OSSClient。
        ossClient.shutdown();
        filename
      }))
      jobId
    }(actorSystem.dispatcher) // run scheduled tasks using the actor system's dispatcher

    jobId
  }

  def allImageurls(): Array[String] = {
    val arr = ArrayBuffer.empty[String]
    val ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
    val objectListing = ossClient.listObjects(bucketName, prefix)
    objectListing.getObjectSummaries.toArray().foreach(arr += _.asInstanceOf[OSSObjectSummary].getKey)
    arr.toArray
  }


  def images(jobId: String): Action[AnyContent] = Action {
    jobId match {
      case id if id.isEmpty => {
        val success = for {
          job <- job2urls.values
          (future, index) <- job.futures.zipWithIndex if future.isCompleted
        }
          yield job.maps(job.urls(index))

        Ok(Json.toJson(Map("urls" -> success)))
      }
      case id if job2urls.get(id).isDefined => {

        val job = job2urls(jobId)
        val pending = ArrayBuffer.empty[String]
        val success = ArrayBuffer.empty[String]
        job.futures.zipWithIndex.foreach(e => {
          val (f, index) = e
          if (f.isCompleted)
            success += job.maps(job.urls(index))
          else
            pending += job.urls(index)
        })

        val r1 = Json.obj(
          "Status" -> "SUCCESS",
          "urls" -> success
        )
        val r2 = Json.obj(
          "Status" -> "PENDING",
          "urls" -> pending
        )
        val r = Json.arr(r1, r2)
        Ok(r)
      }
      case _ =>
        BadRequest("当前没有该作业")
    }
  }

  def upload(): Action[AnyContent] = Action { request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    //    println(s"josnBody is $jsonBody")
    // Expecting json body
    jsonBody
      .map { json =>
        val urls = (json \ "urls").as[Array[String]]
        val jobId = process(urls)
        Ok(Json.toJson(Map("jobId" -> jobId)))
      }
      .getOrElse {
        BadRequest("Expecting application/json request body")
      }
  }

}

object RestfulImageController {
  import scala.collection.JavaConverters._
  val config: Configuration= Configuration.load(Environment.simple())

  val aliyunos: Map[String, String] = config.underlying.getAnyRef("aliyunos").asInstanceOf[java.util.HashMap[String, String]].asScala.toMap

  val endpoint: String = aliyunos("endpoint")
  // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
  val accessKeyId: String = aliyunos("accessKeyId")
  val accessKeySecret: String = aliyunos("accessKeySecret")
  val bucketName: String = aliyunos("bucketName")
  val prefix: String = aliyunos("prefix")
  val job2urls = mutable.HashMap.empty[String, Job]


}
