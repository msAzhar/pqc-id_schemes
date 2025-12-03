#ifndef PORTABLE_RNG_H
#define PORTABLE_RNG_H

#include <stdint.h>

void rng_seed(uint32_t seed);
uint32_t rng_next(void);
int rng_uniform(int limit);

#endif  // PORTABLE_RNG_H
