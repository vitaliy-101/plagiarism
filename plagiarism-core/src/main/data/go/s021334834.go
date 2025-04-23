package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		strNums := strings.Fields(scanner.Text())
		nums := []int{}
		for _, strNum := range strNums {
			num, _ := strconv.Atoi(strNum)
			nums = append(nums, num)
		}
		a := nums[0]
		b := nums[1]
		c := nums[2]
		d := nums[3]
		e := nums[4]
		f := nums[5]
		var mulA, mulD int
		if a < 0 {
			mulA = a * -1
		} else {
			mulA = a
		}
		if d < 0 {
			mulD = d * -1
		} else {
			mulD = d
		}
		a *= mulD
		b *= mulD
		c *= mulD
		d *= mulA
		e *= mulA
		f *= mulA

		if d < 0 {
			d *= -1
			e *= -1
			f *= -1
		}

		bE := b - e
		cF := c - f
		if bE == 0 {
			bE = 1
		}
		y := cF / bE
		x := (c - (y * b)) / a

		fmt.Print(x)
		fmt.Println(".000")
		fmt.Print(y)
		fmt.Println(".000")
	}
}

