// See README.md for license details.

lazy val blinky = (project in file("."))
  .settings(
    organization := "com.carlosedp",
    name := "chisel-blinky",
    version := "0.0.1",
    scalaVersion := "2.13.8",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    maxErrors := 3
  )

// Library default versions
val defaultVersions = Map(
  "chisel3"          -> "3.5.1",
  "chisel-iotesters" -> "1.5.3",
  "chiseltest"       -> "0.5.1",
  "scalatest"        -> "3.2.11",
  "organize-imports" -> "0.5.0",
  "scalautils"       -> "0.9.0"
)
// Import libraries
libraryDependencies ++= Seq("chisel3", "chisel-iotesters", "chiseltest").map { dep: String =>
  "edu.berkeley.cs" %% dep % sys.props
    .getOrElse(dep + "Version", defaultVersions(dep))
}
libraryDependencies += "org.scalatest"                     %% "scalatest"        % defaultVersions("scalatest")
libraryDependencies += "com.carlosedp"                     %% "scalautils"       % defaultVersions("scalautils")
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

addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % defaultVersions("chisel3") cross CrossVersion.full)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:reflectiveCalls",
  "-feature",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)
