package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

var sc = bufio.NewScanner(os.Stdin)

func read() string {
	sc.Scan()
	return sc.Text()
}

func main() {
	sc.Split(bufio.ScanLines)
	for sc.Scan() {
		s := strings.Split(sc.Text(), " ")
		a, _ := strconv.ParseFloat(s[0], 64)
		b, _ := strconv.ParseFloat(s[1], 64)
		c, _ := strconv.ParseFloat(s[2], 64)
		d, _ := strconv.ParseFloat(s[3], 64)
		e, _ := strconv.ParseFloat(s[4], 64)
		f, _ := strconv.ParseFloat(s[5], 64)
		x := ((b * f) - (c * e)) / ((b * d) - (a * e))
		y := ((a * f) - (c * d)) / ((a * e) - (b * d))
		if x == 0 {
			fmt.Printf("0.000 %.3f\n", y)
		} else if y == 0 {
			fmt.Printf("%.3f 0.000\n", x)
		} else {
			fmt.Printf("%.3f %.3f\n", x, y)
		}
	}
}

