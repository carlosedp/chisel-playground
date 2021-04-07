import chisel3._
import chisel3.experimental._
import chisel3.util._
import firrtl.annotations.PresetAnnotation

// Blinking LED top layer
// We use RawModule to avoid Chisel creating a module with implicit clock and reset
class BlinkTop extends RawModule {
  val io = IO(new Bundle {
    // 48MHz Clock input
    val clki  = Input(Clock())
    val reset = Input(AsyncReset())

    // LED outputs
    // --------
    val rgb0 = Output(Bool())
    val rgb1 = Output(Bool())
    val rgb2 = Output(Bool())

    // USB Pins (which should be statically driven if not being used).
    // --------
    val usb_dp    = Output(Bool())
    val usb_dn    = Output(Bool())
    val usb_dp_pu = Output(Bool())
  })

  // initialize registers to their reset value when the bitstream is programmed since there is no reset wire
  annotate(new ChiselAnnotation {
    override def toFirrtl = PresetAnnotation(io.reset.toTarget)
  })

  // Disconnect USB pins
  io.usb_dp := false.B
  io.usb_dn := false.B
  io.usb_dp_pu := false.B

  // Instantiate the PLL with clock
  val pll = Module(new ICE40pllBlackbox())
  pll.io.clki := io.clki

  // Instantiate the RGB Led driver from ICE40 FPGA.
  val ledDrv = Module(new ICE40ledDrvBlackBox())
  io.rgb0 := ledDrv.io.rgb0_out
  io.rgb1 := ledDrv.io.rgb1_out
  io.rgb2 := ledDrv.io.rgb2_out

  chisel3.withClockAndReset(pll.io.clko, io.reset) {
    // In this withClock scope, all synchronous elements are clocked against pll.io.clko.
    val (counterValue, _) = Counter(true.B, 48000000)
    ledDrv.io.rgb0_in := counterValue(23)
    ledDrv.io.rgb1_in := counterValue(24)
    ledDrv.io.rgb2_in := counterValue(25)
  }
}

/**
 * An object extending App to generate the Verilog code.
 */
object Blink extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new BlinkTop(),
    Array("-X", "verilog") ++ args
  )
}
