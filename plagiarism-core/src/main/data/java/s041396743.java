import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] v = line.split(" ");
				double a = Double.parseDouble(v[0]);
				double b = Double.parseDouble(v[1]);
				double c = Double.parseDouble(v[2]);
				double d = Double.parseDouble(v[3]);
				double e = Double.parseDouble(v[4]);
				double f = Double.parseDouble(v[5]);

				double x = (c * e - b * f) / (a * e - b * d) + 0;
				double y = (c * d - a * f) / (b * d - a * e) + 0;

				System.out.println(String.format("%.3f %.3f", x, y));
			}
		} catch (Exception e) {
		}
	}
}