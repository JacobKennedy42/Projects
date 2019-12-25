//billyfs.h

#ifndef _BOBFS_H_
#define _BOBFS_H_

#include "refs.h"
#include "ide.h"
#include "mutex.h"
#include "journal.h"

class Journal;

class BillyFS;

struct BillyBitmap {
    BillyFS* fs;
    char* myBitmap;
    uint32_t offset;


    BillyBitmap (BillyFS *fs, uint32_t offset);
    void clear(void);
    void set(int32_t index);
    void clear(int32_t index);
    int32_t find(void);
    void dump(void)
    {
        Debug::printf("Dumping the bitmap\n");
        for(uint32_t i =0; i < 1024;  i++)
        {
            if(myBitmap[i] & 0x1)
            {
                Debug::printf("True at %d\n", i);
            }
        }
    }
};

class Bitmap {
    BillyFS* fs;
    uint32_t offset;
public:
    Bitmap(BillyFS *fs, uint32_t offset);
    void clear(void);
    void set(int32_t index);
    void clear(int32_t index);
    int32_t find(void);
};

//stores the info held in the inodes on disk
struct NodeInfo
{
    uint16_t type;
    uint16_t nLinks;
    uint32_t size;
    uint32_t direct;
    uint32_t indirect;

    // NodeInfo(uint32_t iNumber, BillyFS* fs);
    // NodeInfo(){}
    void printNode()
    {
        Debug::printf("Type %d | nLinks: %d | size: %d | direct: %d | indirect: %d\n", type, nLinks, size, direct, indirect);
    }
    // NodeInfo(uint16_t type,  uint16_t nLinks, uint32_t size, uint32_t direct,  uint32_t indirect)
    // {
    //     this->type  = type;
    //     this->nLinks = nLinks;
    //     this->size = size;
    //     this->direct = direct;
    //     this->indirect = indirect;
    // }
    
};

class Node { 
    StrongPtr<BillyFS> fs;

    uint32_t getBlockNumber(uint32_t blockIndex);
    
/*    // new variables
    uint16_t type;
    uint16_t links;
    uint32_t size;
    uint32_t direct;
    uint32_t indirect;
    uint32_t iNum;*/
public:
    uint32_t inumber;
    //The info stored on this node's inode on disk

    uint32_t offset;
    NodeInfo myInfo;

    static constexpr uint32_t SIZE = 16;
    static constexpr uint16_t DIR_TYPE = 1;
    static constexpr uint16_t FILE_TYPE = 2;

    Node(StrongPtr<BillyFS> fs, uint32_t inumber);

    uint16_t getType(void);
    uint16_t getLinks(void);
    uint32_t getSize(void);
    uint32_t getDirect(void);
    uint32_t getIndirect(void);
    uint32_t getInum(void);

    void setType(uint16_t type);
    void setLinks(uint16_t type);
    void setSize(uint32_t type);
    void setDirect(uint32_t type);
    void setIndirect(uint32_t type);

    void setTypeInMem(uint16_t type);
    void setLinksInMem(uint16_t type);
    void setSizeInMem(uint32_t type);
    void setDirectInMem(uint32_t type);
    void setIndirectInMem(uint32_t type);

    void setTypeAdhoc(uint16_t type);
    void setLinksAdhoc(uint16_t type);
    void setSizeAdhoc(uint32_t type);
    void setDirectAdhoc(uint32_t type);
    void setIndirectAdhoc(uint32_t type);

    void printNode(void)
    {
        Debug::printf("Type %x | links  %x |  size  %d | direct %x | indirect %x\n", getType(), getLinks(), getSize(), getDirect(), getIndirect());
    }

    int32_t read(uint32_t offset, void* buffer, uint32_t n);
    int32_t readAll(uint32_t offset, void* buffer, uint32_t n);

    int32_t write(uint32_t offset, const void* buffer, uint32_t n);
    int32_t writeAll(uint32_t offset, const void* buffer, uint32_t n);

    StrongPtr<Node> newNode(const char* name, uint32_t type);
    StrongPtr<Node> newFile(const char* name);
    StrongPtr<Node> newDirectory(const char* name);
    StrongPtr<Node> findNode(const char* name);

    bool isFile(void);
    bool isDirectory(void);

    void linkNode(const char* name, StrongPtr<Node> file); // handled through the Journal
	void crashLinkNodeTest(const char* name, StrongPtr<Node> file); // handled through the Journal
    void dump(const char* name);

    static StrongPtr<Node> get(StrongPtr<BillyFS> fs, uint32_t index) {
        StrongPtr<Node> n { new Node(fs,index) };
        return n;
    }
};



class BillyFS {
    Mutex lock;

    uint32_t allocateBlock(void);
public:
    StrongPtr<Ide> device;
    uint32_t dataBitmapBase;
    uint32_t inodeBitmapBase;
    uint32_t inodeBase;
    uint32_t journalBase;
    uint32_t dataBase;
    BillyBitmap* inodeBitmap;
    BillyBitmap* dataBitmap;
    Journal* journal;

    static constexpr uint32_t BLOCK_SIZE = 1024;

    BillyFS(StrongPtr<Ide> device);
    virtual ~BillyFS();
    static StrongPtr<BillyFS> mkfs(StrongPtr<Ide> device);
    static StrongPtr<BillyFS> mount(StrongPtr<Ide> device);

    static StrongPtr<Node> root(StrongPtr<BillyFS> fs);

    friend class Node;
    friend class Bitmap;
};



#endif
