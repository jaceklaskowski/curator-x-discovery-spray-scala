import sbt._

object Version {
  val akka      = "2.3.8"
  val akkaHttp  = "1.0-M2"
  val curator   = "2.7.0"
  val logback   = "1.1.2"
  val scala     = "2.11.4"
  val specs2    = "2.4.15"
}

object Library {
  val akkaActor         = "com.typesafe.akka"  %% "akka-actor"                        % Version.akka
  val akkaHttp          = "com.typesafe.akka"  %% "akka-http-experimental"            % Version.akkaHttp
  val akkaHttpSprayJson = "com.typesafe.akka"  %% "akka-http-spray-json-experimental" % Version.akkaHttp
  val akkaHttpXml       = "com.typesafe.akka"  %% "akka-http-xml-experimental"        % Version.akkaHttp
  val akkaSlf4j         = "com.typesafe.akka"  %% "akka-slf4j"                        % Version.akka
  val akkaStream        = "com.typesafe.akka"  %% "akka-stream-experimental"          % Version.akkaHttp
  val akkaTestkit       = "com.typesafe.akka"  %% "akka-testkit"                      % Version.akka
  val curator           = "org.apache.curator" %  "curator-framework"                 % Version.curator
  val logbackClassic    = "ch.qos.logback"     %  "logback-classic"                   % Version.logback
  val specs2            = "org.specs2"         %% "specs2-core"                       % Version.specs2
}

object Dependencies {

  import Library._

  val all = Seq(
    akkaActor,
    akkaHttp,
    akkaHttpSprayJson,
    akkaHttpXml,
    akkaSlf4j,
    akkaStream,
    curator,
    logbackClassic,
    akkaTestkit % "test",
    specs2      % "test"
  )
}
