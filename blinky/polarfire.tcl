# Microsemi Tcl Script
# libero
# Date: Tue Apr 14 19:41:49 2020
# Directory \\tsclient\cdepaula\repos\chisel-playground\blinky
# File \\tsclient\cdepaula\repos\chisel-playground\blinky\polarfire.tcl


new_project -location {d:/Blinky/Blinky} -name {Blinky} -project_description {} -block_mode 0 -standalone_peripheral_initialization 0 -instantiate_in_smartdesign 1 -ondemand_build_dh 1 -hdl {VERILOG} -family {PolarFire} -die {MPF300T} -package {FCG1152} -speed {STD} -die_voltage {1.0} -part_range {EXT} -adv_options {IO_DEFT_STD:LVCMOS 1.8V} -adv_options {RESTRICTPROBEPINS:1} -adv_options {RESTRICTSPIPINS:0} -adv_options {SYSTEM_CONTROLLER_SUSPEND_MODE:0} -adv_options {TEMPR:EXT} -adv_options {VCCI_1.2_VOLTR:EXT} -adv_options {VCCI_1.5_VOLTR:EXT} -adv_options {VCCI_1.8_VOLTR:EXT} -adv_options {VCCI_2.5_VOLTR:EXT} -adv_options {VCCI_3.3_VOLTR:EXT} -adv_options {VOLTR:EXT} 
set_device -family {PolarFire} -die {MPF300T} -package {FCG1152} -speed {STD} -die_voltage {1.0} -part_range {EXT} -adv_options {IO_DEFT_STD:LVCMOS 1.8V} -adv_options {RESTRICTPROBEPINS:1} -adv_options {RESTRICTSPIPINS:0} -adv_options {SYSTEM_CONTROLLER_SUSPEND_MODE:0} -adv_options {TEMPR:EXT} -adv_options {VCCI_1.2_VOLTR:EXT} -adv_options {VCCI_1.5_VOLTR:EXT} -adv_options {VCCI_1.8_VOLTR:EXT} -adv_options {VCCI_2.5_VOLTR:EXT} -adv_options {VCCI_3.3_VOLTR:EXT} -adv_options {VOLTR:EXT} 
import_files \
         -convert_EDN_to_HDL 0 \
         -hdl_source {./generated/BlinkyTop.v} 
create_links \
         -convert_EDN_to_HDL 0 
import_files \
         -convert_EDN_to_HDL 0 \
         -io_pdc {./constraints/polarfire_evaluation.pdc} 
create_links \
         -convert_EDN_to_HDL 0 
build_design_hierarchy 
build_design_hierarchy 
set_root -module {BlinkyTop::work} 
build_design_hierarchy 
run_tool -name {CONSTRAINT_MANAGEMENT} 
cleanall_tool -name {SYNTHESIZE} 
save_project 
