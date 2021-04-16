// This isn't really a PLL, but it halves the clock.
module PLL0
(
    input clki, // 50 MHz
    output clko, // 25 MHz
    output lock
);

reg half_clk;
assign clko = half_clk;

always @(posedge clki) begin
    half_clk <= !half_clk;
end

endmodule

