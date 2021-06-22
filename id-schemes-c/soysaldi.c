#include <stdio.h>
#include <stdlib.h>  // rand(), srand()
#include <time.h>    // time()
#include <stdbool.h>
#include "sha256.h"
#include "idscheme_funcs.h"

#define NELEMS(x)  (sizeof(x) / sizeof((x)[0]))


#define _SUCCESS_ 0
#define _FAILED_ 1
#define N  512 // y-rows
#define M  2048 // x-cols
#define Q  257

struct SecretKey
{
    int x[M];
};

struct  PublicKey
{
   int y[N];
};

struct Com_c0
{
    int pi;
    int rand_r[M];
    int seed1[N];
    BYTE buf_c0[SHA256_BLOCK_SIZE];
};

struct Com_c1
{
    int pi;
    int vector_Ar[N];
    int seed2[N];
    BYTE buf_c1[SHA256_BLOCK_SIZE];
};

struct Commitments
{
    struct Com_c0 com_c0;
    struct Com_c1 com_c1;
};

struct Params
{
    int resp_param1[M]; // 
    int resp_param2_c0[M]; //
    int resp_param2_c1[N]; 
    int resp_param3[N];
};

struct Challenges
{
    int alpha;
    int ch;
};

typedef struct Commitments Coms;
typedef struct Params Resp_params;
typedef struct Challenges VerifierChallenges;

typedef struct SecretKey SKparams;
typedef struct PublicKey PKparams;

int keygen(PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M]);
int p_coms(Coms *coms_ptr, PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M], int random_r[M]);
void v_challenge(VerifierChallenges *verifier_resp_ptr);
void p_params(VerifierChallenges *verifier_resp_ptr, Resp_params *params_ptr, SKparams *sk_ptr, Coms *coms_ptr, int random_r[M], int matrix_A[][M]);
int v_check(VerifierChallenges *verifier_resp_ptr, Resp_params *params_ptr, PKparams *pk_ptr, Coms *coms_ptr, int matrix_A[][M]);
void printMatrix(int rows, int cols, int matrix_A[][cols]);

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

	generateVector(sk_x, M, 2);
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
int p_coms(Coms *coms_ptr, PKparams *pk_ptr, SKparams *sk_ptr, int matrix_A[][M], int random_r[M]){
    int local_rand_r[M] = {0};
    int local_seed1[N] = {0};
    int local_seed2[N] = {0}; 
    int comp_Ar[N] = {0};
	//printf("vector r:\n");
	generateVector(local_rand_r, M, Q);
	//printVector(rand_r, M);
    generateVector(local_seed1, N, 2);
    generateVector(local_seed2, N, 2);

    int rand_pi = generateSigma(Q);

	vectorMultiplyMatrix(comp_Ar, local_rand_r, N, M, matrix_A);
	modVector(comp_Ar, N, Q);
	//printVector(comp_Ar, N);

    int shuffled_r[M] = {0};
    knuth_shuffle(shuffled_r, local_rand_r, M, rand_pi);

    assignVectorValues(random_r, local_rand_r, M);

    assignVectorValues(coms_ptr->com_c0.rand_r, shuffled_r, M);
    assignVectorValues(coms_ptr->com_c0.seed1, local_seed1, N);
    coms_ptr->com_c0.pi = rand_pi;

    int r_seed1_concat[M+N];
    concat_two_arrays(r_seed1_concat, M, shuffled_r, N, local_seed1);

    BYTE local_buf_c0[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c0.buf_c0, r_seed1_concat);


    coms_ptr->com_c1.pi = rand_pi;
    assignVectorValues(coms_ptr->com_c1.vector_Ar, comp_Ar, N);
    assignVectorValues(coms_ptr->com_c1.seed2, local_seed2, N);

    int pi_r_seed2_concat[1+N+N];
    concat_value_and_two_arrays(pi_r_seed2_concat, rand_pi, N, comp_Ar, N, local_seed2);

    BYTE local_buf_c1[SHA256_BLOCK_SIZE];
    com_func(coms_ptr->com_c1.buf_c1, pi_r_seed2_concat);
    

	return 0;
}

void v_challenge(VerifierChallenges *verifier_resp_ptr) 
{
	int new_ch;
    srand(time(NULL));
    new_ch = rand()%2;
    //printf("%d\n", ch);
    verifier_resp_ptr->ch = new_ch;

    int new_alpha;
    new_alpha = generateSigma(Q);
    verifier_resp_ptr->alpha = new_alpha;
}

//TODO: M leri degistir!!
void p_params(VerifierChallenges *verifier_resp_ptr, Resp_params *params_ptr, SKparams *sk_ptr, Coms *coms_ptr, int random_r[M], int matrix_A[][M])  //int param1[M], int param2[M], int ch, int sk_x[M], int rand_r[M])
{
    int ch = verifier_resp_ptr->ch;
    
    if(ch == 0){
        int beta0[M] = {0};
        int sx[M] = {0};

        int alpha_x[M] = {0};
        int r_alphax[M] = {0};
        int shuffled_r_alphax[M] = {0};

        knuth_shuffle(sx, sk_ptr->x, M, coms_ptr->com_c1.pi);

        scalarMultiplyVector(alpha_x, verifier_resp_ptr->alpha, sk_ptr->x, M);
        addVectors(r_alphax, alpha_x, random_r, M);  
        //modVector(r_alphax, M, Q);
        knuth_shuffle(shuffled_r_alphax, r_alphax, M, coms_ptr->com_c0.pi);  

        assignVectorValues(params_ptr->resp_param1, sx, M);
        assignVectorValues(params_ptr->resp_param2_c0, shuffled_r_alphax, M);
        assignVectorValues(params_ptr->resp_param3, coms_ptr->com_c0.seed1, N);
    }else if(ch == 1){
        int beta1[N] = {0};
        int fix[M] = {0};

        int alpha_x[M] = {0};
        int r_alphax[M] = {0};
        int a_ralphax[N] = {0};

        knuth_shuffle(fix, sk_ptr->x, M, coms_ptr->com_c1.pi);

        scalarMultiplyVector(alpha_x, verifier_resp_ptr->alpha, sk_ptr->x, M);
        modVector(alpha_x, M, Q);
        addVectors(r_alphax, alpha_x, random_r, M);  
        modVector(r_alphax, M, Q);
        vectorMultiplyMatrix(a_ralphax, r_alphax, N, M, matrix_A);
        modVector(a_ralphax, M, Q);

        assignVectorValues(params_ptr->resp_param1 , fix, M);
        assignVectorValues(params_ptr->resp_param2_c1, a_ralphax, N);
        assignVectorValues(params_ptr->resp_param3, coms_ptr->com_c1.seed2, N);
    }else{
        printf("Error: invalid challenge!\n");
    }
}

int v_check(VerifierChallenges *verifier_resp_ptr, Resp_params *params_ptr, PKparams *pk_ptr, Coms *coms_ptr, int matrix_A[][M]){
    //int c1[N], int c2[M], int c3[M], int ch, int param1[M], int param2[M], int matrix_A[][M], int sk_x[M], int pk_y[N], int rand_r[M]
	int result = 0;
    int ch = verifier_resp_ptr->ch;

	if(ch == 0){
        int alphas[M] = {0};
        int sub_beta0_alphas[M] = {0};

        scalarMultiplyVector(alphas, verifier_resp_ptr->alpha, params_ptr->resp_param1, M);
        //modVector(alphas, M, Q);
        subVectors(sub_beta0_alphas, params_ptr->resp_param2_c0, alphas, M);
        modVector(sub_beta0_alphas, M, Q);

        int local_r_seed1_concat[M+N];
        concat_two_arrays(local_r_seed1_concat, M, sub_beta0_alphas, N, params_ptr->resp_param3);

        BYTE local_buf_c0[SHA256_BLOCK_SIZE];
        com_func(local_buf_c0, local_r_seed1_concat);

        int com0_result = coms_equal(coms_ptr->com_c0.buf_c0, local_buf_c0);

        if(com0_result && 1)
        	result = 1;
    }else if(ch == 1){
        int alphay[N] = {0};
        int sub_beta1_alphay[N] = {0};

        scalarMultiplyVector(alphay, verifier_resp_ptr->alpha, pk_ptr->y, N);
        subVectors(sub_beta1_alphay, params_ptr->resp_param2_c1, alphay, N);
        modVector(sub_beta1_alphay, N, Q);

        int local_pi_r_seed2_concat[1+N+N];
        concat_value_and_two_arrays(local_pi_r_seed2_concat, coms_ptr->com_c1.pi, N, sub_beta1_alphay, N, params_ptr->resp_param3);

        BYTE local_buf_c1[SHA256_BLOCK_SIZE];
        com_func(local_buf_c1, local_pi_r_seed2_concat);

        int com1_result = coms_equal(coms_ptr->com_c1.buf_c1, local_buf_c1);

        if(com1_result && 1)
        	result = 1;
    }else{
        result = 0;
    }
    return result;
}

int main(void){
	//srand((unsigned int)time(NULL));
    SKparams sk_params;
    SKparams *sk_ptr = &sk_params;

    PKparams pk_params;
    PKparams *pk_ptr = &pk_params;

    Coms coms;
    Coms *coms_ptr = &coms;

    Resp_params params;
    Resp_params *params_ptr = &params;

    VerifierChallenges verifier_resp;
    VerifierChallenges *verifier_resp_ptr = &verifier_resp;

	static int matrix_A[N][M] = {0};
    static int random_r[M] = {0};

    printf("--- Soydsaldi's ID Scheme ---\n");
    printf("start Key Generation...\n");
	keygen(pk_ptr, sk_ptr, matrix_A);
    printf("finish Key Generation.\n");
    printf("Prover (Compute commitments) ...\n");
	p_coms(coms_ptr, pk_ptr, sk_ptr, matrix_A, random_r);
    printf("Verifier (Generate challenge) ...\n");
	v_challenge(verifier_resp_ptr);
	printf("Challenge: %d\n", verifier_resp_ptr->ch);
    printf("Prover (Send some parameters) ...\n");
	p_params(verifier_resp_ptr, params_ptr, sk_ptr, coms_ptr, random_r, matrix_A);

	int result;
    printf("Verifier (Check the truthfulness) ...\n");
    result = v_check(verifier_resp_ptr, params_ptr, pk_ptr, coms_ptr, matrix_A);
	//printf("Result:%d\n", result);

	if(result == 1)
		printf("Success!\n");
	else
		printf("Failed!\n");	
}



