import java.util.*;

class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			double[] c = new double[6];
			for (int i = 0; i < 6; i++) {
				c[i] = scanner.nextDouble();
			}
			double x = (c[4] * c[2] - c[1] * c[5]) / (c[0] * c[4] - c[3] * c[1]);
			double y = (c[0] * c[5] - c[3] * c[2]) / (c[0] * c[4] - c[3] * c[1]);
			System.out.printf("%.3f %.3f\n", x, y);
		}
	}
}