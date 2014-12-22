name := """curator-x-discovery-spray-scala"""

version := "1.0"

scalaVersion := "2.11.4"

 //val sprayVersion    = "1.3.2"
val akkaHttpVersion = "1.0-M1"
val akkaVersion     = "2.3.8"
val curatorVersion  = "2.7.0"
val specs2Version   = "2.4.15"
val logbackVersion  = "1.1.2"

// libraryDependencies ++= Seq("can", "routing") map { a => "io.spray" %% s"spray-$a" % sprayVersion }
libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaHttpVersion

libraryDependencies += "io.spray" %% "spray-json" % "1.3.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

// libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % "test"

Revolver.settings

libraryDependencies += "org.specs2" %% "specs2-core" % specs2Version % "test" force()

scalacOptions += "-feature"

scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies += "org.apache.curator" % "curator-framework" % curatorVersion

libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackVersion