
//The default constructor
function initMatrixDefault(x, y) {
	var matrix = new Array(x);
	for (var i = 0; i < x; i++) {
       		matrix[i] = new Array(y);
	}
	return matrix;
}

//The constructor, where each element is chosen uniformly at random
function initMatrixRandom(x, y, q) {
	var matrix = new Array(x);
	for (var i = 0; i < x; i++) {
       		matrix[i] = new Array(y);
		for (var j = 0; j < y; j++) {
			matrix[i][j] = nextInt(q);
		}
	}
	return matrix;
}

//Returns A', the transpose of a matrix A
function transpose(A) {
	var A_x = A.length;
	var A_y = A[0].length;
	
	var C = initMatrixDefault(A_y, A_x);
	for (var i = 0; i < A_x; i++) {
		for (var j = 0; j < A_y; j++) {
			C[j][i] = A[i][j];
		}
	}
	return C;
}
	
//Matrix addition, C = A + B, each element modulo q
function addMod(A, B, q) {
	checkDimensions(A, B);
	var A_x = A.length;
	var A_y = A[0].length;
	
	var C = initMatrixDefault(A_x, A_y);
	for (var i = 0; i < A_x; i++) {
		for (var j = 0; j < A_y; j++) {
			C[i][j] = (A[i][j] + B[i][j]) % q;
			if (C[i][j] < 0) {
				C[i][j] += q;
			}
		}
	}
	return C;
}
	
//Vector subtraction, c = a - b
function vectorSubstract(a, b) {
	if (b.length != a.length) {
		alert("Vector length must agree");
		return;
	}
	
	var c = new Array(a.length);
	for (var i = 0; i < a.length; i++) {
		c[i] = a[i] - b[i];
	}
	return c;
}

//Vector multiplication, c = a * b
function vectorMultiply(a, b) {
	if (b.length != a.length) {
		alert("Vector length must agree");
		return;
	}
	
	var c = new Array(a.length);
	for (var i = 0; i < a.length; i++) {
		c[i] = a[i] * b[i];
	}
	return c;
}
	
//Multiplies a vector by a scalar, c = s*v
function scalarMultiplyVector(s, v) {
	var c = new Array(v.length);
	for (var i = 0; i < v.length; i++) {
		c[i] = s * v[i];
	}
	return c;
}

//Multiplies a matrix by a scalar, C = s*A
function scalarMultiplyMod(s, A, q) {
	var A_x = A.length;
	var A_y = A[0].length;
	
	var C = initMatrixDefault(A_x, A_y);
	for (var i = 0; i < A_x; i++) {
		for (var j = 0; j < A_y; j++) {
			C[i][j] = (s * A[i][j]) % q;
			if (C[i][j] < 0) {
				C[i][j] += q;
			}
		}
	}
	return C;
}

//Matrix multiplication, C = A * B
function multiply(A, B) {
	var A_x = A.length;
	var A_y = A[0].length;
	var B_x = B.length;
	var B_y = B[0].length;
	
	if (B_x != A_y) {
		alert("Matrix inner dimensions must agree");
		return;
	}
	
	var C = initMatrixDefault(A_x, B_y);
	var Bcolj = new Array(A_y);
	for (var j = 0; j < B_y; j++) {
		for (var k = 0; k < A_y; k++) {
			Bcolj[k] = B[k][j];
		}
		for (var i = 0; i < A_x; i++) {
			var Arowi = A[i];
			var s = 0;
			for (var k = 0; k < A_y; k++) {
				if (Bcolj[k] == 0) {
					//s += 0;
				} else if (Bcolj[k] == 1) {
					s += Arowi[k];
				} else if (Bcolj[k] == -1) {
					s -= Arowi[k];
				} else {
					s += Arowi[k] * Bcolj[k];
				}
			}
			C[i][j] = s;
		}
	}
	return C;
}

//Matrix multiplication, C = A * B, each element of C modulo q
function multiplyMod(A, B, q) {
	var A_x = A.length;
	var A_y = A[0].length;
	var B_x = B.length;
	var B_y = B[0].length;
	
	if (B_x != A_y) {
		alert("Matrix inner dimensions must agree");
		return;
	}
	
	var C = initMatrixDefault(A_x, B_y);
	var Bcolj = new Array(A_y);
	for (var j = 0; j < B_y; j++) {
		for (var k = 0; k < A_y; k++) {
			Bcolj[k] = B[k][j];
		}
		for (var i = 0; i < A_x; i++) {
			var Arowi = A[i];
			var s = 0;
			for (var k = 0; k < A_y; k++) {
				if (Bcolj[k] == 0) {
					//s += 0;
				} else if (Bcolj[k] == 1) {
					s += Arowi[k];
				} else if (Bcolj[k] == -1) {
					s -= Arowi[k];
				} else {
					s += Arowi[k] * Bcolj[k];
				}
			}
			C[i][j] = s % q;
			if (C[i][j] < 0) {
				C[i][j] += q;
			}
		}
	}
	return C;
}

//For Encrypt
//Multiplies a matrix B by a vector a, c = a * B
function encVectorMultiplyMatrix(a, B) {
	//var A_x = 1;
	var A_y = a.length;
	var B_x = B.length;
	var B_y = B[0].length;
	
	if (B_x != A_y) {
		alert("Matrix inner dimensions must agree");
		return;
	}
		
	var v = new Array(B_y);
	for (var j = 0; j < B_y; j++) {
		v[j] = 0;
	}
		
	for (var i = 0; i < A_y; i++) {
		var Browi = B[i];
		for (var j = 0; j < B_y; j++) {
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


//For Decrypt
//Multiplies a matrix B by a vector a, c = a * B
function decVectorMultiplyMatrix(a, B) {
	//var A_x = 1;
	var A_y = a.length;
	var B_x = B.length;
	var B_y = B[0].length;
	
	if (B_x != A_y) {
		alert("Matrix inner dimensions must agree");
		return;
	}
		
	var v = new Array(B_y);
	for (var j = 0; j < B_y; j++) {
		v[j] = 0;
	}
		
	for (var i = 0; i < A_y; i++) {
		var Browi = B[i];
		for (var j = 0; j < B_y; j++) {
			/*
			if (Browi[j] == 0) {
				//v[j] += 0;
			} else if (Browi[j] == 1) {
				v[j] += a[i];
			} else if (Browi[j] == -1) {
				v[j] -= a[i];
			} else {
			*/
				v[j] += a[i] * Browi[j];	// it will be a little bit faster for decrypt on Firefox
			//}
		}
	}
	return v;
}

//Checks if(size(A) == size(B))
function checkDimensions(A, B) {
	var A_x = A.length;
	var A_y = A[0].length;
	var B_x = B.length;
	var B_y = B[0].length;
	
	if (B_x != A_x || B_y != A_y) {
		alert("Matrix dimensions must agree");
		return;
	}
}

//Modulo q
function mod(A, q) {
	var A_x = A.length;
	var A_y = A[0].length;
	if (q <= 0) {
		alert("modulus not positive");
		return;
	}
	for (var i = 0; i < A_x; i++) {
		for (var j = 0; j < A_y; j++) {
			A[i][j] %= q;
			if (A[i][j] < 0) {
				A[i][j] += q;
			}
		}
	}
}


//Returns the next pseudorandom, uniformly distributed integer between 0(inclusive) and q-1(inclusive)
function nextInt(q) {
	return Math.floor(Math.random() * q);
}

//Returns the pseudorandom integer value between low(inclusive) and high(inclusive)
function rangeValue(low, high) {
	return Math.floor(Math.random() * (high - low + 1) + low);
}

//Shuffles the input array
function shuffle(arr) {
	var arr2 = arr.slice();
	for(var j, x, i = arr2.length; i; j = parseInt(Math.random() * i), x = arr2[--i], arr2[i] = arr2[j], arr2[j] = x);
	return arr2;    
}

//Returns a copy of the original array, truncated or padded with zeros to obtain the specified length
function copyOf(arr1, length) {
	var arr2 = new Array(length);
	for (var i = 0; i < length; i++) {
		if (i >= arr1.length) {
			arr2[i] = 0;
		} else {
			arr2[i] = arr1[i];
		}
	}
	return arr2;
}

//sha256 hash function is used
function sha256(ascii) {
	function rightRotate(value, amount) {
		return (value>>>amount) | (value<<(32 - amount));
	};
	
	var mathPow = Math.pow;
	var maxWord = mathPow(2, 32);
	var lengthProperty = 'length'
	var i, j; // Used as a counter across the whole file
	var result = ''

	var words = [];
	var asciiBitLength = ascii[lengthProperty]*8;
	
	//* caching results is optional - remove/add slash from front of this line to toggle
	// Initial hash value: first 32 bits of the fractional parts of the square roots of the first 8 primes
	// (we actually calculate the first 64, but extra values are just ignored)
	var hash = sha256.h = sha256.h || [];
	// Round constants: first 32 bits of the fractional parts of the cube roots of the first 64 primes
	var k = sha256.k = sha256.k || [];
	var primeCounter = k[lengthProperty];
	/*/
	var hash = [], k = [];
	var primeCounter = 0;
	//*/

	var isComposite = {};
	for (var candidate = 2; primeCounter < 64; candidate++) {
		if (!isComposite[candidate]) {
			for (i = 0; i < 313; i += candidate) {
				isComposite[i] = candidate;
			}
			hash[primeCounter] = (mathPow(candidate, .5)*maxWord)|0;
			k[primeCounter++] = (mathPow(candidate, 1/3)*maxWord)|0;
		}
	}
	
	ascii += '\x80' // Append Ƈ' bit (plus zero padding)
	while (ascii[lengthProperty]%64 - 56) ascii += '\x00' // More zero padding
	for (i = 0; i < ascii[lengthProperty]; i++) {
		j = ascii.charCodeAt(i);
		if (j>>8) return; // ASCII check: only accept characters in range 0-255
		words[i>>2] |= j << ((3 - i)%4)*8;
	}
	words[words[lengthProperty]] = ((asciiBitLength/maxWord)|0);
	words[words[lengthProperty]] = (asciiBitLength)
	
	// process each chunk
	for (j = 0; j < words[lengthProperty];) {
		var w = words.slice(j, j += 16); // The message is expanded into 64 words as part of the iteration
		var oldHash = hash;
		// This is now the undefinedworking hash", often labelled as variables a...g
		// (we have to truncate as well, otherwise extra entries at the end accumulate
		hash = hash.slice(0, 8);
		
		for (i = 0; i < 64; i++) {
			var i2 = i + j;
			// Expand the message into 64 words
			// Used below if 
			var w15 = w[i - 15], w2 = w[i - 2];

			// Iterate
			var a = hash[0], e = hash[4];
			var temp1 = hash[7]
				+ (rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25)) // S1
				+ ((e&hash[5])^((~e)&hash[6])) // ch
				+ k[i]
				// Expand the message schedule if needed
				+ (w[i] = (i < 16) ? w[i] : (
						w[i - 16]
						+ (rightRotate(w15, 7) ^ rightRotate(w15, 18) ^ (w15>>>3)) // s0
						+ w[i - 7]
						+ (rightRotate(w2, 17) ^ rightRotate(w2, 19) ^ (w2>>>10)) // s1
					)|0
				);
			// This is only used once, so *could* be moved below, but it only saves 4 bytes and makes things unreadble
			var temp2 = (rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22)) // S0
				+ ((a&hash[1])^(a&hash[2])^(hash[1]&hash[2])); // maj
			
			hash = [(temp1 + temp2)|0].concat(hash); // We don't bother trimming off the extra ones, they're harmless as long as we're truncating when we do the slice()
			hash[4] = (hash[4] + temp1)|0;
		}
		
		for (i = 0; i < 8; i++) {
			hash[i] = (hash[i] + oldHash[i])|0;
		}
	}
	
	for (i = 0; i < 8; i++) {
		for (j = 3; j + 1; j--) {
			var b = (hash[i]>>(j*8))&255;
			result += ((b < 16) ? 0 : '') + b.toString(16);
		}
	}
	return result;
}

//Shuffles elements of an array depending on sigma, Knuth Shuffle is used
function knuth_shuffle(array, sigma) {
  var shuffled_array = [];
  var currentIndex = array.length, temporaryValue, fixedIndex;//randomIndex;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    //randomIndex = Math.floor(Math.random() * currentIndex);
    fixedIndex = sigma;
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = shuffled_array[currentIndex];
    shuffled_array[currentIndex] = shuffled_array[fixedIndex];
    shuffled_array[fixedIndex] = temporaryValue;
  }

  return array;
}

//Matrix inversion 
function matrix_invert(M){
    // I use Guassian Elimination to calculate the inverse:
    // (1) 'augment' the matrix (left) by the identity (on the right)
    // (2) Turn the matrix on the left into the identity by elemetry row ops
    // (3) The matrix on the right is the inverse (was the identity matrix)
    // There are 3 elemtary row ops: (I combine b and c in my code)
    // (a) Swap 2 rows
    // (b) Multiply a row by a scalar
    // (c) Add 2 rows
    
    //if the matrix isn't square: exit (error)
    if(M.length !== M[0].length){return;}
    
    //create the identity matrix (I), and a copy (C) of the original
    var i=0, ii=0, j=0, dim=M.length, e=0, t=0;
    var I = [], C = [];
    for(i=0; i<dim; i+=1){
        // Create the row
        I[I.length]=[];
        C[C.length]=[];
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
            if(e==0){return}
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

//Returns the next pseudorandom, uniformly distributed integer between 0(inclusive) and q-1(inclusive)
function nextInt(q) {
    return Math.floor(Math.random() * q);
}

//Addition of vectors, c = a + b
function addVectors(a,b){
	if (a.length != b.length) {
		alert("Vector length must agree");
		return;
	}
	
	var c = new Array(a.length);
	for (var i = 0; i < a.length; i++) {
		c[i] = a[i] + b[i];
	}
	return c;
}

// Hamming Weight... amount of 1s
function hw(v){
		wt=0;
		for(var i = 0; i<v.length; i++){
			if(v[i] == 1)
				wt+=1;
		}
		return wt;
	}

module.exports = {
	scalarMultiplyVector,
	scalarMultiplyMod,
	multiply,
	multiplyMod,
	vectorSubstract,
	vectorMultiply,
	initMatrixRandom,
	transpose,
	addMod,
	hw,
	encVectorMultiplyMatrix,
	decVectorMultiplyMatrix,
	mod,
	sha256,
	shuffle,
	knuth_shuffle,
	matrix_invert,
	addVectors,
	nextInt
};