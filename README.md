Framework for ID schemes (javascript)
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
Öncellikle, bir IDscheme için gerekli olan fonksiyonların bulunduğu `idscheme.js` dosyası dahil edilmelidir:

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

