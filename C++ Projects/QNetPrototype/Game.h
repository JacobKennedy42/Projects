#ifndef _GAME_H_
#define _GAME_H_

//#include <stdio.h>
//#include <iostream>
#include <random>
//#include <cmath>

#include <Eigen/Dense>

using namespace std;
using namespace Eigen;

static const int BOARD_SIZE = 4;
static const int BOARD_SQRD = BOARD_SIZE * BOARD_SIZE;

//the board values used to represent the player, the goal, and empty blocks
enum Type {EMPTY, PLAYER, GOAL = -1};
enum Direction {LEFT, RIGHT, UP, DOWN};

extern bool isFinished;
extern VectorXd board;
extern random_device rd;
extern default_random_engine re;

void dbPrintBoard();
void resetBoard();
double movePlayer(int inDir);

#endif
