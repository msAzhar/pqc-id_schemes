// LWE-based idscheme

var IDscheme = require('../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var A_matrix;
var glob_y,
	glob_w;
var glob_tilde_w,
	glob_tilde_y;
var glob_m, 		
	glob_q,
	glob_n;
var c1,
	c2,
	c3;

//--------------------------------------------------------------------------------------

function keyGeneration(n, m, q) {
	var amatrix = IDscheme.initMatrixRandom(m, n, q); //pk
	glob_m = m;
	glob_n = n;
	glob_q = q;
	
	var tilde_w = [];

	for (var i = 0; i < glob_m; i++) { 
		tilde_w[i] = IDscheme.randInt(2);
	}
	
	// w = A.w_tilde mod q
	var aw = IDscheme.vectorMultiplyMatrix(tilde_w, amatrix);
	//	print(aw);
	var w = IDscheme.modVector(aw, glob_q);
	//	print("W:");
	//	print(w);
	
	A_matrix = amatrix;
	glob_w = w;
	glob_tilde_w = tilde_w;
	// print(tilde_w);
}

// Prover: p1()
function p1(){ 
	var amatrix = A_matrix;
	var tilde_y = [];
	
	for (var i = 0; i < glob_m; i++) { 
		tilde_y[i] = IDscheme.randInt(5*glob_m-1);
	}
	glob_tilde_y = tilde_y;
	//print(glob_tilde_y);

	var ay = IDscheme.vectorMultiplyMatrix(tilde_y, amatrix);
	//	print(ay);
	var y = IDscheme.modVector(ay, glob_q);

	glob_y = y;
	//print(y);
}

// Verifier: v1()
function v1(){
	var randomM = Math.floor(Math.random() * (+10 - +1)) + +1; 
	if(randomM>=5){
		var random = 1;
	}else{
		var random = 0;
	}
	return random;
}

// Prover: p2()
function p2(ch){
	var resp;
	var yw = IDscheme.addVectors(glob_tilde_y,glob_tilde_w);
	//print(yw);
	var refuse;
	for(var i=0;i<yw.length;i++){
		if (yw[i]<1 && yw[i]>(5*glob_m-1)){
			refuse = 1; // z perpendicular; 0 - refuse
		}else{
			refuse = 0;
		}
	}
	//print("Refuse: "+refuse);
	//print(ch);
	if(ch==1 && refuse==1){
		resp = 0;	
	}else{
		resp = [];
		resp[0] = glob_tilde_y;
		resp[1] = IDscheme.scalarMultiplyVector(ch, glob_w);
		// print("Resp:");
		// print(resp[1]);
	}
	
	return resp;
}

// Verifier: v2()
function v2(ch,params){
	var d;
	function norm(arr){ //compute norm
		var elements = arr.length;
		var total = 0;
		for(var i=0; i<elements; i++){
			  total += Math.pow(arr[i],2);
		}
		return Math.sqrt(total);
	}

	function compare(arr1,arr2){ //compare function
		for(var i=0; i<glob_m; i++){
			if(arr1[i]!=arr2[i])
				return false;
		}
		return true;
	}

	var condition = 5*Math.pow(glob_m,1.5);
	var norm_z = norm(params[0]);
	//print("Norm: " +norm_z);
	var z = params[0];
	amatrix = A_matrix;	
	//print(z.length);
	//print(amatrix.length);
	var a_transpose = IDscheme.transpose(amatrix);
	var a_transpose = IDscheme.transpose(amatrix);
	var comp1 = IDscheme.vectorMultiplyMatrix(z, amatrix);
	var az = IDscheme.modVector(comp1, glob_q);
	var comp2 = IDscheme.addVectors(params[1], glob_y);
	//print("Az mod q:");
	//print(az);
	//print("cw + y:");
	//print(comp2);

	if( norm_z<=condition && compare(az,comp2)){ 
			print("Success!");
			d=1;
	}else{
			print("Failed!");
			d=0;
	}



}

//===============================================================================
//------------------------------------------- start --------------------------------------------------
var n = 608, 
	q = 1024, 
	m = 960;


function testidscheme() {
	print("Lyubashevsky's ID Scheme:");
	print("Parameters Set:");
	print("n = " + n);
	print("m = " + m);
	print("q = " + q);

	var t0 = new Date().getTime();
	keyGeneration(n, m, q);
	var t1 = new Date().getTime();


	print("Prover: computes some parameters.");
	var t2 = new Date().getTime();
	p1();
	var t3 = new Date().getTime();

	var ch;
	ch = v1();

	print("Verifier: sends a challenge: " + ch);
	print("Prover: if challenge is 1, prover produces message of failure, otherwise, it reveals some parameters to the Verifier.");
	print("Verifier: checks correctness of computations from information revealed by prover and accepts in case of success. ");

	//var t4 = new Date().getTime();
	var response = p2(ch);
	//print(response);
	if(response==0){
		print("Failed!");
	}else{
		var t5 = new Date().getTime();
		v2(ch, response);
		var t6 = new Date().getTime();	
	}
	

	print("Time required by functions in ms:");
	print("Key Generation: " + (t1-t0) + " milliseconds");
	print("Prover (Computations of commitments): " + (t3-t2) + " milliseconds");
	//print("Prover (Revealing some parameters): " + (t5-t4) + " milliseconds");
	print("Verifier (Checking commitments' correctness): " + (t6-t5) + " milliseconds");
}
//***********************************************************
function print(message) {
	//WScript.Echo(message);
	console.log(message);
}
testidscheme();
