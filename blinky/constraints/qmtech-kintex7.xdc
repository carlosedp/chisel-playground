##### Core board #####
# Clock
set_property LOC F22 [get_ports clock]
set_property IOSTANDARD LVCMOS33 [get_ports {clock}]

# LED2_FPGA J26
set_property LOC J26 [get_ports io_led0]
set_property IOSTANDARD LVCMOS33 [get_ports {io_led0}]

# LED3_FPGA H26
set_property LOC H26 [get_ports io_led2]
set_property IOSTANDARD LVCMOS33 [get_ports {io_led2}]

##Buttons
# Sw 2 - AF9
# set_property LOC AF9 [get_ports reset]
# set_property IOSTANDARD LVCMOS33 [get_ports {reset}]

# Sw 3 - AF10
# set_property LOC AF10 [get_ports sw3]
# set_property IOSTANDARD LVCMOS33 [get_ports {sw3}]

##### Daughter board #####

# LED0
# set_property LOC A18 [get_ports io_led0]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_led0}]

# LED1
# set_property LOC A19 [get_ports io_led1]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_led1}]

# LED2
# set_property LOC C17 [get_ports io_led2]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_led2}]

# LED3
# set_property LOC C18 [get_ports io_led3]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_led3}]

# LED4
# set_property LOC E18 [get_ports io_GPIO0[3]]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_GPIO0[3]}]

##Buttons
# SW2
set_property LOC B20 [get_ports reset]
set_property IOSTANDARD LVCMOS33 [get_ports {reset}]
# SW3
# set_property LOC A20 [get_ports d_sw3]
# set_property IOSTANDARD LVCMOS33 [get_ports {d_sw3}]
# SW4
# set_property LOC C19 [get_ports d_sw4]
# set_property IOSTANDARD LVCMOS33 [get_ports {d_sw4}]
# SW5
# set_property LOC B19 [get_ports d_sw5]
# set_property IOSTANDARD LVCMOS33 [get_ports {d_sw5}]


##USB-UART Interface (On jp3 in daughter board) Pin 1 - GND, Pin 3 - TX, Pin 5 - RX
# set_property LOC B12 [get_ports io_UART0tx]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_UART0tx}]
# set_property LOC B11 [get_ports io_UART0rx]
# set_property IOSTANDARD LVCMOS33 [get_ports {io_UART0rx}]