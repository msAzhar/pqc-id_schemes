import java.util.Arrays;

public class Soysaldi {
	
	//-----------------------------------Global variables-----------------------------------

		static int[][] A_matrix;
		static int[] r_vector;
		static int[] x_vector;
		static int[] y_Ax;
		static int glob_m;
		static int glob_n;
		static int glob_q;
		static int glob_sigma;
		static int[] glob_seed1;
		static int[] glob_seed2;
		
		static int glob_alpha;
		
		static String c0, c1;
		
	//--------------------------------------------------------------------------------------

		public static void keyGeneration(int n, int m,int q) {
			int[][] amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
			
			int[] x = new int[m]; //x is sk
			int[] ss = new int[m]; // temp var for x \in F_2^m
			int[] seed1 = new int[n]; //seed1 \in F_2^m
			int[] seed2 = new int[n]; //seed2 \in F_2^m
			
			glob_m = m;
			glob_n = n;
			glob_q = q;
			A_matrix = amatrix;
		
			for (int i = 0; i < (m/2); i++) {
				ss[i] = 1;
			}

			for (int  i = m/2; i < m; i++) {
				ss[i] = 0;
			}
			
			//x = shuffle(ss);		
			x = IDscheme.assign(ss);
			
			for (int i = 0; i < n; i++) {
				seed1[i] = IDscheme.randInt(2);
			}
			
			for (int i = 0; i < n; i++) {
				seed2[i] = IDscheme.randInt(2);
			}
			
			//int[][] a_transpose = IDscheme.transpose(amatrix);
			int[] y = IDscheme.vectorMultiplyMatrix(x, amatrix);
			
			A_matrix = amatrix;
			x_vector = x; // sk
			y_Ax = y; // pk
		
			// !!check randInt(2);
			glob_seed1 = seed1;
			glob_seed2 = seed2;
			
			
		}
		
		// Prover: p1()
		public static void p1(){ //
			int[][] amatrix = A_matrix;
			int[] r = new int[glob_m];
			int[] z = new int[glob_m]; // vector s \in F_q^m
			int[] r0 = new int[glob_n];
			int[] r1 = new int[glob_n];
			int[] x = new int[glob_m];
			int sigma; // rand
			x = IDscheme.assign(x_vector);

			for (int i = 0; i < glob_m; i++) { // u = Z_q^m
				r[i] = IDscheme.randInt(glob_q);
			}
			
			sigma = IDscheme.randInt(4); //glob_q-1 ?
			glob_sigma = sigma;
			
			r_vector = r;
			
			int[] part1 = IDscheme.knuth_shuffle(r, glob_sigma);
			int[] ar = IDscheme.vectorMultiplyMatrix(r, amatrix); // Ar
			
			c0 = IDscheme.com(Arrays.toString(part1) + Arrays.toString(glob_seed1)); // com(pi(r), seed1)
			c1 = IDscheme.com("psi"+Arrays.toString(ar) + Arrays.toString(glob_seed2)); // com(pi, Ar, seed2)
			
			
			
		}
		
		// returns random integer between minimum and maximum range
	    public static int getRandom(int maximum, int minimum){
	        return ((int) (Math.random()*(maximum - minimum))) + minimum;
	    }

		// returns random doubleeger between minimum and maximum range
	    public static double getRandom(double maximum, double minimum){
	        return ((double) (Math.random()*(maximum - minimum))) + minimum;
	    }
	    
		// Verifier: v1()
		public static int v1(){
			int alpha = getRandom(glob_q, 1);
			glob_alpha = alpha;
			int b = getRandom(3, 1);
			return b;
		}
		
		//Multiplies a vector by a scalar, c = s*v
		public static int[] scalarMultiplyVector(int s, int[] v) {
			int[] c = new int[v.length];
			for (int i = 0; i < v.length; i++) {
				c[i] = s * v[i];
			}
			return c;
		}
		
		// Prover: p2()
		public static Object[] p2(int c){
			int ch = c;
			Object[] resp = new Object[3];

			if(ch == 1){
				int[] alpha_x = scalarMultiplyVector(glob_alpha,x_vector);
				int[] part1 = IDscheme.addVectors(r_vector, alpha_x);
				int[] beta_0 = IDscheme.knuth_shuffle(part1, glob_sigma);
				int[] s = IDscheme.knuth_shuffle(x_vector, glob_sigma);
				resp[0] = s;
				resp[1] = beta_0;
				resp[2] = glob_seed1;
			}
			else if(ch == 2){
				int[] alpha_x = scalarMultiplyVector(glob_alpha,x_vector);
				int[] part1 = IDscheme.addVectors(r_vector, alpha_x);
				int[] beta_1 = IDscheme.vectorMultiplyMatrix(part1, A_matrix);
				String psi = "psi";
				resp[0] = psi;
				resp[1] = beta_1;
				resp[2] = glob_seed2;
			}else{
				System.out.println("Error!");
			}
			
			return resp;	
				
		}
		
		// Verifier: v2()
		public static void v2(int c, Object[] params){
			int ch = c;
			Object[] resp = params;

			if(ch == 1){
				String computed_c0; // computations 
	
				int[] part1 = scalarMultiplyVector(glob_alpha,(int[])params[0]);
				int[] part2 = IDscheme.subtractVectors((int[])params[1],part1); //beta_0 -alpha.s
				computed_c0 = IDscheme.com(Arrays.toString(part2) + Arrays.toString(glob_seed1)); //glob_seed1 => params[2]
	
				//System.out.println("computed c0:" + computed_c0);
				//System.out.println("c0:" + c0);
			
				if( computed_c0.equals(c0)){ //TODO isinX check
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else if(ch == 2){
				String computed_c1;
				int[] part1 = scalarMultiplyVector(glob_alpha,y_Ax);
				int[] part2 = IDscheme.subtractVectors((int[])params[1],part1); //beta_1 -alpha.s
				computed_c1 = IDscheme.com((String)params[0] + Arrays.toString(part2) + Arrays.toString(glob_seed2)); //glob_seed2 => params[2]

				//System.out.println("computed c1:" + computed_c1);
				//System.out.println("c1:" + c1);
				
				if( c1.equals(computed_c1)){  
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else{
				System.out.println("Error!");
			}
		}
		public static void main(String[] args) {
			
			int n = 512, 
				q = 257, 
				m = 2048;
		/*
			System.out.println("Soysaldi's ID Scheme:");
			System.out.println("Parameters Set:");
			System.out.println("n = " + n);
			System.out.println("m = " + m);
			System.out.println("q = " + q);
		*/	long t0 = System.nanoTime();
			keyGeneration(n, m, q);
			long t1 = System.nanoTime();

			//System.out.println("Prover: compute commitments c1, c2 and c3:");
			long t2 = System.nanoTime();
			p1();
			long t3 = System.nanoTime();
			int b = v1();
		/*	System.out.println("Verifier: sends a challenge: " + b);
			System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + b + ")");
			System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
		*/	long t4 = System.nanoTime();
			Object[] response_p2 = p2(b);
			long t5 = System.nanoTime();
			v2(b,response_p2);
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
