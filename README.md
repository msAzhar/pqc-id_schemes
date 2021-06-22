Framework for Lattice-based ID schemes 
=====================================

Amaç: Kimlik Doğrulama şemaları için bir framework oluşturmak. Bir ID scheme'i, Prover ve Verifier için gerekli olan fonksiyonları çağırarak uygulayabilmeliyiz. 

### Post-Quantum Cryptography: Lattice-based Identification Schemes
Bu çalışmada ele alınan identification scheme'ler, aşağıdaki tabloda dayalı olduğu problemlerle özetlenmiştir.

| ID scheme                                          | Problems 						|
|:---------------------------------------------------|:---------------------------------|
| Lyubashevsky's Identification Scheme               | SVP in all lattices              |
| Kawachi, Tanaka and Xagawa's Identification Scheme | SIS based              			|
| CLRS Identification Scheme (Cayrel et al.)         | SIS based                        |
| Xagawa and Tanaka's Identification Scheme          | based on NTRU and Stern's scheme |
| Silva's LWE-based Identification Scheme                | LWE 								|
| Soysaldi's Identification Scheme                          | SIS based 						|


### Genel bir kimlik doğrulamanın uygulaması için ihtiyaç duyulan fonksiyonlar
- `scalarMultiplyVector`
- `vectorMultiplyMatrix`
- `mod`
- `addVectors`
- `subtractVectors`
- `initMatrixRandom`
- `hw`
- `matrix_invert`
- `sha256`
- `shuffle`

### Kullanım (Javascript)
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

### Test amaçlı implement edilen IDscheme'ler
1. [Kawachi, Tanaka and Xagawa's Identification Scheme]
2. [CLRS Identification Scheme (Cayrel et al.)]
3. [Xagawa and Tanaka's Identification Scheme (based on NTRU and Stern's scheme)]
4. [Silva's LWE-based Identification Scheme]


[Kawachi, Tanaka and Xagawa's Identification Scheme]: ./id-schemes-js/test/xagawa-test.js
[CLRS Identification Scheme (Cayrel et al.)]: ./id-schemes-js/test/clrs-test.js
[Xagawa and Tanaka's Identification Scheme (based on NTRU and Stern's scheme)]: ./id-schemes-js/test/xagawa-ntru-test.js
[Silva's LWE-based Identification Scheme]: ./id-schemes-js/test/lwe-based-idscheme.js

### Kullanım (Java)

### Kullanım (C)



**Not:** Bu çalışma geliştirilmektedir. kodlarda hata ve eksiklikler bulunabilir.  
**Note:** This project is being developed, it may contain mistakes and deficiencies.  
