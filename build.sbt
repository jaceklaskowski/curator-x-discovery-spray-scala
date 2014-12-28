name         := """curator-x-discovery-spray-scala"""
organization := "pl.japila"
version      := "1.0"
scalaVersion := Version.scala

libraryDependencies := Dependencies.all

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
