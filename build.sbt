ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

/**
 * Reference: https://doc.akka.io/docs/akka-http/current/introduction.html
 */
resolvers += "Akka library repository".at("https://repo.akka.io/maven")

val AkkaVersion = "2.9.3"
val AkkaHttpVersion = "10.6.3"
val CirceVersion = "0.14.9"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2", // Akka HTTP + Circe integration
  "com.typesafe" % "config" % "1.4.3",

  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.scalamock" %% "scalamock" % "6.0.0" % Test,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test, // Add this line
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "receipt-processor-challenge",
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.ScalaLibrary
  )
