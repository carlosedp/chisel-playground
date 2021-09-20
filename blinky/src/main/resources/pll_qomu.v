// This isn't really a PLL but a connection between the eFPGA and the MCU
module PLL0
(
    input clki, // No input since the MCU generates the clock
    output clko, // 25 MHz output from the MCU clock generator
    output lock
);

/* CPU <-> FPGA interface
    * here only one of the two clock sources
    * and corresponding reset signal are used
    * Sys_Clk0/Sys_Clk0_Rst: C16 clock domain
    * Sys_Clk1/Sys_Clk1_Rst: C21 clock domain
    */
qlal4s3b_cell_macro u_qlal4s3b_cell_macro (
    .Sys_Clk0 (clko),
    .Sys_Clk0_Rst (),
    .Sys_Clk1     (),
    .Sys_Clk1_Rst ()
);

endmodule

