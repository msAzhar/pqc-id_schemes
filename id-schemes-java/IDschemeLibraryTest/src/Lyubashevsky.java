import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.math.BigInteger; 
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


public class Lyubashevsky {
	// LWE-based IDscheme

	//-----------------------------------Global variables-----------------------------------

	static int[][] A_matrix;
	static int[] glob_w;
	static int[] glob_tilde_w;
	static int[] glob_y;
	static int[] glob_tilde_y;
	static int glob_m;
	static int glob_n;
	static int glob_q;
	

	//--------------------------------------------------------------------------------------

	public static int[] modVector(int[] v, int q) {
		int[] result = new int[v.length];
		if (q <= 0) {
			System.out.println("Modulus is not positive!");
			//break;
		}
		for (int i = 0; i < v.length; i++) {
			v[i] %= q;
			if (v[i] < 0) {
				v[i] += q;
			}
		}
		return v;
	}
	
	//Multiplies a vector by a scalar, c = s*v
	public static int[] scalarMultiplyVector(int s, int[] v) {
		int[] c = new int[v.length];
		for (int i = 0; i < v.length; i++) {
			c[i] = s * v[i];
		}
		return c;
	}
			
	public static void keyGeneration(int n, int m, int q) {
		int[][] amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
		int[] tilde_w = new int[m]; 
		glob_m = m;
		glob_n = n;
		glob_q = q;
		A_matrix = amatrix;

		for (int i = 0; i < glob_m; i++) { 
			tilde_w[i] = IDscheme.randInt(2);
		}
		
		// w = A.w_tilde mod q
		int[] aw = IDscheme.vectorMultiplyMatrix(tilde_w, amatrix);

		int[] w = modVector(aw, glob_q);
		
		A_matrix = amatrix;
		glob_w = w;
		glob_tilde_w = tilde_w;		
				
	}

	// Prover: p1()
	public static void p1(){ // 
		int[][] amatrix = A_matrix;
		int[] tilde_y = new int[glob_m]; 
		
		for (int i = 0; i < glob_m; i++) { 
			tilde_y[i] = IDscheme.randInt(5*glob_m-1);
		}
		glob_tilde_y = tilde_y;

		int[] ay = IDscheme.vectorMultiplyMatrix(tilde_y, amatrix);
		int[] y = modVector(ay, glob_q);

		glob_y = y;
		
	}
    
	// Verifier: v1()
	public static int v1(){
		int randnum = IDscheme.getRandomInteger(2, 0);
		return randnum;
	}
	
	// Prover: p2()
	public static Object[] p2(int c){
		int ch = c;
		Object[] resp = new Object[2];

		
		int[] yw = IDscheme.addVectors(glob_tilde_y,glob_tilde_w);
		int refuse = 0;
		for(int i=0;i<yw.length;i++){
			if (yw[i]<0 && yw[i]>(5*glob_m-1)){
				refuse = 1; // z perpendicular; 0 - refuse
			}else{
				refuse = 0;
			}
		}
		
		//refuse=1;
		//System.out.print(refuse);
		if(ch==1 && refuse==1){
			System.out.print("Failed!");
			System.exit(0);
		}
		
		int[] cw = scalarMultiplyVector(ch, glob_w);
		resp[0] = glob_tilde_y;
		resp[1] = cw;
		
		return resp;
	}
	
	public static double norm(int[] arr){ //compute norm
		int elements = arr.length;
		int total = 0;
			
		for(int i=0; i<elements; i++){
				  total += Math.pow(arr[i],2);
			}
			return Math.sqrt(total);
		}
	
	public static boolean compare(int[] arr1,int[] arr2){ //compare function
		for(int i=0; i<glob_m; i++){
			if(arr1[i]!=arr2[i]) {
				//System.out.println(arr1[i] + " ve " + arr2[i]);
				return false;
			}
		}
		return true;
	}
	
	// Verifier: v2()
	public static void v2(int ch, Object[] params){
		Object[] resp = params;
		/*System.out.println(Arrays.toString(resp));
		System.out.println(resp[0]);
		System.out.println(Arrays.toString((int[])resp[1]));
*/
	//	int p1 = (int)resp[0];
		
	//	System.out.print(String.valueOf(p1));
		for(int i=0;i<resp.length; i++) {
	//	System.out.print((int)resp[i]);
		}
	
		int d;
		double condition = 5*(Math.pow(glob_m,1.5));
		double norm_z = norm((int[])params[0]);
	
		int[] z = (int[]) params[0];
		int[][] amatrix = A_matrix;	
		
		int[] comp1 = IDscheme.vectorMultiplyMatrix(z, amatrix);
		int[] az = modVector(comp1, glob_q);
		int[] comp2 = IDscheme.addVectors((int[])params[1], glob_y);
	/*	System.out.println("Az length:" + az.length);
		System.out.println("cw+y length:" + comp2.length);
		
		for(int i =0;i<az.length;i++) {
			System.out.println("Az: " + az[i] + "cw + y =: " + comp2[i]);
		}
	*/
		//System.out.println(Arrays.toString(az).equals(Arrays.toString(comp2)));
		boolean condition2 = Arrays.toString(az).equals(Arrays.toString(comp2));
		if( norm_z<=condition && (condition2)){ 
			System.out.println("Success!");
			d=1;
		}else{
			System.out.println("Failed!");
			d=0;
	}
		
	}

	//===============================================================================
	//------------------------------------------- start --------------------------------------------------

	public static void main(String[] args) {
	
		int n = 608, 
			q = 1024, 
			m = 960;
	
		System.out.println("LWE-based ID Scheme:");
		System.out.println("Parameters Set:");
	/*	System.out.println("n = " + n);
		System.out.println("m = " + m);
		System.out.println("q = " + q);
	*/	long t0 = System.nanoTime();
		keyGeneration(n,m, q);
		long t1 = System.nanoTime();

	//	System.out.println("Prover: compute commitments c1, c2 and c3:");
		long t2 = System.nanoTime();
		p1();
		long t3 = System.nanoTime();

		int ch;
		ch = v1();
	//	System.out.println("Verifier: sends a challenge: " + ch);
	//	System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
		System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
		long t4 = System.nanoTime();
		Object[] response = p2(ch);
		long t5 = System.nanoTime();
		v2(ch,response);
		long t6 = System.nanoTime();
		
		System.out.println("Keygen Execution time in milliseconds : " + 
				(t1-t0) / 1000000);
		System.out.println("Commitments comp Execution time in milliseconds : " + 
				(t3-t2) / 1000000);
		//System.out.println("Revealing some params Execution time in milliseconds : " + 
		//		(t5-t4) / 1000000);
		System.out.println("Verification Execution time in milliseconds : " + 
				(t6-t5) / 1000000);

	}

}
