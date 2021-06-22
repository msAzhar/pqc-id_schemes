
int knuth_shuffle(int *result, int *toShuffle, int len, int sigma);
int generateSigma(int m);
int generateErrors(int* v, int  size);

int addVectors(int * result, int* v1, int* v2, int size);
int subVectors(int * result, int* v1, int* v2, int size);
int multVectors(int * result, int* v1, int* v2, int size);
int modVector(int* v, int len, int q);
int subVectorsMod(int *result, int* v1, int* v2, int size, int q);

int addMod(int a, int b, int q);
int subMod(int a, int b, int q);

void generateMatrixModQ(int rows, int cols, int matrix_A[][cols], int q);

int generateVector(int* v, int size, int  q);
void printVector(int *v, int size);

int vectorMultiplyMatrix(int *result, int *v, int rows, int cols, int m[][cols]);
int scalarMultiplyVector(int * result, int scalar, int *v, int size);

int assignVectorValues(int *dest, int *source, int size);
bool areEqual(int *v1, int *v2, int size);

int concat_two_values_and_array(int *result, int val1, int val2, int len, int arr[len]);
int concat_two_arrays(int *result, int len1, int arr1[len1], int len2, int arr2[len2]);
int concat_value_and_array(int *result, int val, int len, int arr[len]);

void printMatrix(int rows, int cols, int matrix_A[][cols]);