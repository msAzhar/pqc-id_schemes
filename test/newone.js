// CLRS

var IDscheme = require('../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var A_matrix;
var r_vector;
var x_vector;
var y_Ax;
var glob_m;
var glob_n;
var glob_q;
var glob_sigma;
var glob_z;
var c1,
	c2,
	c3,
	c7;
var hash_u;
var p_sigma_matrix;
var u_vector;
var glob_alpha;
var glob_beta;

//-------------------------------------------------------------------------------------------

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

// Sparse Square Matrix and vector multiplication, 
// t = B * a, P is a sparse matrix with dimension mxm
function sparseMatrixMultiplyVector(B,a) {
	
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
			t[i] += a[d[k]];
		}
		//t[i]+=temp;
	}

	return t;
}

function generateIdentityMatrix(n){
	var identity_matrix = new Array(n);
	for (var i = 0; i < n; i++) {
       		identity_matrix[i] = new Array(n);
		for (var j = 0; j < n; j++) {
			if(i==j){
				identity_matrix[i][j] = 1;
			}
			else
			{
				identity_matrix[i][j] = 0;
			}
		}
	}
	return identity_matrix;
}

//commitment function
function com(){
	var param;
	if(arguments.length != 1){
		param = (arguments[0]+arguments[1]);
		return IDscheme.sha256(param);
	}else{
		param = arguments[0];
 		return IDscheme.sha256(param);
	}
}

function keyGeneration(n, m, q) {
	glob_m = m;
	glob_n = n;
	glob_q = q;
	var amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
	//var p_matrix = IDscheme.initMatrixRandom(glob_m,glob_m,2);  // binary m*m P_sigma
	var p_matrix = generateIdentityMatrix(glob_m);
	var x;
	var ss = new Array(m); //temp for x 
	

	//print(p_matrix);

	for (var i = 0; i < (m/2); i++) {
		ss[i] = 1;
	}

	for (var i = m/2; i < m; i++) {
		ss[i] = 0;
	}

	var alpha = Math.floor(Math.random() * glob_q);
	glob_alpha = alpha;

		
	x = IDscheme.shuffle(ss);

	// var a_transpose = IDscheme.transpose(amatrix);
	
	var y = IDscheme.vectorMultiplyMatrix(x, amatrix);
	//print("y length:");
	//print(y.length);

	// WARN: glob_q instead of glob_m is used.
	sigma = Math.floor(Math.random() * (glob_q-1)); //glob_m-1
	glob_sigma = sigma;

	/*
	var psigma_matrix =  new Array(glob_m);
	psigma_matrix = IDscheme.knuth_shuffle(p_matrix, sigma); 
	var psigma_matrix = generateIdentityMatrix(glob_m);
	//print(psigma_matrix);
	p_sigma_matrix = psigma_matrix; // global P matrix
	*/
	p_sigma_matrix = p_matrix;

	A_matrix = amatrix;
	x_vector = x; // sk
	y_Ax = y; // pk

}

// Prover: p1()
function p1(){ // A,y,x
	var amatrix = A_matrix;
	//p_matrix is a binary matrix P^mxm
	var x,sigma;
	var u = new Array(glob_m);

	x = x_vector; //print(x); //print(p_matrix);

	for (var i = 0; i < glob_m; i++) { // u = Z_q^m
		u[i] = IDscheme.randInt(glob_q);
	}

	u_vector = u;
	hash_u = com(u);

	var alphax = IDscheme.scalarMultiplyVector(glob_alpha,x);
	var u_alphax = IDscheme.addVectors(u,alphax);

//	print(p_sigma_matrix);
//	print(u_alphax);
	//var beta = IDscheme.vectorMultiplyMatrix(u_alphax, p_sigma_matrix); 
	var beta = sparseMatrixMultiplyVector(p_sigma_matrix,u_alphax);
	glob_beta = beta;
	//var c1 = IDscheme.vectorMultiplyMatrix(x, p_sigma_matrix); 
	c1 = sparseMatrixMultiplyVector(p_sigma_matrix,x);
	//print(z);

	//glob_z = z;

	// var a_transpose = IDscheme.transpose(amatrix);
	var au = IDscheme.vectorMultiplyMatrix(u, amatrix); //Au
	//print(au); 

	var au_x = IDscheme.addVectorsWithPadding(x, au); //Au
	c3 = au_x;
	
	c2 = com(c1,au); 
	//print("Compute C2:");
	//print(c2);
	
/*
	var resp = [];
	resp[0] = c1;
	resp[1] = c2;
	resp[2] = c3;
	return resp
*/	
}

function v1(){ //params
	//var resp = params;
	var amatrix = A_matrix;
	var pmatrix = p_sigma_matrix;
	var y = y_Ax;
	var beta = glob_beta;
	var alpha = glob_alpha;
	
	function to_int(a){
		var t = new Array(a.length)
		for(var i = 0; i<a.length;i++){
			t[i] = parseInt((-1)*a[i].toFixed(0));
		}
		return t;
	}

	var pm_matrix = IDscheme.matrix_invert(pmatrix);
	var p_matrix = new Array(glob_m);
		for (var i =0; i<glob_m; i++){
			p_matrix[i] = to_int(pm_matrix[i]);
	}

	//print("inverted:");
	//print(pm_matrix);

	var temp_a = IDscheme.transpose(amatrix);
	var ap_sigma = IDscheme.multiply(temp_a,pm_matrix); //A.P_sigma
	var ap_transpose = IDscheme.transpose(ap_sigma);
	var op1 = IDscheme.vectorMultiplyMatrix(beta, ap_transpose);
	var op2 = IDscheme.scalarMultiplyVector(alpha,y);
	var c4 = IDscheme.vectorSubtract(op1,op2);

	var c5 = IDscheme.vectorSubtractWithPadding(c3,c4);
	//print("C5: ");
	//print(c5);

	var comp1 = com(c1,c4);
	
	print("Comp c2:");
	print(comp1);
	print("c2:");
	print(c2);
	

	if( c2 == comp1){ 
			print("Success!");
		}else{
			print("Failed!");
	}

	var temp1 = IDscheme.scalarMultiplyVector(alpha,c1);
	var op1 = sparseMatrixMultiplyVector(pm_matrix, temp1);
	var op2 = sparseMatrixMultiplyVector(pm_matrix, beta);

	//var temp2 = IDscheme.vectorSubtract(beta, temp1);
	//var c6 = sparseMatrixMultiplyVector(p_matrix, temp2);
	var c6 = IDscheme.vectorSubtract(op2, op1); // computed vector u

	c7 = com(c6);
	/*
	print("A matrix");
	print(amatrix);
	print("Psigma matrix:");
	print(pmatrix);
	*/
}

function  p2() { //c7
	print("c7: "+c7);
	print("hash u :"+hash_u);

	if( c7 == hash_u){ 
			print("Success!");
		}else{
			print("Failed!");
	}
}


//===============================================================================
//------------------------------------------- start --------------------------------------------------
var n = 512, 
	q = 257, 
	m = 2048;

function testidscheme() {
	print("CLRS (Cayrel et al.) ID Scheme:");
	print("Parameters Set:");
	print("n = " + n);
	print("m = " + m);
	print("q = " + q);
	var t0 = new Date().getTime();
	keyGeneration(n, m, q);
	var t1 = new Date().getTime();

	print("Prover: compute commitments c1, c2:");
	var t2 = new Date().getTime();
	p1();
	v1();
	var t3 = new Date().getTime();
	var t4 = new Date().getTime();
	p2();
	//v3();

/*
	print("Time required by functions in ms:");
	print("Key Generation: " + (t1-t0) + " milliseconds");
	print("Prover (Computations of commitments): " + (t3-t2) + " milliseconds");
	print("Prover (Revealing some parameters): " + (t5-t4) + " milliseconds");
	print("Verifier (Checking commitments' correctness): " + (t7-t6) + " milliseconds");

*/

}
//***********************************************************
function print(message) {
	//WScript.Echo(message);
	console.log(message);
}
testidscheme();
