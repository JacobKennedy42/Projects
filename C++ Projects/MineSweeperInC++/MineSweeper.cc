#include <stdio.h>
#include <stdlib.h>
#include <random>

//#include <Eigen/Dense>

#include "MineSweeper.h"

using namespace Eigen;
using namespace std;

//Tile board [BOARD_SIZE][BOARD_SIZE];
//static VectorXd board = VectorXd (BOARD_SQRD);
//static bool revealed [BOARD_SQRD];

////TODO: delete this
////TODO: this was originally supposed to be in TIle::setMine, but you can't
////declare board (an array of Tiles) without implementing Tile, and you can't
////Use board in Tile without declaring it first. Find a good way around this
////that preferably doesn't use heap memory, doesn't pass board as a param, and
////doesn't change board from array of Tile to array of Tile*.
//void placeMine(int inRow, int inCol)
//{
//	int rLowBound = inRow != 0 ? inRow-1 : 0;
//	int rHiBound = inRow != BOARD_SIZE-1 ? inRow+1 : BOARD_SIZE-1;
//	int cLowBound = inCol != 0 ? inCol-1 : 0;
//	int cHiBound = inCol != BOARD_SIZE-1 ? inCol+1 : BOARD_SIZE-1;
//
//	for (int row = rLowBound; row <= rHiBound; row++)
//	{
//		for (int col = cLowBound; col <= cHiBound; col++)
//		{
//			if (!(row == inRow && col == inCol))
//			{
//				++board(row * BOARD_SIZE + col);
//			}
//		}
//	}
//}

bool minesSet = false;
bool isFinished = false;
int tilesLeft = BOARD_SQRD - MINE_NUM;
//the actual values of the board tiles
VectorXd tileVals = VectorXd (BOARD_SQRD);
//what the net sees (a value of 1 means unrevealed)
VectorXd board = VectorXd (BOARD_SQRD);

random_device rd;
default_random_engine re(rd());

//void initTiles ()
//{
//	for (int r = 0; r < BOARD_SIZE; r++)
//	{
//		for (int c = 0; c < BOARD_SIZE; c++)
//		{
//			board[r][c] = Tile(r, c);
//		}
//	}
//}

//Set the mines at random tile locations, excluding the tile at inX, inY
void setMines (int inIndex)
{
//	printf("setting mines\n");


	int tempTiles[BOARD_SQRD - 1];
	int tempIndex = -1;
	//Fill tempTiles with the tile indices
	for (int i = 0; i < BOARD_SQRD; i++)
	{
		if (i != inIndex)
		{
			tempTiles[++tempIndex] = i;
		}
	}

//	//place tile non-randomly
//	tileVals[0] = -1;
//	tileVals[1] += .1;
//	tileVals[BOARD_SIZE] += .1;
//	tileVals[BOARD_SIZE+1] += .1;

//	printf("made temp tiles\n");

	int randIndex, centerIndex, r, c, rLowBound, rHiBound, cLowBound, cHiBound, adjIndex;
	//place the mines randomly
	for (int lastIndex = BOARD_SQRD - 2; lastIndex > BOARD_SQRD - 2 - MINE_NUM; lastIndex--)
	{
		randIndex = rand() % lastIndex;
		centerIndex = tempTiles[randIndex];

		//set the mine (mine tiles have val = -1)
		tileVals[centerIndex] = -1;

		//Update the values of the adjacent tiles
		r = centerIndex / BOARD_SIZE;
		c = centerIndex % BOARD_SIZE;
		rLowBound = r != 0 ? r-1 : 0;
		rHiBound = r != BOARD_SIZE-1 ? r+2 : BOARD_SIZE;
		cLowBound = c != 0 ? c-1 : 0;
		cHiBound = c != BOARD_SIZE-1 ? c+2 : BOARD_SIZE;
//		printf("r:%d\n low:%d\n high:%d\nc:%d\n low:%d\n high:%d\n", r, c, rLowBound, rHiBound, cLowBound, cHiBound);
		for (int row = rLowBound; row < rHiBound; row++)
		{
			for (int col = cLowBound; col < cHiBound; col++)
			{
				//TODO: might optimize this by putting part of in in the upper loop
				adjIndex = row * BOARD_SIZE + col;
//				printf("temp Index:%d\n", adjIndex);
				//Only update the tile if it does not contain a mine
				if (tileVals(adjIndex) != -1)
				{
					tileVals(adjIndex) += .1;
				}
			}
		}

		//Remove the set mine from the tile pool (puts that last tile in the place of the one removed)
		tempTiles[randIndex] = tempTiles[lastIndex];
	}

//	printf("out of loop. mines set\n");

	minesSet = true;
}

////Set the mines at random tile locations, excluding the tile at inX, inY
//void setMines(int inRow, int inCol)
//{
//	Tile* tempTiles[BOARD_SIZE * BOARD_SIZE - 1];
//	Tile* tempTile;
//
//	int tempIndex = 0;
//	//Fill tempTiles with the tiles from the board
//	for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++)
//	{
//		tempTile = &board[i/BOARD_SIZE][i%BOARD_SIZE];
//		if (!(tempTile->getRow() == inRow && tempTile->getCol() == inCol))
//		{
//			tempTiles[tempIndex++] = tempTile;
//		}
//	}
//
//	int randIndex;
//	//place the mines randomly
//	for (int lastIndex = BOARD_SQRD - 2; lastIndex > BOARD_SIZE * BOARD_SIZE - 2 - MINE_NUM; lastIndex--)
//	{
//		randIndex = rand() % lastIndex;
//		//set the mine (mine tiles have val = -1)
//		board[tempTiles[randIndex]] = -1;
//		//Remove the set mine from the tile pool (puts that last tile in the place of the one removed)
//		tempTiles[randIndex] = tempTiles[lastIndex];
//	}
//
//	minesSet = true;
//}

//Reveal the tile at the given row and col. Clear the area psuedo-recursively if the tile's number is 0
//return the number of points gained
int clickTile (int inIndex)
{
//	printf("clicked tile\n");

	if (!minesSet)
	{
		setMines(inIndex);
	}

//	printf("set mines\n");
//	dbPrintNums();

	//If the tile has already been revealed, give -1 point
	if (board(inIndex) != HIDDEN_VAL)
	{
		return -1;
	}

	//If the tile has a mine, end the game. Give the minimum reward
	if (tileVals[inIndex] == -1)
	{
		isFinished = true;
//		printf("BOOM! tiles left:%d\n", tilesLeft);
		return MIN_REWARD;
	}

	int oldTilesLeft = tilesLeft;

	//reveal the given tile
	board(inIndex) = tileVals(inIndex);
	--tilesLeft;

	//If the given tile has a value of 0, reveal the tiles recursively
	if (tileVals[inIndex] == 0)
	{
//		printf("clicked 0 tile\n");

//		printf("tile index:%d\n", inIndex);

		int r, c, rLowBound, rHiBound, cLowBound, cHiBound;

		//stack that stores the index of the board tiles that have yet to be processed
		//Invariant: all tiles in the stack have a value of 0
		int stack [BOARD_SQRD];
		int stackIndex = 0;
		stack[stackIndex] = inIndex;

		//While the stack is not empty
		while (stackIndex > -1)
		{
			r = stack[stackIndex] / BOARD_SIZE;
			c = stack[stackIndex--] % BOARD_SIZE;
			rLowBound = r != 0 ? r-1 : 0;
			rHiBound = r != BOARD_SIZE-1 ? r+2 : BOARD_SIZE;
			cLowBound = c != 0 ? c-1 : 0;
			cHiBound = c != BOARD_SIZE-1 ? c+2 : BOARD_SIZE;

//			printf("r:%d c:%d\n low:%d %d\n high:%d %d\n", r, c, rLowBound, cLowBound, rHiBound, cHiBound);

			//Look at adjacent tiles
			for (int row = rLowBound; row < rHiBound; row++)
			{
				for (int col = cLowBound; col < cHiBound; col++)
				{
					int tempIndex = row * BOARD_SIZE + col;

					if (board(tempIndex) == HIDDEN_VAL)
					{
						//If adjacent tile has num = 0 and is not revealed, add it to the stack
						if (tileVals[tempIndex] == 0)
						{
							//Assign this tile as the next tile to be processed
							stack[++stackIndex] = tempIndex;

//							printf("added %d\n", tempIndex);
						}
						//reveal the tile
						board(tempIndex) = tileVals(inIndex);
						--tilesLeft;
					}
				}
			}
		}

//		printf("out of loop\n");

	}


//	Tile* stackTile = &board[inRow][inCol];
//	stackTile->setReveal(true);
//
//	if (stackTile->getNumber() == 0)
//	{
//
//		//Invariant: all tiles in the stack have number = 0
//		stackTile->setNext(NULL);
//
//		int r, c, rLowBound, rHiBound, cLowBound, cHiBound;
//		Tile* tempTile;
//
//		while (stackTile != NULL)
//		{
//			r = stackTile->getRow();
//			c = stackTile->getCol();
//			rLowBound = r != 0 ? r-1 : 0;
//			rHiBound = r != BOARD_SIZE-1 ? r+1 : BOARD_SIZE-1;
//			cLowBound = c != 0 ? c-1 : 0;
//			cHiBound = c != BOARD_SIZE-1 ? c+1 : BOARD_SIZE-1;
//
//			for (int row = rLowBound; row <= rHiBound; row++)
//			{
//				for (int col = cLowBound; col <= cHiBound; col++)
//				{
//					if (!(row == r && col == c))
//					{
//						tempTile = &board[row][col];
//
//						//If adjacent tile has num = 0 and is not revealed, add it to the stack
//						if (tempTile->getNumber() == 0 && !tempTile->isRevealed())
//						{
//							//Assign this tile as the next tile to be processed
//							tempTile->setNext(stackTile->getNext());
//							stackTile->setNext(tempTile);
//						}
//						tempTile->setReveal(true);
//					}
//				}
//			}
//
//			stackTile = stackTile->getNext();
//		}
//	}

//	printf("\n");
//	dbPrintBoard();

	//If all non-mine tiles have been cleared, end the game and give the max points possible
	if (!tilesLeft)
	{
		isFinished = true;
//		printf("You win!\n");
//		return BOARD_SQRD;
	}

	//give points equal to the number of tiles revealed
	return oldTilesLeft - tilesLeft;

}

//reset the board (should be done before starting a game)
void resetBoard ()
{
	//Initialize the rand seed
//	random_device rd;
	srand(rd());

	minesSet = false;
	isFinished = false;
	tilesLeft = BOARD_SQRD - MINE_NUM;

	//zero out the tileVals
	tileVals = VectorXd::Zero(tileVals.size());

	//mark all board tiles as unrevealed
	for (int i = 0; i < BOARD_SQRD; ++i)
	{
		board(i) = HIDDEN_VAL;
	}
}

void dbPrintMines ()
{
	for (int r = 0; r < BOARD_SIZE; r++)
	{
		printf("| ");
		for (int c = 0; c < BOARD_SIZE; c++)
		{
			tileVals(r*BOARD_SIZE + c) == -1 ? printf("X") : printf("_");
		}
		printf(" |\n");
	}
}

void dbPrintNums ()
{
	for (int r = 0; r < BOARD_SIZE; r++)
	{
		printf("| ");
		for (int c = 0; c < BOARD_SIZE; c++)
		{
//			printf("%%%lf %d%%", board(r*BOARD_SIZE + c), (int) (board(r*BOARD_SIZE + c) * 10));

			tileVals(r*BOARD_SIZE + c) == -1 ? printf("X") : tileVals(r*BOARD_SIZE + c) == 0 ? printf("_") : printf("%c", (int) (tileVals(r*BOARD_SIZE + c) * 10) + '0');
		}
		printf(" |\n");
	}
}

void dbPrintBoard ()
{
	for (int r = 0; r < BOARD_SIZE; r++)
	{
		printf("| ");
		for (int c = 0; c < BOARD_SIZE; c++)
		{
			board(r*BOARD_SIZE + c) == HIDDEN_VAL ? printf("#") : tileVals(r*BOARD_SIZE + c) == -1 ? printf("X") : tileVals(r*BOARD_SIZE + c) == 0 ? printf("_") : printf("%c", (int) (tileVals(r*BOARD_SIZE + c) * 10) + '0');
		}
		printf(" |\n");
	}
}

//int main ()
//{
//	startGame();
//
////	setMines(0);
//	printf("points: %d\n", clickTile(0));
//
////	dbPrintNums();
////	printf("\n");
////	dbPrintMines();
//	printf("\n");
//	dbPrintBoard();
////	printf("\n");
//
//	printf("points: %d\n", clickTile(BOARD_SQRD/2));
//
////	dbPrintNums();
////	printf("\n");
////	dbPrintMines();
//	printf("\n");
//	dbPrintBoard();
//
//	startGame();
//
////	setMines(0);
//	printf("points: %d\n", clickTile(0));
//
////	dbPrintNums();
////	printf("\n");
////	dbPrintMines();
//	printf("\n");
//	dbPrintBoard();
////	printf("\n");
//
//	printf("points: %d\n", clickTile(BOARD_SQRD/2));
//
////	dbPrintNums();
////	printf("\n");
////	dbPrintMines();
//	printf("\n");
//	dbPrintBoard();
//}
