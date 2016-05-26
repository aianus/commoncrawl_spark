name := "commoncrawl_spark"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.2.0" % "provided"
libraryDependencies += ("com.martinkl.warc" % "warc-hadoop" % "0.1.0").excludeAll(
  ExclusionRule(organization = "org.apache.hadoop")
)
libraryDependencies += "org.jsoup" % "jsoup" % "1.9.2"
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.10",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang" % "scala-compiler" % "2.11.8",
  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "jline" % "jline" % "2.12.1"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

resolvers += Resolver.mavenLocal
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
