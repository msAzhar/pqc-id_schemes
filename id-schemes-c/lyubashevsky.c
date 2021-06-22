#include <stdio.h>
#include <stdlib.h>  // rand(), srand()
#include <time.h>    // time()
#include <math.h>
#include <stdbool.h>
#include "idscheme_funcs.h"

#define _SUCCESS_ 0
#define _FAILED_ 1
#define N  608//3
#define M  960//4 
#define Q  1024//3


struct Params
{
    bool terminate;
    int z_param1[M]; // y_tilda
    int z_param2[N]; // cw
    
};

typedef struct Params Zparams;

//void fillParams(Zparams *z);

int keygen(int matrix_A[][M], int q, int sk_x[M], int pk_y[N]);
int p_coms(int len1, int y_tilda[len1], int len2, int y[len2], int matrix_A[][M]);
int v_challenge();
void p_params(Zparams *zptr, int ch, int len1, int y_tilda[len1], int len2, int w_tilda[len2], int len3, int w[len3]);
int v_check(Zparams *zptr, int matrix_A[][M], int y[N]);

double computeNorm(int *v, int size);


double computeNorm(int *v, int size){
    int i;
    double total = 0.0;
    for(i = 0;i<size; i++){
        total += pow(v[i], 2);
    }
    return sqrt(total);
}

//TODO: comment ler yaz!!
int keygen(int matrix_A[][M], int q, int w_tilda[M], int w[N]){
	generateMatrixModQ(N, M, matrix_A, q);
	//printMatrix(N, M, matrix_A);

	generateVector(w_tilda, M, 2);
	//printVector(w_tilda, M);

	vectorMultiplyMatrix(w, w_tilda, N, M, matrix_A);
    //printVector(w, N);

	return 0;
}

//TODO: prover_commitments
int p_coms(int len1, int y_tilda[len1], int len2, int y[len2], int matrix_A[][M]){
	//printf("vector y_tilda:\n");
	generateVector(y_tilda, len1, (5*len1-1));
	//printVector(y_tilda, M);

	//printf("CC1:\n"); //TODO: printtteki yazilari duzenle!!
	vectorMultiplyMatrix(y, y_tilda, N, M, matrix_A);
	//printVector(y, N);

	return 0;
}

int v_challenge() 
{
	int ch;
    srand(time(NULL));
    ch = rand()%2;
    //printf("%d\n", ch);
    return ch;
}

//TODO: M leri degistir!!
void p_params(Zparams *zptr, int ch, int len1, int y_tilda[len1], int len2, int w_tilda[len2], int len3, int w[len3]){

	int sum_y_tilda_and_w_tilda[M] = {0}; //TODO: add alert: "len1 and len2 must be equal"
	addVectors(sum_y_tilda_and_w_tilda, y_tilda, w_tilda, len1);
	//printVector(sum_y_tilda_and_w_tilda, len1);

    bool condition2 = false;
    int i;
    for(i = 0; i<len1; i++){
        if(sum_y_tilda_and_w_tilda[i]<1 || sum_y_tilda_and_w_tilda[i]>(5*len1-1))
            condition2 = true;
    }

    if(ch == 1 && condition2){
        zptr->terminate = true;
        //printf("%s\n", zptr->terminate ? "true" : "false");
    }else {
        int cw[N] = {0};
        scalarMultiplyVector(cw, ch, w, len3);
        assignVectorValues(zptr->z_param1, y_tilda, len1);
        assignVectorValues(zptr->z_param2, cw, len3);
        //printf("z_param1:\n");
        //printVector(zptr->z_param1,len1);
    }
}

int v_check(Zparams *zptr, int matrix_A[][M], int y[N]){
    int result = 0;

    //if(zptr->terminate)
      //  return result;

	double z_norm;
    z_norm = computeNorm(zptr->z_param1, M); //TODO: struct'a M parametersini eklemek?!
    //printf("Z_NORM: %lf\n", z_norm);

    bool condition1 = false, condition2 = false;
    if(z_norm <= 5*pow(M,1.5))
        condition1 = true;

	int comp_Az[N] = {0};
	int comp_cw_add_y[N] = {0};
	vectorMultiplyMatrix(comp_Az, zptr->z_param1, N, M, matrix_A);
	//printVector(comp_Az, N);

    addVectors(comp_cw_add_y, zptr->z_param2, y, N);
	//printVector(comp_cw_add_y, N);

    condition2 = areEqual(comp_Az, comp_cw_add_y, N);

	if(condition1 && condition2){
        result = 1;
    }else{
        result = 0;
    }
    return result;
}

int main(void){
	//srand((unsigned int)time(NULL));
	int matrix_A[N][M] = {0};
	int w_tilda[M] = {0};
	int w[N] = {0};

    int y_tilda[M] = {0};
    int y[N] = {0};

	Zparams z;
    Zparams *zptr = &z;
    int ch;

    printf("--- Lyubashevsky's ID Scheme ---\n");
    printf("start Key Generation...\n");
	keygen(matrix_A, Q, w_tilda, w);
    printf("finish Key Generation.\n");
    printf("Prover (Compute commitments) ...\n");
	p_coms(M, y_tilda, N, y, matrix_A);
    printf("Verifier (Generate challenge) ...\n");
	ch = v_challenge();
	printf("Challenge: %d\n", ch);
    printf("Prover (Send some parameters) ...\n");
	p_params(zptr, ch, M, y_tilda, M, w_tilda, N, w);

	int result;
    printf("Verifier (Check the truthfulness) ...\n");
	result = v_check(zptr, matrix_A, y);
	//printf("Result:%d\n", result);

	if(result == 1)
		printf("Success!\n");
	else
		printf("Failed!\n");	
        
}



