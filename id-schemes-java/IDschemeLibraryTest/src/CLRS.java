import java.text.DecimalFormat;
import java.util.Arrays;

public class CLRS {
	static double[][] A_matrix;
	static double[] r_vector;
	static double[] x_vector;
	static double[] y_Ax;
	static int glob_m;
	static int glob_n;
	static int glob_q;
	static int glob_sigma;
	static double[] glob_r0;
	static double[] glob_r1;
	
	static double[] glob_z;
	static double[][] p_sigma_matrix;
	static double[] u_vector;
	static double glob_alpha;
	
	static String c1, c2, c3;
	
	public static void keyGeneration(int n, int m, int q) {
		double[][] amatrix = IDscheme.initMatrixRandom(n, m, (double)q); // A, n*m
		double[] x = new double[m]; //e is sk
		double[] ss = new double[m]; // vector s \in F_q^m
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
		
		double[][] a_transpose = IDscheme.transpose(amatrix);
		double[] y = IDscheme.vectorMultiplyMatrix(x, a_transpose);
		
		A_matrix = amatrix;
		x_vector = x; // sk
		y_Ax = y; // pk				
	}

	public static double[] dbl_formatter(double array[]) {
		double[] formatted = new double[array.length];;
		for(int i =0;i<array.length; i ++) {
			DecimalFormat df = new DecimalFormat("#.##");
			double p = Double.valueOf(df.format(array[i]));
			//System.out.println(p);
			formatted[i] = p;
		}
		return formatted;
	}
	
	public static double dbl_formatter(double num) {
		double formatted;
		DecimalFormat df = new DecimalFormat("#.##");
		double p = Double.valueOf(df.format(num));
		//System.out.println(p);
		formatted = p;
		return formatted;
	}
	
	double A[][];
   static double m[][];
    int N;
    public static double[][] generateSubArray (double A[][], int N, int j1){
        m = new double[N-1][];
        for (int k=0; k<(N-1); k++)
                m[k] = new double[N-1];

        for (int i=1; i<N; i++){
              int j2=0;
              for (int j=0; j<N; j++){
                  if(j == j1)
                        continue;
                  m[i-1][j2] = A[i][j];
                  j2++;
              }
          }
        return m;
}
/*
 * Calculate determinant recursively
 */
public static double determinant(double A[][], int N){
    double res;

    // Trivial 1x1 matrix
    if (N == 1) res = A[0][0];
    // Trivial 2x2 matrix
    else if (N == 2) res = A[0][0]*A[1][1] - A[1][0]*A[0][1];
    // NxN matrix
    else{
        res=0;
        for (int j1=0; j1<N; j1++){
             m = generateSubArray (A, N, j1);
             res += Math.pow(-1.0, 1.0+j1+1.0) * A[0][j1] * determinant(m, N-1);
        }
    }
    return res;
}

	// Prover: p1()
	public static void p1(){ //
		
		//p_matrix is a binary matrix P^mxm
		//double[][] p_matrix = initMatrixRandom(glob_m, glob_m, 2);
		double[][] psigma_matrix = IDscheme.generateIdentityMatrix(glob_m);
		//double det = matrixDeterminant(psigma_matrix);
		//System.out.print("Det: "+det);
		double[][] amatrix = A_matrix;
		double[] u = new double[glob_m];
		double[] z = new double[glob_m]; // vector s \in F_q^m
		double[] r0 = new double[glob_n];
		double[] r1 = new double[glob_n];
		double[] x = new double[glob_m];
		int sigma; // rand double
		
		x = IDscheme.assign(x_vector);

		for (int i = 0; i < glob_m; i++) { // u = Z_q^m
			u[i] = IDscheme.randInt(glob_q);
		}

		u_vector = u;

		for (int i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
			r0[i] = IDscheme.randInt(2);
			r1[i] = IDscheme.randInt(2);
		}

		glob_r0 = r0;
		glob_r1 = r1;
		
		// WARN: glob_q instead of glob_m is used.
		sigma = IDscheme.randInt(glob_q-1); //glob_m-1
		glob_sigma = sigma;

		
		/*
		double[][] psigma_matrix =  new double[glob_m][glob_m];
		for(int i=0;i<glob_m;i++) {
		psigma_matrix[i] = knuth_shuffle(p_matrix[i], sigma);
		}
		*/
		
	
		
		/*
		System.out.println("Psigma matrix: ");
		for(int i=0;i<glob_m;i++) {
			for(int j=0;j<glob_m;j++) {
				System.out.println(psigma_matrix[i][j]);
			}
		}
		*/
		
		p_sigma_matrix = psigma_matrix; // global P matrix
		
		//System.out.print(p_sigma_matrix.length);
		//System.out.print(p_sigma_matrix[20][2]);
		
		double[][] p_transpose = IDscheme.transpose(psigma_matrix);
		z = IDscheme.vectorMultiplyMatrix(x, p_transpose); 
		
		glob_z = IDscheme.assign(z);
		//System.out.println(z.length);
	
		double[][] a_transpose = IDscheme.transpose(amatrix);
		double[] au = IDscheme.vectorMultiplyMatrix(u, a_transpose); //Au
		
		double[] pu = IDscheme.vectorMultiplyMatrix(u, p_transpose); //P_sigma.u
		double[] formatted_pu = IDscheme.assign(dbl_formatter(pu));
		
		String sigma_str = Integer.toString(sigma);
		c1 = IDscheme.com(sigma_str+Arrays.toString(au)+Arrays.toString(r0)); 
		c2 = IDscheme.com(Arrays.toString(z)+Arrays.toString(formatted_pu)+Arrays.toString(r1));
		 
		//System.out.println("c1:" + c1);
		//System.out.println("c2:" + c2);
			
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
	public static double v1(){
		double alpha = getRandom(glob_q, 1);
		return alpha;
	}

    /*
    public static int v1(){
		int alpha = getRandom(glob_q, 1);
		return alpha;
	}
	*/
	
	// Prover: p2()	
	public static double[] p2(double alpha){
		double[] betha;
		glob_alpha = alpha;
		double[] u_alphax; // u + alpha.x
		double[] alphax_vector = IDscheme.scalarMultiplyVector(alpha,x_vector);
		u_alphax = IDscheme.addVectors(u_vector,alphax_vector); // u + alpha.x
		//print(u_alphax);
		double[][] p_transpose = IDscheme.transpose(p_sigma_matrix);
		betha = IDscheme.vectorMultiplyMatrix(u_alphax, p_transpose);
		return betha;
	}
	
	// Verifier: v2()
		public static int v2(){
			int b = getRandom(3, 1);
			return b;
		}
		
		
	// Prover: p3()
	public static Object[] p3(int c){
		int ch = c;
		Object[] resp = new Object[4];

		if(ch == 1){
			resp[0] = glob_sigma;
			resp[1] = glob_r0;
		}
		else if(ch == 2){
			resp[0] = IDscheme.assign(glob_z);
			resp[1] = IDscheme.assign(glob_r1);
		}else{
			System.out.println("Error!");
		}
		
		return resp;
	}


	public static boolean comp(double[] arr1,double[] arr2){ //compare function
		for(int i=0; i<glob_m; i++){
			if(arr1[i]!=arr2[i])
				return false;
		}
		return true;
	}
	
	// Verifier: v2()
	public static void v3(int c, Object[] params, double[] betha){
		int ch = c;
		Object[] resp = params;
		double[] local_betha = betha;

		if(ch == 1){
			String comp1, comp2; // computations 
			int sigma = (int)resp[0];
			double[] r0 = (double[])resp[1];
			double alpha = glob_alpha;
			double[][] amatrix = A_matrix;
			double[][] pmatrix = p_sigma_matrix;
			double[] y = y_Ax;
			
			/*
			for(int i =0;i<pmatrix.length;i++) {
				for(int j=0;j<pmatrix.length;j++) {
					System.out.println(pmatrix[i][j]);
				}
			}
			*/
			
			double[][] p_matrix = new double[glob_m][glob_m];
			p_matrix = IDscheme.inverse(pmatrix);
			
			double[][]  ap_sigma = IDscheme.multiply(amatrix,p_matrix); //A.P_sigma
			double[][]  ap_transpose = IDscheme.transpose(ap_sigma);
			
			String sigma_str = Integer.toString(sigma);
			double[] op1 = IDscheme.vectorMultiplyMatrix(betha, ap_transpose);
			
			double[]  op2 = IDscheme.scalarMultiplyVector(alpha,y);
			double[]  op3 = IDscheme.subtractVectors(op1,op2);
			comp1 = IDscheme.com(sigma_str+Arrays.toString(op3)+Arrays.toString(r0));
			
			//System.out.println("computed c1:" + comp1);
			//System.out.println("com1:" + c1);
		
			if( comp1.equals(c1)){ //TODO isinX check
				System.out.println("Success!");
			}else{
				System.out.println("Failed!");
			}

		}
		else if(ch == 2){
			String comp1, comp2;
			double comp3; // computations
			double[] z = (double[])resp[0];
			
			
			double[] r1 = (double[])resp[1];
			double alpha = glob_alpha;
			double[] op1 = IDscheme.scalarMultiplyVector(alpha,z);
			double[] op2 = IDscheme.subtractVectors(betha,op1);
			/* TODO: check Z in {0,1} and HW
			// ?z\in[0,1]
			 // 0 ve 1 lerden mi?
			// HW eshit midir m/2 ye ?
			 */
			double[] formatted_op2 = IDscheme.assign(dbl_formatter(op2));
			
			comp1 = IDscheme.com(Arrays.toString(z)+Arrays.toString(formatted_op2)+Arrays.toString(r1));
			
			//System.out.println("computed c2:" + comp1);
			//System.out.println("com2:" + c2);
			
			if( c2.equals(comp1)){  
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
/*
	System.out.println("CLRS ID Scheme:");
	System.out.println("Parameters Set:");
	System.out.println("n = " + n);
	System.out.println("m = " + m);
	System.out.println("q = " + q);
*/	long t0 = System.nanoTime();
	keyGeneration(n,m, q);
	long t1 = System.nanoTime();

	//System.out.println("Prover: compute commitments c1, c2 and c3:");
	long t2 = System.nanoTime();
	p1();
	long t3 = System.nanoTime();
	double alpha = v1();
	double[] beta = p2(alpha);
	int b = v2();
/*	System.out.println("Verifier: sends a challenge: " + b);
	System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + b + ")");
	System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
*/	long t4 = System.nanoTime();
	Object[] response_p3 = p3(b);
	long t5 = System.nanoTime();
	v3(b,response_p3, beta);
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
