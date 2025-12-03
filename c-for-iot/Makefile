SCHEMES := kawachi lyubashevsky silva soysaldi xagawa
SCHEME ?= kawachi
COMMON_SRCS := src/sha256.c src/idscheme_funcs.c src/portable_rng.c

TARGET ?= native
BUILD_DIR = build/$(TARGET)
ARM_TOOLCHAIN_PREFIX ?= arm-none-eabi
QEMU_SYSTEM_ARM ?= qemu-system-arm
QEMU_MACHINE ?= mps2-an386
QEMU_CPU ?= cortex-m4
QEMU_EXTRA_ARGS ?=

CC_native := gcc
CFLAGS_native := -std=c11 -Wall -Wextra -Iinclude
LDFLAGS_native :=
LDLIBS_native := -lm

CC_arm := $(ARM_TOOLCHAIN_PREFIX)-gcc
CFLAGS_arm := -std=c11 -Wall -Wextra -Iinclude -mcpu=cortex-m4 -mthumb \
  -ffreestanding -fno-builtin -fdata-sections -ffunction-sections
LDFLAGS_arm := -Wl,--gc-sections -specs=rdimon.specs -lc -lrdimon
LDLIBS_arm := -lm

CC = $(CC_$(TARGET))
CFLAGS = $(CFLAGS_$(TARGET))
LDFLAGS = $(LDFLAGS_$(TARGET))
LDLIBS = $(LDLIBS_$(TARGET))

BINARIES = $(addprefix $(BUILD_DIR)/,$(SCHEMES))

.PHONY: all native arm clean

all: native

native: TARGET := native
native: $(BINARIES)

arm: TARGET := arm
arm: $(BINARIES)

run-arm: TARGET := arm
run-arm: $(BUILD_DIR)/$(SCHEME)
	$(QEMU_SYSTEM_ARM) -machine $(QEMU_MACHINE) -cpu $(QEMU_CPU) -kernel $< -semihosting -nographic $(QEMU_EXTRA_ARGS)

$(BUILD_DIR):
	mkdir -p $@

$(BUILD_DIR)/%: src/%.c $(COMMON_SRCS) | $(BUILD_DIR)
	$(CC) $(CFLAGS) $^ -o $@ $(LDFLAGS) $(LDLIBS)

clean:
	rm -rf build
