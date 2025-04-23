#include<bits/stdc++.h>
using namespace std;

const double EPS=1e-8;
typedef vector<double>vec;
typedef vector<vec>mat;

/// Ax=b
vec gauss_jordan(const mat& A,const vec& b){
    int n=A.size();
    mat B(n,vec(n+1));

    for(int i=0;i<n;i++)
        for(int j=0;j<n;j++)B[i][j]=A[i][j];

    for(int i=0;i<n;i++)B[i][n]=b[i];

    for(int i=0;i<n;i++){
        int pivot=i;
        for(int j=i+1;j<n;j++){
            if(abs(B[j][i])<abs(B[pivot][i]))pivot=j;
        }
        swap(B[i],B[pivot]);
        //if(abs(B[i][i])<EPS)return vec();

        for(int j=i+1;j<=n;j++)B[i][j]/=B[i][i];
        for(int j=0;j<n;j++){
            if(i==j)continue;
            for(int k=i+1;k<=n;k++)B[j][k]-=B[j][i]*B[i][k];
        }
    }

    vec x(n);
    for(int i=0;i<n;i++)x[i]=B[i][n];
    return x;
}

int main(){
    mat A(2,vec(2));
    vec b(2);
    while(true){
        for(int i=0;i<2;i++){
            if(scanf("%lf%lf%lf",&A[i][0],&A[i][1],&b[i])==EOF)return 0;
        }

        vec x=gauss_jordan(A,b);
        if(x.size()<2)return 0;
        printf("%.3lf %.3lf\n",x[0],x[1]);
    }
    return 0;
}