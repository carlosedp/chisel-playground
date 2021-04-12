# Chisel Blinky on FOMU

This is a simple Blinky project to demonstrate Chisel functionality with scripts and tooling to be able to synth and run on [Fomu](https://tomu.im/fomu.html).

This project uses two blackbox modules to import Verilog code from ICE40 IP, one for it's' PLL and another for the RGB led driver.

## Synthesis using Open Source tools (yosys/nextpnr)

The faster way to get started is using FuseSoc and generating the files in containers. You would need **Docker** and **FuseSoc**. To program, use [dfu-util](http://dfu-util.sourceforge.net/).

To install Fusesoc (requires Python3 and pip3) and add to your path:

```sh
pip3 install --upgrade --user fusesoc
export PATH=~/.local/bin:$PATH

#Check if it's working:

$ fusesoc --version
1.12.0
```

Then create the project dir and files:

```sh
mkdir fusesoc-fomublink && cd fusesoc-fomublink
fusesoc library add fusesoc-cores https://github.com/fusesoc/fusesoc-cores

# Since Fomublink is not a standalone repo (but a folder in an umbrella repo)
# we clone it locally and add the library as a local dir.
git clone https://github.com/carlosedp/chisel-playground
fusesoc library add fomublink $(pwd)/chisel-playground/fomublink

# Get the container wrapper
wget https://gist.github.com/carlosedp/c0e29d55e48309a48961f2e3939acfe9/raw/bfeb1cfe2e188c1d5ced0b09aabc9902fdfda6aa/runme.py
chmod +x runme.py

# Generate the files (Chisel to Verilog, Synthesys, Place'nRoute, Bitstream packing)
EDALIZE_LAUNCHER=$(realpath ../runme.py) fusesoc run --target=fomu-pvt carlosedp:demo:fomublink:0
```

Then program the bitstream to the Fomu with provided instructions on terminal.


[![https://pbs.twimg.com/ext_tw_video_thumb/1381699719907442690/pu/img/hn2VBCXfQ2d6clcd.jpg](https://pbs.twimg.com/ext_tw_video_thumb/1381699719907442690/pu/img/hn2VBCXfQ2d6clcd.jpg)](https://twitter.com/carlosedp/status/1381700282246119425)

Another option is using local tools. For this you need to download fomu-toolchain from <https://github.com/im-tomu/fomu-toolchain/releases/latest> and add it to your path.

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
