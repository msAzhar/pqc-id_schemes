// Xagawa NTRU-based

var IDscheme = require('../idscheme.js');
//-----------------------------------Global variables-----------------------------------
var a_h;
var a_t;
var y;
var sk_xh;
var sk_xt;
var glob_n;
var glob_q;
var glob_sigma;

var glob_rh;
var glob_rt;
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

function keyGeneration(n, q) {
	var ah = [];
	var at = [];
	var xh = []; //sk
	var xt = []; //sk
	
	glob_n = n;
	glob_q = q;

	for (var i = 0; i < n; i++) {
		ah[i] = IDscheme.nextInt(q);
	}

	for (var i = 0; i < n; i++) {
		at[i] = IDscheme.nextInt(q);
	}


	for (var i = 0; i < n; i++) {
		xh[i] = IDscheme.nextInt(q);
	}

	for (var i = 0; i < n; i++) {
		xt[i] = IDscheme.nextInt(q);
	}

	var part1 = IDscheme.vectorMultiply(ah,xh);
	var part2 = IDscheme.vectorMultiply(at,xt);

	y = IDscheme.addVectors(part1,part2);
	
	a_h = ah;
	a_t = at;

	sk_xh = xh;
	sk_xt = xt;
	/*print("Key Generation:");
	print("Private Key: x_h: "+xh+" and x_t: "+xt);
	print("Public Key: a_h: "+ah+", a_t: "+at+" and y: "+y);
	*/
}

// Prover: p1()
function p1(){ // 
	var rh = [];
	var rt = [];
	var sigma;

	for (var i = 0; i < glob_n; i++){
		rh[i] = IDscheme.nextInt(glob_q);
	}

	for (var i = 0; i < glob_n; i++){
		rt[i] = IDscheme.nextInt(glob_q);
	}

	glob_rh = rh;
	glob_rt = rt;

	sigma = Math.floor(Math.random() * (4)); //glob_q-1
	glob_sigma = sigma;

	var part1 = IDscheme.knuth_shuffle(rh, sigma); 
	var part2 = IDscheme.knuth_shuffle(rt, sigma); 

	var prt1 = IDscheme.addVectors(rh,sk_xh);
	var prt2 = IDscheme.addVectors(rt,sk_xt);
	
	c1 = com("pih","pit",y);
	c2 = com(part1,part2); // com(pi(r_h),pi(r_t))
	c3 = com(prt1,prt2); // com(pi(x+r)_h,pi(x+r)_t)
	
	print("c1:" + c1);
	print("c2:" + c2);
	print("c3:" + c3);

}

// Verifier: v1()
function v1(){
	var random = Math.floor(Math.random() * (+4 - +1)) + +1; 
	return random;
}

// Prover: p2()
function p2(c){
	var ch = c;
	var resp = [];

	//print("Challenge is:");
	//print(ch);
	if(ch == 1){
		resp[0] = IDscheme.knuth_shuffle(sk_xh, glob_sigma);
		resp[1] = IDscheme.knuth_shuffle(sk_xt, glob_sigma);
		resp[2] = IDscheme.knuth_shuffle(glob_rh, glob_sigma);
		resp[3] = IDscheme.knuth_shuffle(glob_rt, glob_sigma);
	}
	else if(ch == 2){
		resp[0] = "pih";
		resp[1] = "pit";
		resp[2] = IDscheme.addVectors(glob_rh, sk_xh);
		resp[3] = IDscheme.addVectors(glob_rt, sk_xt);
	}
	else if(ch == 3){ 
		resp[0] = "pih";
		resp[1] = "pit";
		resp[2] = glob_rh;
		resp[3] = glob_rt;
	}else{
		print("Error!");
	}
	
	return resp;
}

// Verifier: v2()
function v2(c, params){
	var ch = c;
	var resp = params;

	if(ch == 1){
		var comp1, comp2,comp3; // computations

		comp1 = com(resp[2],resp[3]); // c2=com(pi(rh),pi(rt)) hesabi

		var part1 = IDscheme.addVectors(resp[0],resp[2]);//xh+rh
		var part2 = IDscheme.addVectors(resp[1],resp[3]);//xt+rt
		comp2 = com(part1,part2); // 

		if( comp1==c2 && comp2==c3){
			print("Success!");
		}else{
			print("Failed!");
		}

	}
	else if(ch == 2){
		var comp1, comp2,comp3; // computations
		
		//c1 hesabi:
		var part1 = IDscheme.vectorMultiply(resp[2],a_h);
		var part2 = IDscheme.vectorMultiply(resp[3],a_t);
		var part3 = IDscheme.addVectors(part1,part2);
		var part4 = IDscheme.vectorSubtract(part3,y);

		//c1hesabi
		comp1 = com(resp[0],resp[1],part4);
		//c3 hesabi:
		comp2 = com(IDscheme.knuth_shuffle(resp[2]),IDscheme.knuth_shuffle(resp[3])); //com(pi(u))
		
		if(  comp1==c1 && comp2==c3){ 
			print("Success!");
		}else{
			print("Failed!");
		}
	}
	else if(ch == 3){
		var comp1, comp2,comp3; // computations
		var part1 = IDscheme.vectorMultiply(a_h, resp[2]);
		var part2 = IDscheme.vectorMultiply(a_t, resp[3]);
		var part3 = IDscheme.addVectors(part1, part2);

		comp1 = com(resp[0],resp[1],part3); //c1=com()
		comp2 = com(IDscheme.knuth_shuffle(resp[2]),IDscheme.knuth_shuffle(resp[3]));

		if( c1==comp1 && c2==comp2){ 
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
var n = 677, 
	q = 2048;
	


function testidscheme() {
	print("Test Xagawa and Tanaka's ID Scheme (NTRU-based):");
	print("Parameters Set:");
	print("n = " + n);
	print("q = " + q);
	keyGeneration(n, q);

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
