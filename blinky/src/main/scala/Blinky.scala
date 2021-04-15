import chisel3._
import chisel3.util._

/**
 * The blinking LED component.
 */
class Blinky(freq: Int) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
    val led1 = Output(Bool())
    val led2 = Output(Bool())
  })

  val led                         = RegInit(0.U(1.W))
  val (counterValue, counterWrap) = Counter(true.B, freq / 2)
  when(counterWrap) {
    led := ~led
  }

  io.led0 := led
  io.led1 := ~led
  io.led2 := led
}
