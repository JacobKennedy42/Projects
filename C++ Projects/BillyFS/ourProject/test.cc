#include "stdint.h"
#include "debug.h"
#include "ide.h"
#include "billyfs.h"

void kernelStart(void) {
}

void check(const char* name, StrongPtr<Node> f) {
    if (f.isNull()) {
        Debug::printf("*** 0 %s is null\n",name);
        return;
    }

    if (f->isFile()) {
        Debug::printf("*** 0 %s is a file\n",name);
    }
    if (f->isDirectory()) { //////////////////////////
        Debug::printf("*** 0 %s is a directory\n",name);
    }

    Debug::printf("*** 0 %s has %d bytes\n",name,f->getSize());
    Debug::printf("*** 0 %s has %d links\n",name,f->getLinks());
}

StrongPtr<Node> find(StrongPtr<Node> d, const char* name) {
    StrongPtr<Node> f = d->findNode(name);
    check(name,f);
    return f;
}

void kernelMain(void) {

	Debug::printf("*** Let's test the journaling file system!\n");
{
    // let's make our file system and the usuallll
	StrongPtr<Ide> disk { new Ide(3) };
    StrongPtr<BillyFS> fs = BillyFS::mkfs(disk);

	// make the root!
    StrongPtr<Node> root = BillyFS::root(fs);

	/*
	uint32_t data = 0x00100096;
    root->write(0, &data, 4);

    root->printNode();

    uint32_t  blargl;
    root->read(0, &blargl, 4);
    Debug::printf("blarglo  :%x\n", blargl);
    Debug::printf("*** blargl:%x\n", blargl);
	*/
    
	// let's test making new nodes!
	Debug::printf("*** Making new nodes!\n");
	StrongPtr<Node> newNode = root->newFile("toot");
	Debug::printf("*** Let's make sure the new node was created correctly\n");
	newNode->dump("New node");    

	// let's now test writing to new nodes

	/*	
	Debug::printf("ROOT NODE location of datablock %x\n", root->getDirect());
    Debug::printf("CHILD  NODE location of datablock %x\n", newNode->getDirect());

    Debug::printf("inodebase is %x\n", fs->inodeBase);
    Debug::printf("roots inumber %x\n", root->inumber);
    
	root->printNode();
    */

	Debug::printf("*** writing and reading from new node\n");
 
	uint32_t newData  = 0x80085;
	uint32_t newData2 = 0;
    
	newNode->write(0, &newData, 4);
	newNode->read(0, &newData2, 4);
	Debug::printf("*** newData2: %x\n", newData2);
//    newNode->printNode();


//    Debug::printf("*** newData:%x\n", newData);

//    StrongPtr<Node> childFile = root->newFile("child");
	//childFile->printNode();


//	Debug::printf("*** made this far 2\n");   
	//newNode->linkNode("child", childFile);

//	childFile->printNode();

//	Debug::printf(" Root file offset is %x\n", root->offset);
	
	
//	Debug::panic(" Child file offset is %x\n", childFile->offset);
//	Debug::printf("child links %d\n", childFile->getLinks());
    
	
//	newNode->dump("toots");

	Debug::printf("*** doing some link tests\n");

	StrongPtr<Node> d1 = root->newDirectory("d1");
	d1->linkNode("child2", newNode);
	uint32_t newData3 = 0;
	StrongPtr<Node> foundNode = d1->findNode("child2");
	foundNode->read(0, &newData3, 4);
	Debug::printf("*** newData3: %x\n", newData3);

//    root->printNode();
   // uint32_t newData4  = 0x455;
	
	}	
{
	StrongPtr<Ide> disk { new Ide(3) };

	// let's try to recover by mounting the disk
	StrongPtr<BillyFS> newFs = BillyFS::mount(disk);
    
	// let's see if the root's info was recovered!
	StrongPtr<Node> root = BillyFS::root(newFs); Debug::printf("*** Info after we repair\n");
	root->printNode();

	uint32_t newData4 = 0;
	StrongPtr<Node> foundFile = root->findNode("toot");
	foundFile->read(0, &newData4, 4);
	Debug::printf("*** newData4: %x\n", newData4);
}


}

void kernelTerminate(void) {
}
