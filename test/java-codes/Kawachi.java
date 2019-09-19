import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.math.BigInteger; 
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


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

	public static int randInt(int q) {
	    int number = (int)(Math.random()*q);
	    return number;
	}	
	
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
	// FIXIT!!
		public static int[] transpose(int[] A) {
			int A_x = A.length;
			
			int[] C = new int[A_x];
			for (int i = 0; i < A_x; i++) {
				
					C[i] = A[i];
			}
			return C;
		}
	
	public static void keyGeneration(int n, int m, int q) {
		int[][] amatrix = initMatrixRandom(m, n, q); // A, n*m
		int[] x = new int[m];	
		int[] ss = new int[m]; 
		glob_m = m;
		glob_q = q;
		
		for (int i = 0; i < (m/2); i++) {
			ss[i] = 1;
		}

		for (int i = m/2; i < m; i++) {
			ss[i] = 0;
		}
		
		//TODO x = shuffle(ss);
		x = ss;

		int[] x_transpose = x;
		int[] y = vectorMultiplyMatrix(x_transpose, amatrix);
		
		
		A_matrix = amatrix;
		x_vector = x; // sk
		y_Ax = y; // pk
	}

	// Prover: p1()
	public static void p1(){ // 
		int[][] amatrix = A_matrix;
		int[] x = new int[glob_m];
		int[] r = new int[glob_m];
		int[] z = new int[glob_m];
		x = x_vector;

		for (int i = 0; i < glob_m; i++) {
			r[i] = randInt(glob_q);
		}

		r_vector = r;

		z= addVectors(x,r);
		
		int[] rr = transpose(r);
		int[] ar = vectorMultiplyMatrix(r, amatrix);
		int[] pr = new int[glob_m];

		pr = knuth_shuffle(r); // c2
		
		c1 = com("pi"+Arrays.toString(ar));
		c2 = com(Arrays.toString(pr)); // com(pi(r))
		c3 = com(Arrays.toString(knuth_shuffle(z))); // com(pi(x+r))
	
		System.out.println("c1:" + c1);
		System.out.println("c2:" + c2);
		System.out.println("c3:" + c3);
		
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
		String psi="psi";
		int[] v = new int[glob_m];
		int[] r = new int[glob_m];
		int[] x = new int[glob_m];
		int[] u = new int[glob_m];

		if(ch == 1){
			r = r_vector;
			s = knuth_shuffle(x_vector);
			t = knuth_shuffle(r);
			resp[0] = c2;
			resp[1] = c3;
			resp[2] = s;
			resp[3] = t; //t = pi(r) //print(t);
		}
		else if(ch == 2){
			r = r_vector;
			x = x_vector;
			u = addVectors(x,r);
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
			s_ve_t = addVectors((int[])resp[2],(int[])resp[3]);

			comp1 = com(Arrays.toString(s_ve_t)); // s+t
			comp2 = com(Arrays.toString((int[])resp[3])); // com(t); t = pi(r)
		/*
			System.out.println("comp1:" + comp1);
			System.out.println("com3:" + c3);
			
			System.out.println("comp2:" + comp2);
			System.out.println("com2:" + c2);
		*/	
			if( c2.equals(comp2) && c3.equals(comp1)){ //comp(resp[1], comp1)){ // c2==com(t) && c3==(s+t)
				System.out.println("Success!");
			}else{
				System.out.println("Failed!");
			}

		}
		else if(ch == 2){
			String comp1, comp2,comp3; // computations
			int[] temp = (int[])resp[3]; // u=x+r (c3);
			int[] y = y_Ax;;
			//int[][] amatrix = new int[glob_m][];
			int[][] amatrix = A_matrix;
			
			//int[] tt = transpose(temp);
			int[] d = vectorMultiplyMatrix(temp, amatrix);
			int[] t = subtractVectors(d,y);
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
			
			comp1 = com(resp2+Arrays.toString(t));// com(fi,Au-y)
			comp2 = com(Arrays.toString(knuth_shuffle((int[])resp[3]))); //com(pi(u))
			//System.out.println("Comp1: " + comp1);
			if( c1.equals(comp1) && c3.equals(comp2)){ // c1==com(fi,Au-y) && c3==com(fi(u))
				System.out.println("Success!");
			}else{
				System.out.println("Failed!");
			}
		}
		else if(ch == 3){
			String comp1, comp2,comp3; // computations
			int[][] amatrix = A_matrix;
			int[] temp = (int[])resp[3]; //  v = r
			int[] tt = transpose(temp);
			int[] av = vectorMultiplyMatrix(temp, amatrix); // Av
			String resp2 = resp[2].toString();
			comp1 = com(resp2+Arrays.toString(av)); //com(psi,Av)
			comp2 = com(Arrays.toString(knuth_shuffle((int[])resp[3]))); // com(psi(v))
		
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
	
		System.out.println("Kawachi, Xagawa and Tanaka's ID Scheme:");
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
		Object[] response = p2(ch);
		v2(ch,response);

	
	}

}
