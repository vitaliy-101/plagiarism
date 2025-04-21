// White_Devil04
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
 
 
public class Main {
	static int mod = (int) (1e9 + 7);
	static int minInt = Integer.MIN_VALUE; // int range - 2*1e9
	static int maxInt = Integer.MAX_VALUE;
	static long minLong = Long.MIN_VALUE; // long range - 9*1e18
	static long maxLong = Long.MAX_VALUE; 
	static int N = (int) (1e6 + 1); // str length
 
		
		public static void main(String[]args) throws IOException {
			new Main().run();
		}
		void run() throws IOException{
			new Solve().setIO(System.in,System.out).run();
		}
/*
 *  TAKE CARE OF THIS MISTAKES:
 *      1) int and long which to use according to the limits.
 *      2) never try to divide num by any other num in condition conver it to multiply condition.
 *      3) carefully check that you are writing the right conditions at write position.
 *      4) form solution again from scratch or try another method to solve it.
 */
 
/*-----------------------------------------MAIN CODE STARTS HERE------------------------------------------ */
		class Solve extends ioTask{
        void solve() throws IOException{
 
            int n=sc.nextInt();
            int a[]= new int[n];
            for(int i=0;i<n;i++){
                a[i]=sc.nextInt();
            }
            long gcd=a[n-1];
            if(gcd==1) {
                System.out.println(2*n);
                return;
            }
            int i=0,j=0;
            for(i=n-2;i>=0;i--){
                gcd = MyAlgo.gcd(gcd, a[i]);
                if(gcd==1) break;
            }
            if(i==-1){
                System.out.println(-1);
                return;
            }
            for(j=n-1;j>i;j--){
                if(MyAlgo.gcd(a[i], a[j])==1) break;
            }
            System.out.println(i+j+2);
            
 
 
            
			
			
            
			
			
			
			
        }
 
 
        boolean isPrime(int n){
            boolean is=true;
            for(int i=2;i<=Math.sqrt(n);i++){
                if(n%i==0){
                    is=false;
                    break;
                }
            }
            return is;
        }
 
        int te;
        @Override
        void run()throws IOException{
			// MyAlgo.preFact();
            // preCalc();
 
            int t=1;
			// solve();
            t=sc.nextInt();
 
            for(te=1;te<=t;te++) {
                solve();
				//out.flush();
            }
 
            out.close();
        }
 
/* --------------------------------------MAIN CODE ENDS HERE---------------------------------------------- */
 
        long timeMillis;
        void st() {
            timeMillis=System.currentTimeMillis();
        }
        void ed() {
            System.out.println((System.currentTimeMillis()-timeMillis)+"ms");
        }
    }
 
 
 
	
 
    
	/* --------------------------------------CUSTOM FUNCTIONS START HERE---------------------------------------------- */
    
	static class MyAlgo {
 
        static int add(int a,int b) {
            return (a+b)%mod;
        }
 
        static int sub(int a,int b) {
            return (a-b+mod)%mod;
        }
 
        static int mul(int a,int b) {
            return (int)((long)a*b%mod);
        }
        
        static int inv(int a) {
            return power(a, mod - 2);
            // a %= mod;
            // if (a < 0) a += mod;
            // int b = mod, u = 0, v = 1;
            // while (a != 0) {
            //     int t = b / a;
            //     int temp = b - t * a;
            //     b = a;
            //     a = temp;
 
            //     temp = u - t * v;
            //     u = v;
            //     v = temp;
            // }
            // return (u+mod)%mod;
        }
 
        static int power(int a, long b) {
            int res = 1;
            while (b > 0) {
                if ((b & 1) == 1) {
                    res = mul(res, a);
                }
                a = mul(a, a);
                b >>= 1;
            }
            return res;
        }
 
        static long gcd(long a,long b) {
            if(b==0) return a;
			return gcd(b,a%b);
		}
        
		// static int lcm(int a,int b) {
        //     return mul(mul(a,b),inv(gcd(a,b)));
		// }
 
        static int fact[]= new int[N];
        static int invFact[]= new int[N];
 
        static void preFact() {
            fact[0]=1;
            for(int i=1;i<N;i++) {
                fact[i]=mul(fact[i-1],i);
            }
            invFact[N - 1] = power(fact[N - 1], mod - 2);
            for (int i = N - 2; i >= 0; i--) {
                invFact[i] = mul(invFact[i + 1], i + 1);
            }
        }
 
        static int fac(int n) {
            return fact[n];
        }
 
        static long nCr(int n,int r) {
            if (r > n || r < 0) return 0;
            return mul(fact[n], mul(invFact[r], invFact[n - r]));
        }
	}
    
    /* --------------------------------------CUSTOM FUNCTIONS END HERE---------------------------------------------- */
	
	
 
	static class Reader{
        private StringTokenizer in=new StringTokenizer("");
        private InputStream is;
        private BufferedReader br;
        public Reader(File file) throws IOException {
            is=new FileInputStream(file);
            init();
        }
        public Reader(InputStream is) throws IOException{
            this.is=is;
            init();
        }
        private void init() throws IOException {
            br=new BufferedReader(new InputStreamReader(is));
        }
        /**
         * File and OJ only
         */
        boolean hasNext() throws IOException {
            return in.hasMoreTokens()||br.ready();
        }
        String readStr() throws IOException {
            while(!in.hasMoreTokens()) {
                in=new StringTokenizer(br.readLine());
            }
            return in.nextToken();
        }
        int nextInt() throws IOException {
            return Integer.parseInt(readStr());
        }
        long nextLong() throws IOException {
            return Long.parseLong(readStr());
        }
        double nextDouble() throws IOException {
            return Double.parseDouble(readStr());
        }
        String readLine() throws IOException {
            return br.readLine();
        }
    }
 
	class Out{
        PrintWriter out;
        private OutputStream os;
        private void init() {
            out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
        }
        public Out(File file) throws IOException {
            os=new FileOutputStream(file);
            init();
        }
        public Out(OutputStream os) throws IOException{
            this.os=os;
            init();
        }
    }
 
	abstract class ioTask{
        Reader sc;
        PrintWriter out;
        public ioTask setIO(File in,File out) throws IOException{
            this.sc=new Reader(in);
            this.out=new Out(out).out;
            return this;
        }
        public ioTask setIO(File in,OutputStream out) throws IOException{
            this.sc=new Reader(in);
            this.out=new Out(out).out;
            return this;
        }
        public ioTask setIO(InputStream in,OutputStream out) throws IOException{
            this.sc=new Reader(in);
            this.out=new Out(out).out;
            return this;
        }
        public ioTask setIO(InputStream in,File out) throws IOException{
            this.sc=new Reader(in);
            this.out=new Out(out).out;
            return this;
        }
        abstract void run()throws IOException;
    }
 
	
}