import java.util.Arrays;

public class LWEbased {
	// LWE-based IDscheme

		//-----------------------------------Global variables-----------------------------------

		static int[][] A_matrix;
		static int[] b_vector;
		static int[] glob_s;
		static int[] glob_e;
		
		static int glob_sigma;
		static int glob_p;
		static int glob_m;
		static int glob_n;
		static int glob_q;
		static String c1, c2, c3;

		static int[] glob_r1,glob_r2,glob_r3;
		static int[] glob_u, glob_Aus;
		static int[] glob_perm1,glob_perm2,glob_perm3; //permutations
		
		
		//--------------------------------------------------------------------------------------
		public static void keyGeneration(int n, int m, int q) {
			int[][] amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
			int[] e = new int[n]; //e is sk
			int[] s = new int[m]; // vector s \in F_q^m
			int[] tmp_e = new int[n]; //temp vars for e
			glob_m = m;
			glob_n = n;
			glob_q = q;
			A_matrix = amatrix;
			
			for (int i = 0; i < glob_m; i++) {
				s[i] = IDscheme.randInt(glob_q);;
			}	
			
			
			for (int i = 0; i < (n/2); i++) {
				tmp_e[i] = 1;
			}

			for (int i = n/2; i < n; i++) {
				tmp_e[i] = 0;
			}
			
			// e = shuffle(temp_e);
			e = IDscheme.knuth_shuffle(tmp_e);
			
			// b = As + e
			int[] y = IDscheme.vectorMultiplyMatrix(s, amatrix);
			int[] b = IDscheme.addVectors(y,e);
			int p = IDscheme.hw(e);		
					
			b_vector = b;
			glob_p = p;
			glob_s = s;
			glob_e = e;
					
		}
		
		// Prover: p1()
		public static void p1(){ // 
			int[][] amatrix = A_matrix;
			int[] b = new int[glob_n];
			int[] s = new int[glob_m]; // vector s \in F_q^m
			int[] u = new int[glob_m];
			int[] gamma = new int[glob_m];
			
			int[] perm1 = new int[glob_m];
			b = b_vector;
			s=glob_s;

			for (int i = 0; i < glob_m; i++) {
				u[i] = IDscheme.randInt(glob_q);
			}
			glob_u = u;
			
			for (int i = 0; i < glob_m; i++){
				gamma[i] = IDscheme.randInt(glob_q);
				//if(gamma[i]==0)
				//	gamma[i]=randInt(glob_q);
			}

			int[] r1 = new int[glob_n];
			int[] r2 = new int[glob_n];
			int[] r3 = new int[glob_n];
			
			glob_sigma = IDscheme.randInt(4); 
			
			perm1 = IDscheme.knuth_shuffle(gamma, glob_sigma);
			int[] us = IDscheme.addVectors(u,s);
			
			int[] part1 = IDscheme.vectorMultiplyMatrix(us, amatrix); // A(u+s)
			glob_Aus = part1; //A(u+s)
			int[] perm2 = IDscheme.knuth_shuffle(part1,glob_sigma);

			int[] au = IDscheme.vectorMultiplyMatrix(u, amatrix); // Au
			int[] au_b = new int[glob_n]; // Au+b
			for (int i = 0; i < glob_n; i++){
				au_b[i] = au[i] + b_vector[i];
			}

			int[] perm3 = IDscheme.knuth_shuffle(au_b,glob_sigma);

			for (int i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
				r1[i] = IDscheme.randInt(2);
				r2[i] = IDscheme.randInt(2);
				r3[i] = IDscheme.randInt(2);
			}

			glob_perm1 = perm1; //Gamma Sigma nin
			glob_perm2 = perm2; //A(u+s) nin
			glob_perm3 = perm3; //Au+b nin
			glob_r1 = r1;
			glob_r2 = r2;
			glob_r3 = r3;
			
			// com(Pi_gamma,_sigma;r1)
			c1 = IDscheme.com(Arrays.toString(perm1)+Arrays.toString(r1)); 
			// com(Pi_gamma,_sigma(A(u+s));r2)
			c2 = IDscheme.com(Arrays.toString(perm2)+Arrays.toString(r2)); 
			// com(Pi_gamma,_sigma(Au+b);r3)
			c3 = IDscheme.com(Arrays.toString(perm3)+Arrays.toString(r3)); 
			
		/*	System.out.println("c1:" + c1);
			System.out.println("c2:" + c2);
			System.out.println("c3:" + c3);
		*/	
		}
	    
		// Verifier: v1()
		public static int v1(){
			int randnum = IDscheme.getRandomInteger(4, 1);
			return randnum;
		}
		
		// Prover: p2()
		public static Object[] p2(int c){
			int ch = c;
			Object[] resp = new Object[4];

			if(ch == 1){
				resp[0] = glob_r1;
				resp[1] = glob_r2;
				resp[2] = glob_Aus; //A(u+s)
				resp[3] = glob_perm1; ////Pi_gamma,_sigma()
			}
			else if(ch == 2){
				int[] permute_e = IDscheme.knuth_shuffle(glob_e,glob_sigma);

				resp[0] = glob_r2;
				resp[1] = glob_r3;
				resp[2] = glob_perm2; //Pi_gamma,_sigma(A(u+s))
				resp[3] = permute_e; //Pi_gamma,_sigma(e)
			}
			else if(ch == 3){ 
				resp[0] = glob_r1;
				resp[1] = glob_r3;
				resp[2] = glob_perm1; //Pi_gamma,_sigma()
				resp[3] = glob_u;
			}else{
				System.out.println("Error!");
			}
			
			return resp;
		}


		public static boolean comp(int[] arr1,int[] arr2){ //compare function
			for(int i=0; i<glob_m; i++){
				if(arr1[i]!=arr2[i])
					return false;
			}
			return true;
		}
		
		// Verifier: v2()
		public static void v2(int c, Object[] params){
			int ch = c;
			Object[] resp = params;

			if(ch == 1){
				String comp1, comp2,comp3; // computations
				int[] s_ve_t =  new int[glob_m];
				// compute c1=com(Pi_gamma,_sigma;r1);
				comp1 = IDscheme.com(Arrays.toString((int[])resp[3])+Arrays.toString((int[])resp[0])); 
							
				// Pi_gamma,_sigma(A(u+s))
				int[] part1 = IDscheme.knuth_shuffle((int[])resp[2], glob_sigma); 
				// compute c2=com(Pi_gamma,_sigma(A(u+s));r2)
				comp2 = IDscheme.com(Arrays.toString(part1)+Arrays.toString((int[])resp[1])); 
						
			/*
				System.out.println("computed c1:" + comp1);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c2:" + comp2);
			//	System.out.println("com2:" + c2);
			*/	
				if( comp1.equals(c1) && comp2.equals(c2)){ //comp(resp[1], comp1)){ // c2==com(t) && c3==(s+t)
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else if(ch == 2){
				String comp1, comp2;
				int comp3; // computations
				int[] temp = (int[])resp[3]; // u=x+r (c3);
				// compute c2=com(Pi_gamma,_sigma(A(u+s));r2)
				comp1 = IDscheme.com(Arrays.toString((int[])resp[2])+Arrays.toString(glob_r2)); 
				//int[] au_b = new int[glob_n]; // Au+b
				int[] au_b = IDscheme.addVectors((int[])resp[2],(int[])resp[3]);
				
				int[] part1 = IDscheme.addVectors((int[])resp[2], (int[])resp[3]);  
						
				// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
				comp2 = IDscheme.com(Arrays.toString(part1)+Arrays.toString((int[])resp[1])); 

				comp3 = IDscheme.hw((int[])resp[3]); //HW(Pi_gamma,_sigma(e))

			/*
				System.out.println("computed c2:" + comp1);
			//	System.out.println("com2" + c2);						
				System.out.println("computed c3:" + comp2);
			//	System.out.println("com3:" + c3);
			*/
					
				//check c2 and c3 and hw(e)
				if( c2.equals(comp1) && c3.equals(comp2) && comp3==glob_p){ 
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}
			}
			else if(ch == 3){
				String comp1, comp2,comp3; // computations
				int[][] amatrix = A_matrix;
				int[] temp = (int[])resp[3]; //  v = r
			
				// compute c1=com(Pi_gamma,_sigma;r1);
				comp1 = IDscheme.com(Arrays.toString((int[])resp[2])+Arrays.toString((int[])resp[0])); 
				//var u_transpose = IDscheme.transpose(u);
				int[] au = IDscheme.vectorMultiplyMatrix((int[])resp[3], A_matrix); // Au
				int[] au_b = new int[glob_n]; // Au+b dipnot: A,b pk

				au_b = IDscheme.addVectors(au,b_vector);
							
				int[] p1 = IDscheme.knuth_shuffle(au_b,glob_sigma);
				// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
				comp2 = IDscheme.com(Arrays.toString(p1)+Arrays.toString((int[])resp[1]));

				/*
					System.out.println("computed c1:" + comp1);
				//	System.out.println("com1" + c1);						
					System.out.println("computed c3:" + comp2);
				//	System.out.println("com3:" + c3);
				*/
			
				if( c1.equals(comp1) && c3.equals(comp2)){ 
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
		
			int n = 608, 
				q = 1024, 
				m = 960;
		
		/*	System.out.println("LWE-based ID Scheme:");
			System.out.println("Parameters Set:");
			System.out.println("n = " + n);
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
			System.out.println("Verifier: sends a challenge: " + ch);
			/*System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
			System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
			*/long t4 = System.nanoTime();
			Object[] response = p2(3);
			long t5 = System.nanoTime();
			v2(3,response);
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
