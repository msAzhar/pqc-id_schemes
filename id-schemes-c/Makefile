CC=gcc
CFLAGS=-I.

all:  silva.c xagawa.c lyubashevsky.c idscheme_funcs.c sha256.c  
	$(CC) -o kawachi kawachi.c sha256.c idscheme_funcs.c  -I
	$(CC) -o silva silva.c sha256.c idscheme_funcs.c  -I
	$(CC) -o xagawa xagawa.c sha256.c idscheme_funcs.c -I
	$(CC) -o lyubashevsky lyubashevsky.c sha256.c idscheme_funcs.c -I

clean: 
	rm -f *.o
	rm -f silva
	rm -f xagawa
	rf -f lyubashevsky
	rf -f kawachi
