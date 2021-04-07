# Define board parameters
include Makefile.boards
SHELL = bash

# Project name and toplevel
project = toplevel
toplevel = Toplevel

# Use Docker images
DOCKER=docker

PWD = $(shell pwd)
USBDEVICE ?= /dev/bus/usb
DOCKERARGS = run --rm -v $(PWD):/src -w /src
VERILATORARGS = run --name verilator --hostname verilator --rm -it --entrypoint= -v $(PWD):/work -w /work

ifeq ($(DOCKER),docker)
YOSYS          = $(DOCKER) $(DOCKERARGS) hdlc/yosys yosys
NEXTPNR        = $(DOCKER) $(DOCKERARGS) hdlc/nextpnr nextpnr-$(FPGA)
ECPPACK        = $(DOCKER) $(DOCKERARGS) hdlc/prjtrellis ecppack
ICEPACK        = $(DOCKER) $(DOCKERARGS) hdlc/icestorm icepack
OPENOCD_DEF    = $(DOCKER) $(DOCKERARGS) --privileged --device $(USBDEVICE):/dev/bus/usb hdlc/prog openocd
VERILATOR      = $(DOCKER) $(VERILATORARGS) hdlc/verilator
else
YOSYS     = yosys
NEXTPNR   = nextpnr-$(FPGA)
ECPPACK   = ecppack
ICEPACK   = icepack
OPENOCD_DEF   = openocd
OPENOCD_ULX3S   = openocd
VERILATOR = verilator
endif

scala_files = $(wildcard src/main/scala/*scala)
generated_files = generated
verilog_files = $(generated_files)/*.v

# Targets
chisel: $(verilog_files)   ## Generates Verilog code from Chisel sources using Mill
$(verilog_files): $(scala_files) check-board-vars clean
	scripts/mill $(project).run -board ${BOARD} -td $(generated_files)

chisel-sbt:   ## Generates Verilog code from Chisel sources using SBT
	sbt "run -board ${BOARD} -td $(generated_files)"

chisel_tests:
	scripts/mill $(project).test

check: chisel_tests ## Run Chisel tests

fmt:
	scripts/mill $(project).reformat
	scripts/mill $(project).fix

synth: check-board-vars $(project).bit ## Synthesizes for target BOARD with "make BOARD=board synth"

check-board-vars:
	@test -n "$(BOARD)" || (echo "Set BOARD variable to a board from:" ; echo -n "  "; cat Makefile.boards|grep BOARD| cut -d ',' -f 2 |tr -s ')\n' ', ' | sed 's/, $$/\n/'; echo "Eg. make chisel BOARD=polarfireeval"; echo; exit 1)

$(project).json: $(verilog_files)
	$(YOSYS) -p "read_verilog -sv $^; synth_$(FPGA) -json $(generated_files)/$@ -top $(toplevel)"

$(project).config: $(project).json $(LPF)
	$(NEXTPNR) --json $(generated_files)/$< --lpf $(LPF) --textcfg $(generated_files)/$@ $(NEXTPNR_FLAGS) --package $(PACKAGE)

$(project).bit: $(project).config
	$(PACK) --svf $(generated_files)/$(project).svf $(generated_files)/$< $(generated_files)/$@

$(project).svf: $(project).bit

prog: check-board-vars $(project).svf ## Programs target BOARD with "make BOARD=board prog"
	$(OPENOCD) -f $(OPENOCD_JTAG_CONFIG) -f $(OPENOCD_DEVICE_CONFIG) -c "transport select jtag; init; svf $(generated_files)/$(project).svf; exit"

clean: ## Clean all generated files
	@./scripts/mill clean
	@rm -rf obj_dir test_run_dir target
	@rm -rf $(generated_files)
	@rm -rf out
	@rm -f $(project)

help:
	@echo "Makefile targets:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = "[:##]"}; {printf "\033[36m%-20s\033[0m %s\n", $$2, $$5}'
	@echo ""
	@echo "Supported boards:"
	@echo ""
	@grep -E 'BOARD|##' Makefile.boards |sed 's/.*BOARD)\,\(.*\))/\1/' | sed -n 'N;s/\n/ /;p' | awk -F"[ \t]*##" '{printf "\033[36m%-15s\033[0m - %s\n", $$1, $$2}'

.PHONY: chisel clean prog help
.PRECIOUS: $(project).json $(project).config $(project).bit
.DEFAULT_GOAL := help
