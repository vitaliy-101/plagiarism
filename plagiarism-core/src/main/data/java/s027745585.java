import java.util.Scanner;

class SimultaneousEquation {
	public class UnknowNumbers{
		float x;
		float y;
	}

	public UnknowNumbers formula(float para_a, float para_b, float para_c, float para_d, float para_e, float para_f){
		UnknowNumbers numbers = new UnknowNumbers();
		/*
		if(para_a == 0){
			if(para_b == 0){
				System.out.println("a and b should not equals 0 at the same time!!!");
				System.exit(0);
			}
			numbers.y = para_c / para_b;
			if(para_d == 0){
				System.out.println("The parallel lines or the same line!!!");
				System.exit(0);
			}
			numbers.x = ((para_b * para_f) - (para_c * para_e)) / (para_d * para_b);

		} else if((para_a * para_e) - (para_d * para_b) == 0){
			System.out.println("The parallel lines or the same line!!!");
			System.exit(0);
		} else{*/
			numbers.y = ((para_a * para_f) - (para_d * para_c)) / ((para_a * para_e) - (para_d * para_b));
			numbers.x = (para_c - para_b * numbers.y) / para_a;
		//}
		return numbers;
	}
	public static void main(String args[]){

		SimultaneousEquation equation = new SimultaneousEquation();
		UnknowNumbers numbers;
		String[] strParameters;

		Scanner input = new Scanner(System.in);

		while(input.hasNext()){
			strParameters = input.nextLine().split(" ");

			float[] parameters = new float[strParameters.length];


			for(int iPara = 0; iPara < strParameters.length; iPara++){
				parameters[iPara] = Float.parseFloat(strParameters[iPara]);
				/*if(parameters[iPara] < -1000 || parameters[iPara] > 1000){
					System.out.println("Some parameters out of limit!");
					System.exit(0);
				}*/
			}
			numbers = equation.formula(parameters[0],parameters[1],parameters[2],parameters[3],parameters[4],parameters[5]);
			if(numbers.x == -0.0f)	numbers.x = 0.0f;
			if(numbers.y == -0.0f)	numbers.y = 0.0f;
			System.out.printf("%.3f %.3f\n",numbers.x,numbers.y);
		}
	}
}