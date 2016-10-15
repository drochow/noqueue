import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayScala
//import NativePackegerKeys._ // with auto plugins this won't be necessary soon
import com.typesafe.sbt.SbtNativePackager.autoImport._

name := """NoQueue"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "jitpack" at "https://jitpack.io"


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc41",
  "com.nimbusds" % "nimbus-jose-jwt" % "4.11.2",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.flywaydb" %% "flyway-play" % "2.2.1",
  "org.apache.commons" % "commons-email" % "1.4",
  "com.github.tminglei" %% "slick-pg" % "0.12.0",
  "com.github.kenglxn.QRGen" % "android" % "2.2.0"
)

routesGenerator := InjectedRoutesGenerator

// setting a maintainer which is used for all packaging types
maintainer := "David Kaatz"

fork in run := false

fork in run := true

// exposing the play ports
dockerExposedPorts in Docker := Seq(9000, 9443)

// run this with: docker run -p 9000:9000 NoQueue:1.0-SNAPSHOT