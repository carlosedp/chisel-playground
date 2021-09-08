import chisel3._
import chisel3.util.log2Ceil
import chisel3.util.experimental.loadMemoryFromFileInline
import chisel3.experimental.{annotate, ChiselAnnotation}
import firrtl._

class MemoryPort(val bitWidth: Int, val words: Int, val rw: Boolean = true) extends Bundle {
  val addr     = Output(UInt(log2Ceil(words).W))
  val readData = Input(UInt(bitWidth.W))

  val writeEnable = if (rw) Some(Output(Bool())) else None
  val writeData   = if (rw) Some(Output(UInt(bitWidth.W))) else None
  // val writeEnable = (Output(Bool()))
  // val writeData = (Output(UInt(bitWidth.W)))
}

class DualPortRAM(
  sizeBytes: Int = 1,
  bitWidth: Int = 32,
  memoryFile: String = ""
) extends Module {
  val words = sizeBytes / bitWidth
  val io = IO(new Bundle() {
    val loadStorePort = Flipped(new MemoryPort(bitWidth, words, true))
    val fetchPort     = Flipped(new MemoryPort(bitWidth, words, false))
  })

  println(s"Dual-port Memory Parameters:")
  println(s"  Words: $words")
  println(s"  Size: " + words * bitWidth + " Kb")
  println(s"  Bit width: $bitWidth bit")
  println(s"  Addr Width: " + io.loadStorePort.addr.getWidth + " bit")

  // This is required to have readmem outside `ifndef SYNTHESIS` and be synthesized by FPGA tools
  annotate(new ChiselAnnotation {
    override def toFirrtl =
      firrtl.annotations.MemorySynthInit
  })

  val mem = SyncReadMem(words, UInt(bitWidth.W))
  if (memoryFile.trim().nonEmpty) {
    println(s"  Load memory file: " + memoryFile)
    loadMemoryFromFileInline(mem, memoryFile)
  }

  io.loadStorePort.readData := mem.read(io.loadStorePort.addr)
  io.fetchPort.readData := mem.read(io.fetchPort.addr)

  when(io.loadStorePort.writeEnable.get) {
    mem.write(io.loadStorePort.addr, io.loadStorePort.writeData.get)
  }
}

object DualPortRAMObj extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new DualPortRAM(
      sizeBytes = 8,
      bitWidth = 8,
      memoryFile = "sample.hex"
    ),
    Array("-X", "verilog") ++ args
  )
}
