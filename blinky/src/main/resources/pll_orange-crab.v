module PLL0
(
    input clki, // 48 MHz, 0 deg
    output clko, // 25 MHz, 0 deg
    output lock
);
(* FREQUENCY_PIN_CLKI="48" *)
(* FREQUENCY_PIN_CLKOP="25.6" *)
(* ICP_CURRENT="12" *) (* LPF_RESISTOR="8" *) (* MFG_ENABLE_FILTEROPAMP="1" *) (* MFG_GMCREF_SEL="2" *)
EHXPLLL #(
        .PLLRST_ENA("DISABLED"),
        .INTFB_WAKE("DISABLED"),
        .STDBY_ENABLE("DISABLED"),
        .DPHASE_SOURCE("DISABLED"),
        .OUTDIVIDER_MUXA("DIVA"),
        .OUTDIVIDER_MUXB("DIVB"),
        .OUTDIVIDER_MUXC("DIVC"),
        .OUTDIVIDER_MUXD("DIVD"),
        .CLKI_DIV(15),
        .CLKOP_ENABLE("ENABLED"),
        .CLKOP_DIV(23),
        .CLKOP_CPHASE(11),
        .CLKOP_FPHASE(0),
        .FEEDBK_PATH("CLKOP"),
        .CLKFB_DIV(8)
    ) pll_i (
        .RST(1'b0),
        .STDBY(1'b0),
        .CLKI(clki),
        .CLKOP(clko),
        .CLKFB(clko),
        .CLKINTFB(),
        .PHASESEL0(1'b0),
        .PHASESEL1(1'b0),
        .PHASEDIR(1'b1),
        .PHASESTEP(1'b1),
        .PHASELOADREG(1'b1),
        .PLLWAKESYNC(1'b0),
        .ENCLKOP(1'b0),
        .LOCK(lock)
	);
endmodule

