
lazy val root = (project in file("."))
    .settings (
      name          := "Entity-extraction",
      organization  := "com.bk",
      scalaVersion  := "2.12.3",
      version       := "latest"
    )


resolvers += Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)


libraryDependencies ++= Seq(
  "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0" classifier "models",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)

javaOptions in Universal ++= Seq(
  // -J params will be added as jvm parameters
  "-J-Xmx2048m",
  "-J-Xms256m"
)

maintainer in Docker := "Paul Brown <pbrown@equalexperts.com>"
packageSummary in Docker := "Entity Extraction Service"
description in Docker := "Entity extraction service"
packageDescription := "Docker service with entity extraction"
// Only add this if you want to rename your docker image name
packageName in Docker := "octo-entity-extraction"
