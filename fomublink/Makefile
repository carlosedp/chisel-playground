

project = blink
toplayer = BlinkTop

scala_files = $(wildcard src/main/scala/*scala)
generated_files = generated
verilog_files = $(generated_files)/*.v

# Params for Fomu PVT
YOSYSFLAGS ?= -D PVT=1
PNRFLAGS   ?= --up5k --package uwg30
PCF        ?=  constraints/fomu-pvt.pcf
FPGA       ?=  ice40

# Tools Configuration
# DOCKER=docker
# PWD = $(shell pwd)
# DOCKERARGS = run --rm -v $(PWD):/src -w /src
# YOSYS          = $(DOCKER) $(DOCKERARGS) ghdl/synth:beta yosys
# NEXTPNR        = $(DOCKER) $(DOCKERARGS) ghdl/synth:nextpnr-$(FPGA) nextpnr-$(FPGA)
# ICEPACK        = $(DOCKER) $(DOCKERARGS) ghdl/synth:icestorm icepack

YOSYS     = yosys
NEXTPNR   = nextpnr-$(FPGA)
ICEPACK   = icepack
# Targets

all: $(project).dfu

# Generate Verilog files from Chisel
$(verilog_files): $(scala_files)
	scripts/mill $(project).run -td $(generated_files)

# Use *Yosys* to generate the synthesized netlist.
# This is called the **synthesis** and **tech mapping** step.
$(project).json: $(verilog_files)
	$(YOSYS) \
		$(YOSYSFLAGS) \
		-p 'synth_$(FPGA) -top $(toplayer) -json $(generated_files)/$(project).json' $(verilog_files)

# Use **nextpnr** to generate the FPGA configuration.
# This is called the **place** and **route** step.
$(project).asc: $(project).json $(PCF)
	$(NEXTPNR) \
		$(PNRFLAGS) \
		--pcf $(PCF) \
		--json $(generated_files)/$(project).json \
		--asc $(generated_files)/$(project).asc

# Use icepack to convert the FPGA configuration into a "bitstream" loadable onto the FPGA.
# This is called the bitstream generation step.
$(project).bit: $(project).asc
	$(ICEPACK) $(generated_files)/$(project).asc $(generated_files)/$(project).bit

# Use dfu-suffix to generate the DFU image from the FPGA bitstream.
$(project).dfu: $(project).bit
	cp $(generated_files)/$(project).bit $(generated_files)/$(project).dfu
	dfu-suffix -v 1209 -p 70b1 -a $(generated_files)/$(project).dfu

# Use df-util to load the DFU image onto the Fomu.
load: $(project).dfu
	dfu-util -D $(generated_files)/$(project).dfu

clean: ## Clean all generated files
	@./scripts/mill clean
	@rm -f $(project).fir $(project).anno.json $(project).v
	@rm -rf obj_dir test_run_dir target project
	@rm -rf $(generated_files)
	@rm -f $(project)
	@rm -rf *.bit *.json *.svf *.config out *.fir *.v *.f

.PHONY: clean prog help