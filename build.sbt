
name := "tictactoe4x"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // injection
  "net.codingwell" %% "scala-guice" % "4.1.1",
  // test
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  // logging
  "org.clapper" %% "grizzled-slf4j" % "1.3.2",

  // actor
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  // json
  "com.typesafe.play" %% "play-json" % "2.6.8",
)


scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)