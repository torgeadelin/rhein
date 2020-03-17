enablePlugins(ScalaJSPlugin)

scalaVersion := "2.12.10" // or any other Scala version >= 2.10.2

// libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.7.0"

// // ScalaJS Dependencies
// libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
// libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.7.0"

lazy val rhein = project
  .in(file("."))
  .settings(
    name := "rhein",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.7",
      "com.lihaoyi" %%% "scalatags" % "0.7.0",
      "com.lihaoyi" %% "scalatags" % "0.7.0"
    )
  )

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
