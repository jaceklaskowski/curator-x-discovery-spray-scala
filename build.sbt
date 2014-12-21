name := """curator-x-discovery-spray-scala"""

version := "1.0"

scalaVersion := "2.11.4"

val sprayVersion   = "1.3.2"
val akkaVersion    = "2.3.8"
val curatorVersion = "2.7.0"
val specs2Version  = "2.4.15"

libraryDependencies ++= Seq("can", "routing") map { a => "io.spray" %% s"spray-$a" % sprayVersion }

libraryDependencies += "io.spray" %% "spray-json" % "1.3.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % "test"

Revolver.settings

assemblySettings

libraryDependencies += "org.specs2" %% "specs2-core" % specs2Version % "test"

scalacOptions += "-feature"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies += "org.apache.curator" % "curator-framework" % curatorVersion