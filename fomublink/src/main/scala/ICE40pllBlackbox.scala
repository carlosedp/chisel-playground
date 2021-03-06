import chisel3._
// import chisel3.util.HasBlackBoxInline

// Instantiate the IP directly
class SB_GB extends BlackBox() {
  val io = IO(new Bundle {
    val USER_SIGNAL_TO_GLOBAL_BUFFER = Input(Clock())
    val GLOBAL_BUFFER_OUTPUT         = Output(Clock())
  })
}

// Instantiate thru inline verilog. Just here for documentation since
// instantiation above does the same.
// class ICE40pllBlackbox() extends BlackBox with HasBlackBoxInline {
//   val io = IO(new Bundle() {
//     val clki = Input(Clock())
//     val clko = Output(Clock())
//   })

//   setInline(
//     "ICE40pllBlackbox.v",
//     s"""
//        | module ICE40pllBlackbox(
//        |     input clki,
//        |     output clko
//        | );
//        |
//        | SB_GB clk_gb (
//        |         .USER_SIGNAL_TO_GLOBAL_BUFFER(clki),
//        |         .GLOBAL_BUFFER_OUTPUT(clko)
//        |     );
//        | endmodule
//        |""".stripMargin
//   )
// }
