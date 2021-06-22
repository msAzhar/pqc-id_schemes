import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.math.BigInteger; 
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


public class XagawaNTRU {
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

	public static void keyGeneration(int n, int q) {
		int[] ah = new int[n];
		int[] at = new int[n];
		int[] xh = new int[n]; //sk
		int[] xt = new int[n]; //sk
		
		glob_n = n;
		glob_q = q;

		for (int i = 0; i < n; i++) {
			ah[i] = randInt(q);
		}

		for (int i = 0; i < n; i++) {
			at[i] = randInt(q);
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

		int[] part1 = multiplyVectors(ah,xh);
		int[] part2 = multiplyVectors(at,xt);

		//public keys
		y = addVectors(part1,part2);
		
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
			rh[i] = randInt(glob_q);
		}

		for (int i = 0; i < glob_n; i++){
			rt[i] = randInt(glob_q);
		}

		glob_rh = rh;
		glob_rt = rt;
		
		int[] local_rh = rh;
		int[] local_rt = rt;
	
		/*System.out.println("rh:");
		for(int i =0;i<glob_n;i++) {
		System.out.println(glob_rh[i]);
		}*/
		
		sigma = randInt(4); //glob_q-1 ?
		glob_sigma = sigma;
		//System.out.println("Sigma:"+sigma);

		int[] part1 = knuth_shuffle(local_rh, glob_sigma); 
		int[] part2 = knuth_shuffle(local_rt, glob_sigma);
		
		int[] prt1 = addVectors(local_rh,sk_xh);
		int[] prt2 = addVectors(local_rt,sk_xt);
		
		/*System.out.println("Rh + Xh:");
		for(int i =0;i<glob_n;i++) {
		System.out.println(prt1[i]);
		}*/
		
		int[] p1 = knuth_shuffle(prt1,sigma);
		int[] p2 = knuth_shuffle(prt2,sigma);
		
		c1 = com("pih" + "pit" + y);
		c2 = com(Arrays.toString(part1) + Arrays.toString(part2)); // com(pi(r_h),pi(r_t))
		c3 = com(Arrays.toString(p1) + Arrays.toString(p2)); // com(pi(x+r)_h,pi(x+r)_t)
		
		/*
		System.out.println("c1:" + c1);
		System.out.println("c2:" + c2);
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
			resp[0] = knuth_shuffle(sk_xh, glob_sigma);
			resp[1] = knuth_shuffle(sk_xt, glob_sigma);
			resp[2] = knuth_shuffle(glob_rh, glob_sigma);
			resp[3] = knuth_shuffle(glob_rt, glob_sigma);
		}
		else if(ch == 2){
			resp[0] = "pih";
			resp[1] = "pit";
			resp[2] = addVectors(glob_rh, sk_xh);;
			resp[3] = addVectors(glob_rt, sk_xt);;
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

			comp1 = com(Arrays.toString((int[])resp[2])+Arrays.toString((int[])resp[3])); // c2=com(pi(rh),pi(rt)) hesabi

			int[] part1 = addVectors((int[])resp[0],(int[])resp[2]);//xh+rh
			int[] part2 = addVectors((int[])resp[1],(int[])resp[3]);//xt+rt
			comp2 = com(Arrays.toString(part1) + Arrays.toString(part2)); // 

			/*
			System.out.println("comp1:" + comp1);
			System.out.println("com2:" + c2);
			
			System.out.println("comp2:" + comp2);
			System.out.println("com3:" + c3);
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
			comp1 = com(resp0+resp1+y);
			
			int[] p1 = knuth_shuffle((int[])resp[2],glob_sigma);
			int[] p2 = knuth_shuffle((int[])resp[3],glob_sigma);

			//c3 hesabi:
			comp2 = com(Arrays.toString(p1)+Arrays.toString(p2)); 
			
			/*
			System.out.println("comp1:" + comp1);
			System.out.println("com1:" + c1);
			
			System.out.println("comp2:" + comp2);
			System.out.println("com3:" + c3);
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
			comp1 = com(resp0+resp1+y); //c1=com()
			
			int[] p1 = knuth_shuffle((int[])resp[2],glob_sigma);
			int[] p2 = knuth_shuffle((int[])resp[3],glob_sigma);
			comp2 = com(Arrays.toString(p1) + Arrays.toString(p2));

			/*
			System.out.println("comp1:" + comp1);
			
			System.out.println("com1:" + c1);
			
			System.out.println("comp2:" + comp2);
			System.out.println("com2:" + c2);
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
	
		System.out.println("Test Xagawa and Tanaka's ID Scheme (NTRU-based):");
		System.out.println("Parameters Set:");
		System.out.println("n = " + n);
		System.out.println("q = " + q);
		keyGeneration(n, q);
	
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
