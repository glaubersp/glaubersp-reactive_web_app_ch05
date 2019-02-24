name := """reactive_web_app_ch05"""
organization := "org.glauber"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"
logLevel := util.Level.Debug


libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.16.2-play27"
libraryDependencies += "io.github.cquiroz" %% "scala-java-time" % "2.0.0-RC1"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
//libraryDependencies += specs2 % Test

//scalacOptions in Test ++= Seq("-Yrangepos")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
