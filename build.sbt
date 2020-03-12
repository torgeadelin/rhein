enablePlugins(ScalaJSPlugin)

scalaVersion := "2.12.8" // or any other Scala version >= 2.10.2

libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.7.0"

// ScalaJS Dependencies
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.7.0"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
