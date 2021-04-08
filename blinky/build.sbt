// See README.md for license details.

lazy val myproject = (project in file("."))
  .settings(
    organization := "MyOrganization",
    name := "chisel-blinky",
    version := "0.0.1",
    scalaVersion := "2.12.13",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    maxErrors := 3
  )
crossScalaVersions := Seq("2.12.10", "2.11.12")
// Library default versions
val defaultVersions = Map(
  "chisel3"          -> "3.4.3",
  "chisel-iotesters" -> "1.5.0",
  "chiseltest"       -> "0.3.1",
  "scalatest"        -> "3.2.2"
)
// Import libraries
externalResolvers += "Scalautils" at "https://maven.pkg.github.com/carlosedp/scalautils"
libraryDependencies += "com.carlosedp" %% "scalautils" % "0.1.0"

libraryDependencies ++= Seq("chisel3", "chisel-iotesters", "chiseltest").map { dep: String =>
  "edu.berkeley.cs" %% dep % sys.props
    .getOrElse(dep + "Version", defaultVersions(dep))
}
libraryDependencies += "org.scalatest" %% "scalatest" % defaultVersions("scalatest")
addCompilerPlugin("org.scalamacros"     % "paradise"  % "2.1.1" cross CrossVersion.full)

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

def scalacOptionsVersion(scalaVersion: String): Seq[String] =
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _                                              => Seq("-Xsource:2.11")
    }
  }

def javacOptionsVersion(scalaVersion: String): Seq[String] =
  Seq() ++ {
    // Scala 2.12 requires Java 8. We continue to generate
    //  Java 7 compatible code for Scala 2.11
    //  for compatibility with old clients.
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 =>
        Seq("-source", "1.7", "-target", "1.7")
      case _ =>
        Seq("-source", "1.8", "-target", "1.8")
    }
  }

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

scalacOptions ++= scalacOptionsVersion(scalaVersion.value)

javacOptions ++= javacOptionsVersion(scalaVersion.value)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:reflectiveCalls",
  "-feature",
  "-Xfatal-warnings"
)
scalacOptions ++= Seq(
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
