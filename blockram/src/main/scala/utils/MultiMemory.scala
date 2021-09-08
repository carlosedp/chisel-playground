import chisel3._
import chisel3.util.log2Ceil
import chisel3.util.experimental.loadMemoryFromFileInline
import chisel3.experimental.{annotate, ChiselAnnotation}

class MultiMem extends Module {
  val io = IO(new Bundle() {
    val loadStorePort  = Flipped(new MemoryPort2(32, 32, true))
    val loadStorePort2 = Flipped(new MemoryPort2(32, 32, true))
  })
  // This is required to have readmem outside `ifndef SYNTHESIS` and be synthesized by FPGA tools
  annotate(new ChiselAnnotation {
    override def toFirrtl =
      firrtl.annotations.MemorySynthInit
  })
  val mem1 =
    Module(
      new DualPortMultiRAM(
        sizeBytes = 32 * 1024,
        bitWidth = 32,
        memoryFile = "sample.hex"
      )
    )
  mem1.io.loadStorePort <> io.loadStorePort

  val mem2 =
    Module(
      new DualPortMultiRAM(
        sizeBytes = 32 * 1024,
        bitWidth = 32,
        memoryFile = "sample2.hex"
      )
    )
  mem2.io.loadStorePort <> io.loadStorePort2
}

class MemoryPort2(val bitWidth: Int, val words: Int, val rw: Boolean) extends Bundle {
  val addr     = Output(UInt(log2Ceil(words).W))
  val readData = Input(UInt(bitWidth.W))

  val writeEnable = if (rw) Some(Output(Bool())) else None
  val writeData   = if (rw) Some(Output(UInt(bitWidth.W))) else None
}

class DualPortMultiRAM(
  sizeBytes: Int = 1,
  bitWidth: Int = 32,
  memoryFile: String = ""
) extends Module {
  val words = sizeBytes / bitWidth
  val io = IO(new Bundle() {
    val loadStorePort = Flipped(new MemoryPort2(bitWidth, words, true))
  })

  println(s"Dual-port Memory $name Parameters:")
  println(s"  Words: $words")
  println(s"  Size: " + words * bitWidth + " Kb")
  println(s"  Bit width: $bitWidth bit")
  println(s"  Addr Width: " + io.loadStorePort.addr.getWidth + " bit")

  val mem = SyncReadMem(words, UInt(bitWidth.W))
  // This prevents deduping this memory module
  // Ref. https://github.com/chipsalliance/firrtl/issues/2168
  val dedupBlock = WireInit(mem.hashCode.U)

  if (memoryFile.trim().nonEmpty) {
    println(s"  Load memory file: " + memoryFile)
    loadMemoryFromFileInline(mem, memoryFile)
  }

  io.loadStorePort.readData := mem.read(io.loadStorePort.addr)

  when(io.loadStorePort.writeEnable.get) {
    mem.write(io.loadStorePort.addr, io.loadStorePort.writeData.get)
  }
}

object MultiMem extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(
    new MultiMem(),
    Array() ++ args
  )
}
