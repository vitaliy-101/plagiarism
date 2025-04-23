import sys
for line in sys.stdin:
    a,b,c,d,e,f = map(float, line.split())
    print((c*e-b*f)/(a*e-b*d), (a*f-c*d)/(a*e-b*d))