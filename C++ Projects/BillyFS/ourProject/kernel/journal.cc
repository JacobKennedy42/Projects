// journal.cc

#include "journal.h"
#include "machine.h"
#include "heap.h"

static uint8_t zero_1024[BillyFS::BLOCK_SIZE];


uint32_t strlen(const char* st){
    uint32_t len = 0;
    while(*st!='\0'){
        len++;
        st++;
    }
    return len;
}

//underlying implementation of overloaded diskcpy
void diskCpy(StrongPtr<BillyFS> fs, uint32_t dest, uint32_t src, uint32_t nBytes)
{
    uint8_t* buffer = new uint8_t[nBytes];
    fs->device->readAll(src, buffer, nBytes);
    fs->device->writeAll(dest, buffer, nBytes);
}

//alternative to memcpying, copies disk memory to disk memory
void diskCpy (StrongPtr<BillyFS> fs, uint32_t dest, uint32_t src)
{
    diskCpy(fs, dest, src, BillyFS::BLOCK_SIZE);
}

///////////////////
// Journal Entry //
///////////////////

JournalEntry::JournalEntry(Journal* parentJournal, StrongPtr<BillyFS> fs, uint32_t myBase) :
myBase(myBase), 
parentJournal(parentJournal),
fs(fs)
{
    myNodeInfo = new NodeInfo();   
}


void JournalEntry::commit() // copies journal data that represents new change into the area that we actually want to change
{
   	//Debug::printf("***%s\n", this->isValid() ? "Yes" : "No");   
 
	// first, let's make sure this entry is valid, just incase ;)
    if(!this->isValid()) Debug::panic("o fuk Its not valid \n");

    // let's read the blocks to blocks to copy into
    uint32_t iNodeDest     = getiNodeBlockDest();
    uint32_t bitmapDest    = getBitmapBlockDest();
    uint32_t dataBlockDest = getDataBlockDest();

        uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted INDOE BLOCK is %d\n",  expectedDataBlock);

    Debug::printf("inode dest at %x\n", iNodeDest);
    Debug::printf("bitamp dest at %x\n", bitmapDest);
    Debug::printf("datablockDest dest at %x\n", dataBlockDest);
    
    Debug::printf("inode block is source at %x\n", this->getiNodeBlock());
    Debug::printf("bitmap block is source at %x\n", this->getBitmapBlock());
    Debug::printf("data block is source at %x\n", this->getDataBlock());

	// now let's copy in our blocks
    if (iNodeDest != 0xffffffff)
    {
        diskCpy(fs, iNodeDest, this->getiNodeBlock(), 16); // dest, source
    }
    if (bitmapDest != 0)
    {
        diskCpy(fs, bitmapDest, this->getBitmapBlock()); // dest, source
    }
    if(dataBlockDest != 0)
    {
        diskCpy(fs, dataBlockDest, this->getDataBlock()); //dest, source
    }
    
    this->free();
}

uint32_t JournalEntry::getTxStartBlock() { return myBase; } 
uint32_t JournalEntry::getiNodeBlock()   { return myBase + BillyFS::BLOCK_SIZE * 1; }
uint32_t JournalEntry::getBitmapBlock()  { return myBase + BillyFS::BLOCK_SIZE * 2; }
uint32_t JournalEntry::getDataBlock()    { return myBase + BillyFS::BLOCK_SIZE * 3; }
uint32_t JournalEntry::getTxEndBlock()   { return myBase + BillyFS::BLOCK_SIZE * 4; }
bool JournalEntry::isValid()
{
	Debug::printf("Checking valid at %x\n", this->getTxEndBlock());
    uint8_t valid;
    fs->device->readAll(this->getTxEndBlock(), &valid, 1);
    return (valid & 0x1) == 0x1;
}
void JournalEntry::setValid() 
{ 
    // let's write to txend
    uint8_t valid = 0x01; 
    fs->device->writeAll(this->getTxEndBlock(), &valid, 1); 
}

uint32_t JournalEntry::getiNodeBlockDest() //look in txstart for block number of actual block we're editing
{ 
    uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted inode datablock is %d\n",  expectedDataBlock);
    return expectedDataBlock;

/*
    uint32_t offset = myBase + JournalEntry::INODE_BLOCK_DEST_INDEX;
    uint32_t iNodeBlockDest;
    iNodeBlockDest = fs->device->readAll(offset, &iNodeBlockDest, sizeof(iNodeBlockDest));
    return iNodeBlockDest;
    */
}
uint32_t JournalEntry::getBitmapBlockDest()  //
{
    uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::BITMAP_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted datablock is %d\n",  expectedDataBlock);
    return expectedDataBlock;

    /*
    uint32_t offset = myBase + JournalEntry::BITMAP_BLOCK_DEST_INDEX;
    uint32_t bitmapDest;
    bitmapDest =  fs->device->readAll(offset, &bitmapDest, sizeof(bitmapDest));
    return bitmapDest;
    */
}

uint32_t JournalEntry::getDataBlockDest() 
{
            uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::DATA_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted datablock is %d\n",  expectedDataBlock);
    return expectedDataBlock;
    /*uint32_t offset = myBase + JournalEntry::DATA_BLOCK_DEST_INDEX;
    uint32_t dataBlockDest;
    dataBlockDest =  fs->device->readAll(offset, &dataBlockDest, sizeof(dataBlockDest));
    return dataBlockDest;
    */
}

void JournalEntry::copyData(uint32_t blockNumber)
{

    // let's copy the block in
    uint32_t srcIndex = blockNumber * BillyFS::BLOCK_SIZE;
    uint32_t destIndex = this->getDataBlock();
    diskCpy(fs, destIndex, srcIndex);
    
    //Debug::printf("start of journal entry: %x\n", myBase);

    //Debug::printf("copying %x into %x\n", srcIndex, myBase + JournalEntry::DATA_BLOCK_DEST_INDEX);

    dataBlock = srcIndex;
    //Debug::printf("in copy data data block is %d\n", dataBlock);
    // let's keep track of this block number in the journal entry
    fs->device->writeAll(myBase + JournalEntry::DATA_BLOCK_DEST_INDEX, &srcIndex, 4); // is this right????
    uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::DATA_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted datablock is %d\n",  expectedDataBlock);
}

//copies either BillyFS's dataBitmap or the inodeBitmap to journalEntry's dataBitmap block
void JournalEntry::copyBitmapBlock(uint32_t srcIndex)
{
    fs->device->readAll(srcIndex, &myBitmap->myBitmap, 1024);

    // let's keep track of this block number in the journal entry
    bitmapBlock  = srcIndex;
    fs->device->writeAll(myBase + JournalEntry::BITMAP_BLOCK_DEST_INDEX, &srcIndex, 4); // is this right????    
}

//copies BillyFS iNode block to journalEntry's iNode block (*multiple iNodes are located in a single block*)
void JournalEntry::copyiNodeBlock(uint32_t inumber)
{
    uint32_t calculatedOffset = fs->inodeBase + inumber * Node::SIZE;
    //Debug::printf("Inode Base: %x\n",  fs->inodeBase);
        //Debug::printf("inumber %d\n",  inumber);

    //Debug::printf("COying inode lbock thing fofm %x\n",  calculatedOffset);
    uint16_t  expectedType;
    fs->device->readAll(calculatedOffset, &expectedType, 2);
    Debug::printf("%d i  st eh type\n", expectedType);
    uint16_t  expectednLinks;
    fs->device->readAll(calculatedOffset + 2, &expectednLinks, 2);
    Debug::printf("%d i  st eh nlinks\n", expectednLinks);
    uint32_t  expectedSize;
    fs->device->readAll(calculatedOffset + 4, &expectedSize, 4);
    Debug::printf("%d i  st eh size\n", expectedSize);
    uint32_t  expectedDirect;
    fs->device->readAll(calculatedOffset + 8, &expectedDirect, 4);
    Debug::printf("%d i  st eh direct\n", expectedDirect);
    uint32_t  expectedIndirect;
    fs->device->readAll(calculatedOffset + 12, &expectedIndirect, 4);
    Debug::printf("%d i  st eh indirect\n", expectedIndirect);

    myNodeInfo[0]= { expectedType, expectednLinks,  expectedSize,  expectedDirect,  expectedIndirect } ;
    myNodeInfo->printNode();
    //uint32_t srcIndex = (BillyFS::inodeBase + Node::SIZE * inumber);
    //uint32_t destIndex = this->getiNodeBlock();
    //diskCpy(fs, destIndex, srcIndex);
    
    // let's 
    
    // let's keep track of this block number in the journal entry
    //fs->device->writeAll(myBase + JournalEntry::BITMAP_BLOCK_DEST_INDEX, &srcIndex, 4); // is this right????
    //Debug::printf("calclated offset is at %x\n", calculatedOffset);
    fs->device->writeAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &calculatedOffset, 4); // is this right????

    uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted INDOE BLOCK is %d\n",  expectedDataBlock);

}

uint32_t JournalEntry::write(uint32_t offset, const void* buffer, uint32_t n)
{
    // in this method we need to write to the actual data block.

    uint32_t blockNumber = this->getDataBlock() / BillyFS::BLOCK_SIZE; // here we need to set the block to the data block we're altering in data
    uint32_t start = offset % BillyFS::BLOCK_SIZE;
    uint32_t end = start + n;
    if (end > BillyFS::BLOCK_SIZE) Debug::panic("Trying to write more than a block\n");
    
    uint32_t count = end - start;
    
    //Debug::printf("**** BLOCK NUMBER IS PROBABLY FUCKING WRONG %d\n", blockNumber);
    //Debug::printf("******** J OURNALE ENTRY  WRITE IS REIGN TO %x\n", blockNumber*BillyFS::BLOCK_SIZE+start);



    //Debug::printf("*******  THE BEGGINIGNG OF BUFFER IS %x\n", *((uint32_t*)(buffer)));
    uint32_t x = fs->device->write(blockNumber*BillyFS::BLOCK_SIZE+start,
               buffer,
               count);
               
    if( x < 0 ) return x;
    


    uint32_t newSize = offset + x;
    uint32_t oldSize = myNodeInfo->size;
    if(newSize > oldSize)
        myNodeInfo->size = newSize; // set the node size
    
    uint32_t writingLoc = this->getiNodeBlock();
    fs->device->writeAll(writingLoc + 4, &(myNodeInfo->size), 4);


        //Debug::printf("offset %x | x %x  newSize  %x | oldSize  %x \n", offset, x, newSize, oldSize);
    
    myNodeInfo->printNode();

    return x;
      
/*
    uint32_t blockIndex = offset / BillyFS::BLOCK_SIZE;
    uint32_t start = offset % BillyFS::BLOCK_SIZE;
    uint32_t end = start + n;
    if (end > BillyFS::BLOCK_SIZE) {
        end = BillyFS::BLOCK_SIZE;
    }
    uint32_t count = end - start;

    uint32_t blockNumber = getBlockNumber(blockIndex);

    int32_t x = fs->device->write(
               blockNumber*BillyFS::BLOCK_SIZE+start,
               buffer,
               count);

    if (x < 0) {
        return x;
    }

    uint32_t newSize = offset + x;
    uint32_t oldSize = getSize();
    if (newSize > oldSize) {
        setSize(newSize);
    }
    return x;
    
    */
}



//write the entire virtual journal into physical journal's next entry
void JournalEntry::writeToDisk(){
    // first we must write TxStart

    uint32_t writingLoc = this->getTxStartBlock();
    // uint32_t x;
     // write txStart block
    
    /* 
    // write the inode block number
    fs->device->writeAll(writingLoc, &inodeBlock, 4);
    // write the databitmap block number
    fs->device->writeAll(writingLoc + 4, &bitmapBlock, 4);
    // write the data block number
    fs->device->writeAll(writingLoc + 8, &dataBlock, 4);
    */
    //Debug::printf("beforew we writToDisk();\n");
    myNodeInfo->printNode();
    writingLoc = this->getiNodeBlock();

    //Debug::printf("direct is %x\n", myNodeInfo->direct);

    // write iNode block
    // TODO： make this write out in 1 struct u asshole
    fs->device->writeAll(writingLoc, &(myNodeInfo->type), 2);
    fs->device->writeAll(writingLoc + 2, &(myNodeInfo->nLinks), 2);
    fs->device->writeAll(writingLoc + 4, &(myNodeInfo->size), 4);
    fs->device->writeAll(writingLoc  + 8, &(myNodeInfo->direct), 4);
    fs->device->writeAll(writingLoc +  12, &(myNodeInfo->indirect), 4);
    //fs->device->writeAll(writingLoc, &myNodeInfo, 16);

    uint32_t expectedSize;
    fs->device->readAll(writingLoc + 4, &(expectedSize), 4);
    //Debug::printf("THe size after we wrote in memory  is %d\n", expectedSize);


    // write databitmap block
    writingLoc = this->getBitmapBlock();
    fs->device->writeAll(writingLoc, &myBitmap->myBitmap, 1024);
    

        uint32_t expectedDataBlock;
    fs->device->readAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &expectedDataBlock, 4);
    //Debug::printf("expceted INDOE BLOCK is %d\n",  expectedDataBlock);

    // write data block stuff?
    // it's already writtten.
    
    //uint32_t dest = Journal::journalBase + Journal::getNextEntry();
    //uint32_t x = fs->device->writeAll(dest, this, BLOCK_SIZE * 4);
}

void JournalEntry::free()
{
	//Debug::printf("Writing free to %x\n", this->getTxEndBlock());
    //Debug::printf("Freeing the transaction now\n");
     // let's write to txend
    uint8_t valid = 0x00; 
    fs->device->writeAll(this->getTxEndBlock(), &valid, 1);    
}


/////////////
// Journal //
/////////////



uint32_t Journal::allocateBlock(JournalEntry* je) {
    
    // find a freeblocknum in the bobfs data bitmap
    int32_t freeBlockNum = je->myBitmap->find();
    
    je->myBitmap->dump();
    //Debug::printf("%x\n", freeBlockNum);

    if (freeBlockNum == -1)
    {
        return 0;        
    }

    //The block offset of the allcoated block / BLOCK_SIZE
    uint32_t blockIndex = fs->dataBase / BillyFS::BLOCK_SIZE + freeBlockNum;
//    uint32_t blockIndex = fs->dataBase + freeBlockNum * BillyFS::BLOCK_SIZE;
    //I'm assuming you can do this write because if the block was free, it doesn't matter what is in it, just as long as it's zeroed out before it is copied
    //into the journal entry.
    //Debug::printf("blockIndex:%x\n", blockIndex);
    fs->device->writeAll(blockIndex * BLOCK_SIZE, zero_1024, BLOCK_SIZE);
    return blockIndex;
    
/*int32_t index = dataBitmap->find();
    if (index == -1) {
        return 0;
    }
    uint32_t blockIndex = dataBase / BLOCK_SIZE + index;
    device->writeAll(blockIndex * BLOCK_SIZE, zero_1024, BLOCK_SIZE);
    return blockIndex;

    ///////////////////////
    
    // writes freeBlockNum into the journalentry dataBitmap
    uint32_t bitmapAddr = getBitmapBlock();
    
    uint32_t arrayEle = freeBlockNum/32;
    uint32_t bitPos = freeBlockNum%32;

    uint32_t entry;
    fs->device->readAll(&entry, fs->dataBitmapBase+arrayEle*4, 4);
    
    uint32_t mask = 0x1 << (31-bitPos);
    entry = entry | mask;

    fs->device->writeAll(bitmapAddr+arrayEle*4, &entry, 4);
    
    // copies journal entry data bitmap to bobfs's when u commit
    
    return freeBlockNum;*/
}

//Make a journal entry that changes the indirect table
void Journal::changeIndirectTable(uint32_t indirectBlockNum, uint32_t index, uint32_t blockNum)
{
    //TODO: make it so that  it sets inode to -1 and bit map to 0. Commit doesn't commit them when it sees these values. 

    //Debug::printf("o fuk chagne indirec tis gettin gclaled\n");
    JournalEntry* newEntry = new JournalEntry(this, fs, getFreeEntry());
    newEntry->dataBlock = blockNum;
    //Copy the indirect into the journal, then change it, then set this journal valid

    newEntry->copyData(indirectBlockNum);
    //Debug::panic("before device error\n");

    //Debug::printf("--------  THE INDEX IS %x MULTIPLED  BY 4 IS %x\n", index, index * 4);
    newEntry->write(index * 4, &blockNum, 4);

    newEntry->setInodeBlockDest(0xffffffff);
    newEntry->setBitmapBlockDest(0);
    newEntry->setValid();    


}

//get the data block base  on the block index given. If the node does not have that block, then allocate a block and update the journal bitmap
uint32_t Journal::getBlockNum(JournalEntry* je, uint32_t blockIndex)
{
    if (blockIndex == 0)
    {
        uint32_t x = je->myNodeInfo->direct;
        if (x == 0)
        {
            x = allocateBlock(je);
            //Debug::printf("allcoated direct: %x\n", x);
            je->myNodeInfo->direct = x;
    //Debug::printf("direct is %x\n", je->myNodeInfo->direct);

        }
        return x;
    }
    else 
    {
        blockIndex -= 1;
        if (blockIndex >= BillyFS::BLOCK_SIZE/4) {return 0;}
        uint32_t i = je->myNodeInfo->indirect;
        if (i == 0)
        {
            i = allocateBlock(je);
            if (i == 0) {return 0;}
        }
        je->myNodeInfo->indirect = i;
        uint32_t writingLoc = je->getiNodeBlock();
        //Debug::printf("WRITING LOC FOR INDIRECT BLOCK IS AT %x\n",  writingLoc);
        //Debug::printf("direct is %x\n", je->myNodeInfo->direct);
        fs->device->writeAll(writingLoc +  12, &(je->myNodeInfo->indirect), 4);

        //Debug::printf("allcoated indirect: %x\n", i);
        uint32_t x;
    
        const uint32_t xOffset = i * BillyFS::BLOCK_SIZE + blockIndex*4;
        fs->device->readAll(xOffset, &x, sizeof(x));
        if (x==0)
        {
            x = allocateBlock(je);
            //TODO: MAKE SURE TO MAKE THE "CHANGE INDIRECT TABLE" ENTRY BEFORE YOU MAKE THE "CHANGE DATA BLOCK" ENTRY 
            changeIndirectTable(i, blockIndex, x);
        }
        return x;
    }

/*    uint32_t x;
    
    const uint32_t xOffset = i * BillyFS::BLOCK_SIZE + blockIndex*4;
    fs->device->readAll(xOffset, &x, sizeof(x));
    if (x==0)
    {
        x = allocateBlock();
        //TODO: MAKE SURE TO MAKE THE "CHANGE INDIRECT TABLE" ENTRY BEFORE YOU MAKE THE "CHANGE DATA BLOCK" ENTRY 
        changeIndirectTable(i, blockIndex, x);
    }*/
//    return x;
    
/*    if (blockIndex == 0) {
        uint32_t x = getDirect();
        if (x == 0) {
            x = fs->allocateBlock();
            setDirect(x);
        }
        return x;
    } else {
        blockIndex -= 1;
        if (blockIndex >= BillyFS::BLOCK_SIZE/4) return 0;
        uint32_t i = getIndirect();
        if (i == 0) {
            i = fs->allocateBlock();
            if (i == 0){
                return 0;
            }
            setIndirect(i);
        }
        uint32_t x;
        const uint32_t xOffset = i * BillyFS::BLOCK_SIZE + blockIndex*4;
        fs->device->readAll(xOffset,&x,sizeof(x));
        if (x == 0) {
            x = fs->allocateBlock();
            fs->device->writeAll(xOffset,&x,sizeof(x));
        }
        return x;
    }*/  
}

//makes a journal entry, copies over disk blocks, //modifies the entry//, sets entry validity, and commits to disk
uint32_t Journal::write(uint32_t nodeInumber, uint32_t offset, const void* buffer, uint32_t n) // handles the Node::write() method
{
    uint32_t count = 0;

    // creating a journal entry
    JournalEntry* journalEntry = new JournalEntry(this, this->fs, this->getFreeEntry());
    //TODO: might need make node a strongPtr. (though locking might already fix this)

    // find out what block we're altering
    uint32_t blockIndex = offset / BillyFS::BLOCK_SIZE;
    
    //The data block we're writing to
//    uint32_t blockNumber = getBlockNum(journalEntry, blockIndex);

//    Debug::printf("Journal:write(), blockIndex: %d blockNumber: %d\n", blockIndex, blockNumber);
    //Debug::printf("In Journal::write(), block number is %d\n", blockNumber);
//    Debug::printf("Copying all of the data now!\n");
    
    // let's copy the block we're altering into the journal entry, we also need to keep track of which indexes 
    
//    journalEntry->copyData(blockNumber);


	Debug::printf("We're in journal write, printing nodes\n");
    journalEntry->copyiNodeBlock(nodeInumber);
    journalEntry->myNodeInfo->printNode();


    journalEntry->copyBitmapBlock(fs->dataBitmapBase);
    //Debug::printf("after copy bitmap block\n");
    journalEntry->myNodeInfo->printNode();

    
    //The data block we're writing to
    uint32_t blockNumber = getBlockNum(journalEntry, blockIndex);
    journalEntry->copyData(blockNumber);

    //Debug::printf("----------------blockNum: %x, inum: %x\n", blockNumber, nodeInumber);

//    Debug::printf("Printing out the node\n");
    // let's print out this shit
    journalEntry->myNodeInfo->printNode();
                    

//    Debug::printf("Writing to disk\n");
    // now let's write the journal entry to the disk (journal on disk)
    journalEntry->writeToDisk();

//    Debug::printf("Makign modifications to data block\n");
    count = journalEntry->write(offset, buffer, n); // do we need to pass in node???
    
//    Debug::printf("Setting the transaction as valid\n");
    journalEntry->setValid();
    
//    Debug::printf("Commiting now\n");
    journalEntry->commit();

    //entry freed at the end of commit
    
        
    //TODO: need to make an allocate block method that looks at the bitmap in the journal, writes to that bitmap, THEN copies over the journal bitmap to the //actual
    // bitmap. 
    
    // now let's make the desired changes to the block but TO THE BLOCK IN THE JOURNAL
    
    /* somebody put code here pls */
    return count;
    
}

//makes a journal entry, copies over disk blocks, //modifies the entry//, sets entry validity, and commits to disk
JournalEntry* Journal::neuteredWrite(uint32_t nodeInumber, uint32_t offset, const void* buffer, uint32_t n) // handles the Node::write() method
{
    //Debug::printf("neutered \n\n");


    // creating a journal entry
    JournalEntry* journalEntry = new JournalEntry(this, this->fs, this->getFreeEntry());
    //TODO: might need make node a strongPtr. (though locking might already fix this)

    // find out what block we're altering
    uint32_t blockIndex = offset / BillyFS::BLOCK_SIZE;
    
    //The data block we're writing to
//    uint32_t blockNumber = getBlockNum(journalEntry, blockIndex);

//    Debug::printf("Journal:write(), blockIndex: %d blockNumber: %d\n", blockIndex, blockNumber);
    //Debug::printf("In Journal::write(), block number is %d\n", blockNumber);
//    Debug::printf("Copying all of the data now!\n");
    
    // let's copy the block we're altering into the journal entry, we also need to keep track of which indexes 
    
//    journalEntry->copyData(blockNumber);

    //Debug::printf("direct is %x\n", journalEntry->myNodeInfo->direct);

    journalEntry->copyiNodeBlock(nodeInumber);
    journalEntry->myNodeInfo->printNode();


    journalEntry->copyBitmapBlock(fs->dataBitmapBase);
    //Debug::printf("after copy bitmap block\n");
    journalEntry->myNodeInfo->printNode();
    
    //The data block we're writing to
    uint32_t blockNumber = getBlockNum(journalEntry, blockIndex);
    journalEntry->copyData(blockNumber);
    //Debug::printf("**** **DSIOFJDSGHDSHS DTHE BLOCK NUMBER WE ARE COPYING TO %x\n", blockNumber);

//    Debug::printf("Printing out the node\n");
    // let's print out this shit
    journalEntry->myNodeInfo->printNode();               

//    Debug::printf("Writing to disk\n");
    // now let's write the journal entry to the disk (journal on disk)
    journalEntry->writeToDisk();


    //Debug::printf("******** NOW WERE WRITING TO OFFSET %x\n", offset);
//    Debug::printf("Makign modifications to data block\n");
    journalEntry->write(offset, buffer, n); // do we need to pass in node???
    
//    Debug::printf("Setting the transaction as valid\n");
    journalEntry->setValid();

    //entry freed at the end of commit
    
        
    //TODO: need to make an allocate block method that looks at the bitmap in the journal, writes to that bitmap, THEN copies over the journal bitmap to the //actual
    // bitmap. 
    
    // now let's make the desired changes to the block but TO THE BLOCK IN THE JOURNAL
    
    /* somebody put code here pls */
    return journalEntry;
    
}

//returns the blockNum of the next journal entry in journal
uint32_t Journal::getFreeEntry()
{
/*    //TODO: need to zero out the memory when assigning a journal entry
    //TODO: need to look through entries and chck if they are valid bfore assigning them    

    uint32_t startingEntry = currentEntry;
    currentEntry += JournalEntry::SIZE / BillyFS::BLOCK_SIZE;
    while (currentEntry != startingEntry)
    {
        if(currentEntry >= journalEnd)
        {
            currentEntry = journalBase; // then let's wrap around

        }
    }

    //No more entries available
    return -1;
    */
    
	//Debug::printf("*** Journal end %x\n", journalEnd);

	//Debug::printf("*** Journal base %x\n", journalBase);
	//Debug::printf("*** JcurrentENtry  %x\n", currentEntry);
    // if this entry will be longer than the journal end
    if(currentEntry >= journalEnd)
    {
        currentEntry = journalBase; // then let's wrap around
        return currentEntry;
    }
    
    // let's just update the current location of the journal
    currentEntry += JournalEntry::SIZE;
    return currentEntry;
}


void Journal::repair()
{
    // so we need to iterate through our journal entries, 
    uint32_t currentLoc = journalBase;
   
	Debug::printf("We're in repair right now!!!\n");
	
	 for(uint32_t i = 0; i < Journal::NUM_ENTRIES; i++)
    {
		
        // let's get the entries
        // first let's check the valid bit for this one
        JournalEntry* entry = new JournalEntry(this, this->fs, currentLoc);
        Debug::printf("got a new entry yeuh\n");
		if(entry->isValid())
        {
            Debug::printf("We're committing a transaction!\n");
            entry->commit();
        }
        currentLoc += JournalEntry::SIZE;//copies journal entry containing new changes into the areas we actually want to change
    }
}
void JournalEntry::setDataBlockDest(uint32_t dest)
{
    dataBlock = dest;
    fs->device->writeAll(myBase + JournalEntry::DATA_BLOCK_DEST_INDEX, &dataBlock, 4); // is this right????
}
void JournalEntry::setBitmapBlockDest(uint32_t dest)
{
    bitmapBlock = dest;
    fs->device->writeAll(myBase + JournalEntry::BITMAP_BLOCK_DEST_INDEX, &bitmapBlock, 4); // is this right????
}
void JournalEntry::setInodeBlockDest(uint32_t dest)
{
    inodeBlock = dest;
    fs->device->writeAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &inodeBlock, 4); // is this right????
}

void JournalEntry::makeNewNode(uint16_t type, uint16_t nLinks, uint32_t  size,  uint32_t direct, uint32_t indirect, uint32_t iNumber)
{
    myNodeInfo[0]= { type, nLinks,  size,  direct,  indirect } ;
    myNodeInfo->printNode();

    uint32_t writingLoc = this->getiNodeBlock();
    // write iNode block
    // TODO： make this write out in 1 struct u asshole
    fs->device->writeAll(writingLoc, &(myNodeInfo->type), 2);
    fs->device->writeAll(writingLoc + 2, &(myNodeInfo->nLinks), 2);
    fs->device->writeAll(writingLoc + 4, &(myNodeInfo->size), 4);
    fs->device->writeAll(writingLoc  + 8, &(myNodeInfo->direct), 4);
    fs->device->writeAll(writingLoc +  12, &(myNodeInfo->indirect), 4);

    //Debug::printf("JOURNALENTRY::MAKENEWNODE INUMBER BEING ")
    // let's calculate the node's offset and write that
    uint32_t nodeOffset =  fs->inodeBase + iNumber * Node::SIZE;
    inodeBlock = nodeOffset;
    fs->device->writeAll(myBase + JournalEntry::INODE_BLOCK_DEST_INDEX, &nodeOffset, 4);

    //Node* actualNode = new Node(fs, iNumber);

    //return StrongPtr<Node>(actualNode);

}
StrongPtr<Node> Journal::newNode(uint32_t parentInumber,  const char* name, uint16_t type)
{
    JournalEntry* childEntry = new JournalEntry(this, this->fs, this->getFreeEntry());

    // NodeInfo*  parentInfo  = new NodeInfo(parentInumber,  fs);
    // NodeInfo* parentInfo->readData(parentInumber);

    //Debug::printf("In Journal::newNode()  method call \n");

    // get the bitmap
    childEntry->copyBitmapBlock(fs->inodeBitmapBase);
    //Debug::printf("After copying bitmap block\n");
    childEntry->myNodeInfo->printNode(); 


    // find free bitmap space for new iNode
    // TODO find alters bitmap here
    int32_t childInum = childEntry->myBitmap->find(); // returns block number
    if (childInum < 0) {
        Debug::panic("newNode, out if inodes\n");
    }

    // let's make a new node
        Node* newNode = new Node(fs, childInum);

    // set properties of new node
    childEntry->makeNewNode(type, 1, 0, 0, 0, childInum);

    // make sure we don't write anything to anywhere for data block
    childEntry->setDataBlockDest(0);

    // write that entry to disk (don't set valid bit)
    childEntry->writeToDisk();

    // MISSING();
    // let's make the buffer iNumber, nameSize, name
    uint32_t nameSize = strlen(name);
    uint32_t bufferSize = 4  +  4  + nameSize;
    //Debug::printf("*** childInum %d\n", childInum);
    //Debug::printf("***  name size %d\n", nameSize);
    void* buffer  = malloc(bufferSize);
    memcpy(buffer, &childInum, 4);
    memcpy((void*)((char*)(buffer) +  4), &nameSize,  4);
    memcpy((void*)((char*)(buffer) + 8), name, nameSize);


    uint32_t calculatedOffset = fs->inodeBase  + parentInumber * Node::SIZE;
    uint16_t type_;
    fs->device->readAll(calculatedOffset, &type_, 2);
    uint16_t nLinks;
    fs->device->readAll(calculatedOffset + 2, &nLinks, 2);
    uint32_t size;
    fs->device->readAll(calculatedOffset + 4, &size, 4);
    uint32_t  direct;
    fs->device->readAll(calculatedOffset + 8, &direct, 4);
    uint32_t  indirect;
    fs->device->readAll(calculatedOffset + 12, &indirect, 4);
    //Debug::printf("Calculated offset /we're writing metadata to  %x\n", calculatedOffset);
    
    NodeInfo* parentInfo = new NodeInfo();
    parentInfo[0] = {type_, nLinks, size, direct, indirect};

    //Debug::printf("******* THE APRENTS DIRECT IS %x\n",  direct);

    //Debug::printf("********* WERE WRITING BUFFER TO PARENT IN UMEBER %d\n", parentInumber);
    //Debug::printf("**** PARENT SIZE IS %d\n", parentInfo->size);
    JournalEntry* parentEntry = neuteredWrite(parentInumber, parentInfo->size, buffer, bufferSize);

    childEntry->setValid();
    parentEntry->setValid();

    childEntry->commit();
    parentEntry->commit();    

    return StrongPtr<Node>(newNode);

//    return StrongPtr<Node>();
    //return StrongPtr<Node>(newNode);

    // set both journal entries as valid

    //  commit both

    //  free both

    //  donzo 

    /*
    int32_t idx = fs->inodeBitmap->find();
    if (idx < 0) {
        Debug::panic("newNode, out if inodes\n");
    }

        StrongPtr<Node> node = Node::get(fs,idx);
    node->setType(type);
    node->setSize(0);
    node->setLinks(0);    
    node->setDirect(0);
    node->setIndirect(0);

    linkNode(name,node);
    return node;

 
    */


    // call link node
    // return  new Node;
}

//links a file node to the directory it's located within
void Journal::crashLinkNodeTest(const char* name, uint32_t parentINum, uint32_t childINum){ // Handles Node::linkNode()
    // create two entries, but don't commit them until both are complete
    // creates entry for parent editing parent node's "file content"
    // creates entry for child node's change in nLinks
   
    //journal entry: txstart, inode block, bitmap block, data block
    
    //first transaction: add child entry in parent
    uint32_t firstEntryOffset = getFreeEntry();
    //Debug::printf("address of first journal entry to add name to parent: %x\n", firstEntryOffset);
    JournalEntry* firstEntry = new JournalEntry(this, this->fs, firstEntryOffset);   
    
    //  let's copy the iNode info of parent first
    uint32_t calculatedOffset = fs->inodeBase  + parentINum * Node::SIZE;
    uint16_t type;
    fs->device->readAll(calculatedOffset, &type, 2);
    uint16_t nLinks;
    fs->device->readAll(calculatedOffset + 2, &nLinks, 2);
    uint32_t size;
    fs->device->readAll(calculatedOffset + 4, &size, 4);
    uint32_t  direct;
    fs->device->readAll(calculatedOffset + 8, &direct, 4);
    uint32_t  indirect;
    fs->device->readAll(calculatedOffset + 12, &indirect, 4);

    NodeInfo* parentInfo = new NodeInfo();
    parentInfo[0] = {type, nLinks, size, direct, indirect};

    firstEntry->copyiNodeBlock(parentINum);
    firstEntry->copyBitmapBlock(fs->dataBitmapBase);
    uint32_t blockIndex = getBlockNum(firstEntry, parentInfo->size);
    firstEntry->copyData(blockIndex);

    //Debug::printf("adding name entry into parent\n");
    uint32_t nameLen = strlen(name);
    
	firstEntry->write(parentInfo->size, &childINum, 4);
    firstEntry->write(parentInfo->size, &nameLen, 4);
    firstEntry->write(parentInfo->size, name, nameLen);   
 
    //second transaction: changes nLinks in child
    uint32_t secondEntryOffset = getFreeEntry();

	//Debug::printf("*** Free entry 1 %x free entry 2 %x\n",firstEntryOffset, secondEntryOffset); 

    JournalEntry* secondEntry = new JournalEntry(this, this->fs, secondEntryOffset);    
    //copy in place blocks into journalEntry's blocks
    secondEntry->copyiNodeBlock(childINum);
    secondEntry->copyBitmapBlock(fs->dataBitmapBase);
    //secondEntry->copyData(secondEntry->getDataBlockDest()); 
    secondEntry->setDataBlockDest(0);

	firstEntry->writeToDisk();
	secondEntry->writeToDisk();

//   Debug::panic("AFTER COPYING\n"); 

	
	//update child's nLinks 
    uint32_t nLinksOffset = secondEntry->getiNodeBlock()+2; //offset of nLinks
    uint16_t nLinkss;

	//uint32_t expectedType;
    //fs->device->readAll(nLinksOffset - 2, &expectedType, 2);
 	//Debug::printf("*** expected type %d\n", expectedType);

	
	// let's make sure this is the only change were doing 


    fs->device->readAll(nLinksOffset, &nLinkss, 2);
    nLinkss++;
    // set the child nLinks
    fs->device->writeAll(nLinksOffset, &(nLinkss), 2);
   


//	Debug::printf("*** get inode block is %x\n", nLinksOffset);
    //Debug::printf("*** set entry as valid\n");
    firstEntry->setValid();
    secondEntry->setValid();

	return; // UH OH WE CRASHED
}


//links a file node to the directory it's located within
void Journal::linkNode(const char* name, uint32_t parentINum, uint32_t childINum){ // Handles Node::linkNode()
    // create two entries, but don't commit them until both are complete
    // creates entry for parent editing parent node's "file content"
    // creates entry for child node's change in nLinks
   
    //journal entry: txstart, inode block, bitmap block, data block
    
    //first transaction: add child entry in parent
    uint32_t firstEntryOffset = getFreeEntry();
    //Debug::printf("address of first journal entry to add name to parent: %x\n", firstEntryOffset);
    JournalEntry* firstEntry = new JournalEntry(this, this->fs, firstEntryOffset);   
    
    //  let's copy the iNode info of parent first
    uint32_t calculatedOffset = fs->inodeBase  + parentINum * Node::SIZE;
    uint16_t type;
    fs->device->readAll(calculatedOffset, &type, 2);
    uint16_t nLinks;
    fs->device->readAll(calculatedOffset + 2, &nLinks, 2);
    uint32_t size;
    fs->device->readAll(calculatedOffset + 4, &size, 4);
    uint32_t  direct;
    fs->device->readAll(calculatedOffset + 8, &direct, 4);
    uint32_t  indirect;
    fs->device->readAll(calculatedOffset + 12, &indirect, 4);

    NodeInfo* parentInfo = new NodeInfo();
    parentInfo[0] = {type, nLinks, size, direct, indirect};

    firstEntry->copyiNodeBlock(parentINum);
    firstEntry->copyBitmapBlock(fs->dataBitmapBase);
    uint32_t blockIndex = getBlockNum(firstEntry, parentInfo->size);
    firstEntry->copyData(blockIndex);

    //Debug::printf("adding name entry into parent\n");
    uint32_t nameLen = strlen(name);
    
	firstEntry->write(firstEntry->myNodeInfo->size, &childINum, 4);
 
 firstEntry->write(firstEntry->myNodeInfo->size, &nameLen, 4);

    firstEntry->write(firstEntry->myNodeInfo->size, name, nameLen);   
 
    //second transaction: changes nLinks in child
    uint32_t secondEntryOffset = getFreeEntry();

	//Debug::printf("*** Free entry 1 %x free entry 2 %x\n",firstEntryOffset, secondEntryOffset); 

    JournalEntry* secondEntry = new JournalEntry(this, this->fs, secondEntryOffset);    
    //copy in place blocks into journalEntry's blocks
    secondEntry->copyiNodeBlock(childINum);
    secondEntry->copyBitmapBlock(fs->dataBitmapBase);
    //secondEntry->copyData(secondEntry->getDataBlockDest()); 
    secondEntry->setDataBlockDest(0);

	firstEntry->writeToDisk();
	secondEntry->writeToDisk();

//   Debug::panic("AFTER COPYING\n"); 

	
	//update child's nLinks 
    uint32_t nLinksOffset = secondEntry->getiNodeBlock()+2; //offset of nLinks
    uint16_t nLinkss;

	//uint32_t expectedType;
    //fs->device->readAll(nLinksOffset - 2, &expectedType, 2);
 	//Debug::printf("*** expected type %d\n", expectedType);

	
	// let's make sure this is the only change were doing 


    fs->device->readAll(nLinksOffset, &nLinkss, 2);
    nLinkss++;
    // set the child nLinks
    fs->device->writeAll(nLinksOffset, &(nLinkss), 2);
   


//	Debug::printf("*** get inode block is %x\n", nLinksOffset);
    //Debug::printf("*** set entry as valid\n");
    firstEntry->setValid();
    secondEntry->setValid();

	//Debug::printf("*** are entries valid?\n");
	//Debug::printf("***%s\n", firstEntry->isValid() ? "Yes" : "No");   
	//Debug::printf("***%s\n", secondEntry->isValid() ? "Yes" : "No");
    //Debug::printf("commit entries\n");
    firstEntry->commit();

	secondEntry->myNodeInfo->printNode();
	//Debug::printf("*** About to commit child\n\n");
    secondEntry->commit();

	//Debug::printf("*** Finished commiting\n");
    // JournalEntry** entries = { firstEntry, secondEntry };

    /*
    fs->lock.lock();
    node->setLinks(1+node->getLinks());
    uint32_t offset = getSize();
    writeAll(offset,&node->inumber,4);
    uint32_t len = K::strlen(name);
    writeAll(offset+4,&len,sizeof(len));
    writeAll(offset+4+sizeof(len),name,len);
    fs->lock.unlock();    
    */
}
