import java.io.BufferedReader;
import java.io.InputStreamReader;


class Computes{
	public float[] Solve(int a, int b, int c, int d, int e, int f){
		float[] sols = new float[2];
		sols[0] = (float) (e*c-b*f)/(a*e-b*d);
		sols[1] = (float) (a*f-d*c)/(a*e-b*d);
		return sols;
	}
}

public class Main {
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		Computes comp = new Computes();
		try{
			while((line = br.readLine())!=null){
		//		System.out.println(line);
			String[] strs = line.split(" ");
			int[] ints = new int[strs.length];
			for(int i = 0; i< strs.length ; i++){
				 ints[i] = Integer.parseInt(strs[i]);
			//	 System.out.print(ints[i]);
			}
			float[] sols = comp.Solve(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]);
			System.out.printf("%.3f %.3f\n",sols[0],sols[1]);
			}
		} catch(Exception e){}
	}

}