import chisel3.iotesters.PeekPokeTester
import org.scalatest._

class BlinkySpec extends FlatSpec with Matchers {

  "Blinky" should "pass" in {
    chisel3.iotesters.Driver(() => new Blinky(25000)) { c =>
      new PeekPokeTester(c) {

        println("Start the blinking LED")
        for (_ <- 0 until 100) {
          step(10000)
          val ch0 = if (peek(c.io.led0) == 0) 'o' else '*'
          val ch1 = if (peek(c.io.led1) == 1) 'o' else '*'
          printf(ch0 + "  " + ch1 + "\r")
        }
        println("\nEnd the blinking LED")
      }
    } should be(true)
  }

}
