
name := "journey-to-scala"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq (
  // test toolkits for scala
  "org.scalatest" %% "scalatest" % "3.0.7" % Test,
  // circe for json support
  "io.circe" %% "circe-core" % "0.10.0",
  "io.circe" %% "circe-generic" % "0.10.0",
  "io.circe" %% "circe-parser" % "0.10.0",
  "io.circe" %% "circe-optics" % "0.10.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.20.1" // circe extension
)

dependencyOverrides ++= Seq(
  "com.google.guava" % "guava" % "20.0"
)

// assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}

// scala package
enablePlugins(UniversalPlugin)

// add jar
mappings in Universal := {
  // universalMappings: Seq[(File,String)]
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  // removing means filtering
  val filtered = universalMappings filter {
    case (file, name) =>  ! name.endsWith(".jar")
  }
  // add the fat jar
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

