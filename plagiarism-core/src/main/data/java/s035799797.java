import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class Main {

	public static void main(String[] args) {
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
		String abcdef = null;
		String[] aToF = null;
		int[] aToFNum = null;
		double x = 0;
		double y = 0;

		try {
			abcdef = br.readLine();
			aToF = abcdef.split(" ");
			aToFNum = new int[aToF.length];
			for(int i = 0;i<aToF.length;i++){
				aToFNum[i] = Integer.parseInt(aToF[i]);
			}
		} catch (IOException e) {

		}
		//a or d が負であれば、正に直す
		if(aToFNum[0] < 0){
			aToFNum[0] *= -1;
			aToFNum[1] *= -1;
			aToFNum[2] *= -1;
		}
		if(aToFNum[3] < 0){
			aToFNum[3] *= -1;
			aToFNum[4] *= -1;
			aToFNum[5] *= -1;
		}

		//a and d をa,dの公倍数になるように掛ける
		if(aToFNum[0] != aToFNum[3]){
			int temp1 = aToFNum[0];
			aToFNum[0] *= aToFNum[3];
			aToFNum[1] *= aToFNum[3];
			aToFNum[2] *= aToFNum[3];
			aToFNum[3] *= temp1;
			aToFNum[4] *= temp1;
			aToFNum[5] *= temp1;
		}
		int temp2 = aToFNum[1] - aToFNum[4];
		int temp3 = aToFNum[2] - aToFNum[5];

		y = temp3 / temp2;

		x = (aToFNum[2] - aToFNum[1] * y) / aToFNum[0];

		//小数第三位で四捨五入
		System.out.println(String.format("%.3f", x) + " " + String.format("%.3f", y));
	}
}