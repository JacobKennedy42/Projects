#include "vmm.h"
#include "machine.h"
#include "idt.h"
#include "libk.h"
#include "mutex.h"
#include "config.h"
#include "threads.h"
#include "debug.h"

struct VMMInfo {
};

static VMMInfo *info = nullptr;

void VMM::init(uint32_t start, uint32_t size) {
    Debug::printf("| physical range 0x%x 0x%x\n",start,start+size);
    info = new VMMInfo;

    //MISSING();

    /* register the page fault handler */
    IDT::trap(14,(uint32_t)pageFaultHandler_,3);
}


extern "C" void vmm_pageFault(uintptr_t va, uintptr_t *saveState) {
    //Debug::printf("| page fault @ %x\n",va);
    MISSING();
}
