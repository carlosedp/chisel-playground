# Chisel Blinky

This is a simple Blinky project to demonstrate Chisel functionality with build scripts
and tooling to be able to synth on FPGA boards.

The Chisel sources uses a `-board` parameter to define which board is used and generate the proper PLL and build. To support this, an utility module was created.

The project also have a visual test spec that indicates the "blink" behavior that can be run with `./scripts/mill toplevel.test.testOne BlinkySpec`.

## Building

The project provides two building methods. The first and recommended is using [Fusesoc](https://github.com/olofk/fusesoc), a package manager that handles all board backend files and configuration. It also makes adding support to new boards and vendors much easier.

A second method is using the built-in Makefile that provides support for some boards.

### Fusesoc build and generation

To install Fusesoc (requires Python3 and pip3):

```sh
pip3 install --upgrade --user fusesoc
```

Check if it's working:

```sh
$ fusesoc --version
1.12.0
```

If the terminal reports an error about the command not being found check that the directory `~/.local/bin` is in your command search path (`export PATH=~/.local/bin:$PATH`).

Fusesoc allows multiple boards from different vendors to be supported by the project. It uses chisel-generator to generate Verilog from Scala sources and calls the correct board EDA backend to create it's project files.

For example, to generate the programming files for the **ULX3s** based on ECP5:

```sh
mkdir fusesoc-chiselblinky && cd fusesoc-chiselblinky

fusesoc library add fusesoc-cores https://github.com/fusesoc/fusesoc-cores

# Since Blinky is not a standalone repo (but a folder in an umbrella repo)
# We clone it locally and add the library as a local dir.
git clone https://github.com/carlosedp/chisel-playground
fusesoc library add chiselblinky $(pwd)/chisel-playground/blinky

fusesoc run --target=ulx3s_85 carlosedp:demo:chiselblinky:0
...
...
# Output bitstream will be on build/carlosedp_demo_chiselblinky_0/ulx3s_85-trellis
❯ ll build/carlosedp_demo_chiselblinky_0/ulx3s_85-trellis
total 2.7M
-rw-r--r-- 1 cdepaula staff  774 Apr  7 18:53 carlosedp_demo_chiselblinky_0.eda.yml
-rw-r--r-- 1 cdepaula staff  545 Apr  7 18:53 carlosedp_demo_chiselblinky_0.tcl
-rw-r--r-- 1 cdepaula staff  435 Apr  7 18:53 carlosedp_demo_chiselblinky_0.mk
-rw-r--r-- 1 cdepaula staff  608 Apr  7 18:53 Makefile
-rw-r--r-- 1 cdepaula staff 9.5K Apr  7 18:53 carlosedp_demo_chiselblinky_0.blif
-rw-r--r-- 1 cdepaula staff 655K Apr  7 18:53 carlosedp_demo_chiselblinky_0.json
-rw-r--r-- 1 cdepaula staff  44K Apr  7 18:53 carlosedp_demo_chiselblinky_0.edif
-rw-r--r-- 1 cdepaula staff  45K Apr  7 18:53 yosys.log
-rw-r--r-- 1 cdepaula staff 8.7K Apr  7 18:53 next.log
-rw-r--r-- 1 cdepaula staff 1.9M Apr  7 18:53 carlosedp_demo_chiselblinky_0.bit
```

Just program it to your FPGA with `OpenOCD` or in ULX3S case, [`ujprog`](https://github.com/f32c/tools/tree/master/ujprog)

And for the **Microchip Polarfire** evaluation board, just change the target:

```sh
fusesoc run --target=polarfireeval_es carlosedp:demo:chiselblinky:0
```

### Building on containers

The standard build process uses locally installed tools like Java (for Chisel generation)Yosys, NextPNR, Vivado and others. Fusesoc also supports building the complete workflow by using containers thru a command launcher.

To use it:

```sh
# Download the command wrapper
wget https://gist.github.com/carlosedp/c0e29d55e48309a48961f2e3939acfe9/raw/bfeb1cfe2e188c1d5ced0b09aabc9902fdfda6aa/runme.py
chmod +x runme.py

# Run fusesoc with the wrapper as an environment var
EDALIZE_LAUNCHER=$(realpath ./runme.py) fusesoc run --target=ulx3s_85 carlosedp:demo:chiselblinky:0

#The output files will be on the local ./build dir like before
```

Here's a demo of it:

[![asciicast](https://asciinema.org/a/405850.svg)](https://asciinema.org/a/405850)

### Adding support to new boards

Support for new boards can be added in the `chiselblinky.core` file.

Three sections are required:

#### Fileset

Filesets lists the dependency from the chisel-generator, that outputs Verilog from Chisel (Scala) code. It also contains the static files used for each board like constraints that must be copied to the output project dir and used by EDA.

```yaml
  polarfireeval:
    depend: ["fusesoc:utils:generators:0.1.6"]
    files:
      - constraints/polarfire_evaluation.pdc: { file_type: PDC }
```

#### Generate

The generator section contains the Chisel generator parameters. It has the arguments to be passed to Chisel (the board in this example), the project name and the output files created by the generator to be used by the EDA.

```yaml
  polarfireeval:
    generator: chisel
    parameters:
      extraargs: "-board polarfireeval"
      chiselproject: toplevel
      copy_core: true
      output:
        files:
          - generated/Toplevel.v: { file_type: verilogSource }
          - generated/pll_polarfireeval.v: { file_type: verilogSource }
```

#### Target

Finally the target section has the board information to be passed to the EDA tools. Parameters like the package/die or extra parameters to synthesis or PnR. This is highly dependent of the EDA backend. It's name is the one passed on the `--target=` param on FuseSoc. It also references the fileset and generate configs.

```yaml
  polarfireeval_es:
    default_tool: libero
    description: Microsemi Polarfire Evaluation Kit (ES)
    filesets: [polarfireeval]
    generate: [polarfireeval]
    tools:
      libero:
        family: PolarFire
        die: MPF300TS_ES
        package: FCG1152
```

To add support to additional boards, create these sections (or reuse similar ones). More details can be found on [Edalize docs](https://github.com/olofk/edalize/) or using [Corescore](https://github.com/olofk/corescore) and [Blinky](https://github.com/fusesoc/blinky) as examples.

### Makefile build and generation

The Makefiles support synthesis using the Open Source toolchain based on yosys/nextpnr.

At the moment the tools support Lattice ECP5 FPGAs. The build process can use Docker images, so no software other than Docker needs to be installed. If you prefer Podman you can use that too, just adjust it in `Makefile`, `DOCKER=podman`. If the variable is undefined, build proceeds with locally installed tools.

### Building and programming the FPGA

The `Makefile` currently supports the following FPGA boards by defining the `BOARD` parameter on make:

* Lattice [ECP5 Evaluation Board](http://www.latticesemi.com/ecp5-evaluation) - `evn`
* Radiona [ULX3S](https://radiona.org/ulx3s/) - `ulx3s`
* Greg Davill [Orangecrab](https://github.com/gregdavill/OrangeCrab) - `orangecrab`
* Q3k [Colorlight](https://github.com/q3k/chubby75/tree/master/5a-75b) - `colorlight`

For example, to build for the ULX3S Board, run:

```sh
make BOARD=ulx3s synth`
```

After this command, all files will be generated in the `generated` output dir.

To program your FPGA:

```sh
make BOARD=ulx3s prog

# or if your USB device has a different path, pass it on USBDEVICE, like:

make BOARD=ulx3s USBDEVICE=/dev/tty.usbserial-120001 prog
```

Programming using OpenOCD on Docker does not work on Docker Desktop for Mac/Windows since the container is run in a Linux VM and can not see the physical devices connected to the Host. In this case you need OpenOCD installed locally.

For the ULX3S board, the current OpenOCD does not support ft232 protocol so to program it, download [ujprog](https://github.com/emard/ulx3s-bin/tree/master/usb-jtag) for your platform and program using `./ujprog chiselwatt.bit` or to persist in the flash, `./ujprog -j FLASH chiselwatt.bit`.
