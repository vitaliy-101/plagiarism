#include <algorithm>
#include <cmath>
#include <iomanip>
#include <iostream>
#include <list>
#include <vector>
using namespace std;

int main(int argc, char const *argv[]) {
  double x, y;
  double a, b, c, d, e, f;
  while (!(cin >> a >> b >> c >> e >> f >> d).eof()) {
    x = (f * c - b * d) / (a * f - b * e);
    y = (a * d - c * e) / (a * f - b * e);
    std::cout << fixed << setprecision(3) << x << " " << y << '\n';
  }
  return 0;
}

