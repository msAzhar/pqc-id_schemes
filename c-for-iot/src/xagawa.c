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
#define N  677
#define Q  1024

struct SecretKey
{
    int x_h[N];
    int x_t[N];
};

struct  PublicKey
{
   int a_h[N];
   int a_t[N];
   int y[N]; 
};

struct Com_c1
{
    int sigma_h;
    int sigma_t;
    int y[N];
    BYTE buf_c1[SHA256_BLOCK_SIZE];
};

struct Com_c2
{
    int r_h[N];
    int r_t[N];
    BYTE buf_c2[SHA256_BLOCK_SIZE];
};

struct Com_c3
{
    int rx_h[N];
    int rx_t[N];
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
    int resp_param3[N]; // 
    int resp_param4[N]; // 
    int resp_param1_int;
    int resp_param2_int;
    
};

typedef struct Commitments Coms;
typedef struct Params Resp_params;

typedef struct SecretKey SKparams;
typedef struct PublicKey PKparams;

int keygen(PKparams *pk_ptr, SKparams *sk_ptr);
int p_coms(int r_h[N], int r_t[N], int rx_h[N], int rx_t[N], Coms *coms_ptr, SKparams *sk_ptr, PKparams *pk_ptr);
int v_challenge(void);
void p_params(int ch, Resp_params *params_ptr, int r_h[N], int r_t[N], int rx_h[N], int rx_t[N], SKparams *sk_ptr, Coms *coms_ptr);
int v_check(int ch, Coms *coms_ptr, Resp_params *params_ptr, PKparams *pk_ptr);

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
int keygen(PKparams *pk_ptr, SKparams *sk_ptr){
    int a_h[N] = {0};
    int a_t[N] = {0};

	generateVector(a_h, N, Q);
	//printVector(a_h, N);

    generateVector(a_t, N, Q);
    //printVector(a_t, N);

    int x_h[N] = {0};
    int x_t[N] = {0};

	size_t i;
    for(int i = 0; i < N/2; i++){
        x_h[i] = 1;
    }

    for(int i = N/2; i < N; i++){
        x_h[i] = 0;
    }
    //TODO: add shuffle function

    x_t[0] = 1;

    for(int i = 1; i < N; i++){
        x_h[i] = 0;
    }

    int y[N] = {0};
    int ax_h[N] = {0};
    int ax_t[N] = {0};

    multVectors(ax_h, a_h, x_h, N);
    multVectors(ax_t, a_t, x_t, N);
    addVectors(y, ax_h, ax_t, N);

    assignVectorValues(pk_ptr->y, y, N); 

    assignVectorValues(sk_ptr->x_h, x_h, N);
    assignVectorValues(sk_ptr->x_t, x_t, N); 
    
	return 0;
}

//TODO: prover_commitments
int p_coms(int r_h[N], int r_t[N], int rx_h[N], int rx_t[N], Coms *coms_ptr, SKparams *sk_ptr, PKparams *pk_ptr){
	generateVector(r_h, N, Q);
    generateVector(r_t, N, Q);
	//printVector(r_h, N);

    int rand_sigma_h = generateSigma(N);
    int rand_sigma_t = generateSigma(N);

    int shuffled_rh[N] = {0};
    int shuffled_rt[N] = {0};

    coms_ptr->com_c1.sigma_h = rand_sigma_h;
    coms_ptr->com_c1.sigma_t = rand_sigma_t;
    assignVectorValues(coms_ptr->com_c1.y, pk_ptr->y, N);

    int sigmas_ht_y_concat[2+N];
    concat_two_values_and_array(sigmas_ht_y_concat, rand_sigma_h, rand_sigma_t, N, pk_ptr->y);

    BYTE local_buf_c1[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c1.buf_c1, sigmas_ht_y_concat, sizeof(sigmas_ht_y_concat));

    knuth_shuffle(shuffled_rh, r_h, N, coms_ptr->com_c1.sigma_h);
    knuth_shuffle(shuffled_rt, r_t, N, coms_ptr->com_c1.sigma_t);

    // printf("Sigma_h: %d\n", rand_sigma_h);
    // printf("Sigma_t: %d\n", rand_sigma_t);
    
    //printf("Shuffled R_h:\n");
    //printVector(shuffled_rh, N);

	assignVectorValues(coms_ptr->com_c2.r_h, shuffled_rh, N);
    assignVectorValues(coms_ptr->com_c2.r_t, shuffled_rt, N);

    // int *rh_rt_concat = malloc(2*N*sizeof(int));
    // memcpy(rh_rt_concat, shuffled_rh, N*sizeof(int));
    // memcpy(rh_rt_concat + N, shuffled_rt, N*sizeof(int));

    int rh_rt_concat[2*N];
    concat_two_arrays(rh_rt_concat, N, shuffled_rh, N, shuffled_rt);

    BYTE local_buf_c2[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c2.buf_c2, rh_rt_concat, sizeof(rh_rt_concat));

    int shuffled_rx_h[N] = {0};
    int shuffled_rx_t[N] = {0};

    addVectors(rx_h, r_h, sk_ptr->x_h, N);
    addVectors(rx_t, r_t, sk_ptr->x_t, N);

    knuth_shuffle(shuffled_rx_h, rx_h, N, rand_sigma_h);
    knuth_shuffle(shuffled_rx_t, rx_t, N, rand_sigma_t);

    int rxh_rxt_concat[2*N];
    concat_two_arrays(rxh_rxt_concat, N, shuffled_rx_h, N, shuffled_rx_t);

    BYTE local_buf_c3[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c3.buf_c3, rxh_rxt_concat, sizeof(rxh_rxt_concat));

    assignVectorValues(coms_ptr->com_c3.rx_h, shuffled_rx_h, N);
    assignVectorValues(coms_ptr->com_c3.rx_t, shuffled_rx_t, N);

	return 0;
}

int v_challenge(void)
{
    return rng_uniform(3);
}

//TODO: M leri degistir!!
void p_params(int ch, Resp_params *params_ptr, int r_h[N], int r_t[N], int rx_h[N], int rx_t[N], SKparams *sk_ptr, Coms *coms_ptr){

    if(ch == 0){
        int shuffled_xh[N] = {0};
        int shuffled_xt[N] = {0};

        int shuffled_rh[N] = {0};
        int shuffled_rt[N] = {0};

        knuth_shuffle(shuffled_xh, sk_ptr->x_h, N, coms_ptr->com_c1.sigma_h);
        knuth_shuffle(shuffled_xt, sk_ptr->x_t, N, coms_ptr->com_c1.sigma_t);

        // ---- no need ----
        knuth_shuffle(shuffled_rh, r_h, N, coms_ptr->com_c1.sigma_h);
        knuth_shuffle(shuffled_rt, r_t, N, coms_ptr->com_c1.sigma_t);
        // -----------------

        assignVectorValues(params_ptr->resp_param1, shuffled_xh, N);
        assignVectorValues(params_ptr->resp_param2, shuffled_xt, N);
        assignVectorValues(params_ptr->resp_param3, shuffled_rh, N);
        assignVectorValues(params_ptr->resp_param4, shuffled_rt, N);
    }else if(ch == 1){
        params_ptr->resp_param1_int = coms_ptr->com_c1.sigma_h;
        params_ptr->resp_param2_int = coms_ptr->com_c1.sigma_t;
        assignVectorValues(params_ptr->resp_param3, rx_h, N);
        assignVectorValues(params_ptr->resp_param4, rx_t, N);
    }else if(ch == 2){
        params_ptr->resp_param1_int = coms_ptr->com_c1.sigma_h;
        params_ptr->resp_param2_int = coms_ptr->com_c1.sigma_t;
        assignVectorValues(params_ptr->resp_param3, r_h, N);
        assignVectorValues(params_ptr->resp_param4, r_t, N);
    }else{
        printf("Error: invalid challenge!\n");
    }
}

int v_check(int ch, Coms *coms_ptr, Resp_params *params_ptr, PKparams *pk_ptr){
	int result = 0;

	if(ch == 0){
        int rx_h[N] = {0};
        int rx_t[N] = {0};
        addVectors(rx_h, params_ptr->resp_param3, params_ptr->resp_param1, N);
        addVectors(rx_t, params_ptr->resp_param4, params_ptr->resp_param2, N);

        int rh_rt_concat[2*N];
        concat_two_arrays(rh_rt_concat, N, params_ptr->resp_param3, N, params_ptr->resp_param4);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, rh_rt_concat, sizeof(rh_rt_concat));

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        int rxh_rxt_concat[2*N];
        concat_two_arrays(rxh_rxt_concat, N, rx_h, N, rx_t);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, rxh_rxt_concat, sizeof(rxh_rxt_concat));

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

        if(com2_result && com3_result)
        	result = 1;

    }else if(ch == 1){
        int shuffled_rx_h[N] = {0};
        int shuffled_rx_t[N] = {0};

        knuth_shuffle(shuffled_rx_h, params_ptr->resp_param3, N, params_ptr->resp_param1_int);
        knuth_shuffle(shuffled_rx_t, params_ptr->resp_param4, N, params_ptr->resp_param2_int);

        int rxh_rxt_concat[2*N];
        concat_two_arrays(rxh_rxt_concat, N, shuffled_rx_h, N, shuffled_rx_t);

        BYTE local_buf_c3[SHA256_BLOCK_SIZE];
        com_func(local_buf_c3, rxh_rxt_concat, sizeof(rxh_rxt_concat));

        int com3_result = coms_equal(coms_ptr->com_c3.buf_c3, local_buf_c3);

        int sigmas_ht_y_concat[2+N];
        concat_two_values_and_array(sigmas_ht_y_concat, params_ptr->resp_param1_int, params_ptr->resp_param2_int, N, pk_ptr->y);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigmas_ht_y_concat, sizeof(sigmas_ht_y_concat));

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        if(com1_result && com3_result)
        	result = 1;
    }else if(ch == 2){
        int shuffled_rh[N] = {0};
        int shuffled_rt[N] = {0};

        knuth_shuffle(shuffled_rh, params_ptr->resp_param3, N, coms_ptr->com_c1.sigma_h);
        knuth_shuffle(shuffled_rt, params_ptr->resp_param4, N, coms_ptr->com_c1.sigma_t);

        int rh_rt_concat[2*N];
        concat_two_arrays(rh_rt_concat, N, shuffled_rh, N, shuffled_rt);

        BYTE local_buf_c2[SHA256_BLOCK_SIZE];
        com_func(local_buf_c2, rh_rt_concat, sizeof(rh_rt_concat));

        int com2_result = coms_equal(coms_ptr->com_c2.buf_c2, local_buf_c2);

        int sigmas_ht_y_concat[2+N];
        concat_two_values_and_array(sigmas_ht_y_concat, params_ptr->resp_param1_int, params_ptr->resp_param2_int, N, pk_ptr->y);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, sigmas_ht_y_concat, sizeof(sigmas_ht_y_concat));

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

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

    int r_h[N] = {0};
    int r_t[N] = {0};

    int rx_h[N] = {0};
    int rx_t[N] = {0};

    printf("--- Xagawa's ID Scheme ---\n");
    printf("start Key Generation...\n");
    keygen(pk_ptr, sk_ptr);
    printf("finish Key Generation.\n");

    printf("Prover (Compute commitments) ...\n");
    p_coms(r_h, r_t, rx_h, rx_t, coms_ptr, sk_ptr, pk_ptr);
    printf("Verifier (Generate challenge) ...\n");
    int ch = v_challenge();
    printf("Challenge: %d\n", ch);
    printf("Prover (Send some parameters) ...\n");
    p_params(ch, params_ptr, r_h, r_t, rx_h, rx_t, sk_ptr, coms_ptr);

    printf("Verifier (Check the truthfulness) ...\n");
    int result = v_check(ch, coms_ptr, params_ptr, pk_ptr);

    if(result == 1)
        printf("Success!\n");
    else
        printf("Failed!\n");

    return 0;
}



