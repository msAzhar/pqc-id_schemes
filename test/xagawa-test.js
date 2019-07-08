// Xagawa

var IDscheme = require('../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var A_matrix;
var r_vector;
var x_vector;
var y_Ax;
var c1,
	c2,
	c3;

//--------------------------------------------------------------------------------------

//commitment function
function com(){
	var param;
	if(arguments.length != 1){
		param = IDscheme.knuth_shuffle(arguments[1]);
		return IDscheme.sha256(param);
	}else{
		param = arguments[0];
 		return IDscheme.sha256(param);
	}
}

function keyGeneration(n, m, q) {
	var amatrix = IDscheme.initMatrixRandom(m, n, q); // A, n*m
	var x;
	var r = new Array(m);	
	var ss = new Array(m);
	glob_m = m;

	for (var i = 0; i < (m/2); i++) {
		ss[i] = 1;
	}

	for (var i = m/2; i < m; i++) {
		ss[i] = 0;
	}
	
	x = IDscheme.shuffle(ss);
	//print(x);
	
	var x_transpose = x;
	var y = IDscheme.encVectorMultiplyMatrix(x_transpose, amatrix);
	
	for (var i = 0; i < m; i++) {
		r[i] = IDscheme.nextInt(q);
	}
	
	A_matrix = amatrix;
	r_vector = r;
	x_vector = x; // sk
	y_Ax = y; // pk

}

// Prover: p1()
function p1(){ // A,y,x
	var amatrix = A_matrix;
	var r,
		x;
	var z = new Array(glob_m);
	r = r_vector;
	x = x_vector;

	for (var i = 0; i < glob_m; i++){
		z[i] = x[i] + r[i];
	}
	var rr = IDscheme.transpose(r);
	var ar = IDscheme.encVectorMultiplyMatrix(r, amatrix);
	var pr;

	pr = IDscheme.knuth_shuffle(r); // c2
	//print(pr);
	
	c1 = com("pi",ar);
	c2 = com(pr); // com(pi(r))
	c3 = com(IDscheme.knuth_shuffle(z)); // com(pi(x+r))

}

// Verifier: v1()
function v1(){
	var random =Math.floor(Math.random() * (+4 - +1)) + +1; 
	return random;
}

// Prover: p2()
function p2(c){
	var ch = c;
	var s,t;
	var fi;
	var psi,v;
	var r, x;
	var u = new Array();
	var resp = [];

	print("CHallenge is:");
	print(ch);
	if(ch == 1){
		r = r_vector;
		s = IDscheme.knuth_shuffle(x_vector);
		t = IDscheme.knuth_shuffle(r);
		resp[0] = c2;
		resp[1] = c3;
		resp[2] = s;
		resp[3] = t; //t = pi(r) //print(t);
	}
	else if(ch == 2){
		r = r_vector;
		x = x_vector;

		for (var i = 0; i < glob_m; i++){
			u[i] = x[i] + r[i];
		}

		//u = c3; // u=(x+r)
		resp[0] = c1;
		resp[1] = c3;
		resp[2] = fi;
		resp[3] = u; //u
	}
	else if(ch == 3){ 
		//v = r_vector; // v=r
		resp[0] = c1;
		resp[1] = c2;
		resp[2] = psi;
		resp[3] = r_vector;
	}else{
		print("Error!");
	}
	
	return resp;
}

// Verifier: v2()
function v2(c, params){
	var ch = c;
	var resp = params;

	function comp(arr1,arr2){ //compare function
		for(var i=0; i<glob_m; i++){
			if(arr1[i]!=arr2[i])
				return false;
		}
		return true;
	}

	function cikar(arr1,arr2){// Au, y
		var arr = [];
		for(var i = 0; i<n;i++){
			arr[i] = arr1[i] - arr2[i];
		}
		return arr;
	}

	if(ch == 1){
		var comp1, comp2,comp3; // computations
		var s_ve_t =  new Array(glob_m);
		for (i = 0; i<glob_m; i ++){
			s_ve_t[i] = resp[2][i] + resp[3][i]; // s+t
		}

		comp1 = com(s_ve_t); // s+t
		comp2 = com(resp[3]); // com(t); t = pi(r)
	
		//print(resp[0]); //c2
		print("com(t)");
		print(comp2);

		if( resp[0] == comp2 && resp[1]==comp1){//comp(resp[1], comp1)){ // c2==com(t) && c3==(s+t)
			print("Success!");
		}else{
			print("Failed!");
		}

	}
	else if(ch == 2){
		var comp1, comp2,comp3; // computations
		var temp,y;
		var amatrix = new Array();
		amatrix = A_matrix;
		temp = resp[3]; // u=x+r (c3)
		y = y_Ax;
		var tt = IDscheme.transpose(temp);
		var d = IDscheme.decVectorMultiplyMatrix(temp, amatrix);
		var t = cikar(d,y);
		
		comp1 = com(resp[2],t);// com(fi,Au-y)
		comp2 = com(IDscheme.knuth_shuffle(resp[3])); //com(pi(u))
		
		if( resp[0] == comp1 && resp[1] == comp2){ // c1==com(fi,Au-y) && c3==com(fi(u))
			print("Success!");
		}else{
			print("Failed!");
		}
	}
	else if(ch == 3){
		var comp1, comp2,comp3; // computations
		var amatrix =  new Array();
		amatrix = A_matrix;
		var temp = resp[3]; // v= r
		var tt = IDscheme.transpose(temp);
		var av = IDscheme.encVectorMultiplyMatrix(temp, amatrix); // Av
		
		comp1 = com(resp[2],av); //com(psi,Av)
		comp2 = com(IDscheme.knuth_shuffle(resp[3])); // com(psi(v))
		print("Com1:");
		print(comp1);
		print("C1:");
		print(resp[0]);

		if( resp[0]==comp1 && resp[1]== comp2){ // c1==com(psi, Av) && c2==com(psi(v) 
					print("Success!");
		}else{
			print("Failed!");
		}
	}
	else{
		print("Error!");
	}
}

//===============================================================================
//------------------------------------------- start --------------------------------------------------
var n = 512, 
	q = 257, 
	m = 2048;


function testidscheme() {
	print("Test ID Scheme:");
	keyGeneration(n, m, q);
	p1();
	var ch;
	ch = v1();
	var response = p2(ch);
	v2(ch,response);

}
//***********************************************************
function print(message) {
	//WScript.Echo(message);
	console.log(message);
}
testidscheme();