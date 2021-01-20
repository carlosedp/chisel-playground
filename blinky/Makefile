# Define board parameters
include Makefile.boards
SHELL = bash

# Use Docker images
DOCKER=docker

project = blinky
toplayer = Toplayer

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


chisel: $(verilog_files) check-board-vars ## Generates Verilog code from Chisel sources
$(verilog_files): $(scala_files)
	@rm -rf $(generated_files)
	BOARD=${BOARD} scripts/mill $(project).run -td $(generated_files)


chisel_tests: $(project) ## Run Chisel tests
	scripts/mill $(project).test -td $(generated_files)

check: chisel_tests $(tests)
$(tests): $(project)
	@./scripts/run_test.sh $@


synth: check-board-vars $(project).bit ## Synthesizes for target BOARD with "make BOARD=board synth"

check-board-vars:
	@test -n "$(BOARD)" || (echo "Always set BOARD variable to either \"evn\", \"ulx3s\", \"orangecrab\", \"colorlight\", \"polarfireeval\"." ; exit 1)

$(project).json: $(verilog_files)
	$(YOSYS) -p "read_verilog -sv $^; synth_$(FPGA) -json $(generated_files)/$@ -top $(toplayer)"

$(project).config: $(project).json $(LPF)
	$(NEXTPNR) --json $(generated_files)/$< --lpf $(LPF) --textcfg $(generated_files)/$@ $(NEXTPNR_FLAGS) --package $(PACKAGE)

$(project).bit: $(project).config
	$(PACK) --svf $(generated_files)/$(project).svf $(generated_files)/$< $(generated_files)/$@
# if [ -"$(FPGA)" == "ecp5" ]; then \
# elif [ "$(FPGA)" ==  "ice40" ]; then \
# 	$(PACK) $< $@ \
# endif

$(project).svf: $(project).bit

prog: check-board-vars $(project).svf ## Programs target BOARD with "make BOARD=board prog"
	$(OPENOCD) -f $(OPENOCD_JTAG_CONFIG) -f $(OPENOCD_DEVICE_CONFIG) -c "transport select jtag; init; svf $(generated_files)/$(project).svf; exit"

clean: ## Clean all generated files
	@./scripts/mill clean
	@rm -rf obj_dir test_run_dir target project
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
