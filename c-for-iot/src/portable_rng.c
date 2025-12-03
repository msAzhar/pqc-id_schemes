#include "portable_rng.h"

static uint32_t rng_state = 0x6d2a79f5u;

void rng_seed(uint32_t seed) {
    rng_state = seed ? seed : 0x6d2a79f5u;
}

uint32_t rng_next(void) {
    rng_state ^= rng_state << 13;
    rng_state ^= rng_state >> 17;
    rng_state ^= rng_state << 5;
    return rng_state;
}

int rng_uniform(int limit) {
    if (limit <= 0) {
        return 0;
    }

    return (int)(rng_next() % (uint32_t)limit);
}
