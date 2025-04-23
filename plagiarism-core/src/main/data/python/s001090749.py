import sys

lines = sys.stdin.readlines()
for line in lines:
    a = list(map(float, line.split(" ")))
    tmp = a[0]
    for i in range(0, 3):
        a[i] = a[i] * a[3]
    for i in range(3, 6):
        a[i] = a[i] * tmp
    y = (a[2] - a[5]) / (a[1] - a[4])
    x = (a[2] - a[1] * y) / a[0]
    print("{0:0.3f} {1:0.3f}".format(x, y))

