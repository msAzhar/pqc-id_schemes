Framework for Lattice-based ID schemes (javascript)
=====================================

Amaç: Kimlik Doğrulama şemaları için bir framework oluşturmak. Bir ID scheme'i, Prover ve Verifier için gerekli olan fonksiyonları çağırarak uygulayabilmeliyiz. 


### Fonksiyonlar
- `scalarMultiplyVector`
- `scalarMultiplyMod`
- `multiply`
- `multiplyMod`
- `vectorSubstract`
- `vectorMultiply`
- `initMatrixRandom`
- `transpose`
- `addMod`
- `hw`
- `encVectorMultiplyMatrix`
- `decVectorMultiplyMatrix`
- `mod`
- `matrix_invert`
- `addVectors`
- `nextInt`
- `sha256`
- `shuffle`
- `knuth_shuffle`

### Kullanım
Öncellikle, bir IDscheme için gerekli olan fonksiyonların bulunduğu `idscheme.js` modülü dahil edilmelidir:

```javascript
var IDscheme = require('./idscheme.js');
```

p1(), p2() şeklideki Prover'ın hesaplama adımları için gerekli fonksiyonlar şu şekil çağrılır:

```javascript
...

function p1(){
	...
	var alphax_vector = IDscheme.scalarMultiplyVector(alpha,x_vector);
	...
}

... 
```

Aynı şekilde, v1(), v2() gibi Verifier için de çağrılır:

```javascript
...

function v2(){
	...
	resp[1] = IDscheme.knuth_shuffle(sk_xt, glob_sigma);
	...
}

...
``` 

### Post-Quantum Cryptography: Lattice-based Identification Schemes
Bu çalışmada referans olarak kullanılan survey'de verilen identification scheme'ler, aşağıdaki tabloda dayalı olduğu problemlerle özetlenmiştir.

| ID scheme                                          | Problems 						|
|:---------------------------------------------------|:---------------------------------|
| Lyubashevsky's Identification Scheme               | SVP in all lattices              |
| Kawachi, Tanaka and Xagawa's Identification Scheme | SIS based              			|
| CLRS Identification Scheme (Cayrel et al.)         | SIS based                        |
| Xagawa and Tanaka's Identification Scheme          | based on NTRU and Stern's scheme |
| LWE-based Identification Scheme                    | LWE 								|
| HB+ Identification Scheme                          | LWE-based 						|

### Test amaçlı implement edilen IDscheme'ler
1. [Kawachi, Tanaka and Xagawa's Identification Scheme]
2. [CLRS Identification Scheme (Cayrel et al.)]
3. [Xagawa and Tanaka's Identification Scheme (based on NTRU and Stern's scheme)]
4. [LWE-based Identification Scheme]

[Kawachi, Tanaka and Xagawa's Identification Scheme]: ./test/xagawa-test.js
[CLRS Identification Scheme (Cayrel et al.)]: ./test/clrs-test.js
[Xagawa and Tanaka's Identification Scheme (based on NTRU and Stern's scheme)]: ./test/xagawa-ntru-test.js
[LWE-based Identification Scheme]: ./test/lwe-based-idscheme.js




**Not:** Bu çalışma geliştirilmektedir. kodlarda hata ve eksiklikler bulunabilir.  
