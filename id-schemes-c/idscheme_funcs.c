#include<stdbool.h>
#include <memory.h>

int knuth_shuffle(int *result, int *toShuffle, int len, int sigma){
    assignVectorValues(result, toShuffle, len);
    int i, j, tmp; 
     for (i = len - 1; i > 0; i--) { 
         j = sigma; // sigma is used as randomised j 
         tmp = result[j];
         //printf("%d --- %d\n", tmp, toShuffle[i] );
         result[j] = result[i];
         result[i] = tmp;
         //printf("%d --- %d\n", toShuffle[i], toShuffle[j] );
     }
     //printf("FFF:\n");
     //printVector(toShuffle, len);  
    return 0;
}

// Random seed Value used for fandom permutation ove {1, ..., m}.
int generateSigma(int m) 
{
    int sigma;
    srand(time(NULL));
    sigma = rand()%m;
    return sigma;
}

int generateErrors(int* v, int  size) // specific to Silva's scheme
{
    size_t i;
    for (i = 0; i < size/2; i++){
        v[i] = 1;
    }

    for (i = size/2; i < size; i++){
        v[i] = 0;
    }
    //TODO: shuffle result
    return 0;
}

int modVector(int* v, int len, int q) {
    size_t i;
    for (i = 0; i < len; i++) {
        v[i] %= q;
        if (v[i] < 0) {
            v[i] += q;
        }
    }
    return 0;
}

int addVectors(int * result, int* v1, int* v2, int size)
{
    int i;
    for (i = 0; i < size; i++){
        result[i] = v1[i] + v2[i];
    }
    return 0;
}

int subVectors(int * result, int* v1, int* v2, int size)
{
    int i;
    for (i = 0; i < size; i++){
        result[i] = v1[i] - v2[i];
    }
    return 0;
}

int subMod(int a, int b, int q)
{
    if (a>=b)
        return a - b;
    else
        return q - b + a;
}

int addMod(int a, int b, int q)
{
	if ( a==0 ) return b;
	if ( b==0 ) return a;
  	
  	b = q - b;
  	if ( a>=b )
    	return a - b;
  	else
    	return q - b + a;
}

int multVectors(int * result, int* v1, int* v2, int size)
{
    int i;
    for (i = 0; i < size; i++){
        result[i] = v1[i] * v2[i];
    }
    return 0;
}

int subVectorsMod(int *result, int* v1, int* v2, int size, int q)
{
    //int* result = malloc(size * sizeof (int));
    int i;
    for (i = 0; i < size; i++){
        result[i] = subMod(v1[i],v2[i],q); //(v1[i]%q + v2[i]%q + q)%q;
    }
    return 0;
}

void generateMatrixModQ(int rows, int cols, int matrix_A[][cols], int  q) 
{
    size_t i, j;
    for (i = 0; i < rows; i++)
      for (j = 0; j < cols; j++)
        matrix_A[i][j] = rand()%q;
}

void printMatrix(int rows, int cols, int matrix_A[][cols]) 
{
	size_t i, j;
    for (i = 0; i < rows; i++){
      for (j = 0; j < cols; j++){
        printf("%3i ", matrix_A[i][j]);
    }
    printf("\n");
	}
}

void printVector(int *v, int size)
{
    size_t i;
    for (i = 0; i < size; i++){
        printf("%d ", v[i]);
	}
}

int generateVector(int* v, int size, int  q) 
{
    size_t i;
    for (i = 0; i < size; i++){
        v[i] = rand()%q;
    }
    return 0;
}

int vectorMultiplyMatrix(int *result, int *v, int rows, int cols, int m[][cols])
{
    size_t i, j;
    int total;
    for (i = 0; i < rows; i++){
        total=0;
        for (j = 0; j < cols; j++){
            total += m[i][j]*v[j];
        }
    //printf("%d ", total);
    result[i] = total;
    }
    return 0;
}

int scalarMultiplyVector(int * result, int scalar, int *v, int size){
    int i;
    for(i = 0; i<size; i++){
        result[i] = scalar*v[i];
    }
    return 0;
}

int assignVectorValues(int *dest, int *source, int size){
	int i;
	for(i=0; i<size; i++)
    {
        dest[i] = source[i];
    }
    return 0;
}

bool areEqual(int *v1, int *v2, int size){
	for (int i = 0; i < size; ++i)
	{
		if(v1[i] != v2[i]){
			return false;
		}
	}
	return true;
}

int concat_two_values_and_array(int *result, int val1, int val2, int len, int arr[len])
{
    int index = 0;
        
    result[index++] = val1;
    result[index++] = val2;

    for(int i = 0; i<len; i++)
        result[index++] = arr[i];

    return 0;
}

int concat_two_arrays(int *result, int len1, int arr1[len1], int len2, int arr2[len2])
{
    int index = 0;
    for(int i = 0; i < len1; i++)
        result[index++] = arr1[i];

    for(int i = 0; i<len2; i++)
        result[index++] = arr2[i];

    return 0;
}


int concat_value_and_array(int *result, int val, int len, int arr[len])
{
    int index = 0;
        
    result[index++] = val;

    for(int i = 0; i<len; i++)
        result[index++] = arr[i];

    return 0;
}
