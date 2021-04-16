# Clock
set_location_assignment PIN_AF14 -to clock
set_instance_assignment -name IO_STANDARD "3.3-V LVTTL" -to clock

# Reset
set_location_assignment PIN_AA14 -to reset
set_instance_assignment -name IO_STANDARD "3.3-V LVTTL" -to reset

# LEDs
set_location_assignment PIN_V16 -to io_led0
set_instance_assignment -name IO_STANDARD "3.3-V LVTTL" -to io_led0
set_location_assignment PIN_W16 -to io_led1
set_instance_assignment -name IO_STANDARD "3.3-V LVTTL" -to io_led1
set_location_assignment PIN_V17 -to io_led2
set_instance_assignment -name IO_STANDARD "3.3-V LVTTL" -to io_led2