// Currently the top module is written in Chisel
// but ysing a Verilog top module is an option
module toplevel(
  input clock,
  input reset,
  output io_led0,
  output io_led1
  );

  Blinky blinky(
    .clock(clock),
    .reset(~reset),
    .io_led0( io_led0 ),
    .io_led1( io_led1 )
  );
endmodule
