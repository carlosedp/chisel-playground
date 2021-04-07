# Chisel Blinky on FOMU

This is a simple Blinky project to demonstrate Chisel functionality with scripts and tooling to be able to synth and run on [Fomu](https://tomu.im/fomu.html).

This project uses two blackbox modules to import Verilog code from ICE40 IP, one for it's' PLL and another for the RGB led driver.

## Synthesis using Open Source tools (yosys/nextpnr)

To synthesize and program, you need to download fomu-toolchain from <https://github.com/im-tomu/fomu-toolchain/releases/latest> and add it to your path.

For example, on Mac:

```sh
wget https://github.com/im-tomu/fomu-toolchain/releases/download/v1.5.6/fomu-toolchain-macos-v1.5.6.zip
unzip fomu-toolchain-macos-v1.5.6.zip

# Add to path
export PATH=$(realpath ./bin):$PATH
```

Build the bitstream:

```sh
make
```

And finally, with the Fomu connected, program the bitstream:

```sh
make load
```
