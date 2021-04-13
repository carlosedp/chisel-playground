import chisel3._
import scalautils.ParseArguments

// Blinking LED top layer
class Toplevel(board: String, invReset: Boolean = true) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
    val led1 = Output(Bool())
    val led2 = Output(Bool())
  })

  // Instantiate PLL module based on board
  val pll: PLL0 = Module(new PLL0(board))

  // Instantiate the Blink module using 25Mhz from PLL output
  val bl: Blinky = Module(new Blinky(25000000))

  // Connect IO between Toplevel and Blinky
  bl.io <> io

  // Connect Clock and Reset(inverted)
  pll.io.clki := clock
  bl.clock := pll.io.clko
  // Define if reset should be inverted based on board switch
  if (invReset) {
    bl.reset := ~reset.asBool()
  } else {
    bl.reset := reset
  }
}

// The Main object extending App to generate the Verilog code.
object Toplevel extends App {

  // Parse command line arguments and extract required parameters
  // pass the input arguments and a list of parameters to be extracted
  // The funcion will return the parameters map and the remaining non-extracted args
  val (params, chiselargs) = ParseArguments(args, List("board", "invreset"))
  val board: String =
    params.getOrElse("board", throw new IllegalArgumentException("The '-board' argument should be informed."))
  val invReset: Boolean =
    params.getOrElse("invreset", "true").toBoolean

  // Generate Verilog
  (new chisel3.stage.ChiselStage).emitVerilog(
    new Toplevel(board, invReset),
    chiselargs
  )
}
