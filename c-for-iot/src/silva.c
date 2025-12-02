#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "idscheme_funcs.h"
#include "portable_rng.h"
#include "sha256.h"

#define NELEMS(x)  (sizeof(x) / sizeof((x)[0]))

#define _SUCCESS_ 0
#define _FAILED_ 1

#undef N
#define N  608// y-rows
#define M  960 // x-cols
#define Q  1024

struct Com_c1
{
    int sigma;
    int r1[N];
    BYTE buf_c1[SHA256_BLOCK_SIZE];
};

struct Com_c2
{
    int Aus[N];
    int r2[N];
    BYTE buf_c2[SHA256_BLOCK_SIZE];
};

struct Com_c3
{
    int Aub[N];
    int r3[N];
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
    int resp_param1[N]; // 
    int resp_param2[N]; // 

    int resp_ch1_param3[M]; // 
    int resp_ch2_param3[N]; //
    int resp_ch3_param3; //

    int resp_ch1_param4; // 
    int resp_ch2_param4[N];
    int resp_ch3_param4[M];
    
};

typedef struct Commitments Coms;
typedef struct Params Resp_params;

int keygen(int matrix_A[][M], int sk_s[M], int pk_b[N], int errors[N]);
int p_coms(Coms *coms_ptr, int r1[N], int r2[N], int r3[N], int u[M], int us[M], int aus[N], int matrix_A[][M], int sk_s[M], int pk_b[N], int errors[N]);
int v_challenge(void);
void p_params(Resp_params *params_ptr, int ch, int r1[N], int r2[N], int r3[N], int u[M], int us[M], int aus[N], int errors[N]);
int v_check(Coms *coms_ptr, Resp_params *params_ptr, int ch, int matrix_A[][M], int pk_b[N]);

void com_func(BYTE buf[SHA256_BLOCK_SIZE], const void *data, size_t len_bytes);
int coms_equal(BYTE buf1[SHA256_BLOCK_SIZE], BYTE buf2[SHA256_BLOCK_SIZE]);

void com_func(BYTE buf[SHA256_BLOCK_SIZE], const void *data, size_t len_bytes)
{
    SHA256_CTX ctx;

    sha256_init(&ctx);
    sha256_update(&ctx, data, len_bytes);
    sha256_final(&ctx, buf);
}

int coms_equal(BYTE buf1[SHA256_BLOCK_SIZE], BYTE buf2[SHA256_BLOCK_SIZE])
{
    int pass = 1;
    pass = pass && !memcmp(buf2, buf1, SHA256_BLOCK_SIZE);

    return(pass);
}

//TODO: comment ler yaz!!
int keygen(int matrix_A[][M], int sk_s[M], int pk_b[N], int errors[N]){
	generateMatrixModQ(N, M, matrix_A, Q);
	//printMatrix(N, M, matrix_A);

	generateVector(sk_s, M, Q);
	//printVector(sk_s, M);

	generateErrors(errors, N); //yarisi 1 yarisi 0
	//printVector(errors, N);
	//TODO: add Hw counter!!

	int temp_y[N] = {0};
	vectorMultiplyMatrix(temp_y, sk_s, N, M, matrix_A);
    //printVector(temp_y, N);

    modVector(temp_y, N, Q);
    //printVector(temp_y, N);

    addVectors(pk_b, temp_y, errors, N);
    modVector(pk_b, N, Q);
    //printVector(pk_b, N);


    /* Pretty print
    // b = As + e
    vectorMultiplyMatrix(temp_y, sk_s, N, M, matrix_A); //As
    addVectors(pk_b, temp_y, errors, N); // As + e


    com_func(coms_ptr->com_c1.buf_c1, sigma_r1_concat, sizeof(sigma_r1_concat));
    com_func(coms_ptr->com_c2.buf_c2, aus_r2_concat, sizeof(aus_r2_concat));
    com_func(coms_ptr->com_c3.buf_c3, aub_r3_concat, sizeof(aub_r3_concat));

     */
	return 0;
}

//TODO: prover_commitments
int p_coms(Coms *coms_ptr, int r1[N], int r2[N], int r3[N], int u[M], int us[M], int aus[N], int matrix_A[][M], int sk_s[M], int pk_b[N], int errors[N]){
	int rand_sigma = generateSigma(M);
    //printf("vector r1:\n");
	generateVector(r1, N, Q);
	//printVector(r1, M);

    generateVector(r2, N, Q);
    generateVector(r3, N, Q);
    generateVector(u, M, Q);

    //printf("(u+s):\n");
    addVectors(us, u, sk_s, M);
    //printVector(us, M);


	//printf("A(u+s):\n"); 
	vectorMultiplyMatrix(aus, us, N, M, matrix_A);
	modVector(aus, N, Q);
	//printVector(aus, N);
    int shuffled_aus[N] = {0};
    knuth_shuffle(shuffled_aus, aus, N, rand_sigma);

    //coms icerigini doldurma:
    int au[N] = {0};
    int aub[N] = {0};
    int shuffled_aub[N] = {0};
	//printf("Au:\n");
    vectorMultiplyMatrix(au, u, N, M, matrix_A);
    addVectors(aub, au, pk_b, N);
    modVector(aub, N, Q);

    knuth_shuffle(shuffled_aub, aub, N, rand_sigma);

	coms_ptr->com_c1.sigma = rand_sigma;
    assignVectorValues(coms_ptr->com_c1.r1, r1, N);

    int sigma_r1_concat[1+N];
    concat_value_and_array(sigma_r1_concat, rand_sigma, N, r1);

    BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(coms_ptr->com_c1.buf_c1, sigma_r1_concat, sizeof(sigma_r1_concat));

    
    assignVectorValues(coms_ptr->com_c2.Aus, shuffled_aus, N);
    assignVectorValues(coms_ptr->com_c2.r2, r2, N);

    int aus_r2_concat[2*N];
    concat_two_arrays(aus_r2_concat, N, shuffled_aus, N, r2);

    BYTE local_buf_c2[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c2.buf_c2, aus_r2_concat, sizeof(aus_r2_concat));


    assignVectorValues(coms_ptr->com_c3.Aub, shuffled_aub, N);
    assignVectorValues(coms_ptr->com_c3.r3, r3, N);
    //printVector(coms_ptr->com_c3.Aub, N);

    int aub_r3_concat[2*N];
    concat_two_arrays(aub_r3_concat, N, shuffled_aub, N, r3);

    BYTE local_buf_c3[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c3.buf_c3, aub_r3_concat, sizeof(aub_r3_concat));



	return 0;
}

int v_challenge(void)
{
    return rng_uniform(3);
}


void p_params(Resp_params *params_ptr, int ch, int r1[N], int r2[N], int r3[N], int u[M], int us[M], int aus[N], int errors[N]){

    if(ch == 0){
        assignVectorValues(params_ptr->resp_param1, r1, N);
        assignVectorValues(params_ptr->resp_param2, r2, N);
        //assignVectorValues(params_ptr->resp_param3, us, M);
        assignVectorValues(params_ptr->resp_ch1_param3, us, M);
    }else if(ch == 1){
        assignVectorValues(params_ptr->resp_param1, r2, N);
        assignVectorValues(params_ptr->resp_param2, r3, N);
        //assignVectorValues(params_ptr->resp_param3, aus, N);
        assignVectorValues(params_ptr->resp_ch2_param3, aus, N);
        assignVectorValues(params_ptr->resp_ch2_param4, errors, N);
    }else if(ch == 2){
        assignVectorValues(params_ptr->resp_param1, r1, N);
        assignVectorValues(params_ptr->resp_param2, r3, N);
        assignVectorValues(params_ptr->resp_ch3_param4, u, M);
    }else{
        printf("Error: invalid challenge!\n");
    }
}

int v_check(Coms *coms_ptr, Resp_params *params_ptr, int ch, int matrix_A[][M], int pk_b[N]){
	int result = 0;

	if(ch == 0){
        int computed_aus[N] = {0};
        vectorMultiplyMatrix(computed_aus, params_ptr->resp_ch1_param3, N, M, matrix_A);
        modVector(computed_aus, N, Q);
        //printVector(computed_aus, N);

        int shuffled_aus[N] = {0};
        knuth_shuffle(shuffled_aus, computed_aus, N, coms_ptr->com_c1.sigma);

        int aus_r2_concat[2*N];
        concat_two_arrays(aus_r2_concat, N, shuffled_aus, N, params_ptr->resp_param2);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, aus_r2_concat, sizeof(aus_r2_concat));

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        int sigma_r1_concat[1+N];
        concat_value_and_array(sigma_r1_concat, coms_ptr->com_c1.sigma, N, params_ptr->resp_param1);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigma_r1_concat, sizeof(sigma_r1_concat));

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        //if(areEqual(coms_ptr->com_c1.r1, params_ptr->resp_param1, N) && areEqual(coms_ptr->com_c2.Aus, shuffled_aus, N) && areEqual(coms_ptr->com_c2.r2, params_ptr->resp_param2, N))
        if(com2_result && com1_result)
        	result = 1;
    }else if(ch == 1){
        int computed_aub[N] = {0};
        int shuffled_aub[N] = {0};
        addVectors(computed_aub, params_ptr->resp_ch2_param3, params_ptr->resp_ch2_param4, N);
        modVector(computed_aub, N, Q);
        knuth_shuffle(shuffled_aub, computed_aub, N, coms_ptr->com_c1.sigma);

        int shuffled_aus[N] = {0};
        knuth_shuffle(shuffled_aus, params_ptr->resp_ch2_param3, N, coms_ptr->com_c1.sigma);

        int aus_r2_concat[2*N];
        concat_two_arrays(aus_r2_concat, N, shuffled_aus, N, params_ptr->resp_param2);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, aus_r2_concat, sizeof(aus_r2_concat));

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        int aub_r3_concat[2*N];
        concat_two_arrays(aub_r3_concat, N, shuffled_aub, N, params_ptr->resp_param2);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, aub_r3_concat, sizeof(aub_r3_concat));

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

//        if(areEqual(coms_ptr->com_c2.Aus, shuffled_aus, N) && areEqual(coms_ptr->com_c2.r2, params_ptr->resp_param1, N) && areEqual(coms_ptr->com_c3.Aub, shuffled_aub, N) && areEqual(coms_ptr->com_c3.r3, params_ptr->resp_param2, N))
        if(com2_result && com3_result)
        	result = 1;
    }else if(ch == 2){
        int computed_au[N] = {0};
        int computed_aub[N] = {0};
        vectorMultiplyMatrix(computed_au, params_ptr->resp_ch3_param4, N, M, matrix_A);
        addVectors(computed_aub, computed_au, pk_b, N);
        modVector(computed_aub, N, Q);
        int shuffled_aub[N] = {0};
        knuth_shuffle(shuffled_aub, computed_aub, N, coms_ptr->com_c1.sigma);

        int sigma_r1_concat[1+N];
        concat_value_and_array(sigma_r1_concat, coms_ptr->com_c1.sigma, N, params_ptr->resp_param1);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigma_r1_concat, sizeof(sigma_r1_concat));

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        int aub_r3_concat[2*N];
        concat_two_arrays(aub_r3_concat, N, shuffled_aub, N, params_ptr->resp_param2);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, aub_r3_concat, sizeof(aub_r3_concat));

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

        //if(areEqual(coms_ptr->com_c1.r1, params_ptr->resp_param1, N) && areEqual(coms_ptr->com_c3.Aub, shuffled_aub, N) && areEqual(coms_ptr->com_c3.r3, params_ptr->resp_param2, N))
        if(com1_result && com3_result)
        	result = 1;
    }else{
        result = 0;
    }
    return result;
}

int main(void){
    rng_seed(0xC0FFEEu);

    int matrix_A[N][M] = {0};
    int sk_s[M] = {0};
    int pk_b[N] = {0};
    int errors[N] = {0};

    int r1[N] = {0};
    int r2[N] = {0};
    int r3[N] = {0};
    int u[M] = {0};

    int us[M] = {0};
    int aus[N] = {0};

    Coms coms;
    Coms *coms_ptr = &coms;

    Resp_params params;
    Resp_params *params_ptr = &params;

    printf("----- Silva's ID Scheme -----\n");
    printf("start Key Generation...\n");
    keygen(matrix_A, sk_s, pk_b, errors);
    printf("finish Key Generation.\n");

    printf("Prover (Compute commitments) ...\n");
    p_coms(coms_ptr, r1, r2, r3, u, us, aus, matrix_A, sk_s, pk_b, errors);

    printf("Verifier (Generate challenge) ...\n");
    int ch = v_challenge();

    printf("Challenge: %d\n", ch);
    printf("Prover (Send some parameters) ...\n");
    p_params(params_ptr, ch, r1, r2, r3, u, us, aus, errors);

    printf("Verifier (Check the truthfulness) ...\n");
    int result = v_check(coms_ptr, params_ptr, ch, matrix_A, pk_b);

    if(result == 1)
        printf("Success!\n");
    else
        printf("Failed!\n");

    return 0;
}



