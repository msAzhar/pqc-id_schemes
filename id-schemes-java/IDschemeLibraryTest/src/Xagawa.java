import java.util.Arrays;

public class Xagawa {
	// Xagawa NTRU-based

		//-----------------------------------Global variables-----------------------------------
		static int[] a_h;
		static int[] a_t;
		static int[] y;
		static int[] sk_xh;
		static int[] sk_xt;
		static int glob_n;
		static int glob_q;
		static int glob_sigma;

		static int[] glob_rh;
		static int[] glob_rt;
		static String c1, c2, c3;

		//--------------------------------------------------------------------------------------
		public static void keyGeneration(int n, int q) {
			int[] ah = new int[n];
			int[] at = new int[n];
			int[] xh = new int[n]; //sk
			int[] xt = new int[n]; //sk
			
			glob_n = n;
			glob_q = q;

			for (int i = 0; i < n; i++) {
				ah[i] = IDscheme.randInt(q);
			}

			for (int i = 0; i < n; i++) {
				at[i] = IDscheme.randInt(q);
			}

			// temps 
			int[] t1 = new int[n];
			int[] t2 = new int[n];
			
			// d's value is n/2-1
			for (int i = 0; i < (n/2); i++) {
				t1[i] = 1;
			}

			// n's value is 677
			for (int i = (n-1)/2; i < n; i++) {
				t1[i] = 0;
			}
			/*
			for(int i =0; i<t1.length; i++) {
			System.out.println(t1[i]);
			}*/
			
			//TODO!
			//xh = shuffle(t1);
			xh = t1;
			
			t2[0] = 1;

			for (int i = 1; i < n; i++) {
				t2[i] = 0;
			}
			
			//TODO:
			//xt = shuffle(t2);
			xt = t2;

			int[] part1 = IDscheme.multiplyVectors(ah,xh);
			int[] part2 = IDscheme.multiplyVectors(at,xt);

			//public keys
			y = IDscheme.addVectors(part1,part2);
			
			a_h = ah;
			a_t = at;

			//private keys
			sk_xh = xh;
			sk_xt = xt;
			/*
			System.out.println("Sk Xh:");
			for(int i =0;i<n;i++) {
			System.out.println(xh[i]);
			}*/
		}

		// Prover: p1()
		public static void p1(){ // 
			int[] rh = new int[glob_n];
			int[] rt = new int[glob_n];
			int sigma;

			for (int i = 0; i < glob_n; i++){
				rh[i] = IDscheme.randInt(glob_q);
			}

			for (int i = 0; i < glob_n; i++){
				rt[i] = IDscheme.randInt(glob_q);
			}

			glob_rh = rh;
			glob_rt = rt;
			
			int[] local_rh = rh;
			int[] local_rt = rt;
		
			/*System.out.println("rh:");
			for(int i =0;i<glob_n;i++) {
			System.out.println(glob_rh[i]);
			}*/
			
			sigma = IDscheme.randInt(4); //glob_q-1 ?
			glob_sigma = sigma;
			//System.out.println("Sigma:"+sigma);

			int[] part1 = IDscheme.knuth_shuffle(local_rh, glob_sigma); 
			int[] part2 = IDscheme.knuth_shuffle(local_rt, glob_sigma);
			
			int[] prt1 = IDscheme.addVectors(local_rh,sk_xh);
			int[] prt2 = IDscheme.addVectors(local_rt,sk_xt);
			
			/*System.out.println("Rh + Xh:");
			for(int i =0;i<glob_n;i++) {
			System.out.println(prt1[i]);
			}*/
			
			int[] p1 = IDscheme.knuth_shuffle(prt1,sigma);
			int[] p2 = IDscheme.knuth_shuffle(prt2,sigma);
			
			c1 = IDscheme.com("pih" + "pit" + y);
			c2 = IDscheme.com(Arrays.toString(part1) + Arrays.toString(part2)); // com(pi(r_h),pi(r_t))
			c3 = IDscheme.com(Arrays.toString(p1) + Arrays.toString(p2)); // com(pi(x+r)_h,pi(x+r)_t)
			
			/*
			System.out.println("c1:" + c1);
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

		// Function to copy an Object array in Java
		private static <T> int[] copyArray(int[] source)
		{
			return source.clone();
		}
		
		// Prover: p2()
		public static Object[] p2(int c){
			int ch = c;
			Object[] resp = new Object[4];

			//print("Challenge is:");
			//print(ch);
			if(ch == 1){
				resp[0] = IDscheme.knuth_shuffle(sk_xh, glob_sigma);
				resp[1] = IDscheme.knuth_shuffle(sk_xt, glob_sigma);
				resp[2] = IDscheme.knuth_shuffle(glob_rh, glob_sigma);
				resp[3] = IDscheme.knuth_shuffle(glob_rt, glob_sigma);
			}
			else if(ch == 2){
				resp[0] = "pih";
				resp[1] = "pit";
				resp[2] = IDscheme.addVectors(glob_rh, sk_xh);;
				resp[3] = IDscheme.addVectors(glob_rt, sk_xt);;
			}
			else if(ch == 3){
				resp[0] = "pih";
				resp[1] = "pit";
				resp[2] = glob_rh;
				resp[3] = glob_rt;
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
				String comp1, comp2; // computations

				comp1 = IDscheme.com(Arrays.toString((int[])resp[2])+Arrays.toString((int[])resp[3])); // c2=com(pi(rh),pi(rt)) hesabi

				int[] part1 = IDscheme.addVectors((int[])resp[0],(int[])resp[2]);//xh+rh
				int[] part2 = IDscheme.addVectors((int[])resp[1],(int[])resp[3]);//xt+rt
				comp2 = IDscheme.com(Arrays.toString(part1) + Arrays.toString(part2)); // 

			/*
				System.out.println("computed c2:" + comp1);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c3:" + comp2);
			//	System.out.println("com3:" + c3);
			*/
				
				if( comp1.equals(c2) && comp2.equals(c3)){
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}

			}
			else if(ch == 2){
				String comp1, comp2; // computations
				
				/* 
				System.out.println("Resp2:");
				for(int i =0;i<glob_n;i++) {
				System.out.println(((int[])resp[2])[i]);
				}
				*/
				
				//c1 hesabi
				String resp0 = resp[0].toString();
				String resp1 = resp[1].toString();
				//System.out.println(resp0);
				
				//c1 hesabi
				comp1 = IDscheme.com(resp0+resp1+y);
				
				int[] p1 = IDscheme.knuth_shuffle((int[])resp[2],glob_sigma);
				int[] p2 = IDscheme.knuth_shuffle((int[])resp[3],glob_sigma);

				//c3 hesabi:
				comp2 = IDscheme.com(Arrays.toString(p1)+Arrays.toString(p2)); 
				
			/*
				System.out.println("computed c1:" + comp1);
			//	System.out.println("com1" + c1);						
				System.out.println("computed c3:" + comp2);
			//	System.out.println("com3:" + c3);
			*/
				
				if(comp1.equals(c1) && comp2.equals(c3)){ 
					System.out.println("Success!");
				}else{
					System.out.println("Failed!");
				}
			}
			else if(ch == 3){
				String comp1, comp2; // computations

				String resp0 = resp[0].toString();
				String resp1 = resp[1].toString();
				comp1 = IDscheme.com(resp0+resp1+y); //c1=com()
				
				int[] p1 = IDscheme.knuth_shuffle((int[])resp[2],glob_sigma);
				int[] p2 = IDscheme.knuth_shuffle((int[])resp[3],glob_sigma);
				comp2 = IDscheme.com(Arrays.toString(p1) + Arrays.toString(p2));

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
		
			int n = 677, 
				q = 2048;
		
			/*System.out.println("Test Xagawa and Tanaka's ID Scheme (NTRU-based):");
			System.out.println("Parameters Set:");
			System.out.println("n = " + n);
			System.out.println("q = " + q);
			*/long t0 = System.nanoTime();
			keyGeneration(n, q);
			long t1 = System.nanoTime();

			//System.out.println("Prover: compute commitments c1, c2 and c3:");
			long t2 = System.nanoTime();
			p1();
			long t3 = System.nanoTime();

			int ch;
			ch = v1();
			/*System.out.println("Verifier: sends a challenge: " + ch);
			System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
			*/System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
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
