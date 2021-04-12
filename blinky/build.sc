import mill._
import mill.scalalib._
import scalafmt._
import coursier.MavenRepository
import $ivy.`com.goyeau::mill-scalafix:0.2.1`
import com.goyeau.mill.scalafix.ScalafixModule

def mainClass = Some("Toplevel")

val defaultVersions = Map(
  "scala"             -> "2.12.13",
  "chisel3"           -> "3.4.3",
  "chisel-iotesters"  -> "1.5.3",
  "chiseltest"        -> "0.3.3",
  "scalatest"         -> "3.2.7",
  "organize-imports"  -> "0.5.0",
  "paradise"          -> "2.1.1",
  "semanticdb-scalac" -> "4.4.12"
)
val binCrossScalaVersions  = Seq("2.12.10", "2.11.12")
val fullCrossScalaVersions = Seq("2.12.10", "2.11.12")

trait HasChisel3 extends ScalaModule {
  override def ivyDeps = Agg(
    ivy"edu.berkeley.cs::chisel3:${defaultVersions("chisel3")}"
  )
}

trait HasChiselTests extends CrossSbtModule {
  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:${defaultVersions("scalatest")}",
      ivy"edu.berkeley.cs::chisel-iotesters:${defaultVersions("chisel-iotesters")}",
      ivy"edu.berkeley.cs::chiseltest:${defaultVersions("chiseltest")}"
    )
    def repositories = super.repositories ++ Seq(
      MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")

    def testOne(args: String*) = T.command {
      super.runMain("org.scalatest.run", args: _*)
    }
  }
}

trait HasMacroParadise extends ScalaModule {
  // Enable macro paradise for @chiselName et al
  val macroPlugins        = Agg(ivy"org.scalamacros:::paradise:${defaultVersions("paradise")}")
  def scalacPluginIvyDeps = macroPlugins
  def compileIvyDeps      = macroPlugins
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

trait CodeQuality extends ScalafixModule with ScalafmtModule {
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${defaultVersions("organize-imports")}")
  // Override semanticdb version due to unavailable 4.4.0 for Scala 2.12.13.
  override def scalacPluginIvyDeps =
    Agg(ivy"org.scalameta:::semanticdb-scalac:${defaultVersions("semanticdb-scalac")}")
}

trait ScalacOptions extends ScalaModule {
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-language:reflectiveCalls",
      "-feature",
      "-Xfatal-warnings",
      "-Ywarn-value-discard",
      "-Ywarn-dead-code",
      "-Ywarn-unused"
    )
  }
}

object toplevel
    extends CrossSbtModule
    with HasChisel3
    with HasChiselTests
    with HasXsource211
    with HasMacroParadise
    with CodeQuality
    with ScalacOptions {
  override def millSourcePath = super.millSourcePath
  def crossScalaVersion       = defaultVersions("scala")
}
