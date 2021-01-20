import chisel3.iotesters._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class BlinkySpec extends AnyFlatSpec with Matchers {
  "Blinky" should "pass" in {
    chisel3.iotesters.Driver(() => new Blinky(15000)) { c =>
      new PeekPokeTester(c) {

        println("Start the blinking LED")
        for (_ <- 0 until 25) {
          step(20000)
          val ch0 = if (peek(c.io.led0) == 0) 'o' else '*'
          val ch1 = if (peek(c.io.led1) == 0) 'o' else '*'
          val ch2 = if (peek(c.io.led2) == 0) 'o' else '*'
          printf("Leds: " + ch0 + "  " + ch1 + "  " + ch2 + "\r")
        }
        println("\nEnd the blinking LED")
      }
    } should be(true)
  }

}
