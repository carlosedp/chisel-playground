# Microsemi Tcl Script
# libero
# Date: Tue Jan 19 17:07:30 2021
# Directory C:\Projects\Blinky
# File C:\Projects\Blinky\exported.tcl


new_project -location {C:\Projects\Blink} -name {Blink} -project_description {} -block_mode 0 -standalone_peripheral_initialization 0 -instantiate_in_smartdesign 1 -ondemand_build_dh 1 -use_relative_path 0 -linked_files_root_dir_env {} -hdl {VERILOG} -family {PolarFire} -die {MPF300TS_ES} -package {FCG1152} -speed {-1} -die_voltage {1.0} -part_range {IND} -adv_options {IO_DEFT_STD:LVCMOS 1.8V} -adv_options {RESTRICTPROBEPINS:1} -adv_options {RESTRICTSPIPINS:0} -adv_options {SYSTEM_CONTROLLER_SUSPEND_MODE:0} -adv_options {TEMPR:IND} -adv_options {VCCI_1.2_VOLTR:EXT} -adv_options {VCCI_1.5_VOLTR:EXT} -adv_options {VCCI_1.8_VOLTR:EXT} -adv_options {VCCI_2.5_VOLTR:EXT} -adv_options {VCCI_3.3_VOLTR:EXT} -adv_options {VOLTR:IND} 
set_device -family {PolarFire} -die {MPF300TS_ES} -package {FCG1152} -speed {-1} -die_voltage {1.0} -part_range {IND} -adv_options {IO_DEFT_STD:LVCMOS 1.8V} -adv_options {RESTRICTPROBEPINS:1} -adv_options {RESTRICTSPIPINS:0} -adv_options {SYSTEM_CONTROLLER_SUSPEND_MODE:0} -adv_options {TEMPR:IND} -adv_options {VCCI_1.2_VOLTR:EXT} -adv_options {VCCI_1.5_VOLTR:EXT} -adv_options {VCCI_1.8_VOLTR:EXT} -adv_options {VCCI_2.5_VOLTR:EXT} -adv_options {VCCI_3.3_VOLTR:EXT} -adv_options {VOLTR:IND} 
import_files \
         -convert_EDN_to_HDL 0 \
         -hdl_source {Z:\repos\chisel-playground\blinky\generated\pll_polarfireeval.v} \
         -hdl_source {Z:\repos\chisel-playground\blinky\generated\Toplayer.v} 
import_files \
         -convert_EDN_to_HDL 0 \
         -io_pdc {Z:\repos\chisel-playground\blinky\constraints\polarfire_evaluation.pdc} 
build_design_hierarchy 
set_root -module {Blinky::work} 
build_design_hierarchy 
run_tool -name {SYNTHESIZE} 
run_tool -name {PLACEROUTE} 


run_tool -name {GENERATEPROGRAMMINGDATA}
run_tool -name {GENERATEPROGRAMMINGFILE}

