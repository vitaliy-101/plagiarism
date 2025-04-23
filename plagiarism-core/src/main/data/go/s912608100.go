package main

import (
        "fmt"
        "bufio"
        "os"
        "math"
)

func main() {
        var a, b, c, d, e, f float64
        sc := bufio.NewScanner(os.Stdin)
        for sc.Scan() {
                fmt.Sscanf(sc.Text(), "%f %f %f %f %f %f", &a, &b, &c, &d, &e, &f)

                det := a*e - b*d
                x := (e*c - b*f) / det
                y := (a*f - c*d) / det
                if x == 0.0 {
                        x = math.Abs(x)
                }
                if y == 0.0 {
                        y = math.Abs(y)
                }
                fmt.Printf("%.3f %.3f\n", x, y)
        }
}

