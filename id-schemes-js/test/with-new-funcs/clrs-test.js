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
var glob_r0;
var glob_r1;
var glob_z;
var c1,
	c2,
	c3;
var p_sigma_matrix;
var u_vector;
var glob_alpha;

//-------------------------------------------------------------------------------------------


// !! functions defined in this file will be moved to the IDscheme module !!

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

// random binary P_sigma matrix generation
function initRandomSquareMatrix(x) {
	var matrix = new Array(x);
	for (var i = 0; i < x; i++) {
       		matrix[i] = new Array(x);
			for (var j = 0; j < x/16; j++) {
				matrix[i][j] = 1;
			}
			for (var j = x/16; j < x; j++) {
				matrix[i][j] = 0;
			}
	}
	return matrix;
}

//returns locations of 1s in a matrix
function indexes(A){
	var m = A.length;
	var t = Array(m)
	for (var i = 0; i < m; i++) {
		//get array of locations
		var d = _indexes(A[i]);
		t[i]=d;
	}
	return t;
}

//commitment function
function com(){
	var param;
	if(arguments.length != 1){
		//param = IDscheme.knuth_shuffle(arguments[1]);
		param = (arguments[0]+arguments[1]);
		return IDscheme.sha256(param);
	}else{
		param = arguments[0];
 		return IDscheme.sha256(param);
	}
}

function keyGeneration(n, m, q) {
	var amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
	var x;
	var ss = new Array(m); //temp for x 
	glob_m = m;
	glob_n = n;
	glob_q = q;

	for (var i = 0; i < (m/2); i++) {
		ss[i] = 1;
	}

	for (var i = m/2; i < m; i++) {
		ss[i] = 0;
	}
		
	x = IDscheme.shuffle(ss);

	//var a_transpose = IDscheme.transpose(amatrix);
	var ts1 = new Date().getTime();
	var y = IDscheme.vectorMultiplyMatrix(x, amatrix);
	var ts2 = new Date().getTime();
	//print(y);
	//print(ts2-ts1);

	A_matrix = amatrix;
	x_vector = x; // sk
	y_Ax = y; // pk

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
			v[j] += a[i] * Browi[j];
		}
	}
	return v;
}

// Prover: p1()
function p1(){ // A,y,x
	var amatrix = A_matrix;
	//p_matrix is a binary matrix P^mxm
	//var p_matrix = IDscheme.initMatrixRandom(glob_m,glob_m,2); 
	var pm = initRandomSquareMatrix(glob_m);
	//print(pm);
	var p_matrix = new Array(glob_m);
	for(var i= 0; i<p_matrix.length; i++){
		p_matrix[i] = IDscheme.shuffle(pm[i]);
	}
	/*
	print("P matrix:");
	print(p_matrix[5]);
	print("HW:");
	print(IDscheme.hw(p_matrix[5]));
	*/
	/*
	for(var i=0; i<p_matrix.length;i++){
		print(IDscheme.hw(p_matrix[i]));
	}
	*/
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

	for (var i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
		r0[i] = IDscheme.randInt(2);
		r1[i] = IDscheme.randInt(2);
	}

	glob_r0 = r0;
	glob_r1 = r1;
	// WARN: glob_q instead of glob_m is used.
	sigma = Math.floor(Math.random() * (glob_q-1)); //glob_m-1
	glob_sigma = sigma;



	var psigma_matrix =  p_matrix;
	//psigma_matrix = IDscheme.knuth_shuffle(p_matrix, sigma); 
//	print(psigma_matrix);
	//p_sigma_matrix = psigma_matrix; // global P matrix

	p_sigma_matrix = p_matrix;

/*
	print("P_sigma: ");
	for(var i =0;i<glob_m; i++){
		for(var j=0; j<glob_m; j++){
			print(p_sigma_matrix);
		}
	}
*/
//	var z = IDscheme.vectorMultiplyMatrix(x, psigma_matrix); 
	//print(z);

	var o = IDscheme.transpose(psigma_matrix);
	var p = indexes(o);
	//print(p);
	/*for(var i=0;i<p.length;i++){
		for(var j=0;j<p[i].length;j++){
			print(psigma_matrix[p[i][j]]);
		}
	}
	*/
	var ts1 = new Date().getTime();
	//var z = vectorMultiplyMatrix(x, psigma_matrix);
	var z = sparseMatrixMultiplyVector(o,x);
	//var z = sparseMatrixMultiplyVector3(o,x,p);
	var ts2 = new Date().getTime();
	//print(ts2-ts1);
	glob_z = z;

	
	var au = IDscheme.vectorMultiplyMatrix(u, amatrix); //Au
	//print(au); 
	var pu = IDscheme.vectorMultiplyMatrix(u, psigma_matrix); //P_sigma.u
	//print(pu);

	
	c1 = com(sigma+au,r0); // c0 
	print("Keygen C1:");
	print(c1);
	c2 = com(z+pu,r1);
	print("Keygen C2:");
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
	//print(u_alphax);
	betha = IDscheme.vectorMultiplyMatrix(u_alphax, p_sigma_matrix);
	//print("Betha:");
	//print(betha);
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

	function to_int(a){
	var t = new Array(a.length)
	for(var i = 0; i<a.length;i++){
		t[i] = parseInt((-1)*a[i].toFixed(0));
	}
	return t;
	}

	if(ch == 1){
		var comp1, comp2; // computations
		var sigma = resp[0];
		var r0 = resp[1];
		var alpha = glob_alpha;
		var amatrix = A_matrix;
		var pmatrix = p_sigma_matrix;
		var y = y_Ax;
		//print(pmatrix);
		var pm_matrix = IDscheme.matrix_invert(pmatrix);
		//	print(pm_matrix);
		var p_matrix = new Array(glob_m);
		for (var i =0; i<glob_m; i++){
			p_matrix[i] = to_int(pm_matrix[i]);
		}
		//print(p_matrix);

		/*
		print("A matrix");
		print(amatrix);
		print("Psigma matrix:");
		print(pmatrix);
		*/
		var temp_a = IDscheme.transpose(amatrix);
		var ap_sigma = IDscheme.multiply(temp_a,p_matrix); //A.P_sigma
		var ap_transpose = IDscheme.transpose(ap_sigma);
		var op1 = IDscheme.vectorMultiplyMatrix(betha, ap_transpose);
		var op2 = IDscheme.scalarMultiplyVector(alpha,y);
		var op3 = IDscheme.vectorSubtract(op1,op2);
		comp1 = com(glob_sigma+op3,r0)

		//print("computation 1:");
		//print(comp1);

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
	keyGeneration(n, m, q);
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
