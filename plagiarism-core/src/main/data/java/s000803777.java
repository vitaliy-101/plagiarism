import java.io.*;

class Main {
	public static void main(String args[]) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String t[], s;
			double a[] = new double[6];
			while((s = reader.readLine())  != null) {
				t = s.split(" ");
				for(int i = 0;i < a.length;i++) {
					a[i] = Double.parseDouble(t[i]);
				}
				double z = a[0]*a[4]-a[1]*a[3];
				double x = (a[4]*a[2]-a[1]*a[5])/z;
				double y = (-a[3]*a[2]+a[0]*a[5])/z;

				if(x == -0.0)
					x = 0.0;
				if(y == -0.0)
					y = 0.0;
				System.out.printf("%4.3f %4.3f\n", x, y);

			}

		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
}