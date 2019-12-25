// journal.h
#include "stdint.h"
#include "refs.h"


#ifndef _JOURNAL_H_
#define _JOURNAL_H_

#include "billyfs.h"

class BillyFS;
class Node;
class BillyBitmap;
class NodeInfo;

class Journal;


const uint32_t BLOCK_SIZE = 1024;

/*
    txStart:B
    - iNode offset
    - bitmap offset
    - data block offset
    */
    /*
    txEnd:
    - isValid
    */
//The pages of the journal
struct JournalEntry {
    /*TxBegin block, data-bitmap block, an inode table
    block, and a new data block, TxEnd block */
    
    static const uint32_t BLOCK_SIZE = 1024; // FIX THIS SHIT LATER
    static const uint32_t SIZE = BLOCK_SIZE * 5; // TODO FIX THIS SHIT MAKE IT WORK IWTH BOBFS::BLOCK_SIZE
    
    static const uint32_t INODE_BLOCK_DEST_INDEX = 0;
    static const uint32_t BITMAP_BLOCK_DEST_INDEX = 4;
    static const uint32_t DATA_BLOCK_DEST_INDEX = 8;
  
    //TxStart: has the block Nums (formatted like in direct and indirect) to this entry, it's inode, it's bitmap and it's data block
    uint32_t myBase;
    //  this is stored in  offset form
    uint32_t inodeBlock;
    uint32_t bitmapBlock;
    uint32_t dataBlock;
    
    // journal reference
    Journal* parentJournal;
    StrongPtr<BillyFS> fs;

    //The inode
    NodeInfo* myNodeInfo;
    
    //The bitmap
    BillyBitmap* myBitmap;
  
    //Instead of copying data to mem, we copy it into disk, change it in disk, copy it back to bobfs disk
    //uint8_t myDataBlock = new uint8_t[BLOCK_SIZE];

    //TxEnd: has the entry valid bit
    bool valid;

    JournalEntry(Journal* parentJournal, StrongPtr<BillyFS> fs, uint32_t myBase); 
    
    uint32_t getTxStartBlock();              //gets offsets of the journalEntry's blocks
    uint32_t getiNodeBlock(); 
    uint32_t getBitmapBlock();
    uint32_t getDataBlock();
    uint32_t getTxEndBlock();
    bool isValid();
    
    uint32_t getiNodeBlockDest();            //looks in txstart for block numbers of BillyFS blocks 
    uint32_t getBitmapBlockDest();
    uint32_t getDataBlockDest();
    
    void setInodeBlockDest(uint32_t dest);
    void setBitmapBlockDest(uint32_t dest);
    void setDataBlockDest(uint32_t dest);
    
    void copyData(uint32_t blockIndex);      //copy BillyFS's data block to journalEntry's data block
    void copyBitmapBlock(uint32_t srcIndex); //copies either BillyFS's dataBitmap or the inodeBitmap to journalEntry's Bitmap block
    void copyiNodeBlock(uint32_t inumber);   //copies BillyFS iNode block to journalEntry's iNode block
    

    void makeNewNode(uint16_t type, uint16_t nLinks, uint32_t  size,  uint32_t direct, uint32_t indirect, uint32_t iNumber);
    uint32_t write(uint32_t offset, const void* buffer, uint32_t n); //write to the actual data block we want to modify

    void commit(); //writes journal entry containing new changes into the areas we actually want to change
    
    void setValid();
    void setInvalid();
        
    void writeToDisk(); //writes to physical journal
    void free(); // free's the journal entry by setting valid bit to false
    
    
    //TODO getBlockNumber();
    
    // setTxBegin() - block numbers 
    // 
    // 
};
struct Journal {
    /*Superblock,
    Circular buffer, FIFO*/
    const uint32_t NUM_ENTRIES =  20;
    
    StrongPtr<BillyFS> fs;
    //Stored as block nums (like in direct and indirect)
    uint32_t journalBase;
    uint32_t currentEntry;
    uint32_t journalEnd;
    
    Journal(StrongPtr<BillyFS> fs, uint32_t base) : 
        fs(fs), 
        journalBase(base), 
        currentEntry(base), 
        journalEnd(base + (NUM_ENTRIES * (JournalEntry::SIZE))) 
    {

    } //base * num_entries * 5
    
    //uint32_t getNextEntry(); 
    void changeIndirectTable(uint32_t indirectBlockNum, uint32_t index, uint32_t blockNum);
    uint32_t getFreeEntry(); //return the index of the next free transaction in the transactions array
    void repair();
    uint32_t write(uint32_t inumber, uint32_t offset, const void* buffer, uint32_t n); //handles the Node::write() method
    JournalEntry* neuteredWrite(uint32_t nodeInumber, uint32_t offset, const void* buffer, uint32_t n); // handles the Node::write() method

    void crashLinkNodeTest(const char* name, uint32_t parent, uint32_t  child); //handles the Node::linkNode() method 
    void linkNode(const char* name, uint32_t parent, uint32_t  child); //handles the Node::linkNode() method
    //StrongPtr<Node> newNode(uint32_t parentInumber, const char* name, uint16_t type);
    StrongPtr<Node> newNode(uint32_t parentInumber, const char* name, uint16_t type);

    uint32_t getBlockNum (JournalEntry* je, uint32_t blockIndex);
    uint32_t allocateBlock(JournalEntry* je);  //allocates a block in the journalEntry data bitmap


    friend class JournalEntry;
};

#endif
