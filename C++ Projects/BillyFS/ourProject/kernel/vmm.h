#ifndef _VMM_H_
#define _VMM_H_

#include "stdint.h"
#include "atomic.h"
#include "mutex.h"


// The virtual memory interface
namespace VMM {
    constexpr uint32_t FRAME_SIZE = (1 << 12);

    // Called to initialize the available physical memory pool
    void init(uint32_t start, uint32_t size);

};

#endif
