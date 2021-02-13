name := """Youtube Analyzer"""
organization := ""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0"
libraryDependencies += "com.vdurmont" % "emoji-java" % "5.1.1"
libraryDependencies += "org.powermock" % "powermock-module-junit4" % "2.0.7" % Test
libraryDependencies += "org.powermock" % "powermock-api-mockito2" % "2.0.4" % Test
libraryDependencies += "junit" % "junit" % "4.8.1" % "test"
libraryDependencies += "org.mockito" % "mockito-core" % "2.7.19" % Test
