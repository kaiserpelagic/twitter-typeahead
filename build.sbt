name := "TwitterTypeahead"

version := "0.0.1-SNAPSHOT"

organization := "net.liftmodules"

liftVersion <<= liftVersion ?? "2.5-SNAPSHOT"

liftEdition <<= liftVersion apply { _.substring(0,3) }

name <<= (name, liftEdition) { (n, e) =>  n + "_" + e }

scalaVersion := "2.10.0"

//crossScalaVersions := Seq("2.10.0", "2.10.1")

resolvers += "CB Central Mirror" at "http://repo.cloudbees.com/content/groups/public"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions += "-language:postfixOps"

scalacOptions += "-language:implicitConversions"

scalacOptions += "-language:existentials"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5.1"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "provided",
    "ch.qos.logback"    %  "logback-classic"    % "1.0.6"
  )
}

publishTo <<= version { _.endsWith("SNAPSHOT") match {
        case true  => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        case false => Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }
}

// For local deployment:

credentials += Credentials( file("sonatype.credentials") )

// For the build server:

credentials += Credentials( file("/private/liftmodules/sonatype.credentials") )

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
	<url>https://github.com/liftmodules/amqp</url>
	<licenses>
	    <license>
	      <name>Apache 2.0 License</name>
	      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
	      <distribution>repo</distribution>
	    </license>
	 </licenses>
	 <scm>
	    <url>git@github.com:kaiserpelagic/twitter-typeahead.git</url>
	    <connection>scm:git:git@github.com:kaiserpelagic/twitter-typeahead.git</connection>
	 </scm>
	 <developers>
	    <developer>
	      <id>kaiserpelagic</id>
	      <name>Greg Kaiserpelagic</name>
	      <url>https://github.com/kaiserpelagic</url>
	     </developer>
	 </developers>
 )


