import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IDscheme {
	public static int randInt(int q) {
	    int number = (int)(Math.random()*q);
	    return number;
	}
	
	// returns random integer between minimum and maximum range
    public static int getRandom(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

	// returns random doubleeger between minimum and maximum range
    public static double getRandom(double maximum, double minimum){
        return ((double) (Math.random()*(maximum - minimum))) + minimum;
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
	
	public static double[][] initMatrixRandom(int x, int y, double q) {
		double[][] matrix = new double[x][y];
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

		public static double[] subtractVectorsWithPadding(double[] a, double[] b) {
			int length = a.length > b.length ? a.length : b.length;// check it!
	        double[] result = new double[length];
	        double[] b_t = new double[length];
	        
	        if(a.length > b.length) {
	        	b_t = padding(b,a.length);
	        }
	        
	        for (int i = 0; i < length; i++) {
	            result[i] = a[i] - b_t[i];
	        }

	        return result;
	        
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
		public static double[][] transpose(double[][] matrix) {
			double[][] transpose = new double[matrix[0].length][matrix.length];

			for (int i = 0; i < matrix.length; i++)
				for (int j = 0; j < matrix[i].length; j++)
					transpose[j][i] = matrix[i][j];
			return transpose;
		}
		
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
}
