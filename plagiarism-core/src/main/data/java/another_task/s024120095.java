
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws NumberFormatException,
			IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		StringBuilder builder = new StringBuilder();
		int n = Integer.parseInt(reader.readLine());
		while (n-- > 0) {
			int a = Integer.parseInt(reader.readLine());
			int count = 0;
			for (int i = 5; i < a; i *= 5) {
				count += a / i;
			}
			System.out.println(count);
		}

	}
}