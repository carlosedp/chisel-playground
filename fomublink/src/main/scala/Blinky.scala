import chisel3._
import chisel3.stage._
import chisel3.util._

// Blinking LED top layer
class BlinkTop extends Module {
  val io = IO(new Bundle {
    // 48MHz Clock input is implicit
    // LED outputs
    // --------
    val rgb0 = Output(UInt(1.W))
    val rgb1 = Output(UInt(1.W))
    val rgb2 = Output(UInt(1.W))

    // USB Pins (which should be statically driven if not being used).
    // --------
    val usb_dp = Output(UInt(1.W))
    val usb_dn = Output(UInt(1.W))
    val usb_dp_pu = Output(UInt(1.W))
  })

  io.usb_dp := "b0".U
  io.usb_dn := "b0".U
  io.usb_dp_pu := "b0".U

  // Instantiate the PLL with implicit clock
  val pll = Module(new ICE40pllBlackbox())
  pll.io.clki := clock

  // Instantiate the RGB Led driver from ICE40 FPGA.
  val ledDrv = Module(new ICE40ledDrvBlackBox())
  io.rgb0 := ledDrv.io.rgb0_out
  io.rgb1 := ledDrv.io.rgb1_out
  io.rgb2 := ledDrv.io.rgb2_out

  withClockAndReset(pll.io.clko, 0.B) {
    // In this withClock scope, all synchronous elements are clocked against pll.io.clko.
    val (counterValue, counterWrap) = Counter(true.B, 48000000)
    when(counterValue >= 0.U && counterValue < 16000000.U) {
      ledDrv.io.rgb0_in := 1.U
      ledDrv.io.rgb1_in := 0.U
      ledDrv.io.rgb2_in := 0.U
    }.elsewhen(counterValue >= 16000000.U && counterValue < 32000000.U) {
      ledDrv.io.rgb0_in := 0.U
      ledDrv.io.rgb1_in := 1.U
      ledDrv.io.rgb2_in := 0.U
    }.elsewhen(counterValue >= 32000000.U && counterValue < 48000000.U) {
      ledDrv.io.rgb0_in := 0.U
      ledDrv.io.rgb1_in := 0.U
      ledDrv.io.rgb2_in := 1.U
    }
  }
}

/** An object extending App to generate the Verilog code.
  */
object Blink extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new BlinkTop(),
    Array("-X", "verilog") ++ args
  )
}
