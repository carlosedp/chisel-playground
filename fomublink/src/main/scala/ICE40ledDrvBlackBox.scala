import chisel3._
import chisel3.util.HasBlackBoxInline

class ICE40ledDrvBlackBox() extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle() {
    // LED inputs and outputs
    val rgb0_in  = Input(Bool())
    val rgb1_in  = Input(Bool())
    val rgb2_in  = Input(Bool())
    val rgb0_out = Output(Bool())
    val rgb1_out = Output(Bool())
    val rgb2_out = Output(Bool())
  })

  setInline(
    "ICE40ledDrvBlackBox.v",
    s"""
       | module ICE40ledDrvBlackBox(
       |     input rgb0_in,
       |     input rgb1_in,
       |     input rgb2_in,
       |     output rgb0_out,
       |     output rgb1_out,
       |     output rgb2_out
       | );
       |
       |     // Instantiate iCE40 LED driver hard logic, connecting up
       |     // counter state and LEDs.
       |     //
       |     // Note that it's possible to drive the LEDs directly,
       |     // however that is not current-limited and results in
       |     // overvolting the red LED.
       |     //
       |     // See also:
       |     // https://www.latticesemi.com/-/media/LatticeSemi/Documents/ApplicationNotes/IK/ICE40LEDDriverUsageGuide.ashx?document_id=50668
       |     SB_RGBA_DRV #(
       |         .CURRENT_MODE("0b1"),       // half current
       |         .RGB0_CURRENT("0b000011"),  // 4 mA
       |         .RGB1_CURRENT("0b000011"),  // 4 mA
       |         .RGB2_CURRENT("0b000011")   // 4 mA
       |     ) RGBA_DRIVER (
       |         .CURREN(1'b1),
       |         .RGBLEDEN(1'b1),
       |         .RGB2PWM(rgb0_in),     // Blue
       |         .RGB1PWM(rgb1_in),     // Red
       |         .RGB0PWM(rgb2_in),     // Green
       |         .RGB0(rgb0_out),
       |         .RGB1(rgb1_out),
       |         .RGB2(rgb2_out)
       |     );
       |
       | endmodule
       |""".stripMargin
  )
}
