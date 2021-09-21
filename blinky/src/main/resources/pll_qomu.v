// This isn't really a PLL but a connection between the eFPGA and the MCU
module PLL0
(
    input clki, // No input since the MCU generates the clock
    output clko, // 24 MHz output from the MCU clock generator (bootloader has 24Mhz and 48Mhz clocks)
    output lock
);

/* CPU <-> FPGA interface
    * here only one of the two clock sources
    * and corresponding reset signal are used
    * Sys_Clk0/Sys_Clk0_Rst: C16 clock domain (default as 24Mhz from bootloader)
    * Sys_Clk1/Sys_Clk1_Rst: C21 clock domain (default as 48Mhz from bootloader)
    */
qlal4s3b_cell_macro u_qlal4s3b_cell_macro (
    .Sys_Clk0 (clko), // we expect 25Mhz but the default 24Mhz from MCU is close :)
    .Sys_Clk0_Rst (),
    .Sys_Clk1     (),
    .Sys_Clk1_Rst ()
);

endmodule

