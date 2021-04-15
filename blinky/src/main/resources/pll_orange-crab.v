module PLL0(
  input  clki,
  output reg clko,
  output reg lock
);

  always @* begin
    lock <= 1;
    clko <= clki;
  end

endmodule

