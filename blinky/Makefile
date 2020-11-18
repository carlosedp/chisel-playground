# Define board parameters
include Makefile.boards
SHELL = bash

# Use Docker images
DOCKER=docker

project = blinky

PWD = $(shell pwd)
USBDEVICE ?= /dev/bus/usb
DOCKERARGS = run --rm -v $(PWD):/src -w /src
VERILATORARGS = run --name verilator --hostname verilator --rm -it --entrypoint= -v $(PWD):/work -w /work

YOSYS          = $(DOCKER) $(DOCKERARGS) ghdl/synth:beta yosys
NEXTPNR        = $(DOCKER) $(DOCKERARGS) ghdl/synth:nextpnr-$(FPGA) nextpnr-$(FPGA)
ECPPACK        = $(DOCKER) $(DOCKERARGS) ghdl/synth:trellis ecppack
ICEPACK        = $(DOCKER) $(DOCKERARGS) ghdl/synth:icestorm icepack
OPENOCD_DEF    = $(DOCKER) $(DOCKERARGS) --privileged --device $(USBDEVICE):/dev/bus/usb ghdl/synth:prog openocd
OPENOCD_ULX3S  = $(DOCKER) $(DOCKERARGS) --privileged --device $(USBDEVICE):/dev/bus/usb alpin3/ulx3s openocd
VERILATOR      = $(DOCKER) $(VERILATORARGS) verilator/verilator

# Uncomment to use local tools for synthesis
#YOSYS     = yosys
#NEXTPNR   = nextpnr-$(FPGA)
#ECPPACK   = ecppack
#OPENOCD_DEF   = openocd
#OPENOCD_ULX3S   = openocd
#VERILATOR = verilator

scala_files = $(wildcard src/main/scala/*scala)
generated_files = generated
verilog_files = $(generated_files)/*.v

tests = $(sort $(patsubst tests/%.out,%,$(wildcard tests/*.out)))

# Targets

$(verilog_files): $(scala_files)
	scripts/mill $(project).run -td $(generated_files)

scala_tests: $(project)
	scripts/mill $(project).test -td $(generated_files)

check: scala_tests $(tests)

$(tests): $(project)
	@./scripts/run_test.sh $@

synth: check-board-vars $(project).bit ## Synthesizes for target BOARD with "make BOARD=board synth"

check-board-vars:
	@test -n "$(LPF)" || (echo "If synthesizing or programming, use \"synth\" or \"prog\" targets with BOARD variable to either \"evn\", \"ulx3s\", \"orangecrab\", \"colorlight\"\n" ; exit 1)

$(project).json: $(verilog_files)
	$(YOSYS) -p "read_verilog -sv $^; synth_$(FPGA) -json $@ -top BlinkyTop"

$(project).config: $(project).json $(LPF)
	$(NEXTPNR) --json $< --lpf $(LPF) --textcfg $@ $(NEXTPNR_FLAGS) --package $(PACKAGE)

$(project).bit: $(project).config
	$(PACK) --svf $(project).svf $< $@
# if [ -"$(FPGA)" == "ecp5" ]; then \
# elif [ "$(FPGA)" ==  "ice40" ]; then \
# 	$(PACK) $< $@ \
# endif

$(project).svf: $(project).bit

prog: check-board-vars $(project).svf ## Programs target BOARD with "make BOARD=board prog"
	$(OPENOCD) -f $(OPENOCD_JTAG_CONFIG) -f $(OPENOCD_DEVICE_CONFIG) -c "transport select jtag; init; svf $(project).svf; exit"

clean: ## Clean all generated files
	@./scripts/mill clean
	@rm -f $(project).fir $(project).anno.json $(project).v
	@rm -rf obj_dir test_run_dir target project
	@rm -rf $(generated_files)
	@rm -f $(project)
	@rm -rf *.bit *.json *.svf *.config out *.fir *.v

help:
	@echo "Makefile targets:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = "[:##]"}; {printf "\033[36m%-20s\033[0m %s\n", $$2, $$5}'
	@echo ""
	@echo "Supported boards:"
	@echo ""
	@grep -E 'BOARD|##' Makefile.boards |sed 's/.*BOARD)\,\(.*\))/\1/' | sed -n 'N;s/\n/ /;p' | awk -F"[ \t]*##" '{printf "\033[36m%-15s\033[0m - %s\n", $$1, $$2}'

.PHONY: clean prog help
.PRECIOUS: $(project).json $(project).config $(project).bit
.DEFAULT_GOAL := help
