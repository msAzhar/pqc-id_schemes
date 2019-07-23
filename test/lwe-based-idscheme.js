// LWE-based idscheme

var IDscheme = require('../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var A_matrix;
var b_vector;
var glob_p;
var glob_sigma;
var glob_s,
	glob_e;
var glob_m, 		
	glob_q,
	glob_n;
var c1,
	c2,
	c3;

//--------------------------------------------------------------------------------------

//commitment function
function com(){
	var param;
	param = (arguments[0]+arguments[1]);
	return IDscheme.sha256(param);
}


function keyGeneration(n, m, q) {
	//A, m*n
	var ee = // e \in F_q^n // n=608
	[-7, -9,1,2,-5,9,2,1,-2,4,-3,0,-5,0,7,-2,-4,-3,0,-6,0,1,6,5,2,6,-10,7,-2,2,0,-8,5,3,0,9,5,0,-4,-3,3,2,0,4,8,1,-4,-16,-8,8,10,-2,-6,0,2,12,-8,-2,9,6,-6,-1,-6,2,1,-9,1,4,8,4,4,-1,4,2,8,4,-1,3,11,-12,1,3,5,-15,2,3,9,-2,2,6,-3,-5,-6,1,-5,1,-3,1,11,-1,5,2,1,-2,-2,2,-1,3,-6,3,2,4,-2,16,2,3,8,6,1,11,-12,-7,-2,2,-10,-7,7,10,1,-8,-2,8,6,3,-1,6,-5,0,3,4,2,-4,-2,7,-2,4,-7,-1,-3,8,4,0,8,-3,-11,3,10,10,-7,-2,-9,1,-3,-3,-7,3,2,-8,-11,-1,-3,-4,-3,3,-7,7,6,-2,-8,-2,6,4,-4,3,0,7,-2,-4,-2,-15,2,-6,6,0,0,6,-7,2,-6,-4,3,5,3,9,-9,5,6,-8,-7,4,4,5,0,0,-3,-7,-4,-7,-6,-9,-7,2,5,-6,0,-1,10,6,-6,-7,0,2,7,3,-3,10,-3,2,-4,-10,3,5,-5,2,-7,-3,-5,-4,0,-7,-2,3,-13,4,-6,-7,0,3,1,5,16,-4,-3,-2,2,0,14,0,-5,0,3,1,-1,-14,2,-5,0,5,0,-1,4,-1,-6,0,2,6,7,-3,-9,1,-9,8,-8,2,1,7,14,-2,-5,-2,3,-6,-2,-6,4,7,2,-7,6,13,10,5,-13,-12,0,0,-1,4,2,2,-2,-12,6,-2,0,2,6,-2,-5,1,-2,-3,-7,-3,-6,0,2,11,6,-2,-6,9,-1,-4,14,8,-8,-1,6,1,2,-1,-7,3,-13,2,1,9,7,-1,-2,-3,-1,10,5,5,2,-8,-4,11,16,2,-2,-5,-3,5,3,0,2,3,2,-4,-3,8,-5,0,2,-2,7,5,-6,1,-3,2,-8,1,6,4,12,-1,1,-11,3,3,5,-6,1,-5,-8,-4,-7,-9,0,-5,1,4,3,8,-2,8,3,11,-5,-9,6,4,0,7,1,1,-10,0,5,-6,7,5,-4,-1,4,3,-6,1,-7,2,-7,3,4,11,6,6,2,-6,6,0,2,-1,3,4,4,6,-7,3,3,3,-10,6,-5,7,-5,-14,7,2,-8,12,3,-10,8,15,1,0,1,2,3,-2,1,-2,7,-16,5,1,9,-3,-4,-5,0,8,4,1,-11,-5,-5,-10,4,8,-5,2,9,-3,2,2,0,-2,3,-1,0,5,-3,4,4,0,1,11,-9,-6,11,6,-10,-1,10,6,-5,-11,1,-2,-2,-6,0,2,-3,10,5,-7,6,3,0,-12,-4,-3,6,3,-2,-2,-10,0,-2,-2,-5,0,9,9,0,-15,10,0,-7,-7,-14,-3,-2,6,1,-3,-5,6,3,4,1,1,5,-3,2,0,13,4,6,6,-13,2,-6,-5,-2,-4,-2,-2,-4,2,5,-6,8,1,-8,1,-4,-3,2,3,-3,1];
	var amatrix = IDscheme.initMatrixRandom(m, n, q); //pk
	glob_m = m;
	glob_n = n;
	glob_q = q;
	// vector s \in F_q^m 
	var s = [];
	var e = [];
	e = ee; //e is sk

	// s is sk
	for (var i = 0; i < glob_m; i++) { 
		s[i] = IDscheme.nextInt(glob_q);
	}
	
	// b = As + e
	var y = IDscheme.encVectorMultiplyMatrix(s, amatrix);
	//print(y);
	var b = IDscheme.addVectors(y,e); //pk
	var p = IDscheme.hw(e); //pk
	/*print("p:");
	print(p);
	print("b:");
	print(b);
	*/
	// pk
	A_matrix = amatrix;
	b_vector = b;
	glob_p = p;

	//sk
	glob_s = s;
	glob_e = e;
}

var glob_r1,glob_r2,glob_r3;
var glob_u, glob_Aus;
var glob_perm1,glob_perm2,glob_perm3; //permutations

// Prover: p1()
function p1(){ // A,s,b
	var amatrix = A_matrix;
	var b,s=[];
	var sigma;
	var u = new Array(glob_m);
	var gamma = new Array(glob_m);
	var r1 = [];
	var r2 = [];
	var r3 = [];
	b = b_vector;
	s=glob_s;
	for (var i = 0; i < glob_m; i++){
		u[i] = IDscheme.nextInt(glob_q);
	}
	glob_u = u;

	for (var i = 0; i < glob_m; i++){
		gamma[i] = IDscheme.nextInt(glob_q);
		if(gamma[i]==0)
			gamma[i]=IDscheme.nextInt(glob_q);
	}

	sigma = Math.floor(Math.random() * (glob_n-1)); //sigma \in S_n
	glob_sigma = sigma;

	var perm1 = [];
	perm1 = IDscheme.knuth_shuffle(gamma, sigma);

	var us = IDscheme.addVectors(u,s);

	var part1 = IDscheme.encVectorMultiplyMatrix(us, amatrix); // A(u+s)
	glob_Aus = part1; //A(u+s)
	var perm2 = IDscheme.knuth_shuffle(part1,sigma);

	var au = IDscheme.encVectorMultiplyMatrix(u, amatrix); // Au
	var au_b = []; // Au+b
	for (var i = 0; i < glob_n; i++){
		au_b[i] = au[i] + b_vector[i];
	}

	var perm3 = IDscheme.knuth_shuffle(au_b,sigma);

	for (var i = 0; i < glob_n; i++) { // r0 = {0,1}^n, r1 = {0,1}^n
		r1[i] = IDscheme.nextInt(2);
		r2[i] = IDscheme.nextInt(2);
		r3[i] = IDscheme.nextInt(2);
	}

	glob_perm1 = perm1; //Gamma Sigma nin
	glob_perm2 = perm2; //A(u+s) nin
	glob_perm3 = perm3; //Au+b nin
	glob_r1 = r1;
	glob_r2 = r2;
	glob_r3 = r3;

	//print(us);

	c1 = com(perm1, r1); // com(Pi_gamma,_sigma;r1)
	c2 = com(perm2, r2); // com(Pi_gamma,_sigma(A(u+s));r2)
	c3 = com(perm3, r3); // com(Pi_gamma,_sigma(Au+b);r3)

	print("c1:" + c1);
	print("c2:" + c2);
	print("c3:" + c3);
}

// Verifier: v1()
function v1(){
	var random =Math.floor(Math.random() * (+4 - +1)) + +1; 
	return random;
}

// Prover: p2()
function p2(ch){
	var resp = [];

	//print("CHallenge is:");
	//print(ch);
	if(ch == 1){
		resp[0] = glob_r1;
		resp[1] = glob_r2;
		resp[2] = glob_Aus; //A(u+s)
		resp[3] = glob_perm1; ////Pi_gamma,_sigma()
	}
	else if(ch == 2){
		var permute_e = IDscheme.knuth_shuffle(glob_e,glob_sigma);

		resp[0] = glob_r2;
		resp[1] = glob_r3;
		resp[2] = glob_perm2; //Pi_gamma,_sigma(A(u+s))
		resp[3] = permute_e; //Pi_gamma,_sigma(e)
	}
	else if(ch == 3){ 
		resp[0] = glob_r1;
		resp[1] = glob_r3;
		resp[2] = glob_perm1; //Pi_gamma,_sigma()
		resp[3] = glob_u;
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
		var comp1, comp2, comp3; // computations

		// compute c1=com(Pi_gamma,_sigma;r1);
		comp1 = com(resp[3],resp[0]); 
		
		// Pi_gamma,_sigma(A(u+s))
		var part1 = IDscheme.knuth_shuffle(resp[2], glob_sigma); 
		// compute c2=com(Pi_gamma,_sigma(A(u+s));r2)
		comp2 = com(part1,resp[1]); 
		/*
		print("C1");
		print(comp2);
		print("C2");
		print(comp2);
		*/
		if( comp1==c1 && comp2==c2){
			print("Success!");
		}else{
			print("Failed!");
		}

	}
	else if(ch == 2){
		var comp1, comp2, comp3; // computations

		// compute c2=com(Pi_gamma,_sigma(A(u+s));r2)
		comp1 = com(resp[2],glob_r2); 
		
		var au_b = []; // Au+b
		for (var i = 0; i < glob_n; i++){
			au_b[i] = resp[2][i] + resp[3][i];
		}
		var part1 = IDscheme.addVectors(resp[2], resp[3]); 
		var part2 = IDscheme.knuth_shuffle(part1, glob_sigma); 
		
		// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
		comp2 = com(part2,resp[1]); 

		comp3 = IDscheme.hw(resp[3]); //HW(Pi_gamma,_sigma(e))

		/*
		print("C2:");
		print(comp1);
		print("C3:");
		print(comp2);
		print(c3);
		*/

		if(comp1==c2 && comp2==c3 && comp3==glob_p){ //check c2 and c3 and hw(e)
			print("Success!");
		}else{
			print("Failed!");
		}
	}
	else if(ch == 3){
		var comp1, comp2, comp3; // computations

		// compute c1=com(Pi_gamma,_sigma;r1);
		comp1 = com(resp[2],resp[0]); 

		//var u_transpose = IDscheme.transpose(u);
		var au = IDscheme.encVectorMultiplyMatrix(resp[3], A_matrix); // Au
		var au_b = []; // Au+b not: A,b pk
		for (var i = 0; i < glob_n; i++){
			au_b[i] = au[i] + b_vector[i];
		}
		
		var p1 = IDscheme.knuth_shuffle(au_b,glob_sigma);
		// compute c3=com(Pi_gamma,_sigma(Au+b);r3)
		comp2 = com(p1,resp[1]);
		/*
		print("C1");
		print(comp1);
		print("C3");
		print(comp2);
		*/
		if( comp1==c1 && comp2==c3){
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
var n = 608, 
	q = 1024, 
	m = 960;


function testidscheme() {
	print("LWE-based ID Scheme:");
	print("Parameters Set:");
	print("n = " + n);
	print("m = " + m);
	print("q = " + q);
	keyGeneration(n, m, q);

	print("Prover: compute commitments c1, c2 and c3:");
	p1();
	var ch;
	ch = v1();

	print("Verifier: sends a challenge: " + ch);
	print("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
	print("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
	
	var response = p2(ch);
	v2(ch,response);

}
//***********************************************************
function print(message) {
	//WScript.Echo(message);
	console.log(message);
}
testidscheme();
