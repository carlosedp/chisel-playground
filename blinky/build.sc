import mill._
import mill.scalalib._
import scalafmt._
import coursier.MavenRepository
import $ivy.`com.goyeau::mill-scalafix:0.2.2`
import com.goyeau.mill.scalafix.ScalafixModule

def mainClass = Some("Toplevel")

val defaultVersions = Map(
  "scala"             -> "2.12.13",
  "chisel3"           -> "3.4.3",
  "chisel-iotesters"  -> "1.5.3",
  "chiseltest"        -> "0.3.3",
  "scalatest"         -> "3.2.9",
  "organize-imports"  -> "0.5.0",
  "semanticdb-scalac" -> "4.4.20"
)
val binCrossScalaVersions = Seq("2.12.10")

trait HasChisel3 extends ScalaModule {
  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"edu.berkeley.cs::chisel3:${defaultVersions("chisel3")}",
    ivy"com.carlosedp::scalautils:0.4.0"
  )
  override def scalacPluginIvyDeps = super.scalacPluginIvyDeps() ++ Agg(
    ivy"edu.berkeley.cs:::chisel3-plugin:${defaultVersions("chisel3")}"
  )
  override def scalacOptions = super.scalacOptions() ++ Seq(
    // Enables autoclonetype2 in 3.4.x (remove in chisel3 3.5)
    "-P:chiselplugin:useBundlePlugin"
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
      "-language:reflectiveCalls",
      "-feature",
      "-Xfatal-warnings",
      "-Ywarn-value-discard",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-Xsource:2.11"
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
