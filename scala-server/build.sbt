val scala3Version = "3.1.1"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.8"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-server",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "com.typesafe.akka" % "akka-actor-typed_2.13" % AkkaVersion,
      "com.typesafe.akka" % "akka-stream-typed_2.13" % AkkaVersion,
      "com.typesafe.akka" % "akka-http_2.13" % AkkaHttpVersion
    )
  )
