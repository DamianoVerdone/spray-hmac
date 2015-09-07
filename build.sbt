name := "spray-hmac"

organization := "mbilski"

version := "1.0.0"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.3", "2.11.7")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "io.spray" %% "spray-routing" % "1.3.3",
    "com.typesafe.akka" %% "akka-actor" % "2.3.13",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.12.4" % "test",
    "io.spray" %% "spray-testkit" % "1.3.2" % "test"
  )
}
