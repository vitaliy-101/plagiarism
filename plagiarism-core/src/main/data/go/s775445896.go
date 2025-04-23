package main

import "fmt"

func main() {

	for {
		var a, b, c, d, e, f float64
		var x, y float64
		i, _ := fmt.Scanf("%f %f %f %f %f %f", &a, &b, &c, &d, &e, &f)
		if i != 6 {
			return
		}

		// x = ((e * c) - (b * f)) / ((a * e) - (b * d))
		y = ((a * f) - (d * c)) / ((a * e) - (b * d))
		x = (c - b*y) / a

		fmt.Printf("%.3f %.3f\n", x, y)
	}
}

