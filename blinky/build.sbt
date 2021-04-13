// See README.md for license details.

lazy val blinky = (project in file("."))
  .settings(
    organization := "com.carlosedp",
    name := "chisel-blinky",
    version := "0.0.1",
    scalaVersion := "2.12.13",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    maxErrors := 3
  )

crossScalaVersions := Seq("2.12.10")
// Library default versions
val defaultVersions = Map(
  "chisel3"          -> "3.4.3",
  "chisel-iotesters" -> "1.5.3",
  "chiseltest"       -> "0.3.3",
  "scalatest"        -> "3.2.7",
  "organize-imports" -> "0.5.0"
)
// Import libraries
libraryDependencies ++= Seq("chisel3", "chisel-iotesters", "chiseltest").map { dep: String =>
  "edu.berkeley.cs" %% dep % sys.props
    .getOrElse(dep + "Version", defaultVersions(dep))
}
libraryDependencies += "org.scalatest"                     %% "scalatest"        % defaultVersions("scalatest")
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % defaultVersions("organize-imports")

// Aliases
addCommandAlias("com", "all compile test:compile")
addCommandAlias("rel", "reload")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:reflectiveCalls",
  "-feature",
  "-Xfatal-warnings",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Xsource:2.11"
)
