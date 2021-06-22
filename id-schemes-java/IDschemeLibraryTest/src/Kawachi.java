import java.util.Arrays;

public class Kawachi {
	// Kawachi's IDscheme

		//-----------------------------------Global variables-----------------------------------

		static int[][] A_matrix;
		static int[] r_vector;
		static int[] x_vector;
		static int[] y_Ax;
		static int glob_m;
		static int glob_q;
		static String c1, c2, c3;

		//--------------------------------------------------------------------------------------
		public static void keyGeneration(int n, int m, int q) {
			int[][] amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
			int[] x = new int[m];	
			int[] ss = new int[m]; // temp var for x vector 
			glob_m = m;
			glob_q = q;
			
			for (int i = 0; i < (m/2); i++) {
				ss[i] = 1;
			}

			for (int i = m/2; i < m; i++) {
				ss[i] = 0;
			}
			
			// x = shuffle(ss);
			x = IDscheme.knuth_shuffle(ss);

			int[] y = IDscheme.vectorMultiplyMatrix(x, amatrix);
			
			A_matrix = amatrix;
			x_vector = x; // sk
			y_Ax = y; // pk
		}

		// Prover: p1()
		public static void p1(){ // 
			int[][] amatrix = A_matrix;
			int[] x = new int[glob_m];
			int[] r = new int[glob_m];
			int[] z = new int[glob_m]; // temp for (x+r)
			x = x_vector;

			for (int i = 0; i < glob_m; i++) {
				r[i] = IDscheme.randInt(glob_q);
			}

			r_vector = r;

			z = IDscheme.addVectors(x,r);
			
			int[] ar = IDscheme.vectorMultiplyMatrix(r, amatrix);
			int[] pr = new int[glob_m]; // pi(r) icin

			pr = IDscheme.knuth_shuffle(r); // c2
			
			c1 = IDscheme.com("pi"+Arrays.toString(ar));
			c2 = IDscheme.com(Arrays.toString(pr)); // com(pi(r))
			c3 = IDscheme.com(Arrays.toString(IDscheme.knuth_shuffle(z))); // com(pi(x+r))
		
		/*	System.out.println("c1:" + c1);
			System.out.println("c2:" + c2);
			System.out.println("c3:" + c3);
		*/	
		}

		// returns random integer between minimum and maximum range
	    public static int getRandomInteger(int maximum, int minimum){
	        return ((int) (Math.random()*(maximum - minimum))) + minimum;
	    }
	    
		// Verifier: v1()
		public static int v1(){
			int randnum = getRandomInteger(4, 1);
			return randnum;
		}
		
		// Prover: p2()
		public static Object[] p2(int c){
			int ch = c;
			Object[] resp = new Object[4];
			int[] s = new int[glob_m];
			int[] t = new int[glob_m];
			String fi ="pi";
			String psi="pi";
			int[] v = new int[glob_m];
			int[] r = new int[glob_m];
			int[] x = new int[glob_m];
			int[] u = new int[glob_m];

			if(ch == 1){
				r = r_vector;
				s = IDscheme.knuth_shuffle(x_vector);
				t = IDscheme.knuth_shuffle(r);
				resp[0] = c2;
				resp[1] = c3;
				resp[2] = s;
				resp[3] = t; //t = pi(r) //print(t);
			}
			else if(ch == 2){
				r = r_vector;
				x = x_vector;
				u = IDscheme.addVectors(x,r);
				resp[0] = c1;
				resp[1] = c3;
				resp[2] = fi;
				resp[3] = u; //u=(x+r)
			}
			else if(ch == 3){ 
				//v = r_vector; // v=r
				resp[0] = c1;
				resp[1] = c2;
				resp[2] = fi; //psi
				resp[3] = r_vector;
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
				String comp1, comp2,comp3; // computations
				int[] s_ve_t =  new int[glob_m];
				s_ve_t = IDscheme.addVectors((int[])resp[2],(int[])resp[3]);

				comp1 = IDscheme.com(Arrays.toString(s_ve_t)); // s+t
				comp2 = IDscheme.com(Arrays.toString((int[])resp[3])); // com(t); t = pi(r)
			
				// HW ?= m/2
				int hw_s = IDscheme.hw((int[])resp[2]);
				//System.out.println(hw_s==(glob_m/2));
			/*
				System.out.println("computed c2:" + comp2);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c3:" + comp1);
			//	System.out.println("com3:" + c3);
			*/	
				if( c2.equals(comp2) && c3.equals(comp1) && hw_s==(glob_m/2)){ //comp(resp[1], comp1)){ // c2==com(t) && c3==(s+t)
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else if(ch == 2){
				String comp1, comp2,comp3; // computations
				int[] temp = (int[])resp[3]; // u=x+r (c3);
				int[] y = y_Ax;;
				
				int[][] amatrix = A_matrix;
				
				int[] d = IDscheme.vectorMultiplyMatrix(temp, amatrix);
				int[] t = IDscheme.subtractVectors(d,y);
				
				/*int a[] = {2,1,7,3};
				int A[][] = { {1, 1, 1}, 
	                    {2, 2, 2}, 
	                    {3, 5, 3}, 
	                    {4, 4, 4}};
				int[] res = vectorMultiplyMatrix(a,A);
				for(int i =0;i<res.length;i++) {
				System.out.println("Matrxmult: " + res[i]);
				}
				*/
				
				String resp2 = resp[2].toString();
				
				comp1 = IDscheme.com(resp2+Arrays.toString(t));// com(fi,Au-y)
				comp2 = IDscheme.com(Arrays.toString(IDscheme.knuth_shuffle((int[])resp[3]))); //com(pi(u))
				
			/*
				System.out.println("computed c1:" + comp1);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c3:" + comp2);
			//	System.out.println("com3:" + c3);
			*/
				if( c1.equals(comp1) && c3.equals(comp2)){ // c1==com(fi,Au-y) && c3==com(fi(u))
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}
			}
			else if(ch == 3){
				String comp1, comp2,comp3; // computations
				int[][] amatrix = A_matrix;
				int[] v = (int[])resp[3]; //  v = r
				
				int[] av = IDscheme.vectorMultiplyMatrix(v, amatrix); // Av
				String resp2 = resp[2].toString();
				comp1 = IDscheme.com(resp2+Arrays.toString(av)); //com(psi,Av)
				comp2 = IDscheme.com(Arrays.toString(IDscheme.knuth_shuffle((int[])resp[3]))); // com(psi(v))
				
			/*
				System.out.println("computed c1:" + comp1);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c2:" + comp2);
			//	System.out.println("com2:" + c2);
			*/
				if( c1.equals(comp1) && c2.equals(comp2)){ 
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else{
				System.out.println("Error!");
			}
		}

		//===============================================================================
		//------------------------------------------- start --------------------------------------------------

		public static void main(String[] args) {
		
			int n = 512, 
				q = 257, 
				m = 2048;
		
		/*	System.out.println("Kawachi, Xagawa and Tanaka's ID Scheme:");
			System.out.println("Parameters Set:");
			System.out.println("n = " + n);
			System.out.println("m = " + m);
			System.out.println("q = " + q);*/
			long t0 = System.nanoTime();
			keyGeneration(n,m, q);
			long t1 = System.nanoTime();
			
			//System.out.println("Prover: compute commitments c1, c2 and c3:");
			long t2 = System.nanoTime();
			p1();
			long t3 = System.nanoTime();
			int ch;
			ch = v1();
			//System.out.println("Verifier: sends a challenge: " + ch);
			//System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
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
