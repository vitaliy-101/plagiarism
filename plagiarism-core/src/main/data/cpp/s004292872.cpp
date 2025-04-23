#include<iostream>
using namespace std;
int main(){
	double a[60000]={0};
	int i=0;
	while(cin>>a[i++]);
	int n=i-1;
	for(int j=0;j<n;j+=6){
		double k=a[j]/a[j+3],x=0,y=0;
		a[j+3]*=k,a[j+4]*=k,a[j+5]*=k;
		y=(a[j+2]-a[j+5])/(a[j+1]-a[j+4]);
		x=(a[j+2]-a[j+1]*y)/a[j];
		cout<<x<<" "<<y<<endl;
	}
	return 0;
}