name := "spray-hmac"

organization := "mbilski"

version := "1.0.0"

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  Seq(
    "io.spray" % "spray-routing" % "1.2.1",
    "com.typesafe.akka" %% "akka-actor" % "2.2.4",
    "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
    "org.scalacheck" %% "scalacheck" % "1.11.3" % "test",
    "io.spray" % "spray-testkit" % "1.2.1" % "test"
  )
}
