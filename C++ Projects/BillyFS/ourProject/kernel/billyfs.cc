//billyfs.cc with bobfs and node

#include "billyfs.h"
#include "libk.h"
#include "heap.h"

//TxStart: the disk offsets of the blocks we're changing
//The updated inode
//The updated bitmap
//The updated data block
//

// change getBlockNumber() function to not allocate a block


static uint8_t zero_1024[BillyFS::BLOCK_SIZE];

bool streq(const char* a, const char* b) {
    int i = 0;
    while (true) {
        char x = a[i];
        char y = b[i];
        if (x != y) return false;
        if (x == 0) return true;
        i++;
    }
}

// NodeInfo::NodeInfo(uint32_t iNumber, BillyFS* fs)
// {
//     uint32_t writingLoc = fs->inodeBase + iNumber * Node::SIZE ;
//     // write iNode block
//     // TODO： make this write out in 1 struct u asshole
//     fs->device->readAll(writingLoc, &(type), 2);
//     fs->device->readAll(writingLoc + 2, &(nLinks), 2);
//     fs->device->readAll(writingLoc + 4, &(size), 4);
//     fs->device->readAll(writingLoc  + 8, &(direct), 4);
//     fs->device->readAll(writingLoc +  12, &(indirect), 4);

//}


/////////////////
// BillyBitMap //
/////////////////

BillyBitmap::BillyBitmap (BillyFS* fs, uint32_t offset) : 
    fs(fs),
    offset(offset)
{
    myBitmap = new char[BillyFS::BLOCK_SIZE];
}

void BillyBitmap::clear()
{
    for (uint32_t i = 0; i < BillyFS::BLOCK_SIZE; i++)
    {
        myBitmap[i] = 0;
    }
}

//
void BillyBitmap::set(int32_t index)
{
    uint32_t wordIndex = index/8;
    uint32_t bitIndex = index%8;

    myBitmap[wordIndex] |= (1 << bitIndex);
}

void BillyBitmap::clear(int32_t index) {
    uint32_t wordIndex = index/8; 
    uint32_t bitIndex = index%8;

    myBitmap[wordIndex] &= ~(1 << bitIndex);
}

int32_t BillyBitmap::find(void) {
    for (uint32_t i=0; i<BillyFS::BLOCK_SIZE; i++) {
        uint8_t word = myBitmap[i];
        if (word != 0xFF) { 
            uint8_t mask = 1;
            for (uint32_t j=0; j<8; j++) {
                if ((word & mask) == 0) {
                    //word |= mask;
                    myBitmap[i] |= mask;
                    return i * 8 + j;
                }
                mask = mask * 2;
            }
        }
    }
    return -1;
}

////////////
// Bitmap //
////////////

Bitmap::Bitmap(BillyFS* fs, uint32_t offset) :
    fs(fs), offset(offset)
{}
void Bitmap::clear(void) {
    Debug::panic("should not use bitmap clear\n");
    fs->device->writeAll(offset,zero_1024,BillyFS::BLOCK_SIZE);
}

void Bitmap::set(int32_t index) {
    Debug::panic("should not use bitmap set\n");
    uint32_t wordOffset = (index/32)*4;
    uint32_t bitIndex = index%32;

    uint32_t word;
 
    fs->device->readAll(offset+wordOffset,&word,sizeof(word));

    word |= (1 << bitIndex);

    fs->device->writeAll(offset+wordOffset,&word,sizeof(word));
}

void Bitmap::clear(int32_t index) {
    Debug::panic("should not use bitmap clear(uint32_t)\n");
    uint32_t wordOffset = (index/32) * 4; 
    uint32_t bitIndex = index%32;

    uint32_t word;
 
    fs->device->readAll(offset+wordOffset,&word,sizeof(word));

    word &= ~(1 << bitIndex);

    fs->device->writeAll(offset+wordOffset,&word,sizeof(word));
}

int32_t Bitmap::find(void) {
    Debug::panic("should not use bitmap find\n");
    for (uint32_t i=0; i<BillyFS::BLOCK_SIZE; i += 4) {
        uint32_t word;
        fs->device->readAll(offset+i,&word,sizeof(word));
        if (word != 0xFF) { // TODO: CHECK THIS SHIT
            uint32_t mask = 1;
            for (uint32_t j=0; j<32; j++) {
                if ((word & mask) == 0) {
                    word |= mask;
                    fs->device->writeAll(offset+i,&word,sizeof(word));
                    return i * 8 + j;
                }
                mask = mask * 2;
            }
        }
    }
    return -1;
}

//////////
// Node //
//////////

Node::Node(StrongPtr<BillyFS> fs, uint32_t inumber) : 
fs(fs), inumber(inumber) {
    offset = fs->inodeBase + inumber * SIZE;
}

/*
void Node::setTypeInMem(uint16_t type) { this->type = type; } 
void Node::setLinksInMem(uint16_t links) { this->links = links; } 
void Node::setSizeInMem(uint32_t size) { this->size = size; }
void Node::setDirectInMem(uint32_t direct) { this->direct = direct;}
void Node::setIndirectInMem(uint32_t indirect) { this->indirect = indirect; }

void Node::getTypeInMem() { return this->type; }
void Node::getLinksInMem() { return this->type; }
void Node::getSizeInMem() { return this->type; }
void Node::getDirectInMem() { return this->type; }
void Node::getIndirectInMem() { return this->type; }

//dont need anymore if we have inodeInfo struct
*/ 
// our adhoc bullshit
void Node::setTypeAdhoc(uint16_t type) {
    Debug::printf("The offset is at %x\n");
    fs->device->writeAll(offset+0, &type, 2);
}
void Node::setLinksAdhoc(uint16_t links) {
    fs->device->writeAll(offset+2, &links, 2);
}
void Node::setSizeAdhoc(uint32_t size) {
    fs->device->writeAll(offset+4, &size, 4);
}
void Node::setDirectAdhoc(uint32_t direct) {
    fs->device->writeAll(offset+8, &direct, 4);
}
void Node::setIndirectAdhoc(uint32_t indirect) {
    fs->device->writeAll(offset+12, &indirect, 4);
}

uint16_t Node::getType(void) {
    uint16_t x;
    fs->device->readAll(offset+0, &x, 2);
    return x;
}
void Node::setType(uint16_t type) {
    Debug::panic("*** Node::setType is being called and it SHOULDN'T BE");
    fs->device->writeAll(offset+0, &type, 2);
}
uint16_t Node::getLinks(void) {
    uint16_t x;
    fs->device->readAll(offset+2, &x, 2);
    return x;
}
void Node::setLinks(uint16_t links) {
    Debug::panic("*** Node::setLinks is being called and it SHOULDN'T BE");
    //fs->device->writeAll(offset+2, &links, 2);
    /*
    TODO:
    uint32_t nodeInJournal = JournalEntry::getiNodeBlock() + inumber * SIZE;
    uint32_t nodeInDisk = offset + 2;
    journalEntry::writeToJournal(nodeInJournal, nodeInDisk);
    */
}
uint32_t Node::getSize(void) {
    uint32_t x;
    fs->device->readAll(offset+4, &x, 4);
    return x;
}
void Node::setSize(uint32_t size) {
    Debug::panic("*** Node::setSize is being called and it SHOULDN'T BE\n");
    fs->device->writeAll(offset+4, &size, 4);
}
uint32_t Node::getDirect(void) {
    uint32_t x;
    fs->device->readAll(offset+8, &x, 4);
    return x;
}
void Node::setDirect(uint32_t direct) {
    Debug::panic("*** Node::setDirect is being called and it SHOULDN'T BE\n");
    fs->device->writeAll(offset+8, &direct, 4);
}
uint32_t Node::getIndirect(void) {
    uint32_t x;
    fs->device->readAll(offset+12, &x, 4);
    return x;
}
void Node::setIndirect(uint32_t indirect) {
    Debug::panic("*** Node::setIndirect is being called and it SHOULDN'T BE");
    fs->device->writeAll(offset+12, &indirect, 4);
}
uint32_t Node::getInum() {
    return inumber;
}

bool Node::isDirectory(void) {
    return getType() == DIR_TYPE;
}

bool Node::isFile(void) {
    return getType() == FILE_TYPE;
}

StrongPtr<Node> Node::findNode(const char* name) {
    uint32_t sz = getSize();
    uint32_t offset = 0;

    while (offset < sz) {
        uint32_t ichild;
        readAll(offset,&ichild,4);
        offset += 4;
        uint32_t len;
        readAll(offset,&len,4);
        offset += 4;
        char* ptr = (char*) malloc(len+1);
        readAll(offset,ptr,len);
        offset += len;
        ptr[len] = 0;

        auto cmp = streq(name,ptr);
        free(ptr);

        if (cmp) {
            StrongPtr<Node> child = Node::get(fs,ichild);
            return child;
        }
    }

    StrongPtr<Node> nothing;
    return nothing;
}

//TODO: implement this in journal (or journal entry)
uint32_t Node::getBlockNumber(uint32_t blockIndex) {
    // Debug::panic("Should not be calling getBlockNumber in node class\n");


if (blockIndex == 0) {
        uint32_t x = getDirect();
        if (x == 0) {
            return 0;
        }
        return x;
    } else {
        blockIndex -= 1;
        if (blockIndex >= BillyFS::BLOCK_SIZE/4) return 0;
        uint32_t i = getIndirect();
        if (i == 0) { 
            return 0;
        }
        uint32_t x;
        const uint32_t xOffset = i * BillyFS::BLOCK_SIZE + blockIndex*4;
        fs->device->readAll(xOffset,&x,sizeof(x));
        if (x == 0) {
            return 0;
        }
        return x;
    }
    /*if (blockIndex == 0) {
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

//writes a block to this node's file data            
int32_t Node::write(uint32_t offset, const void* buffer, uint32_t n) {

    return fs->journal->write(inumber, offset, buffer, n); // let's send this to the journal
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

int32_t Node::writeAll(uint32_t offset, const void* buffer_, uint32_t n) {

    fs->lock.lock();
    int32_t total = 0;
    char* buffer = (char*) buffer_;

    while (n > 0) {
        int32_t cnt = write(offset,buffer,n);
        if (cnt <= 0) {
            fs->lock.unlock();
            return total;
        }

        total += cnt;
        n -= cnt;
        offset += cnt;
        buffer += cnt;
    }
    fs->lock.unlock();
    return total;
}

int32_t Node::read(uint32_t offset, void* buffer, uint32_t n) {
    Debug::printf("calling node read\n");
    uint32_t sz = getSize();
    if (sz <= offset) {
        return 0;
    }

    uint32_t remaining = sz - offset;
    if (remaining < n) n = remaining;

    uint32_t blockIndex = offset / BillyFS::BLOCK_SIZE;
    uint32_t start = offset % BillyFS::BLOCK_SIZE;
    uint32_t end = start + n;
    if (end > BillyFS::BLOCK_SIZE) end = BillyFS::BLOCK_SIZE;
    uint32_t count = end - start;

    uint32_t blockNumber = getBlockNumber(blockIndex);

    if (blockNumber == 0)
    {
        return fs->device->read(blockNumber*BillyFS::BLOCK_SIZE+start, &zero_1024, count);
    }

    return fs->device->read(blockNumber*BillyFS::BLOCK_SIZE+start, buffer, count);
}

int32_t Node::readAll(uint32_t offset, void* buffer_, uint32_t n) {
    fs->lock.lock();
    int32_t total = 0;
    char* buffer = (char*) buffer_;

    while (n > 0) {
        int32_t cnt = read(offset,buffer,n);
        if (cnt <= 0) {
            fs->lock.unlock();
            return total;
        }

        total += cnt;
        n -= cnt;
        offset += cnt;
        buffer += cnt;
    }
    fs->lock.unlock();
    return total;
}
void Node::crashLinkNodeTest(const char* name, StrongPtr<Node> node) {
	    fs->journal->crashLinkNodeTest(name, inumber, node->inumber /*child*/); 
	
}
 
void Node::linkNode(const char* name, StrongPtr<Node> node) {
    //StrongPtr<Node> parent {this};
    fs->journal->linkNode(name, inumber, node->inumber /*child*/);
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

StrongPtr<Node> Node::newNode(const char* name, uint32_t type) {
    
    return fs->journal->newNode(this->inumber, name, type);
    //return StrongPtr<Node>(newNode);


    //return fs->journal->newNode(this->inumber, name, type);

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
}

StrongPtr<Node> Node::newFile(const char* name) {
    StrongPtr<Node> output  = newNode (name,FILE_TYPE);
    return output;
    //return newNode(name, FILE_TYPE);
}

StrongPtr<Node> Node::newDirectory(const char* name) {
    return newNode(name, DIR_TYPE);
}

void Node::dump(const char* name) {
    uint32_t type = getType();
    switch (type) {
    case DIR_TYPE:
        Debug::printf("*** 0 directory:%s(%d)\n",name,getLinks());
        {
            uint32_t sz = getSize();
            uint32_t offset = 0;

            while (offset < sz) {
                uint32_t ichild;
                readAll(offset,&ichild,4);
                offset += 4;
                uint32_t len;
                readAll(offset,&len,4);
                offset += 4;
                char* ptr = (char*) malloc(len+1);
                readAll(offset,ptr,len);
                offset += len;
                ptr[len] = 0;              
                
                StrongPtr<Node> child = Node::get(fs,ichild);
                child->dump(ptr);
                free(ptr);
            }
        }
        break;
    case FILE_TYPE:
        Debug::printf("*** 0 file:%s(%d,%d)\n",name,getLinks(),getSize());
        break;
    default:
         Debug::panic("unknown i-node type %d\n",type);
    }
}

//start of billyfs(part2).cc

///////////
// BillyFS //
///////////

BillyFS::BillyFS(StrongPtr<Ide> device) :
    lock(),
    device(device),
    dataBitmapBase(BLOCK_SIZE),
    inodeBitmapBase(2 * BLOCK_SIZE),
    inodeBase(3 * BLOCK_SIZE),
    journalBase(4 * BLOCK_SIZE + Node::SIZE * BLOCK_SIZE * 8), //TODO:double check this
    dataBase(4 * BLOCK_SIZE + Node::SIZE * BLOCK_SIZE * 8 + /*JournalEntry::SIZE*/ (1024 * 5) * (20) /*Journal::NUM_ENTRIES*/)
{
    Debug::printf("data Bitmap:%x\n", dataBitmapBase);
    Debug::printf("inode Bitmap:%x\n", inodeBitmapBase);
    Debug::printf("inodes:%x\n", inodeBase);
    Debug::printf("journal:%x\n", journalBase);
    Debug::printf("data blocks:%x\n", dataBase);
    inodeBitmap = new BillyBitmap(this,inodeBitmapBase);
    dataBitmap = new BillyBitmap(this,dataBitmapBase);
//    journal = new Journal(this, journalBase);
    
    //TODO: Change depending on if our disk has unused space
    // we have a limited amount of memory in our file system.
    // the addition of the journal cuts into the number of data blocks
    // we don't need to change the base o
    for(uint32_t i = 0; i < 20/*Journal::NUM_ENTRIES*/ ; i++)
    {
        dataBitmap->set(i); 
    }
}

BillyFS::~BillyFS() {
    delete dataBitmap;
    delete inodeBitmap;
}

StrongPtr<Node> BillyFS::root(StrongPtr<BillyFS> fs) {
    uint32_t rootIndex;
    fs->device->readAll(8,&rootIndex,sizeof(rootIndex));
    return Node::get(fs,rootIndex);
}

uint32_t BillyFS::allocateBlock(void) {
    Debug::panic("Should not call allocateBlock from node\n");
    int32_t index = dataBitmap->find();
    if (index == -1) {
        return 0;
    }
    uint32_t blockIndex = dataBase / BLOCK_SIZE + index;
    device->writeAll(blockIndex * BLOCK_SIZE, zero_1024, BLOCK_SIZE);
    return blockIndex;
}

StrongPtr<BillyFS> BillyFS::mount(StrongPtr<Ide> device) {
    // TODO: concurrency, locking, etc
    // Per i-node, per file system, ...?
    // MISSING();
    StrongPtr<BillyFS> fs { new BillyFS(device) };
    fs->journal = new Journal (fs, fs->journalBase);
    
    // WE SHOLD RELALY UNCOMMEN TTHIS
    fs->journal->repair();
    return fs;
}

StrongPtr<BillyFS> BillyFS::mkfs(StrongPtr<Ide> device) {
    device->writeAll(0,zero_1024,BLOCK_SIZE);
    device->writeAll(0,"BOBFS439",8);

    uint32_t root = 42;
    device->writeAll(8,&root,sizeof(root));    

    uint32_t expectedRoot;
    device->readAll(8,  &expectedRoot,  4);
    Debug::printf("espcted rog %d\n",  expectedRoot);

    StrongPtr<BillyFS> fs { new BillyFS(device) };

    fs->journal = new Journal (fs, fs->journalBase);

    fs->inodeBitmap->clear();
    fs->inodeBitmap->set(root);
    fs->dataBitmap->clear();

    StrongPtr<Node> rootNode = Node::get(fs,root);

    rootNode->setTypeAdhoc(Node::DIR_TYPE);
    rootNode->setSizeAdhoc(0);
    rootNode->setLinksAdhoc(1);
    rootNode->setDirectAdhoc(0);
    rootNode->setIndirectAdhoc(0);

    return fs;
}

//end of billyfs(part1).cc
