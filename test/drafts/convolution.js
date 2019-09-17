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

function test(){

	var a = [0,1,1,0,0,0,1];
	var c = [2,1,2,3,4,5,2];

	//var b = _d(a);
	//print(b);

	var f = convolution(a,c);
	print(f);
}

test();



