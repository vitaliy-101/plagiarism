# coding: utf-8
# Your code here!

while True:
   try:
      a, b, c, d, e, f = map(float, input().split())
      x = (c * e - b * f) / (a * e - b * d)
      y = (c - a * x) / b
      
      if x == 0:
         print('{:.3f}'.format(abs(x)), '{:.3f}'.format(y))
      elif y == 0:
         print('{:.3f}'.format(x), '{:.3f}'.format(abs(y)))
      else:
         print('{:.3f}'.format(x), '{:.3f}'.format(y))
   except EOFError:
      break

