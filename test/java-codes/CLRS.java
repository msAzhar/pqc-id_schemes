import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.math.BigInteger; 
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 



public class CLRS {
	// Cayrel's IDscheme

	//-----------------------------------Global variables-----------------------------------

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

	//--------------------------------------------------------------------------------------

	public static int randInt(double q) {
	    int number = (int)(Math.random()*q);
	    return number;
	}	
	
	public static double[] assign(double[] a) {
       
        double[] result = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ;
        }

        return result;
    }
	
	public static double[] addVectors(double[] a, double[] b) {
        int length = a.length < b.length ? a.length : b.length;
        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] + b[i];
        }

        return result;
    }
  
	public static double[] padding(double[] arr, int length) {
		double[] result  = new double[length];
		for (int i = 0; i < length; i++) {
			if (i >= arr.length) {
				result[i] = 0;
			} else {
				result[i] = arr[i];
			}
		}
		return result;
	}
	
	public static double[] addVectorsWithPadding(double[] a, double[] b) {
		int length = a.length > b.length ? a.length : b.length;
        double[] result = new double[length];
        double[] b_t = new double[length];
        
        if(a.length != b.length) {
        	if(a.length > b.length) {
        		b_t = padding(b,a.length);
        	}else if(a.length < b.length) {
        		b_t = padding(a,b.length);
        	}
        	
        	for (int i = 0; i < length; i++) {
                result[i] = a[i] + b_t[i];
            }

            return result;
        }else {
        	for (int i = 0; i < length; i++) {
                result[i] = a[i] + b[i];
            }

            return result;
        }
        
    }
	
	public static double[] multiplyVectors(double[] a, double[] b) {
        int length = a.length < b.length ? a.length : b.length;
        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] * b[i];
        }

        return result;
    }
	
	public static double[] subtractVectors(double[] a, double[] b) {
        int length = a.length < b.length ? a.length : b.length;
        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            result[i] = a[i] - b[i];
        }

        return result;
    }
	
	public static double[] knuth_shuffle(double[] arr, int sigma)
    {
        int size = arr.length;
        double[] toShuffle = new double[size];
        double[] result = new double[size];
        toShuffle=assign(arr);
        for (int i = size - 1; i > 0; i--)
        {
            int fixedIndex = sigma;
            double tmp = toShuffle[i];
            toShuffle[i] = toShuffle[fixedIndex];
            toShuffle[fixedIndex] = tmp;
        }
        result = toShuffle;
        return result;
    }
	
	public static double[][] knuth_shuffle(double[][] arr, int sigma)
    {
        int size = arr.length;
        double[][] toShuffle = new double[size][];
        double[][] result = new double[size][];
        toShuffle=arr;      
 
	    for (int i = arr.length - 1; i > 0; i--) {
	        for (int j = arr[i].length - 1; j > 0; j--) {
	        	//double fixedIndex = sigma;
	            int m = sigma + i;
	            int n = sigma +j;

	            double temp = toShuffle[i][j];
	            toShuffle[i][j] = toShuffle[m][n];
	            toShuffle[m][n] = temp;
	        }
	    }
	    
        result = toShuffle;
        return result;
	}
	
	public static double[] knuth_shuffle(double[] arr)
    {
        int size = arr.length;
        double[] toShuffle = new double[size];
        double[] result = new double[size];
        toShuffle=assign(arr);
      //  Random r = new Random();
        for (int i = size - 1; i > 0; i--)
        {
           // double fixedIndex = r.randInt(i+1); //!!Dikkat
        	int fixedIndex = 5;
            double tmp = toShuffle[i];
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
		// Convert byte array doubleo signum representation 
		BigInteger number = new BigInteger(1, hash); 

		// Convert message digest doubleo hex value 
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
	
	public static double[][] initMatrixRandom(int x, int y, double q) {
		double[][] matrix = new double[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				matrix[i][j] = randInt(q);
			}
		}
		return matrix;
	}
	
	public static double[][]  generateIdentityMatrix(int n){
		double[][] matrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(i==j) {
					matrix[i][j] = 1;
				}else {
					matrix[i][j] = 0;
				}
			}
		}
		return matrix;
	}

	public static double[] vectorMultiplyMatrix(double[] a, double[][] B) {
		//var A_x = 1;
		int A_y = a.length;
		int B_x = B.length;
		int B_y = B[0].length;
		
		if (B_x != A_y) {
			System.out.println("Matrix inner dimensions must agree");
			System.exit(0);
		}
			
		double[] v = new double[B_y];
		for (int j = 0; j < B_y; j++) {
			v[j] = 0;
		}
			
		for (int i = 0; i < A_y; i++) {
			double[] Browi = B[i];
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
	private static double[][] transpose(double[][] matrix) {
		double[][] transpose = new double[matrix[0].length][matrix.length];

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				transpose[j][i] = matrix[i][j];
		return transpose;
	}
	/*
	public static double[][] transpose(double[][] A) {
		int A_x = A.length;
		int A_y = A[0].length;
		
		double[][] C = new double[A_y][A_x];
		for (int i = 0; i < A_x; i++) {
			for (int j = 0; j < A_y; j++) {
				C[j][i] = A[i][j];
			}
		}
		return C;
	}
	*/
	
	//Returns A', the transpose of a matrix A 
	// FIXIT!!
		public static double[] transpose(double[] A) {
			int A_x = A.length;
			
			double[] C = new double[A_x];
			for (int i = 0; i < A_x; i++) {
				
					C[i] = A[i];
			}
			return C;
		}
		
		
		//Multiplies a vector by a scalar, c = s*v
		public static double[] scalarMultiplyVector(double s, double[] v) {
			double[] c = new double[v.length];
			for (int i = 0; i < v.length; i++) {
				c[i] = s * v[i];
			}
			return c;
		}
		
		// Hamming Weight... amount of 1s
		public static double hw(double[] v){
				double wt=0;
				for(int i = 0; i<v.length; i++){
					if(v[i] == 1)
						wt+=1;
				}
				return wt;
			}
/*		
		public static double determinant(double[][] matrix) {
			if (matrix.length != matrix[0].length)
				throw new IllegalStateException("invalid dimensions");

			if (matrix.length == 2)
				return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

			double det = 0;
			for (int i = 0; i < matrix[0].length; i++)
				det += Math.pow(-1, i) * matrix[0][i] * determinant(minor(matrix, 0, i));
;
			return det;
		}
		
		public static double matrixDeterminant (double[][] matrix) {
			double temporary[][];
			double result = 0;

			if (matrix.length == 1) {
				result = matrix[0][0];
				return (result);
			}

			if (matrix.length == 2) {
				result = ((matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]));
				return (result);
			}

			for (int i = 0; i < matrix[0].length; i++) {
				temporary = new double[matrix.length - 1][matrix[0].length - 1];

				for (int j = 1; j < matrix.length; j++) {
					for (int k = 0; k < matrix[0].length; k++) {
						if (k < i) {
							temporary[j - 1][k] = matrix[j][k];
						} else if (k > i) {
							temporary[j - 1][k - 1] = matrix[j][k];
						}
					}
				}

				result += matrix[0][i] * Math.pow (-1, (double) i) * matrixDeterminant (temporary);
			}
			return (result);
		}

		public static double[][] inverse(double[][] matrix) {
			double[][] inverse = new double[matrix.length][matrix.length];

			// minors and cofactors
			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[i].length; j++)
					inverse[i][j] = Math.pow(-1, i + j)
							* matrixDeterminant(minor(matrix, i, j));

			// adjugate and determinant
			double det = 1.0 / matrixDeterminant(matrix);
			for (int i = 0; i < inverse.length; i++) {
				for (int j = 0; j <= i; j++) {
					double temp = inverse[i][j];
					inverse[i][j] = inverse[j][i] * det;
					inverse[j][i] = temp * det;
				}
			}

			return inverse;
		}

		public static double[][] minor(double[][] matrix, int row, int column) {
			double[][] minor = new double[matrix.length - 1][matrix.length - 1];

			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; i != row && j < matrix[i].length; j++)
					if (j != column)
						minor[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
			return minor;
		}
		
*/
		public static double[][] inverse(double[][] M) {
			double[][] I = new double[M.length][M.length];

		    // I use Guassian Elimination to calculate the inverse:
		    // (1) 'augment' the matrix (left) by the identity (on the right)
		    // (2) Turn the matrix on the left into the identity by elemetry row ops
		    // (3) The matrix on the right is the inverse (was the identity matrix)
		    // There are 3 elemtary row ops: (I combine b and c in my code)
		    // (a) Swap 2 rows
		    // (b) Multiply a row by a scalar
		    // (c) Add 2 rows
		    
		    //if the matrix isn't square: exit (error)
		    if(M.length != M[0].length){
		    	System.out.print("Matrix is not square!");
		    	//return; 
		    	}
		    
		    //create the identity matrix (I), and a copy (C) of the original
		    int i=0, ii=0, j=0, dim=M.length;
		    double e=0, t=0;
		    double[][] C = new double[M.length][M.length];;
		    for(i=0; i<dim; i+=1){
		        // Create the row
		        I[i] = new double[M.length];
		        C[i] = new double[M.length];
		        for(j=0; j<dim; j+=1){
		            
		            //if we're on the diagonal, put a 1 (for identity)
		            if(i==j){ I[i][j] = 1; }
		            else{ I[i][j] = 0; }
		            
		            // Also, make the copy of the original
		            C[i][j] = M[i][j];
		        }
		    }
		    
		    // Perform elementary row operations
		    for(i=0; i<dim; i+=1){
		        // get the element e on the diagonal
		        e = C[i][i];
		        
		        // if we have a 0 on the diagonal (we'll need to swap with a lower row)
		        if(e==0){
		            //look through every row below the i'th row
		            for(ii=i+1; ii<dim; ii+=1){
		                //if the ii'th row has a non-0 in the i'th col
		                if(C[ii][i] != 0){
		                    //it would make the diagonal have a non-0 so swap it
		                    for(j=0; j<dim; j++){
		                        e = C[i][j];       //temp store i'th row
		                        C[i][j] = C[ii][j];//replace i'th row by ii'th
		                        C[ii][j] = e;      //repace ii'th by temp
		                        e = I[i][j];       //temp store i'th row
		                        I[i][j] = I[ii][j];//replace i'th row by ii'th
		                        I[ii][j] = e;      //repace ii'th by temp
		                    }
		                    //don't bother checking other rows since we've swapped
		                    break;
		                }
		            }
		            //get the new diagonal
		            e = C[i][i];
		            //if it's still 0, not invertable (error)
		            if(e==0){
		            	System.out.print("Error: matrix is not invertable!");
		            	break; }
		        }
		        
		        // Scale this row down by e (so we have a 1 on the diagonal)
		        for(j=0; j<dim; j++){
		            C[i][j] = C[i][j]/e; //apply to original matrix
		            I[i][j] = I[i][j]/e; //apply to identity
		        }
		        
		        // Subtract this row (scaled appropriately for each row) from ALL of
		        // the other rows so that there will be 0's in this column in the
		        // rows above and below this one
		        for(ii=0; ii<dim; ii++){
		            // Only apply to other rows (we want a 1 on the diagonal)
		            if(ii==i){continue;}
		            
		            // We want to change this element to 0
		            e = C[ii][i];
		            
		            // Subtract (the row above(or below) scaled by e) from (the
		            // current row) but start at the i'th column and assume all the
		            // stuff left of diagonal is 0 (which it should be if we made this
		            // algorithm correctly)
		            for(j=0; j<dim; j++){
		                C[ii][j] -= e*C[i][j]; //apply to original matrix
		                I[ii][j] -= e*I[i][j]; //apply to identity
		            }
		        }
		    }
		    
		    //we've done all operations, C should be the identity
		    //matrix I should be the inverse:
		    return I;
		}

		public static double[][] multiply(double[][] a, double[][] b) {
			if (a[0].length != b.length)
				throw new IllegalStateException("invalid dimensions");

			double[][] matrix = new double[a.length][b[0].length];
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < b[0].length; j++) {
					double sum = 0;
					for (int k = 0; k < a[i].length; k++)
						sum += a[i][k] * b[k][j];
					matrix[i][j] = sum;
				}
			}

			return matrix;
		}
/*
		public static double[][] rref(double[][] matrix) {
			double[][] rref = new double[matrix.length][];
			for (int i = 0; i < matrix.length; i++)
				rref[i] = Arrays.copyOf(matrix[i], matrix[i].length);

			int r = 0;
			for (int c = 0; c < rref[0].length && r < rref.length; c++) {
				int j = r;
				for (int i = r + 1; i < rref.length; i++)
					if (Math.abs(rref[i][c]) > Math.abs(rref[j][c]))
						j = i;
				if (Math.abs(rref[j][c]) < 0.00001)
					continue;

				double[] temp = rref[j];
				rref[j] = rref[r];
				rref[r] = temp;

				double s = 1.0 / rref[r][c];
				for (j = 0; j < rref[0].length; j++)
					rref[r][j] *= s;
				for (int i = 0; i < rref.length; i++) {
					if (i != r) {
						double t = rref[i][c];
						for (j = 0; j < rref[0].length; j++)
							rref[i][j] -= t * rref[r][j];
					}
				}
				r++;
			}

			return rref;
		}
*/
	
	public static void keyGeneration(int n, int m, int q) {
		double[][] amatrix = initMatrixRandom(n, m, q); // A, n*m
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
		x = assign(ss);
		
		double[][] a_transpose = transpose(amatrix);
		double[] y = vectorMultiplyMatrix(x, a_transpose);
		
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
		double[][] psigma_matrix = generateIdentityMatrix(glob_m);
		//double det = matrixDeterminant(psigma_matrix);
		//System.out.print("Det: "+det);
		double[][] amatrix = A_matrix;
		double[] u = new double[glob_m];
		double[] z = new double[glob_m]; // vector s \in F_q^m
		double[] r0 = new double[glob_n];
		double[] r1 = new double[glob_n];
		double[] x = new double[glob_m];
		int sigma; // rand double
		
		x = assign(x_vector);

		for (int i = 0; i < glob_m; i++) { // u = Z_q^m
			u[i] = randInt(glob_q);
		}

		u_vector = u;

		for (int i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
			r0[i] = randInt(2);
			r1[i] = randInt(2);
		}

		glob_r0 = r0;
		glob_r1 = r1;
		
		// WARN: glob_q instead of glob_m is used.
		sigma = randInt(glob_q-1); //glob_m-1
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
		
		System.out.print(p_sigma_matrix.length);
		System.out.print(p_sigma_matrix[20][2]);
		
		double[][] p_transpose = transpose(psigma_matrix);
		z = vectorMultiplyMatrix(x, p_transpose); 
		
		glob_z = assign(z);
		//System.out.println(z.length);
	
		double[][] a_transpose = transpose(amatrix);
		double[] au = vectorMultiplyMatrix(u, a_transpose); //Au
		
		double[] pu = vectorMultiplyMatrix(u, p_transpose); //P_sigma.u
		double[] formatted_pu = assign(dbl_formatter(pu));
		
		String sigma_str = Integer.toString(sigma);
		c1 = com(sigma_str+Arrays.toString(au)+Arrays.toString(r0)); 
		c2 = com(Arrays.toString(z)+Arrays.toString(formatted_pu)+Arrays.toString(r1));
		 
		System.out.println("c1:" + c1);
		System.out.println("c2:" + c2);
			
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
	
	// Prover: p2()	
	public static double[] p2(double alpha){
		double[] betha;
		glob_alpha = alpha;
		double[] u_alphax; // u + alpha.x
		double[] alphax_vector = scalarMultiplyVector(alpha,x_vector);
		u_alphax = addVectors(u_vector,alphax_vector); // u + alpha.x
		//print(u_alphax);
		double[][] p_transpose = transpose(p_sigma_matrix);
		betha = vectorMultiplyMatrix(u_alphax, p_transpose);
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
			resp[0] = assign(glob_z);
			resp[1] = assign(glob_r1);
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
			p_matrix = inverse(pmatrix);
			
			double[][]  ap_sigma = multiply(amatrix,p_matrix); //A.P_sigma
			double[][]  ap_transpose = transpose(ap_sigma);
			
			String sigma_str = Integer.toString(sigma);
			double[] op1 = vectorMultiplyMatrix(betha, ap_transpose);
			
			double[]  op2 = scalarMultiplyVector(alpha,y);
			double[]  op3 = subtractVectors(op1,op2);
			comp1 = com(sigma_str+Arrays.toString(op3)+Arrays.toString(r0));
			
			System.out.println("comp1:" + comp1);
			System.out.println("com1:" + c1);
			

		
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
			double[] op1 = scalarMultiplyVector(alpha,z);
			double[] op2 = subtractVectors(betha,op1);
			/* TODO: check Z in {0,1} and HW
			// ?z\in[0,1]
			 // 0 ve 1 lerden mi?
			// HW eshit midir m/2 ye ?
			 */
			double[] formatted_op2 = assign(dbl_formatter(op2));
			
			comp1 = com(Arrays.toString(z)+Arrays.toString(formatted_op2)+Arrays.toString(r1));
			
			System.out.println("comp1:" + comp1);
			System.out.println("com2:" + c2);
			
			/*
			double[] a = {2,3,4,0};
			double[] b = {2,3};
			double[] d = addVectorsWithPadding(b,a);
			for(int i =0;i<d.length;i++) {
			System.out.println("d:" + d[i]);
			}
			*/
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

	System.out.println("CLRS ID Scheme:");
	System.out.println("Parameters Set:");
	System.out.println("n = " + n);
	System.out.println("m = " + m);
	System.out.println("q = " + q);
	long t0 = System.nanoTime();
	keyGeneration(n,m, q);
	long t1 = System.nanoTime();

	System.out.println("Prover: compute commitments c1, c2 and c3:");
	long t2 = System.nanoTime();
	p1();
	long t3 = System.nanoTime();
	double alpha = v1();
	double[] beta = p2(alpha);
	int b = v2();
	System.out.println("Verifier: sends a challenge: " + b);
	System.out.println("Prover: reveals some parameters depending on challenge.(Current challenge is: " + b + ")");
	System.out.println("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
	long t4 = System.nanoTime();
	Object[] response_p3 = p3(2);
	long t5 = System.nanoTime();
	v3(2,response_p3, beta);
	long t6 = System.nanoTime();
	
	
	/*
	System.out.println("Keygen Execution time in milliseconds : " + 
	 
			(t1-t0) / 1000000);
	System.out.println("Commitments comp Execution time in milliseconds : " + 
			(t3-t2) / 1000000);
	//System.out.println("Revealing some params Execution time in milliseconds : " + 
	//		(t5-t4) / 1000000);
	System.out.println("Verification Execution time in milliseconds : " + 
			(t6-t5) / 1000000);
	*/
	}

}

