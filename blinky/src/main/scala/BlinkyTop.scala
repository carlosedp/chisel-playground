import chisel3._

// Blinking LED top layer
class BlinkyTop extends Module {
  val io = IO(new Bundle {
    val led0 = Output(UInt(1.W))
    val led1 = Output(UInt(1.W))
  })

  val b = Module(new Blinky(25000000))

  b.io <> io
  b.reset <> ~reset.asBool()
}
