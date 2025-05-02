#pragma GCC optimize "O3"
#include <vector>
#include <list>
#include <map>
#include <set>
#include <deque>
#include <stack>
#include <bitset>
#include <algorithm>
#include <functional>
#include <numeric>
#include <utility>
#include <sstream>
#include <iostream>
#include <iomanip>
#include <cstdio>
#include <cmath>
#include <cstdlib>
#include <cctype>
#include <string>
#include <cstring>
#include <ctime>
#define whole(f,x,...) ([&](decltype((x)) whole) { return (f)(begin(whole), end(whole), ## __VA_ARGS__); })(x)
#define ALL(a)  (a).begin(),(a).end()
#define RALL(a) (a).rbegin(), (a).rend()
#define PB push_back
#define MP make_pair
#define SZ(a) int((a).size())
#define EACH(i,c) for(typeof((c).begin()) i=(c).begin(); i!=(c).end(); ++i)
#define EXIST(s,e) ((s).find(e)!=(s).end())
#define SORT(c) sort((c).begin(),(c).end())
#define FOR(i,a,b) for(int i=(a);i<(b);++i)
#define REP(i,n)  FOR(i,0,n)
#define REP_FROM(i,m,n) for (int i = (m); (i) < int(n); ++(i))
#define REP_REV(i,n) for (int i = (n)-1; (i) >= 0; --(i))
#define REP_FROMREV(i,m,n) for (int i = (n)-1; (i) >= int(m); --(i))
#define CLR(a) memset((a), 0 ,sizeof(a))
#define dump(x)  cerr << #x << " = " << (x) << endl;
#define debug(x) cerr << #x << " = " << (x) << " (L" << __LINE__ << ")" << " " << __FILE__ << endl;
#ifndef LOCAL
#define cerr if (false) cerr
#endif
using namespace std;inline int toInt(string s) {int v; istringstream sin(s);sin>>v;return v;}
template<class T> inline string toString(T x) {ostringstream sout;sout<<x;return sout.str();}template<class T> inline T sqr(T x) {return x*x;}typedef vector<int> VI;typedef vector<VI> VVI;typedef vector<string> VS;typedef pair<int, int> PII;typedef long long LL;template <class T> inline void setmax(T & a, T const & b) { a = max(a, b); }template <class T> inline void setmin(T & a, T const & b) { a = min(a, b); }template <class T> T sq(T x) { return x * x; }template <class T> T clamp(T a, T l, T r) { return min(max(a, l), r); }const double EPS = 1e-10;const double PI  = acos(-1.0);void solve(int a, int b, int c, int d, int e, int f) {double x, y;y = (d*c-a*f)/(double)(d*b-a*e);x = (c-b*y)/(double)a;printf("%.3f %.3f\n", x, y);}int main(int argc, char *argv[]) {int a, b, c, d, e, f;while(cin >> a >> b >> c >> d >> e >> f) {solve(a,b,c,d,e,f);}return 0;}