import chisel3.iotesters._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class BlinkySpec extends AnyFlatSpec with Matchers {
  "Blinky" should "pass" in {
    chisel3.iotesters.Driver(() => new Blinky(25000)) { c =>
      new PeekPokeTester(c) {

        println("Start the blinking LED")
        for (_ <- 0 until 25) {
          step(10000)
          val l0 = if (peek(c.io.led0) == 0) 'o' else '*'
          val l1 = if (peek(c.io.led1) == 0) 'o' else '*'
          val l2 = if (peek(c.io.led2) == 0) 'o' else '*'
          printf("Leds: " + l0 + "  " + l1 + "  " + l2 + "\r")
        }
        println("\nEnd the blinking LED")
      }
    } should be(true)
  }
}

// Verilator sim
class VerilatorSpec extends AnyFlatSpec with Matchers {
  "VerilatorSim" should "pass" in {
    Driver.execute(
      Array("--backend-name", "verilator"),
      () => new Blinky(1)
    ) { c =>
      new WaveformCounterTester(c, 25)
    } should be(true)
  }
}

// Generate VCD output
class WaveformCounterSpec extends AnyFlatSpec with Matchers {
  "WaveformCounter" should "pass" in {
    Driver.execute(
      Array("--generate-vcd-output", "on"),
      () => new Blinky(1)
    ) { c =>
      new WaveformCounterTester(c, 25)
    } should be(true)
  }
}

class WaveformCounterTester(dut: Blinky, cycles: Int)
    extends PeekPokeTester(dut) {
  for (_ <- 0 until cycles) {
    step(1)
  }
}
