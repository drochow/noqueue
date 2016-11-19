import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayScala

name := """NoQueue"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-language:reflectiveCalls", "-language:postfixOps", "-language:implicitConversions")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "jitpack" at "https://jitpack.io"

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

scalariformSettings

libraryDependencies ++= Seq(
  specs2 % Test,
	"org.specs2"            %% "specs2-matcher-extra"       % "3.8.5"           % Test,
  "com.nimbusds"          % "nimbus-jose-jwt"             % "4.11.2",
  "org.postgresql"        % "postgresql"                  % "9.4-1202-jdbc41",
  "com.typesafe.slick"    %% "slick-hikaricp"             %  "3.1.1",
  "com.typesafe.slick"    %%  "slick" % "3.1.1",
  "org.scalatest"         %%  "scalatest"                 %      "2.2.5"       %    "test",
  "com.h2database"        %   "h2"                        %      "1.4.187"     %   "test"
)

fork in run := true

routesGenerator := InjectedRoutesGenerator

// setting a maintainer which is used for all packaging types
maintainer := "David Kaatz"

fork in run := false

// exposing the play ports
dockerExposedPorts in Docker := Seq(9000, 9443)


// run this with: docker run -p 9000:9000 NoQueue:1.0-SNAPSHOT