name := "backend"
version := "0.1.0-SNAPSHOT"

description := "An example GraphQL server written with akka-http and sangria."

scalaVersion := "2.12.1"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.3",

  //slick posggreql
  "org.postgresql" % "postgresql" % "42.0.0",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.github.tminglei" %% "slick-pg" % "0.15.0-M4",


// json4s adapter
  "com.github.tminglei" %% "slick-pg_json4s" % "0.15.0-M4",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.12.0",
  "org.sangria-graphql" %% "sangria-json4s-native" % "1.0.0",

  "org.scalatest" %% "scalatest" % "3.0.1" % Test


)

lazy val lang = RootProject(file("../very-util-lang"))

lazy val json = RootProject(file("../very-util-json"))

lazy val redis_client = RootProject(file("../scala-redis-client"))

lazy val root = (project in file(".")).dependsOn(json,lang,redis_client)