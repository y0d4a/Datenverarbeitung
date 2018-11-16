val ScalatraVersion = "2.6.3"

organization := "de.htw-berlin.ecco"

name := "floatBackendApp"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.12"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
"org.mongodb.spark" %% "mongo-spark-connector" % "2.3.1",
  "org.apache.spark" %% "spark-core" % "2.3.2",
  "org.apache.spark" %% "spark-sql" % "2.3.2",
  "org.apache.spark" %% "spark-streaming" % "2.3.2" % "provided",
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "com.sun.jersey" % "jersey-core" % "1.19.4",
  "com.sun.jersey" % "jersey-server" % "1.19.4",
  "com.sun.jersey" % "jersey-client" % "1.19.4",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s"   %% "json4s-jackson" % "3.5.2"
  
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
