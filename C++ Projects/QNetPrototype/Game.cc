#include <random>

#include "Game.h"

using namespace Eigen;
using namespace std;

////the board values used to represent the player, the goal, and empty blocks
//enum Type {EMPTY, PLAYER, GOAL = -1};
//enum Direction {LEFT, RIGHT, UP, DOWN};

VectorXd board = VectorXd::Zero(BOARD_SQRD);

//used to randomize the board
uniform_int_distribution<int> randTile (0, BOARD_SIZE - 1);
random_device rd;
default_random_engine re(rd());

//whether or not the game is finished
bool isFinished = false;

//the position of the goal and player
int goalX = 0;
int goalY = 0;
int playerX = 0;
int playerY = 0;

//reset the board for a new game
void resetBoard ()
{
	isFinished = false;

	//get rid of the previous goal and player
	board(goalY * BOARD_SIZE + goalX) = EMPTY;
	board(playerY * BOARD_SIZE + playerX) = EMPTY;
	//randomly place a new goal and the player
	goalX = randTile(re);
	goalY = randTile(re);
	board(goalY * BOARD_SIZE + goalX) = GOAL;
	playerX = randTile(re);
	playerY = randTile(re);
	//make sure that the player does not spawn on the goal
	while (playerX == goalX && playerY == goalY)
	{
		playerX = randTile(re);
		playerY = randTile(re);
	}
	board(playerY * BOARD_SIZE + playerX) = PLAYER;

//	//start the player and goal in the same starting position (opposite corners)
////	playerX = 0;
////	playerY = 0;
//	playerX = 0;
//	playerY = BOARD_SIZE - 1;
//	board(playerY * BOARD_SIZE + playerX) = PLAYER;
////	goalX = BOARD_SIZE - 1;
////	goalY = BOARD_SIZE - 1;
//	goalX = BOARD_SIZE - 1;
//	goalY = 0;
//	board(goalY * BOARD_SIZE + goalX) = GOAL;

//	dbPrintBoard();
//	printf("\n");
}

//Move the player in a given direction
//return the reward (the change in distance between the player and the goal)
double movePlayer (int inDir)
{
	int reward = -1;
	board(playerY * BOARD_SIZE + playerX) = EMPTY;

	//remove the player from their previous position;

	if (inDir == LEFT)
	{
		if (playerX  != 0)
		{
			if (goalX < playerX)
			{
				reward = 1;
			}

			--playerX;
		}
	}
	else if (inDir == RIGHT)
	{
		if (playerX != BOARD_SIZE - 1)
		{
			if (goalX > playerX)
			{
				reward = 1;
			}

			++playerX;
		}
	}
	else if (inDir == UP)
	{
		if (playerY != 0)
		{
			if (goalY < playerY)
			{
				reward = 1;
			}

			--playerY;
		}
	}
	else if (inDir == DOWN)
	{
		if (playerY != BOARD_SIZE - 1)
		{
			if (goalY > playerY)
			{
				reward = 1;
			}

			++playerY;
		}
	}

	//put the player in their new postion
	board(playerY * BOARD_SIZE + playerX) = PLAYER;

	//give a big reward when the player reaches the goal
	if (playerX == goalX && playerY == goalY)
	{
		reward = 10;
		isFinished = true;
	}

//	printf("dir:%d, reward:%d\n", inDir, reward);
//	dbPrintBoard();
//	printf("\n");

	return reward;;
}

void dbPrintBoard()
{
	for (int i = 0; i < BOARD_SQRD; ++i)
	{
		board(i) == EMPTY ? printf("_") : board(i) == PLAYER ? printf("O"): printf("X");
		if (i % BOARD_SIZE == BOARD_SIZE - 1)
		{
			printf("\n");
		}
	}
}

//int main ()
//{
//	uniform_int_distribution<int> randDir (0, 3);
//	int totalTurns = 0;
//	for (int i = 0; i < 1000000; ++i)
//	{
//		resetBoard();
//
//		while (!isFinished)
//		{
//			movePlayer(randDir(re));
//			++totalTurns;
//		}
//
//		int batchSize = 10000;
//		if (i % batchSize == batchSize - 1)
//		{
//			printf("%lf\n", totalTurns / (double) batchSize);
//			totalTurns = 0;
//		}
//	}
//}
