import java.util.Scanner;
public class Main
{
	public static void main(String[] args){
		//??\???a,b,c,d,e,f
		Scanner scan = new Scanner(System.in);
		//??£???????¨????ax+by=c, dx+ey=f???x,y????±???????
		int a = scan.nextInt();
		int b = scan.nextInt();
		int c = scan.nextInt();
		int d = scan.nextInt();
		int e = scan.nextInt();
		int f = scan.nextInt();
		int a2 = scan.nextInt();
		int b2 = scan.nextInt();
		int c2 = scan.nextInt();
		int d2 = scan.nextInt();
		int e2 = scan.nextInt();
		int f2 = scan.nextInt();
		simultaneousEquation(a,b,c,d,e,f);
		simultaneousEquation(a2,b2,c2,d2,e2,f2);



	}

	//??£???????¨????????????????
	public static void simultaneousEquation(int a, int b, int c, int d, int e,int f){
		double x = 0;
		double y = 0;
	    x = (c*e - b*f)/(a*e - b*d);
	    y = (c*d - a*f)/(b*d - a*e);
		System.out.printf("%.3f %.3f\n",x,y);

	}
}