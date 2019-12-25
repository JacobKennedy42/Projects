
#include "heap.h"
#include "debug.h"
#include "stdint.h"
#include "atomic.h"

//pos==free
//neg==used

//////////////////////// structs and stuff ////////////////////////
struct header {
    int32_t size;
    header* prev;
    header* next;
} __attribute__ ((packed));
typedef struct header header;

struct footer{
    int32_t size;
} __attribute__ ((packed));
typedef struct footer footer;

//////////////////////// constants ////////////////////////
const int32_t H_SIZE = 4;
const int32_t F_SIZE = 4;
const int32_t FREE_NODE_SIZE = 16;

//////////////////////// global variables ////////////////////////
void*   heapPtr;  // constant. points to curr top of heap, aka where free space starts
int32_t heapSize; // constant. total size of the heap
header* listPtr;  // pointer to front of linked list / list head

//////////////////////// random functions ////////////////////////
int32_t absVal(int32_t num) {
    return num >= 0 ? num : -num;
}

void* incrementPtr(void* p, int32_t numBytes){
    return static_cast<char*>(p) + numBytes;
}

int32_t align(int32_t num) {
    int32_t remainder = num%4;
    switch(remainder){
        case 1: num+=3;
        break;
        case 2: num+=2;
        break;
        case 3: num+=1;
    }
    return num;
}

bool ptrInBounds(void* ptr){
    return ptr >= heapPtr && ptr <= static_cast<char*>(heapPtr)+heapSize;
}

footer* getFooter(void* headPtr){
    header* h = (static_cast<header*>(headPtr));
    return static_cast<footer*>(incrementPtr(headPtr, H_SIZE+absVal(h->size)));
}
header* getHeader(void* footPtr){
    footer* f = static_cast<footer*>(footPtr);
    return static_cast<header*>(incrementPtr(f, -(H_SIZE+absVal(f->size))));
}

header* getRightHeader(void* currHead){
    int32_t size = static_cast<header*>(currHead)->size;
    header* right = static_cast<header*>(incrementPtr(currHead, H_SIZE+absVal(size)+F_SIZE));
    if(ptrInBounds(right))
        return right;
    else
        return nullptr;
}

header* getLeftHeader(void* currHead){
    footer* leftFooter = static_cast<footer*>(incrementPtr(currHead, -F_SIZE));
    header* leftHeader = static_cast<header*>(getHeader(leftFooter));
    if(ptrInBounds(leftFooter))
        return leftHeader;
    else
        return nullptr;
}

void setFooter(void* head, int32_t newSize){
    footer* ftr = static_cast<footer*>(getFooter(head));
    ftr->size = newSize;
}

//////////////////////// functions and stuff ////////////////////////

void printNode(void* p) {
    header h = *(static_cast<header*>(p));
    footer f = *(getFooter(p));
    if(h.size!=f.size) {
        Debug::printf("*** ?????????????????????????????header not match footer?????????????????????????????????\n");
    }
    Debug::printf("*** %p %p %p:", p, h.prev, h.next);
    Debug::printf(" header size: %i ",  h.size);
    Debug::printf(" footer size: %i\n", f.size);
}
void printHeap(){
    Debug::printf("*** --------printing heap--------\n");
    void* i = heapPtr;
    while(i<(static_cast<char*>(heapPtr)+heapSize)){
        header h = *(static_cast<header*>(i));
        printNode(i);
        i = incrementPtr(i, absVal(h.size)+H_SIZE+F_SIZE);
    }
    Debug::printf("*** --------printing heap--------\n");
}
void printLinkedList(){
    Debug::printf("*** --------printing free list--------\n");
    header* i = listPtr;
    while(i!=nullptr){
        printNode(i);
        i = i->next;
    }
    Debug::printf("*** --------printing free list--------\n");
}
void checkHeap(){
    int32_t numFreeNodes = 0;
    int32_t numHeads=0;
    int32_t numLastNodes=0;
    int32_t totalSize=0;

    // go thru heap and count free nodes
    void* i = heapPtr;
    while(i<(static_cast<char*>(heapPtr)+heapSize)){
        header h = *(static_cast<header*>(i));
        if (h.size > 0)
            numFreeNodes++;
        if(h.prev == nullptr && h.next != nullptr && h.size > 0)
            numHeads++;
        else if(h.prev != nullptr && h.next == nullptr && h.size > 0)
            numLastNodes++;
        totalSize+=(absVal(h.size)+H_SIZE+F_SIZE);
        i = incrementPtr(i, absVal(h.size)+H_SIZE+F_SIZE);
    }
    // go thru free list and count free nodes
    header* j = listPtr;
    while(j!=nullptr){
        numFreeNodes--;
        j = j->next;
    }
    if(numFreeNodes != 0)
        Debug::printf("*** ??????????????????????????????? free node count aint matching up\n");
    if(numHeads>1)
        Debug::printf("*** MORE THAN ONE HEAD???????????????????????\n");
    if(numLastNodes>1)
        Debug::printf("*** MORE THAN ONE LAST NODE???????????????????????\n");
    if(totalSize!=heapSize){
        Debug::printf("*** HEAP SIZE NOT CORRECT???????????????????????\n");
        Debug::printf("*** num expected: %i\n", heapSize);
        Debug::printf("*** num actual: %i\n", totalSize);
    }
    if(numFreeNodes != 0 || numHeads>1 || numLastNodes>1 || totalSize!=heapSize){
        printHeap();
        printLinkedList();
    }
}

void heapInit(void* base, size_t bytes) {
    heapPtr  = base;
    heapSize = static_cast<int32_t>(bytes);

    // make whole heap into one free block
    listPtr  = static_cast<header*>(base);
    int32_t size = heapSize - H_SIZE - F_SIZE;
    listPtr[0] = {size, nullptr, nullptr};
    setFooter(listPtr, size);

    //Debug::printf("*** heap initialized\n");
}

void* perfectFitMalloc(header* blockToTake){
    // Debug::printf("*** perfectFitMalloc \n");
    // give away its pointers, aka remove this block from free list 
    // if was original list head, transfer headship to its next
    // turn free block into used block

    blockToTake->prev->next = blockToTake->next;
    blockToTake->next->prev = blockToTake->prev;

    if (listPtr == blockToTake)
        listPtr = blockToTake->next;

    blockToTake[0] = {-(blockToTake->size), nullptr, nullptr};
    setFooter(blockToTake, blockToTake->size);

    // checkHeap();

    return incrementPtr(blockToTake, H_SIZE);
}

void* splitMalloc(int32_t reqBytes, header* blockToSplit){
    // Debug::printf("*** gotta split, wanna malloc %i + 8 bytes: \n", reqBytes);
    // req bytes no include overhead!!!!!!!!
    // edit left free block stats
    // edit right used block stats

    int32_t blockToSplitSize = blockToSplit->size;

    blockToSplit->size = blockToSplitSize-reqBytes-H_SIZE-F_SIZE;
    setFooter(blockToSplit, blockToSplitSize-reqBytes-H_SIZE-F_SIZE);

    header* newHead = getRightHeader(blockToSplit);
    newHead[0] = {-reqBytes, nullptr, nullptr};
    setFooter(newHead, -reqBytes);

    // checkHeap();

    return incrementPtr(newHead, H_SIZE);
}

void* malloc(size_t bytes) {
    int32_t reqBytes = static_cast<int32_t>(bytes);
    // malloc(invalid)
    if(reqBytes<0)
        return nullptr;

    // malloc(0)
    if(reqBytes==0)
        return heapPtr;

    // malloc(0<x<=8)
    if(reqBytes < 8)
        reqBytes = 8;
    reqBytes = align(reqBytes);

    header* curr = listPtr;
    while(curr!=nullptr){
        if (curr->size >= reqBytes){
            // if leftover shreds cant hold new node w data
            if (curr->size - reqBytes <= FREE_NODE_SIZE)
                return perfectFitMalloc(curr);

            // if found block is extra space
            if (curr->size > reqBytes+H_SIZE+F_SIZE)
                return splitMalloc(reqBytes, curr);
        }
        else
            curr = curr->next;
    }
    // couldn't find fitting block
    return nullptr;
}

void mergeLeft(header* curr) {
    // Debug::printf("*** LEFT merge ONLY \n");
    // left is free, curr is used
    // so only update left's size

    header* left = getLeftHeader(curr);
    left->size = left->size + F_SIZE + H_SIZE + absVal(curr->size); 
    setFooter(left, left->size);
    // checkHeap();
}

void mergeRight(header* curr) {
    // Debug::printf("*** RIGHT merge ONLY \n");
    // curr is used, right is free
    // make all of curr's stats = right's
    // make all ptrs to right point to curr
    // if right was list head, make curr new list head

    header* right = getRightHeader(curr);

    curr[0] = {absVal(curr->size)+F_SIZE+H_SIZE+right->size, right->prev,right->next};
    setFooter(curr, curr->size);

    curr->prev->next = curr;
    curr->next->prev = curr;

    if (listPtr == right)
        listPtr = curr;

    // checkHeap();
}

void mergeBothWays(header* curr) {
    // Debug::printf("*** BOTH sides merge \n");
    // left is free, curr is used, right is free
    // remove right from free list and give away its ptrs
    // give potential headership to its next
    // merge right to the left

    mergeLeft(curr);

    header* right = getRightHeader(curr);

    header* prevBlock = right->prev;
    header* nextBlock = right->next;
    right->prev->next = nextBlock;
    right->next->prev = prevBlock;

    if (listPtr == right)
        listPtr = right->next;

    mergeLeft(right);
    
    // checkHeap();
}
    
void addToFreeNodeList(header* curr){
    // Debug::printf("*** no merge, add to beginning of free list \n");
    // if no adjacent free nodes to merge with, add node to beginning of list
    // make its prev null and its next what listptr originally was
    // make it the new head

    curr[0] = {absVal(curr->size), nullptr, listPtr};
    setFooter(curr, absVal(curr->size));
    listPtr->prev = curr;
    listPtr = curr;

    // checkHeap();
}

void free(void* p) {

    header* hdr = static_cast<header*>(incrementPtr(p, -H_SIZE));
    if (ptrInBounds(hdr)) {
        header* right = getRightHeader(hdr);
        header* left  = getLeftHeader(hdr);

        if(right->size > 0 && left->size >0){
            mergeBothWays(hdr);
            return;
        } 
        if(left->size > 0){
            mergeLeft(hdr);
            return;
        }
        if(right->size > 0){
            mergeRight(hdr);
            return;
        }
        addToFreeNodeList(hdr);

    }
    
}
/*****************/
/* C++ operators */
/*****************/

void* operator new(size_t size) {
    void* p =  malloc(size);
    if (p == 0) Debug::panic("out of memory");
    return p;
}

void operator delete(void* p) noexcept {
    return free(p);
}

void operator delete(void* p, size_t sz) {
    return free(p);
}

void* operator new[](size_t size) {
    void* p =  malloc(size);
    if (p == 0) Debug::panic("out of memory");
    return p;
}

void operator delete[](void* p) noexcept {
    return free(p);
}

void operator delete[](void* p, size_t sz) {
    return free(p);
}
