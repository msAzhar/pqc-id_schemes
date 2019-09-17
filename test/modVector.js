//Modulo q
function modVector(v, q) {
	var x = v.length;
	console.log("X:"+x);
	if (q <= 0) {
		alert("modulus not positive");
		return;
	}
	for (var i = 0; i < x; i++) {
		v[i] %= q;
		if (v[i] < 0) {
			v[i] += q;
		}
	}
	return v;
}
