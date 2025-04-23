#include <iostream>

using namespace std;

#define EPS 1e-9

int main()
{
	int a,b,c,d,e,f;
	float x,y,dx,dy;
	while( cin >> a >> b >>c >>d >> e >> f ) {
		x = (c*e-b*f)/(float)(a*e-b*d);
		y = (c - a*x)/(float)b;
		

		printf("%.3f %.3f\n",x+EPS,y+EPS);
	}



	return 0;
}