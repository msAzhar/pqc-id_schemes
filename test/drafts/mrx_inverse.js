//Matrix inversion 
function matrix_invert(M){
    // I use Guassian Elimination to calculate the inverse:
    // (1) 'augment' the matrix (left) by the identity (on the right)
    // (2) Turn the matrix on the left into the identity by elemetry row ops
    // (3) The matrix on the right is the inverse (was the identity matrix)
    // There are 3 elemtary row ops: (I combine b and c in my code)
    // (a) Swap 2 rows
    // (b) Multiply a row by a scalar
    // (c) Add 2 rows
    
    //if the matrix isn't square: exit (error)
    if(M.length !== M[0].length){return;}
    
    //create the identity matrix (I), and a copy (C) of the original
    var i=0, ii=0, j=0, dim=M.length, e=0, t=0;
    var I = [], C = [];
    for(i=0; i<dim; i+=1){
        // Create the row
        I[I.length]=[];
        C[C.length]=[];
        for(j=0; j<dim; j+=1){
            
            //if we're on the diagonal, put a 1 (for identity)
            if(i==j){ I[i][j] = 1; }
            else{ I[i][j] = 0; }
            
            // Also, make the copy of the original
            C[i][j] = M[i][j];
        }
    }
    
    // Perform elementary row operations
    for(i=0; i<dim; i+=1){
        // get the element e on the diagonal
        e = C[i][i];
        
        // if we have a 0 on the diagonal (we'll need to swap with a lower row)
        if(e==0){
            //look through every row below the i'th row
            for(ii=i+1; ii<dim; ii+=1){
                //if the ii'th row has a non-0 in the i'th col
                if(C[ii][i] != 0){
                    //it would make the diagonal have a non-0 so swap it
                    for(j=0; j<dim; j++){
                        e = C[i][j];       //temp store i'th row
                        C[i][j] = C[ii][j];//replace i'th row by ii'th
                        C[ii][j] = e;      //repace ii'th by temp
                        e = I[i][j];       //temp store i'th row
                        I[i][j] = I[ii][j];//replace i'th row by ii'th
                        I[ii][j] = e;      //repace ii'th by temp
                    }
                    //don't bother checking other rows since we've swapped
                    break;
                }
            }
            //get the new diagonal
            e = C[i][i];
            //if it's still 0, not invertable (error)
            if(e==0){return}
        }
        
        // Scale this row down by e (so we have a 1 on the diagonal)
        for(j=0; j<dim; j++){
            C[i][j] = C[i][j]/e; //apply to original matrix
            I[i][j] = I[i][j]/e; //apply to identity
        }
        
        // Subtract this row (scaled appropriately for each row) from ALL of
        // the other rows so that there will be 0's in this column in the
        // rows above and below this one
        for(ii=0; ii<dim; ii++){
            // Only apply to other rows (we want a 1 on the diagonal)
            if(ii==i){continue;}
            
            // We want to change this element to 0
            e = C[ii][i];
            
            // Subtract (the row above(or below) scaled by e) from (the
            // current row) but start at the i'th column and assume all the
            // stuff left of diagonal is 0 (which it should be if we made this
            // algorithm correctly)
            for(j=0; j<dim; j++){
                C[ii][j] -= e*C[i][j]; //apply to original matrix
                I[ii][j] -= e*I[i][j]; //apply to identity
            }
        }
    }
    
    //we've done all operations, C should be the identity
    //matrix I should be the inverse:
    return I;
}

//The default constructor
function initMatrixDefault(x, y) {
    var matrix = new Array(x);
    for (var i = 0; i < x; i++) {
            matrix[i] = new Array(y);
    }
    return matrix;
}

//Matrix multiplication, C = A * B
function multiply(A, B) {
    var A_x = A.length;
    var A_y = A[0].length;
    var B_x = B.length;
    var B_y = B[0].length;
    
    if (B_x != A_y) {
        alert("Matrix inner dimensions must agree");
        return;
    }
    
    var C = initMatrixDefault(A_x, B_y);
    var Bcolj = new Array(A_y);
    for (var j = 0; j < B_y; j++) {
        for (var k = 0; k < A_y; k++) {
            Bcolj[k] = B[k][j];
        }
        for (var i = 0; i < A_x; i++) {
            var Arowi = A[i];
            var s = 0;
            for (var k = 0; k < A_y; k++) {
                if (Bcolj[k] == 0) {
                    //s += 0;
                } else if (Bcolj[k] == 1) {
                    s += Arowi[k];
                } else if (Bcolj[k] == -1) {
                    s -= Arowi[k];
                } else {
                    s += Arowi[k] * Bcolj[k];
                }
            }
            C[i][j] = s;
        }
    }
    return C;
}

//var m = [[1,1,1,1][1,1,0,0][1,0,1,0][1,0,0,0]];
var mrx = [[-1, 2, 5], [3, 4, 6], [-8, 2, 12]];
//print(mrx);
var t = matrix_invert(mrx);
//print(t);

function ceil_values(m){
    var c = new Array(m.length);
    for(var i=0;i<m.length;i++){
        c[i]=new Array(m.length);
        for(var j=0;j<m.length;j++){
            c[i][j]=Math.round(m[i][j]);
        }
    }
    return c;
}

//var t2 = ceil_values(t);
//print(t2);

/*
var mx = [[1,0,1],[0,1,0],[1,0,0]]
var t3 = matrix_invert(mx);
var t4 = ceil_values(t3);
print("Mx:");
print(mx);
print("Mx inverse:");
print(t4);

var tr = multiply(mx,t4);
print("Mx \times Mx_inverse:");
print(tr);
*/

var p = [[1,1,1],[1,0,0],[1,1,0]]
var pt = matrix_invert(p);
var t5 = ceil_values(pt);
print("Mx:");
print(p);
print("Mx inverse:");
print(pt);

var carp = multiply(p,pt);
print("Mx ve Mx_inverse carpimi:");
print(carp);
/*
//singular matrix:
var mx2 = [[1,0,1],[0,1,1],[1,0,1]]
var t32 = matrix_invert(mx2);
var t42 = ceil_values(t32);
print(t42);
*/
// 
function print(message) {
    //WScript.Echo(message);
    console.log(message);
}