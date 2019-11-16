package services

import java.io.File
import java.util.UUID

import scala.concurrent.Future

case class Job (jobID: String,
                urls: Array[String],
                maps: Map[String, String], // url => uuid_string
                futures: Array[Future[String]])
