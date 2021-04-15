#!/usr/bin/python3
import os
import sys

system_name = "carlosedp_demo_chiselblinky_0"
print("")
print("Build was completed")
print("To program the board run:")
print("")
if sys.argv is None:
    exit
if sys.argv[1] == 'prog-artya7-35t':
    interfaceConfig = os.path.join(os.getcwd(), "../src/"+system_name, "openocd/digilent-hs1.cfg")
    boardConfig = os.path.join(os.getcwd(), "../src/"+system_name, "openocd/xilinx-xc7.cfg")
    bitFile = os.path.join(os.getcwd(), "Toplevel.bit")
    print("openocd -f {} -f {} -c \"transport select jtag;init; pld load 0 {}; exit\"".format(interfaceConfig, boardConfig, bitFile))
elif sys.argv[1] == 'prog-ulx3s':
    print("ujprog {}".format(os.path.join(os.getcwd(), system_name+".bit")))
    print("Other programming options are listed here https://github.com/emard/ulx3s/blob/master/doc/MANUAL.md#programming-options")
elif sys.argv[1] == 'dfu-util-fomu':
    print("Download and install dfu-util from http://dfu-util.sourceforge.net/")
    print("dfu-util -e -d 1209:5bf0 -D {}".format(os.path.join(os.getcwd(), system_name+".bin")))
print("")