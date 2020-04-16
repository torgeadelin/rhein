enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSJUnitPlugin)

scalaVersion := "2.12.8"

libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.7.0"
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test->default"
libraryDependencies += "junit" % "junit" % "4.12" % "test"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
