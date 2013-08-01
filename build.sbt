name := "TwitterTypeahead"

version := "0.0.1"

organization := "net.liftmodules"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.10.0", "2.10.1")

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions += "-language:postfixOps"

scalacOptions += "-language:implicitConversions"

scalacOptions += "-language:existentials"

scalacOptions += "-unchecked"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5.1"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "ch.qos.logback"    %  "logback-classic"    % "1.0.6"
  )
}

