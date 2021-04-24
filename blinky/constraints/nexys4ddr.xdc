# Clock pin
set_property PACKAGE_PIN E3 [get_ports {clock}]
set_property IOSTANDARD LVCMOS33 [get_ports {clock}]
create_clock -period 10.0 [get_ports {clock}]

# LEDs
set_property PACKAGE_PIN H17  [get_ports {io_led0}]
set_property PACKAGE_PIN K15  [get_ports {io_led1}]
set_property PACKAGE_PIN J13  [get_ports {io_led2}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_led0}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_led1}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_led2}]

# Center button assigned to reset
set_property PACKAGE_PIN N17 [get_ports { reset }]
set_property IOSTANDARD LVCMOS33 [get_ports { reset }]
