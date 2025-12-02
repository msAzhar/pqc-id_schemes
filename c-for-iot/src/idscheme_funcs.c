#include "idscheme_funcs.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "portable_rng.h"

int knuth_shuffle(int *result, const int *toShuffle, int len, int sigma) {
    assignVectorValues(result, toShuffle, len);
    for (int i = len - 1; i > 0; i--) {
        int j = sigma;  // sigma is used as a deterministic index
        int tmp = result[j];
        result[j] = result[i];
        result[i] = tmp;
    }

    return 0;
}

int generateSigma(int m) {
    return rng_uniform(m);
}

int generateErrors(int *v, int size) {
    size_t i;
    for (i = 0; i < (size_t)(size / 2); i++) {
        v[i] = 1;
    }

    for (; i < (size_t)size; i++) {
        v[i] = 0;
    }

    return 0;
}

int modVector(int *v, int len, int q) {
    for (int i = 0; i < len; i++) {
        v[i] %= q;
        if (v[i] < 0) {
            v[i] += q;
        }
    }
    return 0;
}

int addVectors(int *result, const int *v1, const int *v2, int size) {
    for (int i = 0; i < size; i++) {
        result[i] = v1[i] + v2[i];
    }
    return 0;
}

int subVectors(int *result, const int *v1, const int *v2, int size) {
    for (int i = 0; i < size; i++) {
        result[i] = v1[i] - v2[i];
    }
    return 0;
}

int subMod(int a, int b, int q) {
    if (a >= b) {
        return a - b;
    }
    return q - b + a;
}

int addMod(int a, int b, int q) {
    if (a == 0) {
        return b;
    }
    if (b == 0) {
        return a;
    }

    int complement = q - b;
    if (a >= complement) {
        return a - complement;
    }
    return q - complement + a;
}

int multVectors(int *result, const int *v1, const int *v2, int size) {
    for (int i = 0; i < size; i++) {
        result[i] = v1[i] * v2[i];
    }
    return 0;
}

int subVectorsMod(int *result, const int *v1, const int *v2, int size, int q) {
    for (int i = 0; i < size; i++) {
        result[i] = subMod(v1[i], v2[i], q);
    }
    return 0;
}

void generateMatrixModQ(int rows, int cols, int matrix_A[][cols], int q) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            matrix_A[i][j] = rng_uniform(q);
        }
    }
}

void printMatrix(int rows, int cols, int matrix_A[][cols]) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%3i ", matrix_A[i][j]);
        }
        printf("\n");
    }
}

void printVector(const int *v, int size) {
    for (int i = 0; i < size; i++) {
        printf("%d ", v[i]);
    }
}

int generateVector(int *v, int size, int q) {
    for (int i = 0; i < size; i++) {
        v[i] = rng_uniform(q);
    }
    return 0;
}

int vectorMultiplyMatrix(int *result, const int *v, int rows, int cols, int m[][cols]) {
    for (int i = 0; i < rows; i++) {
        int total = 0;
        for (int j = 0; j < cols; j++) {
            total += m[i][j] * v[j];
        }
        result[i] = total;
    }
    return 0;
}

int scalarMultiplyVector(int *result, int scalar, const int *v, int size) {
    for (int i = 0; i < size; i++) {
        result[i] = scalar * v[i];
    }
    return 0;
}

int assignVectorValues(int *dest, const int *source, int size) {
    memcpy(dest, source, (size_t)size * sizeof(int));
    return 0;
}

bool areEqual(const int *v1, const int *v2, int size) {
    for (int i = 0; i < size; ++i) {
        if (v1[i] != v2[i]) {
            return false;
        }
    }
    return true;
}

int concat_two_values_and_array(int *result, int val1, int val2, int len, const int arr[len]) {
    int index = 0;

    result[index++] = val1;
    result[index++] = val2;

    for (int i = 0; i < len; i++) {
        result[index++] = arr[i];
    }

    return 0;
}

int concat_two_arrays(int *result, int len1, const int arr1[len1], int len2, const int arr2[len2]) {
    int index = 0;
    for (int i = 0; i < len1; i++) {
        result[index++] = arr1[i];
    }

    for (int i = 0; i < len2; i++) {
        result[index++] = arr2[i];
    }

    return 0;
}

int concat_value_and_array(int *result, int val, int len, const int arr[len]) {
    int index = 0;

    result[index++] = val;

    for (int i = 0; i < len; i++) {
        result[index++] = arr[i];
    }

    return 0;
}

int concat_value_and_two_arrays(int *result, int val, int len1, const int arr1[len1], int len2, const int arr2[len2]) {
    int index = 0;

    result[index++] = val;

    for (int i = 0; i < len1; i++) {
        result[index++] = arr1[i];
    }

    for (int i = 0; i < len2; i++) {
        result[index++] = arr2[i];
    }

    return 0;
}
