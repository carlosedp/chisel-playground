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

  // Alternate blink leds every second
  val led              = RegInit(0.U(1.W))
  val (_, counterWrap) = Counter(true.B, freq / 2)
  when(counterWrap) {
    led := ~led
  }

  io.led0 := led
  io.led1 := ~led

  // 1 beathe cycle every second
  io.led2 := breathe(freq, 1000)

  // To set a specific PWM value instead (Eg. 10% duty cycle)
  // io.led2 := pwmAbs(10.U)

  //-------------- Utility Functions --------------
  /**
   * pwmAbs creates a PWM output from a 0 to 100 Duty Cycle
   *
   * @param dutyCycle UInt from 0 to 100 (as in percent)
   * @return Bool output
   */
  def pwmAbs(dutyCycle: UInt) = {
    // Divide input frequency by 1024 to avoid switchind the LEDs at high freq
    val clockDiv = RegInit(0.U(log2Ceil(1024).W))
    clockDiv := clockDiv + 1.U
    val cnt = RegInit(0.U(7.W))
    val out = RegInit(false.B)
    when(clockDiv === 0.U) {
      cnt := Mux(cnt === 100.U, 0.U, cnt + 1.U)
      out := dutyCycle > cnt
    }
    out
  }

  /**
   * breathe creates a gradual off-on-off cycle with pwmAbs function
   *
   * @param freq is the module clock frequency
   * @param speed in miliseconts for a full off-on-off cycle
   * @return Bool output
   */
  def breathe(freq: Int, speed: Int) = {
    val pwmRange = 100
    val pwm      = RegInit(0.U(log2Ceil(pwmRange).W))
    val upReg    = RegInit(true.B)
    val (_, cw)  = Counter(true.B, (freq * (speed.toFloat / 1000) / 2 / pwmRange).toInt)
    when(cw) {
      when(pwm < pwmRange.U && upReg)(pwm := pwm + 1.U)
        .elsewhen(pwm === pwmRange.U && upReg)(upReg := false.B)
        .elsewhen(pwm > 0.U && !upReg)(pwm := pwm - 1.U)
        .otherwise(upReg := true.B)
    }
    pwmAbs(pwm)
  }
}
