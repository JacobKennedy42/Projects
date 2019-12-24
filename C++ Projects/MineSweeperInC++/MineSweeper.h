#ifndef _MINESWEEPER_H_
#define _MINESWEEPER_H_

#include <random>
#include <Eigen/Dense>

using namespace std;
using namespace Eigen;

//const int TILE_SIZE = 10;//50;
const int BOARD_SIZE = 3;//20;
const int BOARD_SQRD = BOARD_SIZE * BOARD_SIZE;
const int MINE_NUM = 1;//100;
const int MIN_REWARD = BOARD_SQRD * -1;//-10;

////Set a mine on the board
//void placeMine(int inRow, int inCol);

//class Tile
//{
//	//Used for stack purposes
//	Tile* next;
//
//	int number;
//	int r;
//	int c;
//	bool mine;
//	bool revealed;
//
//	public:
//
//	Tile ()
//	{
//		//Do Nothing
//	}
//
//	Tile (int inRow, int inCol)
//	{
//		next = NULL;
//		number = 0;
//		r = inRow;
//		c = inCol;
//		mine = false;
//		revealed = false;
//	}
//
//	Tile* getNext ()
//	{
//		return next;
//	}
//
//	void setNext (Tile* inTile)
//	{
//		next = inTile;
//	}
//
//	int getNumber ()
//	{
//		return number;
//	}
//
//	void setNumber(int inNum)
//	{
//		number = inNum;
//	}
//
//	void incrementNum ()
//	{
//		if (!mine)
//		{
//			++number;
//		}
//	}
//
//	int getRow ()
//	{
//		return r;
//	}
//
//	int getCol()
//	{
//		return c;
//	}
//
//	bool hasMine ()
//	{
//		return mine;
//	}
//
//	void setMine (bool inBool)
//	{
//		mine = inBool;
//
//		//increment the numbers of the adjacent tiles (since there is now an additional mine next to them)
//		if (inBool)
//		{
//			number = -1;
//			placeMine(r, c);
//		}
//	}
//
//	bool isRevealed ()
//	{
//		return revealed;
//	}
//
//	void setReveal (bool inBool)
//	{
//		revealed = inBool;
//	}
//};

//extern Tile board [BOARD_SIZE][BOARD_SIZE];

static const int HIDDEN_VAL = 1;//-1;

//static bool isFinished = false;
extern bool isFinished;
extern int tilesLeft;
//static VectorXd tileVals = VectorXd (BOARD_SQRD);
extern VectorXd tileVals;
extern VectorXd board;
extern random_device rd;
extern default_random_engine re;

void placeMine(int inRow, int inCol);
void initTiles ();
void setMines ();
void setMines(int inRow, int inCol);
int clickTile (int inIndex);
void resetBoard();
void dbPrintMines ();
void dbPrintNums ();
void dbPrintBoard ();

#endif
