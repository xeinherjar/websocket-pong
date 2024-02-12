val scala3Version = "3.3.1"
val http4sVersion = "1.0.0-M40"
val circeVersion = "0.14.+"

addCommandAlias("f", ";scalafixAll;scalafmtAll")

libraryDependencies ++= Seq(
  "com.comcast" %% "ip4s-core" % "3.3.+",
  "ch.qos.logback" % "logback-core" % "1.4.14",
  "ch.qos.logback" % "logback-classic" % "1.4.14",
  "org.typelevel" %% "cats-core" % "2.10.+",
  "org.typelevel" %% "cats-core" % "2.10.+",
  "org.typelevel" %% "log4cats-slf4j" % "2.6.+",
  "co.fs2" %% "fs2-core" % "3.6.1",
  "org.typelevel" %% "cats-effect" % "3.5.+",
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % circeVersion,
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % circeVersion,
  "com.disneystreaming" %% "weaver-cats" % "0.+" % Test
)

testFrameworks += new TestFramework("weaver.framework.CatsEffect")

ThisBuild / evictionErrorLevel := Level.Info
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root = project
  .in(file("."))
  .settings(
    name := "pong",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
  )
