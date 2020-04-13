module toplevel(
  input clock,
  input reset,
  output io_led0,
  output io_led1
  );

  // wire res;
  // wire reset;

  // assign res = ~reset;

  Blinky blinky(
    .clock(clock),
    .reset(~reset),
    .io_led0( io_led0 ),
    .io_led1( io_led1 )
  );
endmodule
