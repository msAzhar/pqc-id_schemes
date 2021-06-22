// Xagawa NTRU-based

function assign(a){
	 var result = [a.length];

        for (var i = 0; i < a.length; i++) {
            result[i] = a[i] ;
        }

        return result;
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

//Returns the next pseudorandom, uniformly distributed integer between 0(inclusive) and q-1(inclusive)
function randInt(q) {
	return Math.floor(Math.random() * q);
}

//Shuffles the input array
function shuffle(arr) {
	var arr2 = arr.slice();
	for(var j, x, i = arr2.length; i; j = parseInt(Math.random() * i), x = arr2[--i], arr2[i] = arr2[j], arr2[j] = x);
	return arr2;    
}

//Shuffles elements of an array depending on sigma, Knuth Shuffle is used
function knuth_shuffle(array, sigma) {
  //var shuffled_array = [];
  var result =[];
  var shuffled_array = assign(array);
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

  result = shuffled_array; 
  return result;
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

//commitment function
function com(){
	var param;
	param = (arguments[0]+arguments[1]);
	return sha256(param);
}

function keyGeneration(n, q) {
	var ah = [];
	var at = [];
	var xh = []; //sk
	var xt = []; //sk
	
	glob_n = n;
	glob_q = q;

	for (var i = 0; i < n; i++) {
		ah[i] = randInt(q);
	}

	for (var i = 0; i < n; i++) {
		at[i] = randInt(q);
	}

	// temps 
	var t1 = [];
	var t2 = [];
	
	// d's value is n/2-1
	for (var i = 0; i < (n/2); i++) {
		t1[i] = 1;
	}

	// n's value is 677
	for (var i = (n-1)/2; i < n; i++) {
		t1[i] = 0;
	}
	
	//print(t1);
	xh = shuffle(t1);
	/*
	print("xh:");
	print(xh);
	print(xh.length); //677
	*/
	t2[0] = 1;

	for (var i = 1; i < n; i++) {
		t2[i] = 0;
	}
	
	xt = shuffle(t2);

	print("Ah:"+ah.length);
	print("Xh:"+xh.length);
	/*
	var ts1 = new Date().getTime();
	var part1 = IDscheme.vectorMultiply(ah,xh);
	var ts2 = new Date().getTime();
	print(ts2-ts1);
	var ts3 = new Date().getTime();
	var part2 = IDscheme.vectorMultiply(at,xt);
	var ts3 = new Date().getTime();
	print(ts2-ts1);
	*/
	
	var ts1 = new Date().getTime();
	var part1 = sparseVectorMultiply(xh,ah);
	var ts2 = new Date().getTime();
	//print(ts2-ts1);
	var ts3 = new Date().getTime();
	var part2 = sparseVectorMultiply(xt,at);
	var ts4 = new Date().getTime();
	//print(ts4-ts3);
	
	y = addVectors(part1,part2);
	
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
		rh[i] = randInt(glob_q);
	}

	for (var i = 0; i < glob_n; i++){
		rt[i] = randInt(glob_q);
	}

	glob_rh = rh;
	glob_rt = rt;

	sigma = Math.floor(Math.random() * (4)); //glob_q-1
	glob_sigma = sigma;

	var part1 = knuth_shuffle(rh, glob_sigma); 
	var part2 = knuth_shuffle(rt, glob_sigma); 

	var prt1 = addVectors(rh,sk_xh);
	var prt2 = addVectors(rt,sk_xt);

	var p1 = knuth_shuffle(prt1,glob_sigma);
	var p2 = knuth_shuffle(prt2,glob_sigma);
	
	c1 = com("pih","pit",y);
	c2 = com(part1,part2); // com(pi(r_h),pi(r_t))
	c3 = com(p1,p2); // com(pi(x+r)_h,pi(x+r)_t)
	
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
		resp[0] = knuth_shuffle(sk_xh, glob_sigma);
		resp[1] = knuth_shuffle(sk_xt, glob_sigma);
		resp[2] = knuth_shuffle(glob_rh, glob_sigma);
		resp[3] = knuth_shuffle(glob_rt, glob_sigma);
	}
	else if(ch == 2){
		resp[0] = "pih";
		resp[1] = "pit";
		resp[2] = addVectors(glob_rh, sk_xh);
		resp[3] = addVectors(glob_rt, sk_xt);
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

		var part1 = addVectors(resp[0],resp[2]);//xh+rh
		var part2 = addVectors(resp[1],resp[3]);//xt+rt
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
		var part1 = vectorMultiply(resp[2],a_h);
		var part2 = vectorMultiply(resp[3],a_t);
		var part3 = addVectors(part1,part2);
		var part4 = vectorSubtract(part3,y);

		//c1hesabi
		comp1 = com(resp[0],resp[1],part4);
		//c3 hesabi:
		comp2 = com(knuth_shuffle(resp[2],glob_sigma),knuth_shuffle(resp[3],glob_sigma)); //com(pi(u))
		
		if(  comp1==c1 && comp2==c3){ 
			print("Success!");
		}else{
			print("Failed!");
		}
	}
	else if(ch == 3){
		var comp1, comp2,comp3; // computations
		var part1 = vectorMultiply(a_h, resp[2]);
		var part2 = vectorMultiply(a_t, resp[3]);
		var part3 = addVectors(part1, part2);

		comp1 = com(resp[0],resp[1],part3); //c1=com()
		comp2 = com(knuth_shuffle(resp[2],glob_sigma),knuth_shuffle(resp[3],glob_sigma));

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
	var t0 = new Date().getTime();
	keyGeneration(n, q);
	var t1 = new Date().getTime();

	//print("Prover: compute commitments c1, c2 and c3:");
	var t2 = new Date().getTime();
	p1();
	var t3 = new Date().getTime();
	var ch;
	ch = v1();
	/*print("Verifier: sends a challenge: " + ch);
	print("Prover: reveals some parameters depending on challenge.(Current challenge is: " + ch + ")");
	print("Verifier: checks commitment correctness from information revealed by prover and accept in case of success. ");
	*/
	var t4 = new Date().getTime();
	var response = p2(ch);
	var t5 = new Date().getTime();
	v2(ch,response);
	var t6 = new Date().getTime();
	print("Ch: "+ch);
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
