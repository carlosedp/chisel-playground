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

  val blk = RegInit(1.U)
  val (counterValue, counterWrap) = Counter(true.B, freq)
  val CNT_MAX = (freq / 2 - 1).U;

  when(counterValue === CNT_MAX) {
    blk := ~blk
  }
  io.led0 := blk
  io.led1 := ~blk
  io.led2 := blk

  // io.led0 := counterValue(22)
  // io.led1 := counterValue(23)
  // io.led2 := counterValue(24)

}
