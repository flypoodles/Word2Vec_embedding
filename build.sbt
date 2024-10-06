ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.12"
val meta = """META.INF(.)*""".r
ThisBuild / assemblyMergeStrategy := {
    case PathList("META-INF", xs @_*) =>
    xs match {
        case "MANIFEST.MF" :: Nil => MergeStrategy.discard
        case "services" :: _ => MergeStrategy.concat
        case _ => MergeStrategy.discard
    }
    case "reference.conf" => MergeStrategy.concat
    case x if x.endsWith(".proto") => MergeStrategy.rename
    case x if x.contains("hadoop") => MergeStrategy.first
    case x => MergeStrategy.first
}
lazy val root = (project in file("."))
  .settings(
    name := "wordCount",
      assembly / mainClass := Some("Entry"),
      assembly / assemblyJarName := "mapreduce.jar",
    libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.3.4",
    libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.4" ,
    libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.4",

    libraryDependencies += "com.knuddels" % "jtokkit" % "1.1.0",

    libraryDependencies += "org.slf4j"%"slf4j-api"%"2.0.16",

    libraryDependencies += "ch.qos.logback"%"logback-classic"% "1.4.6",

    libraryDependencies += "com.typesafe" % "config"% "1.4.3",

    libraryDependencies += "org.deeplearning4j" % "deeplearning4j-ui-model" % "1.0.0-M2.1",

    libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-M2.1",

    libraryDependencies += "org.deeplearning4j" % "deeplearning4j-nlp" % "1.0.0-M2.1",
    libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-M2.1",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",


  )



