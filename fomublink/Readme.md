# Chisel Blinky on FOMU

This is a simple Blinky project to demonstrate Chisel functionality with scripts and tooling to be able to synth and run on [Fomu](https://tomu.im/fomu.html).

This project uses two blackbox modules to import Verilog code from ICE40 IP, one for it's' PLL and another for the RGB led driver.

## Synthesis using Open Source tools (yosys/nextpnr)

To synthesize and program, you need to download fomu-toolchain from <https://github.com/im-tomu/fomu-toolchain/releases/latest>.

Unzip it into your computer and add it's `bin` dir to the path.
