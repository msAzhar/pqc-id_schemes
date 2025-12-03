# C for IoT (Cortex-M4 friendly build)

This directory contains a lightly refactored copy of the C identification scheme demos with deterministic randomness and a cross-compilation toolchain definition for ARM Cortex-M4 microcontrollers.

## Building

* Native (helpful for quick sanity checks):

  ```bash
  make -C c-for-iot
  ```

* Cortex-M4 (uses `arm-none-eabi-gcc` and semihosting via `rdimon`):

  ```bash
  make -C c-for-iot TARGET=arm ARM_TOOLCHAIN_PREFIX=arm-none-eabi
  ```

The resulting executables are placed under `c-for-iot/build/<target>/`.

## Running under QEMU

Each built binary is a freestanding ELF that can be executed with semihosting. After building the ARM target you can either call
`qemu-system-arm` directly or rely on the `run-arm` make target:

```bash
# Build the Cortex-M4 version of every demo
make -C c-for-iot TARGET=arm ARM_TOOLCHAIN_PREFIX=arm-none-eabi

# Run one of the schemes (defaults to kawachi) under QEMU with semihosting
make -C c-for-iot TARGET=arm SCHEME=kawachi run-arm

# Equivalent manual invocation if you prefer to run QEMU yourself
qemu-system-arm -machine mps2-an386 -cpu cortex-m4 -kernel c-for-iot/build/arm/kawachi \
  -semihosting -nographic
```

Pass any additional QEMU flags via `QEMU_EXTRA_ARGS="-d in_asm"` on the make command line if you need tracing. All schemes seed the portable RNG deterministically at startup; adjust the call to `rng_seed()` inside the individual `main` functions if a different seed is desired.
