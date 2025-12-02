#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "idscheme_funcs.h"
#include "portable_rng.h"
#include "sha256.h"

#define NELEMS(x)  (sizeof(x) / sizeof((x)[0]))

#define _SUCCESS_ 0
#define _FAILED_ 1
#define N  512//3 // y-rows
#define M  2048//4 // x-cols
#define Q  257//3

struct SecretKey
{
    int x[M];
};

struct  PublicKey
{
   int y[N];
};

struct Com_c1
{
    int sigma;
    int vector_Ar[N];
    BYTE buf_c1[SHA256_BLOCK_SIZE];
};

struct Com_c2
{
    int rand_r[M];
    BYTE buf_c2[SHA256_BLOCK_SIZE];
};

struct Com_c3
{
    int sum_xr[M];
    BYTE buf_c3[SHA256_BLOCK_SIZE];
};

struct Commitments
{
    struct Com_c1 com_c1;
    struct Com_c2 com_c2;
    struct Com_c3 com_c3;

};

struct Params
{
    int resp_param1[M]; // 
    int resp_param2[M]; // 
    int resp_param1_int;
};

typedef struct Commitments Coms;
typedef struct Params Resp_params;

typedef struct SecretKey SKparams;
typedef struct PublicKey PKparams;

int keygen(PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M]);
int p_coms(Coms *coms_ptr, PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M], int rand_r[M]);
int v_challenge(void);
void p_params(int ch, Resp_params *params_ptr, SKparams *sk_ptr, Coms *coms_ptr, int rand_r[M]);
int v_check(int ch, Resp_params *params_ptr, PKparams *pk_ptr, Coms *coms_ptr, int matrix_A[][M]);

void com_func(BYTE buf[SHA256_BLOCK_SIZE], BYTE data[]);
int coms_equal(BYTE buf1[SHA256_BLOCK_SIZE], BYTE buf2[SHA256_BLOCK_SIZE]);

void com_func(BYTE buf[SHA256_BLOCK_SIZE], BYTE data[])
{
    SHA256_CTX ctx;
    size_t len = NELEMS(data);

    sha256_init(&ctx);
    sha256_update(&ctx, data, len);
    sha256_final(&ctx, buf);
}

int coms_equal(BYTE buf1[SHA256_BLOCK_SIZE], BYTE buf2[SHA256_BLOCK_SIZE])
{
    int pass = 1;
    pass = pass && !memcmp(buf2, buf1, SHA256_BLOCK_SIZE);

    return(pass);
}

//TODO: comment ler yaz!!
int keygen(PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M]){
    int sk_x[M] = {0}; 
    int pk_y[N] = {0};
	generateMatrixModQ(N, M, matrix_A, Q);
	//printMatrix(N, M, matrix_A);

	generateVector(sk_x, M, Q);
	//printVector(sk_x, M);
    assignVectorValues(sk_ptr->x, sk_x, M);

	vectorMultiplyMatrix(pk_y, sk_x, N, M, matrix_A);
    //printVector(pk_y, N);

    modVector(pk_y, N, Q);
    //printVector(pk_y, N);

    // pk_ptr->y = {0};
    // sk_ptr->x = {0};
    assignVectorValues(pk_ptr->y, pk_y, N);

	return 0;
}

//TODO: prover_commitments
int p_coms(Coms *coms_ptr, PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M], int rand_r[M]){
    int c1[N] = {0};
    int c2[M] = {0};
    int c3[M] = {0}; 
    int local_rand_r[M] = {0};
	
    int rand_sigma = generateSigma(M);
    int shuffled_local_rand_r[M] = {0};
    int shuffled_sum_xr[M] = {0};

	generateVector(local_rand_r, M, Q);

	vectorMultiplyMatrix(c1, local_rand_r, N, M, matrix_A);
	modVector(c1, N, Q);
	//printVector(c1, N);

    int sigma_and_Ar[1+N];
    concat_value_and_array(sigma_and_Ar, rand_sigma, N, c1);

    BYTE local_buf_c1[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c1.buf_c1, sigma_and_Ar);

    knuth_shuffle(shuffled_local_rand_r, local_rand_r, M, rand_sigma);

	//printf("c2:\n");
	assignVectorValues(c2, local_rand_r, M);
	//printVector(c2, M);
    BYTE local_buf_c2[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c2.buf_c2, shuffled_local_rand_r);

	//printf("c3:\n");
	addVectors(c3, sk_ptr->x, local_rand_r, M);
	modVector(c3, M, Q);
	//printVector(c3, M);
    assignVectorValues(rand_r, local_rand_r, M);
    knuth_shuffle(shuffled_sum_xr, c3, M, rand_sigma);

    BYTE local_buf_c3[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c3.buf_c3, shuffled_sum_xr);

    coms_ptr->com_c1.sigma = rand_sigma;
    assignVectorValues(coms_ptr->com_c1.vector_Ar, c1, N);
    assignVectorValues(coms_ptr->com_c2.rand_r, shuffled_local_rand_r, M);
    assignVectorValues(coms_ptr->com_c3.sum_xr, shuffled_sum_xr, M);

	return 0;
}

int v_challenge(void)
{
    return rng_uniform(3);
}

//TODO: M leri degistir!!
void p_params(int ch, Resp_params *params_ptr, SKparams *sk_ptr, Coms *coms_ptr, int rand_r[M])  //int param1[M], int param2[M], int ch, int sk_x[M], int rand_r[M])
{
     if(ch == 0){
        int shuffled_x[M] = {0};
        knuth_shuffle(shuffled_x, sk_ptr->x, M, coms_ptr->com_c1.sigma);
        assignVectorValues(params_ptr->resp_param1 , shuffled_x, M);
        assignVectorValues(params_ptr->resp_param2, coms_ptr->com_c2.rand_r, M);
    }else if(ch == 1){
        int sum_xr[M] = {0};
        addVectors(sum_xr, sk_ptr->x, rand_r, M);
        modVector(sum_xr, M, Q);

        params_ptr->resp_param1_int = coms_ptr->com_c1.sigma;
        assignVectorValues(params_ptr->resp_param2, sum_xr, M);
    }else if(ch == 2){
        params_ptr->resp_param1_int = coms_ptr->com_c1.sigma;
        assignVectorValues(params_ptr->resp_param2, rand_r, M);
    }else{
        printf("Error: invalid challenge!\n");
    }
}

int v_check(int ch, Resp_params *params_ptr, PKparams *pk_ptr, Coms *coms_ptr, int matrix_A[][M]){
    //int c1[N], int c2[M], int c3[M], int ch, int param1[M], int param2[M], int matrix_A[][M], int sk_x[M], int pk_y[N], int rand_r[M]
	int result = 0;

	if(ch == 0){
        int local_sum_xr[M] = {0};
        addVectors(local_sum_xr, params_ptr->resp_param1, params_ptr->resp_param2, M);
        modVector(local_sum_xr, M, Q);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, local_sum_xr);

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, params_ptr->resp_param2);

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        if(com2_result && com3_result)
        	result = 1;
    }else if(ch == 1){
        int comp1[N] = {0};
        int comp1_res[N] = {0};
        vectorMultiplyMatrix(comp1, params_ptr->resp_param2, N, M, matrix_A);
        modVector(comp1, N, Q);
        subVectorsMod(comp1_res, comp1, pk_ptr->y, N, Q);

        int sigma_and_Ar[1+N];
        concat_value_and_array(sigma_and_Ar, params_ptr->resp_param1_int, N, comp1_res);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigma_and_Ar);

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        int shuffled_sum_xr[M] = {0};
        knuth_shuffle(shuffled_sum_xr, params_ptr->resp_param2, M, params_ptr->resp_param1_int);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, shuffled_sum_xr);

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

        if(com1_result && com3_result)
        	result = 1;
    }else if(ch == 2){
        int comp2[N] = {0};
        vectorMultiplyMatrix(comp2, params_ptr->resp_param2, N, M, matrix_A);
        modVector(comp2, N, Q);

        int sigma_and_Ar[1+N];
        concat_value_and_array(sigma_and_Ar, params_ptr->resp_param1_int, N, comp2);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigma_and_Ar);

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        int shuffled_r[M] = {0};
        knuth_shuffle(shuffled_r, params_ptr-> resp_param2, M, params_ptr->resp_param1_int);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, shuffled_r);

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        if(com1_result && com2_result)
        	result = 1;
    }else{
        result = 0;
    }
    return result;
}

int main(void){
    rng_seed(0xC0FFEEu);

    SKparams sk_params;
    SKparams *sk_ptr = &sk_params;

    PKparams pk_params;
    PKparams *pk_ptr = &pk_params;

    Coms coms;
    Coms *coms_ptr = &coms;

    Resp_params params;
    Resp_params *params_ptr = &params;

    int matrix_A[N][M] = {0};
    int rand_r[M] = {0};

    printf("--- Kawachi's ID Scheme ---\n");
    printf("start Key Generation...\n");
    keygen(pk_ptr, sk_ptr, matrix_A);
    printf("finish Key Generation.\n");

    printf("Prover (Compute commitments) ...\n");
    p_coms(coms_ptr, pk_ptr, sk_ptr, matrix_A, rand_r);

    printf("Verifier (Generate challenge) ...\n");
    int ch = v_challenge();
    printf("Challenge: %d\n", ch);

    printf("Prover (Send some parameters) ...\n");
    p_params(ch, params_ptr, sk_ptr, coms_ptr, rand_r);

    printf("Verifier (Check the truthfulness) ...\n");
    int result = v_check(ch, params_ptr, pk_ptr, coms_ptr, matrix_A);

    if(result == 1)
        printf("Success!\n");
    else
        printf("Failed!\n");

    return 0;
}



