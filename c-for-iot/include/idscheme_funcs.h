#ifndef IDSCHEME_FUNCS_H
#define IDSCHEME_FUNCS_H

#include <stdbool.h>
#include <stddef.h>

int knuth_shuffle(int *result, const int *toShuffle, int len, int sigma);
int generateSigma(int m);
int generateErrors(int *v, int size);

int addVectors(int *result, const int *v1, const int *v2, int size);
int subVectors(int *result, const int *v1, const int *v2, int size);
int multVectors(int *result, const int *v1, const int *v2, int size);
int modVector(int *v, int len, int q);
int subVectorsMod(int *result, const int *v1, const int *v2, int size, int q);

int addMod(int a, int b, int q);
int subMod(int a, int b, int q);

void generateMatrixModQ(int rows, int cols, int matrix_A[][cols], int q);

int generateVector(int *v, int size, int q);
void printVector(const int *v, int size);

int vectorMultiplyMatrix(int *result, const int *v, int rows, int cols, int m[][cols]);
int scalarMultiplyVector(int *result, int scalar, const int *v, int size);

int assignVectorValues(int *dest, const int *source, int size);
bool areEqual(const int *v1, const int *v2, int size);

int concat_two_values_and_array(int *result, int val1, int val2, int len, const int arr[len]);
int concat_two_arrays(int *result, int len1, const int arr1[len1], int len2, const int arr2[len2]);
int concat_value_and_array(int *result, int val, int len, const int arr[len]);
int concat_value_and_two_arrays(int *result, int val, int len1, const int arr1[len1], int len2, const int arr2[len2]);

void printMatrix(int rows, int cols, int matrix_A[][cols]);

#endif  // IDSCHEME_FUNCS_H
