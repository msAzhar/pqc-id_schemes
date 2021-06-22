const { sqrt } = require('mathjs')
const { matrix, ones, inv } = require('mathjs')

console.log(sqrt(-4).toString()) // 2i

console.log(matrix([1, 4, 9, 16, 25]))

const b = matrix(ones([2, 3]))
print(b)

console.log(inv([[1,2,3],
     [4,5,6],
     [7,3,9]]))

console.log(inv([[1, 2], [3, 4]]))  // returns [[-2, 1], [1.5, -0.5]]
console.log(inv(4))                 // returns 0.25


var a =[5.66,6.7,8.9,0,4.7,2.314]
print(a)
var u = to_int(a)
print(u)
function to_int(a){
	var t = new Array(a.length)
	for(var i = 0; i<a.length;i++){
		t[i] = a[i].toFixed(0);
	}
	return t;
}

function print (value) {
  console.log(value)
}