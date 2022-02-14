import mill._
import mill.scalalib._
import scalafmt._
import coursier.MavenRepository
import $ivy.`com.goyeau::mill-scalafix:0.2.2`
import com.goyeau.mill.scalafix._

def mainClass = Some("Toplevel")

val defaultVersions = Map(
  "scala"             -> "2.13.8",
  "chisel3"           -> "3.5.1",
  "chisel-iotesters"  -> "1.5.3",
  "chiseltest"        -> "0.5.1",
  "scalatest"         -> "3.2.11",
  "organize-imports"  -> "0.6.0",
  "scalautils"        -> "0.9.0",
  "semanticdb-scalac" -> "4.4.35"
)

trait HasChisel3 extends ScalaModule {
  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"edu.berkeley.cs::chisel3:${defaultVersions("chisel3")}",
    ivy"com.carlosedp::scalautils:${defaultVersions("scalautils")}"
  )
  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"edu.berkeley.cs:::chisel3-plugin:${defaultVersions("chisel3")}"
  )
}

trait HasChiselTests extends CrossSbtModule {
  object test extends Tests {
    override def ivyDeps = super.ivyDeps() ++ Agg(
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

trait CodeQuality extends ScalafixModule with ScalafmtModule {
  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:${defaultVersions("organize-imports")}")
  // Override semanticdb version due to unavailable 4.4.10 for Scala 2.12.14.
  override def scalacPluginIvyDeps =
    super.scalacPluginIvyDeps() ++ Agg(ivy"org.scalameta:::semanticdb-scalac:${defaultVersions("semanticdb-scalac")}")
}

trait Aliases extends Module {
  def fmt() = T.command {
    toplevel.reformat()
    toplevel.fix()
  }
}

trait ScalacOptions extends ScalaModule {
  override def scalacOptions = T {
    super.scalacOptions() ++ Seq(
      "-unchecked",
      "-deprecation",
      "-language:reflectiveCalls",
      "-feature",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Ywarn-dead-code",
      "-Ywarn-unused"
    )
  }
}

object toplevel
    extends CrossSbtModule
    with HasChisel3
    with HasChiselTests
    with CodeQuality
    with Aliases
    with ScalacOptions {
  override def millSourcePath = super.millSourcePath
  def crossScalaVersion       = defaultVersions("scala")
}
