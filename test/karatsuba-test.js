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

//Multiplies a matrix B by a vector a, c = a * B
function vectorMultiplyMatrix2(a, B) {
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
	
	var temp =[];
	
	for (var j = 0; j < B_y; j++) {
		for (var i = 0; i < A_y; i++) {
			temp.push(B[i][j]);
		}
		//v[j] += karatsuba(temp[i]);
	}
	var z=[];
	var t=[];

	/*for(var d = 0; d<B_x; d+=3){
	for(var k=0; k<temp.length/B_y; k++){
		//for (var j = 0; j < A_y; j++) {
			//var c = karatsuba(a,temp[k]);

			z.push(temp[k]);
		//}
	}
	}*/

	for(var k=0; k<B_x; k++){
		for(var i=0; i<k;i++){
			z.push(temp[k]);
		}
		t.push(z);
	}
	console.log("Temp: ");
	console.log(temp);
	console.log(z);
	console.log(t);
	return v;
}

//Karatsuba multiplication
function karatsuba(a, b) {	
	var n = b.length;
	if (n <= 32) {
		var cn = (n << 1) - 1;
		//var c = new Array(cn).fill(0);
		var c = new Array(cn);
		for (var k = 0; k < cn; k++) {
			c[k] = 0;
		}
		for (var k = 0; k < cn; k++) {
			for (var i = Math.max(0, k - n + 1); i <= Math.min(k, n - 1); i++) {
				c[k] += b[i] * a[k - i];
			}
		}
		return c;
	} else {
		var n1 = (n >> 1);
		var a1 = copyOf(a.slice(), n1);
		var a2 = copyOfRange(a.slice(), n1, n);
		var b1 = copyOf(b.slice(), n1);
		var b2 = copyOfRange(b.slice(), n1, n);

		// make a copy of a1 that is the same length as a2
		var A = copyOf(a1.slice(), a2.length);
		addIntPoly(A, a2);
		// make a copy of b1 that is the same length as b2
		var B = copyOf(b1.slice(), b2.length);
		addIntPoly(B, b2);

		var c1 = karatsuba(a1, b1);
		var c2 = karatsuba(a2, b2);
		var c3 = karatsuba(A, B);
		subIntPoly(c3, c1);
		subIntPoly(c3, c2);
				
		//var c = new Array((n << 1) - 1).fill(0);
		var c = new Array((n << 1) - 1);
		for (var k = 0; k < c.length; k++) {
			c[k] = 0;
		}
		for (var i = 0; i < c1.length; i++) {
			c[i] = c1[i];
		}
		for (var i = 0; i < c3.length; i++) {
			c[n1 + i] += c3[i];
		}
		for (var i = 0; i < c2.length; i++) {
			c[(n1 << 1) + i] += c2[i];
		}
		return c;
	}
}

//Returns a copy of the original array, 
//truncated or padded with zeros to obtain the specified length
function copyOf(arr1, length) {
	//var arr2 = new Array(length);
	var arr2 = [];
	for (var i = 0; i < length; i++) {
		if (i >= arr1.length) {
			arr2[i] = 0;
		} else {
			arr2[i] = arr1[i];
		}
	}
	return arr2;
}

//Returns a new array containing the specified range from the original array, 
//truncated or padded with nulls to obtain the required length
function copyOfRange(arr1, from, to) {
	var length = to - from;
	//var arr2 = new Array(length);
	var arr2 = [];
	for (var i = 0; i < length; i++) {
		if (i >= arr1.length || from >= arr1.length) {
			arr2[i] = 0;
		} else {
			arr2[i] = arr1[from];
		}
		from++;
	}
	return arr2;
}

//Polynomial addition
function addIntPoly(a, b) {
	for (var i = 0; i < b.length; i++) {
		a[i] += b[i];
	}
}

//Polynomial subtraction
function subIntPoly(a, b) {
	for (var i = 0; i < b.length; i++) {
		a[i] -= b[i];
	}
}


var a = [2,1,0];
var b = [1,-1,2];

var dr = karatsuba(a,b);
console.log(dr);
//var B = [[1,-1,2],[0,-3,1]];
var B = [[1,0],[-1,-3],[2,1]];

console.log(a);
console.log(B);
var c = vectorMultiplyMatrix(a,B);
console.log(c);



var d = vectorMultiplyMatrix2(a,B);
console.log(d);



