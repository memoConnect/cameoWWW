import play.PlayScala

name := """cameoWWW"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.typesafe.play.plugins" %% "play-statsd" % "2.3.0"
  //  "com.typesafe" %% "play-plugins-mailer" % "2.2.0"
)