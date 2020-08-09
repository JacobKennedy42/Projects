#include "DNAtrium.h"
#include <fstream> //ofstream
#include <sys/stat.h>//mkdir

string SAVE_DIR = "creatureStats";

void saveCreatureStats (const uint& turn, Board& board)
{
	mkdir(SAVE_DIR.c_str(), 0777);
	string fileName = SAVE_DIR + "/turn" + to_string(turn);
	cout << "Writing creature stats out to " << fileName << endl;
	ofstream outFile;
	outFile.open(fileName);
	board.print(outFile);
	board.printCreatures(outFile);
	outFile.close();
}

int main()
{
	uint width = 10;
	uint height = 10;
	uint numTurns = 100;
	uint turnsPerSave = 1000;
	cout << "Board width: " << flush;
	cin >> width;
	cout << "Board height: " << flush;
	cin >> height;
	cout << "Number of turns: " << flush;
	cin >> numTurns;
	cout << "Turns per save: " << flush;
	cin >> turnsPerSave;

	Board board(width, height);

	//TODO: try mapping instructions to their number of args. When mutating, check the number of args before making the arg list

	for (int r = 0; r < board.rows(); ++r)
	{
		for (int c = 0; c < board.cols(); ++c)
		{
			if ((r+c)%2)
			{
				board.addCreature(r, c, 100, deque<Creature::Instruction>{
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE),
					Creature::Instruction(MUTATE)
				});
			}
			else
			{
				board.addCreature(r, c, 100, deque<Creature::Instruction>{
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW),
					Creature::Instruction(GROW)
				});
			}
		}
	}

//	board.print(cout);
	for (int turn = 0; turn < numTurns; ++turn)
	{
		if (turn % turnsPerSave == 0)
		{
			saveCreatureStats(turn, board);
		}
		board.runTurn();
//		board.print(cout);
	}

	//write out the creature characteristics to a text file
	saveCreatureStats(numTurns, board);

//	TODO: make give work the opposite way of eat.
//	TODO: give use arg0, mod _energy
//	TODO: make delete and inject wrap around
//	TODO: maybe make inject cost energy equal to amount injected and delete gain energy likewise

	return 0;
}
