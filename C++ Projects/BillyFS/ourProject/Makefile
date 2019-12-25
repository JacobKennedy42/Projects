OK = $(sort $(wildcard *.ok))
TESTS = $(patsubst %.ok,%,$(OK))
RAWS = $(patsubst %.ok,%.raw,$(OK))
OUTS = $(patsubst %.ok,%.out,$(OK))
DIFFS = $(patsubst %.ok,%.diff,$(OK))
RESULTS = $(patsubst %.ok,%.result,$(OK))
CCS = $(patsubst %.ok,%.cc,$(OK))
KERNELS = $(patsubst %.ok,%.kernel,$(OK))

PUBLIC=/u/gheith/public
PUBLIC_PATH=${PUBLIC}/bin
PUBLIC_LDPATH=${PUBLIC}/lib/expect5.45

all : t0.kernel

test : $(RESULTS);

$(TESTS) : % :

$(KERNELS) : %.kernel :
	(cd kernel; rm -f kernel.cc)
	(cd kernel; ln -s ../$*.cc kernel.cc)
	(cd kernel; make clean)
	(cd kernel; make)
	
$(RAWS) : %.raw : .FORCE %.kernel
	test -f $*.img || (test -f $*.src && cp $*.src $*.img) || (dd if=/dev/zero of=$*.img bs=1024 count=8323 > /dev/null 2>&1)
	timeout 20 qemu-system-i386 -nographic -smp 4 --monitor none --serial file:$*.raw -drive file=kernel/kernel.img,index=0,media=disk,format=raw -drive file=$*.img,index=3,media=disk,format=raw -device isa-debug-exit,iobase=0xf4,iosize=0x04 2> /dev/null || true

.FORCE:
	
$(OUTS) : %.out : .FORCE %.raw
	-grep  '^\*\*\*' $*.raw > $*.out 2>&1

$(DIFFS) : %.diff : .FORCE %.out
	-diff -wBb $*.out $*.ok > $*.diff 2>&1

$(RESULTS) : %.result : .FORCE %.diff
	@echo -n "--- $* ... "
	-@(test -s $*.diff && (echo "fail ---" ; echo "look for clues in $*.raw, $*.out, $*.ok, $*.cc"; echo "--- expected ---" ; cat $*.ok; echo "--- found ---" ; cat $*.out)) || (echo "pass ---")

% :
	(make -C kernel $@)

clean:
	rm -rf *.out *.raw *.diff *.result
	(make -C kernel clean)
