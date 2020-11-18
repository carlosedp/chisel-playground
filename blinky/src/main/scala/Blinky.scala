import chisel3._

/**
  * The blinking LED component.
  */
class Blinky(freq: Int) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(UInt(1.W))
    val led1 = Output(UInt(1.W))
  })
  val CNT_MAX = (freq / 2 - 1).U;

  val cntReg = RegInit(chiselTypeOf(CNT_MAX), init = 1.U)
  val blkReg = RegInit(1.U(1.W))

  cntReg := cntReg + 1.U
  when(cntReg === CNT_MAX) {
    cntReg := 0.U
    blkReg := ~blkReg
  }
  io.led0 := blkReg
  io.led1 := ~blkReg
}

/**
  * An object extending App to generate the Verilog code.
  */
object Blinky extends App {
  chisel3.Driver.execute(args, () => new BlinkyTop())
}
