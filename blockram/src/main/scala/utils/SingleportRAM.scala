import chisel3._
import chisel3.util.experimental.loadMemoryFromFileInline
import chisel3.experimental.{annotate, ChiselAnnotation}

class SinglePortRAM(words: Int = 1, width: Int = 32, memoryFile: String = "") extends Module {
  val addrWidth = chiselTypeOf((words * 1024).U)
  val io = IO(new Bundle {
    val addr1    = Input(addrWidth)
    val dataIn1  = Input(UInt(width.W))
    val en1      = Input(Bool())
    val we1      = Input(Bool())
    val dataOut1 = Output(UInt(width.W))
  })
  println(s"Dual-port Memory Parameters:")
  println(s"  Size (words): $words")
  println(s"  Size (kb): " + words * width + " Kb")
  println(s"  Width: $width bit")
  println(s"  Addr Width: " + io.addr1.getWidth + " bit")

  // This is required to have readmem outside `ifndef SYNTHESIS` and be synthesized by FPGA tools
  annotate(new ChiselAnnotation {
    override def toFirrtl =
      firrtl.annotations.MemorySynthInit
  })

  val mem = SyncReadMem(words, UInt(width.W))
  if (memoryFile.trim().nonEmpty) {
    println(s"  Load memory file: " + memoryFile)
    loadMemoryFromFileInline(mem, memoryFile)
  }

  when(io.we1) {
    mem.write(io.addr1, io.dataIn1)
  }
  io.dataOut1 := mem.read(io.addr1, io.en1)
}

object SinglePortRAMObj extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new SinglePortRAM(16, width = 64, memoryFile = "sample.hex"),
    Array("-X", "verilog") ++ args
  )
}
