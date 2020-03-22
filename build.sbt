enablePlugins(ScalaJSPlugin)

scalaVersion := "2.12.8"

libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.7.0"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
