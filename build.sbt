name := """curator-x-discovery-spray-scala"""

version := "1.0"

scalaVersion := "2.11.4"

val sprayVersion = "1.3.2"

libraryDependencies ++= Seq("can", "routing") map { a => "io.spray" %% s"spray-$a" % sprayVersion }

libraryDependencies += "io.spray" %% "spray-json" % "1.3.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"

libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % Test

Revolver.settings

assemblySettings

libraryDependencies += "org.specs2" %% "specs2-core" % "2.4.9" % "test"

scalacOptions += "-feature"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies += "org.apache.curator" % "curator-framework" % "2.6.0"