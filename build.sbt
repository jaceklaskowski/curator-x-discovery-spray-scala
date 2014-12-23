name         := """curator-x-discovery-spray-scala"""
version      := "1.0"
scalaVersion := Version.scala

libraryDependencies ++= List(
  Library.akkaActor,
  Library.akkaHttp,
  Library.akkaHttpSprayJson,
  Library.akkaHttpXml,
  Library.akkaSlf4j,
  Library.akkaStream,
  Library.curator,
  Library.logbackClassic,
  Library.sprayJson,
  Library.akkaTestkit % "test",
  Library.specs2      % "test"
)

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

scalacOptions in Test += "-Yrangepos"

Revolver.settings
