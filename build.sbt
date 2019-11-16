name := "imageservice"
 
version := "1.0" 
      
lazy val `imageservice` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"
libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += ehcache
libraryDependencies += ws
// https://mvnrepository.com/artifact/com.aliyun.oss/aliyun-sdk-oss
libraryDependencies += "com.aliyun.oss" % "aliyun-sdk-oss" % "3.5.0"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.1"

//libraryDependencies ++= Seq(
//  "org.scalatestplus.play" %% "scalatestplus-play" % "x.x.x" % "test"
//)

libraryDependencies += specs2 % Test


//PlayKeys.devSettings += "play.server.provider" -> "play.core.server.NettyServerProvider"