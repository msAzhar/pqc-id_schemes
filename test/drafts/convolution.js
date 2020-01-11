//Basic convolution algorithm, t = a * c
// c is general polynomial and a is a binary polynomial
function convolution(a,c) {
	
	var n = a.length;
	var m = c.length;
	
	if (n != m) {
		alert("Vectors' dimensions must match!");
		return;
	}
		
	//var t = new Array(2*n);
	var t = [];
	for (var j = 0; j < 2*n; j++) {
		t[j] = 0;
	}
	
	//get array of locations
	var b = _d(a);

	for (var j = 0; j < b.length; j++) {
		for (var k = 0; k < n; k++) {
			t[k+b[j]] = t[k+b[j]] + c[k]
		}
	}
	return t;
}

// Array of locations for '1'
function _d(v){
	var b = [];
	for(var i = 0; i<v.length; i++){
		if(v[i] == 1){
			b.push(i);
		}
	}
	return b;
}

function print(message) {
	//WScript.Echo(message);
	console.log(message);
}

//Multiplies a matrix B by a vector a, c = a * B
function vectorMultiplyMatrix(a, B) {
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

// Array of locations for '1'
function _indexes(v){
	var b = [];
	for(var i = 0; i<v.length; i++){
		if(v[i] == 1){
			b.push(i);
		}
	}
	return b;
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

//The default constructor
function initMatrixDefault(x, y) {
	var matrix = new Array(x);
	for (var i = 0; i < x; i++) {
       		matrix[i] = new Array(y);
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

//Sparse vector and matrix multiplication, t = a * B, 
// a is sparse vector
function sparseVectorMultiplyMatrix(a,B) {
	
	var k = a.length;
	var m = B.length;
	var n = B[0].length;
	
	if (k != m) {
		alert("Inner dimensions must match!");
		return;
	}
	
	var t = new Array(n);
	for (var j = 0; j < n; j++) {
		t[j] = 0;
	}
	print("t: "+t);

	//get array of locations
	var d = _indexes(a);
	print(d.length);
	print("d:" +d);
	for(var k = 0; k < d.length; k++) {
		//print(B[d[k]]);
		t=addVectors(t,B[d[k]])
	}

	return t;
}

//Vector multiplication, c = a * b, a is a sparse vector
function sparseVectorMultiply(a, b) {
	if (b.length != a.length) {
		alert("Vector length must agree");
		return;
	}
	
	var c = new Array(a.length);
	for (var i = 0; i < a.length; i++) {
		c[i] = 0;
	}

	//get array of locations
	var d = _indexes(a);

	for (var i = 0; i < d.length; i++) {
		c[d[i]] = b[d[i]];
	}
	return c;
}

function addElems(a){
	var total = 0;
	for (var i=0; i < a.length; i++){
		total +=a[i];
	}
	return total;
}

// Sparse Square Matrix and vector multiplication, 
// t = B * a, P is a sparse matrix with dimension mxm
// transpose is needed
function sparseMatrixMultiplyVector(B,a) {
	
	var k = a.length; 
	var m = B.length;
	var n = B[0].length; // n=m
	
	if (k != m) {
		alert("Inner dimensions must match!");
		return;
	}
	var o = transpose(B);
	print(o);
	var t = new Array(m);
	for (var j = 0; j < m; j++) {
		t[j] = 0;
	}

	for (var i = 0; i < m; i++) {
		//get array of locations
		var d = _indexes(o[i]);
		//print(d.length);
		//print("d:" +d);
		var temp = 0;
		
		for(var k = 0; k < d.length; k++) {
			//print("a[d[k]]:" + a[d[k]]);
			temp += a[d[k]];
		}
		t[i]+=temp;
	}

	return t;
}

// Sparse Square Matrix and vector multiplication, 
// t = B * a, P is a sparse matrix with dimension mxm
// does not require transpose
function sparseMatrixMultiplyVector2(B,a) {
	
	var k = a.length; 
	var m = B.length;
	var n = B[0].length; // n=m
	
	if (k != m) {
		alert("Inner dimensions must match!");
		return;
	}

	var t = new Array(m);
	for (var j = 0; j < m; j++) {
		t[j] = 0;
	}

	for (var i = 0; i < m; i++) {
		//get array of locations
		var d = _indexes(B[i]);

		var temp = 0;
		
		for(var k = 0; k < d.length; k++) {
			temp += a[d[k]];
		}
		t[i]+=temp;
	}

	return t;
}

//returns locations of 1s in a matrix
function indexes(A){
	var m = A.length;
	var t = Array(m)
	print(A);
	for (var i = 0; i < m; i++) {
		//get array of locations
		var d = _indexes(A[i]);
		t[i]=d;
		print(d);
	}
	print(t);
	return t;
}

//Sparse Square Matrix and vector multiplication, 
//t = B * a, B is a sparse matrix with dimension mxm, ids is an array of indexes of 1s.
function sparseMatrixMultiplyVector3(B,a,ids) {
	
	var k = a.length; 
	var m = B.length;
	var n = B[0].length; // n=m
	
	if (k != m) {
		alert("Inner dimensions must match!");
		return;
	}

	var t = new Array(m);
	for (var j = 0; j < m; j++) {
		t[j] = 0;
	}


	for (var i = 0; i < m; i++) {
		var temp = 0;
		for(var j = 0; j < ids[i].length; j++) {
			temp += a[ids[i][j]];
			print(temp);
		}
		t[i]+=temp;
	}

	return t;
}


function test(){

	var a = [0,1,1,0,0,0,1];
	var c = [2,1,2,3,4,5,2];

 	var deneme = sparseVectorMultiply(a,c);
 	print("SparseVECTOR: "+ deneme);
	//var b = _d(a);
	//print(b);
	var g = [1,0,0,1];
	var h = [[1,2,1],[2,1,1],[1,1,1],[1,1,2]];
	//var c = sparseVectorMultiplyMatrix(g,h);


	var f = vectorMultiplyMatrix(g,h);
	print(f);

	var j = [1,0,0,1];
	var n = [[1,2,1,1],[2,1,1,1],[1,1,1,2],[1,1,2,2]];
	var m = vectorMultiplyMatrix(j,n);
	print(m);

	var x = sparseVectorMultiplyMatrix(j,n);
	print("X:");
	print(x);

	var r = [[-3,0,3,2],[1,7,-1,9]];

	var t = [[-3,1],[0,7],[3,-1],[2,9]];
	var x = [2,-3,4,-1];

	var z = vectorMultiplyMatrix(x,t);
	print(z);

	var w = sparseVectorMultiplyMatrix(g,h);
	print(w);

	var q = [1,2,3,4];
	var t = [[1,0,1,0],[0,0,0,1],[1,0,0,1],[1,1,0,0]];
	var t2 = [[1,0,1,1],[0,0,0,1],[1,0,0,0],[0,1,1,0]];

	var v = vectorMultiplyMatrix(q,t);
	print(v);

	var l = sparseMatrixMultiplyVector(t,q)
	print(l);
/*
	var l2 = sparseMatrixMultiplyVector2(t2,q)
	print(l2);

	var c = indexes(t2);
	print(c);

	print("t: " + t);
	print("q: " + q);
	print("c: " + c);
	var hah = sparseMatrixMultiplyVector(t,q);
	print(hah);

	var haha = sparseMatrixMultiplyVector3(t2,q,c);
	print(haha);

*/
/*
	var f = convolution(a,c);
	print(f);
*/
}

test();

/*
//Sparse Square Matrix and vector multiplication, t = B * a, 
// B is a sparse matrix with dimension mxm
function sparseMatrixMultiplyVector(B,a) {
	
	var k = a.length; 
	var m = B.length;
	var n = B[0].length; // n=m
	
	if (k != m) {
		alert("Inner dimensions must match!");
		return;
	}

	var o = transpose(B);
	
	var t = new Array(m);
	for (var j = 0; j < m; j++) {
		t[j] = 0;
	}

	for (var i = 0; i < m; i++) {
		//get array of locations
		var d = _indexes(o[i]);
	
		var temp = 0;	
		for(var k = 0; k < d.length; k++) {
			temp += a[d[k]];
		}
		t[i]+=temp;
	}
	return t;
}
*/



