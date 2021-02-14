import chisel3._

// Blinking LED top layer
class Toplayer extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
    val led1 = Output(Bool())
    val led2 = Output(Bool())
  })

  // Instantiate PLL module based on board
  val board = sys.env("BOARD")
  val pll = Module(new PLL0(board))

  // Instantiate the Blink module using 25Mhz from PLL output
  val bl = Module(new Blinky(25000000))

  // Connect IO between Toplayer and Blinky
  bl.io <> io

  // Connect Clock and Reset(inverted)
  pll.io.clki := clock
  bl.clock := pll.io.clko
  bl.reset := ~reset.asBool()
}

// An object extending App to generate the Verilog code.
object Toplayer extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new Toplayer(),
    Array("-X", "verilog") ++ args
  )
}
