# Aizu Problem 0004: Simultaneous Equation
#
import sys, math, os

# read input:
PYDEV = os.environ.get('PYDEV')
if PYDEV=="True":
    sys.stdin = open("sample-input.txt", "rt")


for line in sys.stdin:
    a, b, c, d, e, f = [int(_) for _ in line.split()]
    det = a * e - b * d
    det1 = c * e - b * f
    det2 = a * f - c * d
    x = round(det1 / det, 3)
    y = round(det2 / det, 3)
    #if -.001 < x < 0:
    #    x = 0
    print("%.3f %.3f" % (x, y))