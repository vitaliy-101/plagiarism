while True:
	try:
		data = map(float, raw_input().split())
		[a, b, c, d, e, f] = data
		
		n = (c*e - b*f)
		m = (a*f - c*d)
		i = (a*e - b*d)
		x = float(n) / (i)
		y = float(m) / (i)

		x = '%.3f' % (round(x,3))
		y = '%.3f' % (round(y,3))

		if x == '-0.000':
			x = '0.000'
		if y == '-0.000':
			y = '0.000'
			
		print(x, y)
	
	except EOFError:
		break
