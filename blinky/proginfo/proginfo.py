#!/usr/bin/python3
import os
import sys
import glob
from string import Template

def main():
    print("Build completed")
    # If possible, reuse the templates just passing the correct files
    if sys.argv[1] == 'artya7-35t':
        d = {
            'interfaceConfig': findFile("digilent-hs1.cfg"),
            'boardConfig': findFile("xilinx-xc7.cfg"),
            'bitstream': findFile("Toplevel.bit")
        }
        render(d, findFile('artix7.txt'))
    elif sys.argv[1] == 'ulx3s-85f':
        d = {
            'interfaceConfig': findFile("ft231x.cfg"),
            'boardConfig': findFile("LFE5U-85F.cfg"),
            'bitstream': findFile("carlosedp_demo_chiselblinky_0.bit"),
            'bitstreamSVF': findFile("carlosedp_demo_chiselblinky_0.svf")
        }
        render(d, findFile('ulx3s.txt'))
    elif sys.argv[1] == 'dfu-util':
        d = {
            'bitstream': findFile("carlosedp_demo_chiselblinky_0.bit")
        }
        render(d, findFile('dfu-util.txt'))
    print("")

# Utility Functions

def findFile(f):
    r = glob.glob("../**/" + f, recursive=True)
    if not r:
        print("ERROR: Could not find file " + f)
        exit(1)
    return os.path.realpath(r[0])

def render(d, template):
    with open(os.path.join(os.getcwd(),template)) as f:
            src = Template(f.read())
            output = src.substitute(d)
            print(output)

if __name__ == "__main__":
    if len(sys.argv) <= 1:
        print("Board not supplied")
        exit(1)
    main()
