import chisel3._
// import chisel3.experimental.{ChiselAnnotation, annotate}
// import firrtl.annotations.MemoryFileInlineAnnotation

class SubByteMemory(words: Int, bits: Int, addrBits: Int, filename: String) extends Module {
  val io = IO(new Bundle {
    val dataOut    = Output(Vec(bits / 8, UInt(8.W)))
    val dataIn     = Input(Vec(bits / 8, UInt(8.W)))
    val readAddr   = Input(UInt(addrBits.W))
    val readEnable = Input(Bool())
    val writeAddr  = Input(UInt(addrBits.W))
    val writeMask  = Input(Vec(bits / 8, Bool()))
  })

  println(s"Memory initialized with file: $filename")
  val mem = SyncReadMem(words, Vec(bits / 8, UInt(8.W)))
  mem.write(io.writeAddr, io.dataIn, io.writeMask)
  io.dataOut := mem.read(io.readAddr, io.readEnable)
}

object SubByteMemoryObj extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new SubByteMemory(16, 64, 32, "sample.hex"),
    Array("-X", "verilog") ++ args
  )
}
