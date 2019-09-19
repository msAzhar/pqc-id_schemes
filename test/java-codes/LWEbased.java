import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.math.BigInteger; 
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


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

	//--------------------------------------------------------------------------------------

	public static int randInt(int q) {
	    int number = (int)(Math.random()*q);
	    return number;
	}	
	
	// returns random integer between minimum and maximum range
    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }
	
    //assign function
	public static int[] assign(int[] a) {
       
        int[] result = new int[a.length];

        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ;
        }

        return result;
    }
	
	// Addition of vectors, c = a + b
	public static int[] addVectors(int[] a, int[] b) {
        int length = a.length < b.length ? a.length : b.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] + b[i];
        }

        return result;
    }
	
	// Dot product of vectors, c = a * b
	public static int[] multiplyVectors(int[] a, int[] b) {
        int length = a.length < b.length ? a.length : b.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] * b[i];
        }

        return result;
    }
	
	//  Subtraction of vectors, c = a - b
	public static int[] subtractVectors(int[] a, int[] b) {
        int length = a.length < b.length ? a.length : b.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] - b[i];
        }

        return result;
    }
	
	// Shuffles elements of an array depending on sigma, 
	// Knuth (Fisher Yates) Shuffle is used
	public static int[] knuth_shuffle(int[] arr, int sigma)
    {
        int size = arr.length;
        int[] toShuffle = new int[size];
        int[] result = new int[size];
        toShuffle=assign(arr);
        for (int i = size - 1; i > 0; i--)
        {
            int fixedIndex = sigma;
            int tmp = toShuffle[i];
            toShuffle[i] = toShuffle[fixedIndex];
            toShuffle[fixedIndex] = tmp;
        }
        result = toShuffle;
        return result;
    }
	
	 // Shuffle function, Knuth (Fisher Yates) shuffle is used
	public static int[] knuth_shuffle(int[] arr)
    {
        int size = arr.length;
        int[] toShuffle = new int[size];
        int[] result = new int[size];
        toShuffle=assign(arr);
      //  Random r = new Random();
        for (int i = size - 1; i > 0; i--)
        {
           // int fixedIndex = r.nextInt(i+1); //!!Dikkat
        	int fixedIndex = 5;
            int tmp = toShuffle[i];
            toShuffle[i] = toShuffle[fixedIndex];
            toShuffle[fixedIndex] = tmp;
        }
        result = toShuffle;
        return result;
    }
	
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
	{ 
		// Static getInstance method is called with hashing SHA 
		MessageDigest md = MessageDigest.getInstance("SHA-256"); 

		// digest() method called 
		// to calculate message digest of an input 
		// and return array of byte 
		return md.digest(input.getBytes(StandardCharsets.UTF_8)); 
	} 
	
	public static String toHexString(byte[] hash) 
	{ 
		// Convert byte array into signum representation 
		BigInteger number = new BigInteger(1, hash); 

		// Convert message digest into hex value 
		StringBuilder hexString = new StringBuilder(number.toString(16)); 

		// Pad with leading zeros 
		while (hexString.length() < 32) 
		{ 
			hexString.insert(0, '0'); 
		} 

		return hexString.toString(); 
	}
	
	//commitment function
	public static String com(String param){
		String result = new String();
		try {
			result = toHexString(getSHA(param));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static int[][] initMatrixRandom(int x, int y, int q) {
		int[][] matrix = new int[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				matrix[i][j] = randInt(q);
			}
		}
		return matrix;
	}
	
	//Multiplies a matrix B by a vector a, c = a * B
	public static int[] vectorMultiplyMatrix(int[] a, int[][] B) {
		//var A_x = 1;
		int A_y = a.length;
		int B_x = B.length;
		int B_y = B[0].length;
		
		if (B_x != A_y) {
			System.out.println("Matrix inner dimensions must agree");
			System.exit(0);
		}
			
		int[] v = new int[B_y];
		for (int j = 0; j < B_y; j++) {
			v[j] = 0;
		}
			
		for (int i = 0; i < A_y; i++) {
			int[] Browi = B[i];
			for (int j = 0; j < B_y; j++) {
				if (a[i] == 0) {
					//v[j] += 0;
				} else if (a[i] == 1) {
					v[j] += Browi[j];	
				} else if (a[i] == -1) {
					v[j] -= Browi[j];
				} else {
					v[j] += a[i] * Browi[j];
				}
			}
		}
		return v;
	}
	
	//Returns A', the transpose of a matrix A
	public static int[][] transpose(int[][] A) {
		int A_x = A.length;
		int A_y = A[0].length;
		
		int[][] C = new int[A_y][A_x];
		for (int i = 0; i < A_x; i++) {
			for (int j = 0; j < A_y; j++) {
				C[j][i] = A[i][j];
			}
		}
		return C;
	}
	
	//Returns A', the transpose of a matrix A 
		public static int[] transpose(int[] A) {
			int A_x = A.length;
			int[] C = new int[A_x];
			for (int i = 0; i < A_x; i++) {
				
					C[i] = A[i];
			}
			return C;
		}
		
		// Hamming Weight... amount of 1s
		public static int hw(int[] v){
				int wt=0;
				for(int i = 0; i<v.length; i++){
					if(v[i] == 1)
						wt+=1;
				}
				return wt;
			}
	
	public static void keyGeneration(int n, int m, int q) {
		int[][] amatrix = initMatrixRandom(m, n, q); // A, n*m
		int[] e = new int[n]; //e is sk
		int[] s = new int[m]; // vector s \in F_q^m
		glob_m = m;
		glob_n = n;
		glob_q = q;
		A_matrix = amatrix;
		
		for (int i = 0; i < glob_m; i++) {
			s[i] = randInt(glob_q);;
		}	
		
		int[] ee = {-7, -9,1,2,-5,9,2,1,-2,4,-3,0,-5,0,7,-2,-4,-3,0,-6,0,1,6,5,2,6,-10,7,-2,2,0,-8,5,3,0,9,5,0,-4,-3,3,2,0,4,8,1,-4,-16,-8,8,10,-2,-6,0,2,12,-8,-2,9,6,-6,-1,-6,2,1,-9,1,4,8,4,4,-1,4,2,8,4,-1,3,11,-12,1,3,5,-15,2,3,9,-2,2,6,-3,-5,-6,1,-5,1,-3,1,11,-1,5,2,1,-2,-2,2,-1,3,-6,3,2,4,-2,16,2,3,8,6,1,11,-12,-7,-2,2,-10,-7,7,10,1,-8,-2,8,6,3,-1,6,-5,0,3,4,2,-4,-2,7,-2,4,-7,-1,-3,8,4,0,8,-3,-11,3,10,10,-7,-2,-9,1,-3,-3,-7,3,2,-8,-11,-1,-3,-4,-3,3,-7,7,6,-2,-8,-2,6,4,-4,3,0,7,-2,-4,-2,-15,2,-6,6,0,0,6,-7,2,-6,-4,3,5,3,9,-9,5,6,-8,-7,4,4,5,0,0,-3,-7,-4,-7,-6,-9,-7,2,5,-6,0,-1,10,6,-6,-7,0,2,7,3,-3,10,-3,2,-4,-10,3,5,-5,2,-7,-3,-5,-4,0,-7,-2,3,-13,4,-6,-7,0,3,1,5,16,-4,-3,-2,2,0,14,0,-5,0,3,1,-1,-14,2,-5,0,5,0,-1,4,-1,-6,0,2,6,7,-3,-9,1,-9,8,-8,2,1,7,14,-2,-5,-2,3,-6,-2,-6,4,7,2,-7,6,13,10,5,-13,-12,0,0,-1,4,2,2,-2,-12,6,-2,0,2,6,-2,-5,1,-2,-3,-7,-3,-6,0,2,11,6,-2,-6,9,-1,-4,14,8,-8,-1,6,1,2,-1,-7,3,-13,2,1,9,7,-1,-2,-3,-1,10,5,5,2,-8,-4,11,16,2,-2,-5,-3,5,3,0,2,3,2,-4,-3,8,-5,0,2,-2,7,5,-6,1,-3,2,-8,1,6,4,12,-1,1,-11,3,3,5,-6,1,-5,-8,-4,-7,-9,0,-5,1,4,3,8,-2,8,3,11,-5,-9,6,4,0,7,1,1,-10,0,5,-6,7,5,-4,-1,4,3,-6,1,-7,2,-7,3,4,11,6,6,2,-6,6,0,2,-1,3,4,4,6,-7,3,3,3,-10,6,-5,7,-5,-14,7,2,-8,12,3,-10,8,15,1,0,1,2,3,-2,1,-2,7,-16,5,1,9,-3,-4,-5,0,8,4,1,-11,-5,-5,-10,4,8,-5,2,9,-3,2,2,0,-2,3,-1,0,5,-3,4,4,0,1,11,-9,-6,11,6,-10,-1,10,6,-5,-11,1,-2,-2,-6,0,2,-3,10,5,-7,6,3,0,-12,-4,-3,6,3,-2,-2,-10,0,-2,-2,-5,0,9,9,0,-15,10,0,-7,-7,-14,-3,-2,6,1,-3,-5,6,3,4,1,1,5,-3,2,0,13,4,6,6,-13,2,-6,-5,-2,-4,-2,-2,-4,2,5,-6,8,1,-8,1,-4,-3,2,3,-3,1};
				
		// b = As + e
		int[] y = vectorMultiplyMatrix(s, amatrix);
		int[] b = addVectors(y,e);
		int p = hw(e);		
				
		b_vector = b;
		glob_p = p;
		glob_s = s;
		glob_e = e;
				
	}

	static int[] glob_r1,glob_r2,glob_r3;
	static int[] glob_u, glob_Aus;
	static int[] glob_perm1,glob_perm2,glob_perm3; //permutations
	
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
			u[i] = randInt(glob_q);
		}
		glob_u = u;
		
		for (int i = 0; i < glob_m; i++){
			gamma[i] = randInt(glob_q);
			//if(gamma[i]==0)
			//	gamma[i]=randInt(glob_q);
		}

		int[] r1 = new int[glob_n];
		int[] r2 = new int[glob_n];
		int[] r3 = new int[glob_n];
		
		glob_sigma = randInt(4); 
		
		perm1 = knuth_shuffle(gamma, glob_sigma);
		int[] us = addVectors(u,s);
		
		int[] part1 = vectorMultiplyMatrix(us, amatrix); // A(u+s)
		glob_Aus = part1; //A(u+s)
		int[] perm2 = knuth_shuffle(part1,glob_sigma);

		int[] au = vectorMultiplyMatrix(u, amatrix); // Au
		int[] au_b = new int[glob_n]; // Au+b
		for (int i = 0; i < glob_n; i++){
			au_b[i] = au[i] + b_vector[i];
		}

		int[] perm3 = knuth_shuffle(au_b,glob_sigma);

		for (int i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
			r1[i] = randInt(2);
			r2[i] = randInt(2);
			r3[i] = randInt(2);
		}

		glob_perm1 = perm1; //Gamma Sigma nin
		glob_perm2 = perm2; //A(u+s) nin
		glob_perm3 = perm3; //Au+b nin
		glob_r1 = r1;
		glob_r2 = r2;
		glob_r3 = r3;
		
		c1 = com(Arrays.toString(perm1)+Arrays.toString(r1)); // com(Pi_gamma,_sigma;r1)
		c2 = com(Arrays.toString(perm2)+Arrays.toString(r2)); // com(Pi_gamma,_sigma(A(u+s));r2)
		c3 = com(Arrays.toString(perm3)+Arrays.toString(r3)); // com(Pi_gamma,_sigma(Au+b);r3)
		
		System.out.println("c1:" + c1);
		System.out.println("c2:" + c2);
		System.out.println("c3:" + c3);
		
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

		if(ch == 1){
			resp[0] = glob_r1;
			resp[1] = glob_r2;
			resp[2] = glob_Aus; //A(u+s)
			resp[3] = glob_perm1; ////Pi_gamma,_sigma()
		}
		else if(ch == 2){
			int[] permute_e = knuth_shuffle(glob_e,glob_sigma);

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
			comp1 = com(Arrays.toString((int[])resp[3])+Arrays.toString((int[])resp[0])); 
						
			// Pi_gamma,_sigma(A(u+s))
			int[] part1 = knuth_shuffle((int[])resp[2], glob_sigma); 
			// compute c2=com(Pi_gamma,_sigma(A(u+s));r2)
			comp2 = com(Arrays.toString(part1)+Arrays.toString((int[])resp[1])); 
					
		/*
			System.out.println("comp1:" + comp1);
			System.out.println("com1:" + c1);
			
			System.out.println("comp2:" + comp2);
			System.out.println("com2:" + c2);
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
			comp1 = com(Arrays.toString((int[])resp[2])+Arrays.toString(glob_r2)); 
			//int[] au_b = new int[glob_n]; // Au+b
			int[] au_b = addVectors((int[])resp[2],(int[])resp[3]);
			
			int[] part1 = addVectors((int[])resp[2], (int[])resp[3]);  
					
			// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
			comp2 = com(Arrays.toString(part1)+Arrays.toString((int[])resp[1])); 

			comp3 = hw((int[])resp[3]); //HW(Pi_gamma,_sigma(e))

		/*
			System.out.println("comp1:" + comp1);
			System.out.println("com2" + c2);						
			System.out.println("comp2:" + comp2);
			System.out.println("com3:" + c3);
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
			comp1 = com(Arrays.toString((int[])resp[2])+Arrays.toString((int[])resp[0])); 
			//var u_transpose = IDscheme.transpose(u);
			int[] au = vectorMultiplyMatrix((int[])resp[3], A_matrix); // Au
			int[] au_b = new int[glob_n]; // Au+b dipnot: A,b pk

			au_b = addVectors(au,b_vector);
						
			int[] p1 = knuth_shuffle(au_b,glob_sigma);
			// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
			comp2 = com(Arrays.toString(p1)+Arrays.toString((int[])resp[1]));

			/*
				System.out.println("comp1:" + comp1);
				System.out.println("com1" + c1);						
				System.out.println("comp2:" + comp2);
				System.out.println("com3:" + c3);
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
	
		System.out.println("LWE-based ID Scheme:");
		System.out.println("Parameters Set:");
		System.out.println("n = " + n);
		System.out.println("m = " + m);
		System.out.println("q = " + q);
		keyGeneration(n,m, q);
	
		System.out.println("Prover: compute commitments c1, c2 and c3:");
		p1();
		int ch;
		ch = v1();
		System.out.println("Verifier: sends a challenge: " + ch);
		System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
		System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
		Object[] response = p2(2);
		v2(2,response);
	}

}

