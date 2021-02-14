import mill._, scalalib._, scalafmt._
import coursier.MavenRepository
import java.io._
import java.nio.file.{Paths, Files}
import $ivy.`org.scalatra.scalate::scalate-core:1.9.6`, org.fusesource.scalate._
import $ivy.`org.slf4j:slf4j-simple:1.7.30`

object Deps {
  val mainClass = "Toplayer"
  val scalaVersion = "2.12.12"
  val chiselVersion = "3.4.1"
}

/**
  * Scala 2.12 module that is source-compatible with 2.11.
  * This is due to Chisel's use of structural types. See
  * https://github.com/freechipsproject/chisel3/issues/606
  */
trait HasXsource211 extends ScalaModule {
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-deprecation",
      "-unchecked",
      "-Xsource:2.11"
    )
  }
}

trait HasChisel3 extends ScalaModule {
  override def ivyDeps = Agg(
    ivy"edu.berkeley.cs::chisel3:${Deps.chiselVersion}"
  )
}

trait HasChiselTests extends CrossSbtModule {
  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.2.2",
      ivy"edu.berkeley.cs::chisel-iotesters:1.5.0",
      ivy"edu.berkeley.cs::chiseltest:0.3.1"
    )
    def repositories = super.repositories ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

trait HasMacroParadise extends ScalaModule {
  // Enable macro paradise for @chiselName et al
  val macroPlugins = Agg(ivy"org.scalamacros:::paradise:2.1.1")
  def scalacPluginIvyDeps = macroPlugins
  def compileIvyDeps = macroPlugins
}

object blinky
    extends CrossSbtModule
    with HasChisel3
    with HasChiselTests
    with HasXsource211
    with ScalafmtModule
    with HasMacroParadise {
  override def millSourcePath = super.millSourcePath
  def crossScalaVersion = Deps.scalaVersion
  def mainClass = Some(Deps.mainClass)
}
