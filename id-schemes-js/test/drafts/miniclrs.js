// CLRS

var IDscheme = require('../../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var A_matrix;
var r_vector;
var x_vector;
var y_Ax;
var glob_m;
var glob_n;
var glob_q;
var glob_sigma;
var glob_r0;
var glob_r1;
var glob_z;
var c1,
	c2,
	c3;
var p_sigma_matrix;
var u_vector;
var glob_alpha;
var p_transpose;

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

function determinant(m){
  var numRow = m.length;
  var numCol = m[0].length;
  var det = 0;
  var row, col;
  var diagLeft, diagRight;

  if (numRow !== numCol) {
    throw new Error("Not a square matrix.")
  }

  if (numRow === 1) {
    return m[0][0];
  } 
  else if (numRow === 2) {
    return m[0][0] * m[1][1] - m[0][1] * m[1][0];
  }

  for (col = 0; col < numCol; col++) {
    diagLeft = m[0][col];
    diagRight = m[0][col];

    for( row=1; row < numRow; row++ ) {
      diagRight *= m[row][(((col + row) % numCol) + numCol) % numCol];
      diagLeft *= m[row][(((col - row) % numCol) + numCol) % numCol];
    }

    det += diagRight - diagLeft;
  }

  return det;
}

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
	

	//get array of locations
	var d = _indexes(a);
	
	for(var k = 0; k < d.length; k++) {
		//print(B[d[k]]);
		t=IDscheme.addVectors(t,B[d[k]])
	}

	return t;
}

//Multiplies a matrix B by a vector a, c = a * B
function vectorMultiplyMatrix(a, B) {
	//var A_x = 1;
	var A_y = a.length;
	var B_x = B[0].length;
	var B_y = B.length;
	
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
				v[j] += a[i] * Browi[j];
		}
	}
	return v;
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
	var amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
	var x = new Array(m);
	//var ss = new Array(m); //temp for x 
	glob_m = m;
	glob_n = n;
	glob_q = q;

	for (var i = 0; i < (m/2); i++) {
		x[i] = 1;
	}

	for (var i = m/2; i < m; i++) {
		x[i] = 0;
	}
		
	//x = IDscheme.shuffle(ss);

	//var a_transpose = IDscheme.transpose(amatrix);
	
	//var y = IDscheme.vectorMultiplyMatrix(x, amatrix);
	var y = sparseVectorMultiplyMatrix(x, amatrix);

	print("vector x (sk):");
	print(x);

	print("matrix A (pk):");
	print(amatrix);

	print("y = Ax (pk):");
	print(y);

	

	A_matrix = amatrix;
	x_vector = x; // sk
	y_Ax = y; // pk

}

// Prover: p1()
function p1(){ // A,y,x
	var amatrix = A_matrix;
	//p_matrix is a binary matrix P^mxm
	//var p_matrix = IDscheme.initMatrixRandom(glob_m,glob_m,2); 
	var x,sigma;
	var u = new Array(glob_m);
	var z = new Array(glob_m);
	var r0 = new Array(glob_n);
	var r1 = new Array(glob_n);
	
	x = x_vector; //print(x); //print(p_matrix);

	for (var i = 0; i < glob_m; i++) { // u = Z_q^m
		u[i] = IDscheme.randInt(glob_q);
	}

	u_vector = u;
	print("\n U vector:");
	print(u);

	for (var i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
		r0[i] = IDscheme.randInt(2);
		r1[i] = IDscheme.randInt(2);
	}

	glob_r0 = r0;
	glob_r1 = r1;
	// WARN: glob_q instead of glob_m is used.
	sigma = Math.floor(Math.random() * (glob_q-1)); //glob_m-1
	glob_sigma = sigma;



	var psigma_matrix22 = [[1,0,1,0],[1,0,0,1],[1,1,0,0],[0,0,1,0]];
	var psigma_matrix = [[1,0,0,0],[0,1,0,0],[0,0,1,0],[0,0,0,1]];

	var det1 = determinant(psigma_matrix);
	var det2 = determinant(psigma_matrix22);
	print("DETERMINANTS:");
	print("det1: " + det1);
	print("det2: " + det2);
	//psigma_matrix = IDscheme.knuth_shuffle(p_matrix, sigma); 
	//print(psigma_matrix);
	p_sigma_matrix = psigma_matrix; // global P matrix
	print("\n P_sigma matrix:");
	print(p_sigma_matrix);
/*
	print("P_sigma: ");
	for(var i =0;i<glob_m; i++){
		for(var j=0; j<glob_m; j++){
			print(p_sigma_matrix);
		}
	}
*/
	//print(x.length);
	//print(psigma_matrix.length);
	p_transpose=IDscheme.transpose(psigma_matrix);
	var z = vectorMultiplyMatrix(x, p_transpose); 
	//print(z);

	print("\n z = P_sigma.x");
	print(z);
	glob_z = z;

	
	var au = IDscheme.vectorMultiplyMatrix(u, amatrix); //Au
	print("\n Au:");
	print(au); 
	var pu = IDscheme.vectorMultiplyMatrix(u, p_transpose); //P_sigma.u
	//print(pu);

	
	c1 = com(sigma+au,r0); // c0 
	print("** sigma+au:");
	print(sigma+au);
	print("\nCommitment C1:");
	print(c1);
	c2 = com(z+pu,r1);
	print("Commitment C2:");
	print(c2);
}

// Verifier: v1()
function v1(){
	var alpha = Math.floor(Math.random() * glob_q);; 
	//print(random);
	return alpha;
}

// Prover: p2()
function p2(alpha){
	var betha;
	glob_alpha = alpha;
	var u_alphax; // u + alpha.x
	var alphax_vector = IDscheme.scalarMultiplyVector(alpha,x_vector);
	u_alphax = IDscheme.addVectors(u_vector,alphax_vector); // u + alpha.x
	print("\n u + alpha.x:");
	print(u_alphax);
	betha = IDscheme.vectorMultiplyMatrix(u_alphax, p_transpose);
	print("Beta:");
	print(betha);
	return betha;
}

// Verifier: v2()
function v2(){
	var random = Math.floor(Math.random() * (+3 - +1)) + +1; 
	//print(random);
	return random;
}

// Prover: p3()
function p3(c){
	var ch = c;
	var resp = [];

	//print("CHallenge is:");
	//print(ch);
	if(ch == 1){
		resp[0] = glob_sigma;
		resp[1] = glob_r0;
	}
	else if(ch == 2){
		resp[0] = glob_z;
		resp[1] = glob_r1;
	}
	else{
		print("Error!");
	}
	
	return resp;

}

// Verifier: v3()
function v3(c, params, betha_){
	var ch = c;
	var resp = params;
	var betha = betha_;

	function isinX(a,X){ //include_check function
		return X.includes(a);
	}

	function check_z(z){
		for(var i = 0; i<z.length; i++){
			result = isinX(z[i], [0,1]);
			if(result==false)
				return false;
		}
		return true;
	}

	function check_z2(z){
		wt=0;
		for(var i = 0; i<z.length; i++){
			if(z[i] != 0)
				wt+=1;
		}
		if(wt>=glob_m/2)
			return true;
		else
			return false;
	}

	if(ch == 1){
		var comp1, comp2; // computations
		var sigma = resp[0];
		var r0 = resp[1];
		var alpha = glob_alpha;
		var amatrix = A_matrix;
		var pmatrix = p_sigma_matrix;
		var y = y_Ax;

		print("Verifier: ");
		print("P_sigma MATRIX:");
		print(pmatrix);
		var p_matrix = IDscheme.matrix_invert(pmatrix);
		print("P_MATRIX_INVERSED:");
		print(p_matrix);
		/*
		print("A matrix");
		print(amatrix);
		print("Psigma matrix:");
		print(pmatrix);
		*/
		var temp_a = IDscheme.transpose(amatrix);
		var ap_sigma = IDscheme.multiply(temp_a,p_matrix); //A.(P_sigma)^-1
		var ap_transpose = IDscheme.transpose(ap_sigma);
		var op1 = IDscheme.vectorMultiplyMatrix(betha, ap_transpose);
		var op2 = IDscheme.scalarMultiplyVector(alpha,y);
		var op3 = IDscheme.vectorSubtract(op1,op2);

		print("\n A.(P_sigma^(-1)).beta-alpha.y (Au):");
		print(op3);

		print("** glob_sigma+Au:");
		print(glob_sigma+op3);
		comp1 = com(glob_sigma+op3,r0)

		print("computation of commitment 1:");
		print(comp1);

		var sM = [];
		for (var i = 0; i < glob_q; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
			sM[i] = i;
		}

		comp2 = isinX(sigma, sM);

		 if( c1 == comp1 && comp2){ 
			print("Success!");
		}else{
			print("Failed!");
		}
	
	}
	else if(ch == 2){
		var comp1, comp2; // computations
		var z = resp[0];
		var r1 = resp[1];
		var alpha = glob_alpha;
		var op1 = IDscheme.scalarMultiplyVector(alpha,z);
		var op2 = IDscheme.vectorSubtract(betha,op1);
		var check1_z = check_z(z);// ?z\in[0,1]
		var check2_z = check_z2(z); 
		//print(check1_z); // 0 ve 1 lerden mi?
		//print(check2_z);
		comp1 = com(z+op2,r1);
		print("computation 1:");
		print(comp1);

		if( c2 == comp1 && check2_z){ // TODO: ikinci sharti ekle
			print("Success!");
		}else{
			print("Failed!");
		}

	}else{
		print("Error!");
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
	keyGeneration(2, 4, q);
	var t1 = new Date().getTime();

	print("Prover: compute commitments c1, c2:");
	var t2 = new Date().getTime();
	p1();
	var t3 = new Date().getTime();
	var alfa;
	alfa = v1();

	print("Verifier: computes and sends alpha : " + alfa);
	
	var t4 = new Date().getTime();
	var resp_p2 = p2(alfa);
	var t5 = new Date().getTime();

	print("Prover: computes and sends betha.(betha = P_sigma(u+alpha.x))");
	
	var b;
	b = v2(); // 0 veya 1 
	print("Verifier: sends a challenge: " + b);
	var resp_p3 = p3(b);
	print("Prover: reveals some parameters depending on challenge.(Current challenge is: " + b + ")");
	print("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
	var t6 = new Date().getTime();
	var resp = v3(b,resp_p3, resp_p2); // ch, resp_p3, betha
	var t7 = new Date().getTime();

	print("Time required by functions in ms:");
	print("Key Generation: " + (t1-t0) + " milliseconds");
	print("Prover (Computations of commitments): " + (t3-t2) + " milliseconds");
	print("Prover (Revealing some parameters): " + (t5-t4) + " milliseconds");
	print("Verifier (Checking commitments' correctness): " + (t7-t6) + " milliseconds");
}
//***********************************************************
function print(message) {
	//WScript.Echo(message);
	console.log(message);
}
testidscheme();
