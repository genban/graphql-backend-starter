name:= "coin-trader"

version := "0.1"

description := "auto buy and sale from each platforms to get price difference"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-deprecation","-language:implicitConversions")

libraryDependencies ++= Seq(
  "com.squareup.okhttp3" % "okhttp" % "3.9.1",
  "org.scalatest"       %% "scalatest"             % "3.0.1" % Test
)

def latestScalafmt = "1.3.0"

commands += Command.args("scalafmt", "Run scalafmt cli.") {
  case (state, args) =>
    val Right(scalafmt) = org.scalafmt.bootstrap.ScalafmtBootstrap.fromVersion(latestScalafmt)
    scalafmt.main("--non-interactive" +: args.toArray)
    state
}

lazy val lang = RootProject(file("../very-util-lang"))

lazy val json = RootProject(file("../very-util-json"))

lazy val log = RootProject(file("../very-util-log"))

lazy val scalaj = RootProject(file("../very-util-scalaj-http"))

lazy val akka = RootProject(file("../very-util-akka"))

lazy val root = (project in file("."))
  .dependsOn(json, lang, log, scalaj, akka)

mainClass in Compile := Some("Server")
