#include "DNAtrium.h"

Board::Board(uint width, uint height) : _width(width), _height(height),
	_grid(height, vector<Creature*>(width)), _turn(0) {}

void Board::print(ostream& out)
{
	out << endl;

	for (int r = 0; r < _height; ++r)
	{
		for (int c = 0; c < _width; ++c)
		{
			if (_grid[r][c] == nullptr)
			{
				out << ".";
			}
			else
			{
				_grid[r][c]->print(out);
			}
		}
		out << endl;
	}
}

//print the stats of each creature
void Board::printCreatures(ostream& out)
{
	for (int r = 0; r < _height; ++r)
	{
		for (int c = 0; c < _width; ++c)
		{
			if (_grid[r][c] != nullptr)
			{
				out << endl;
				_grid[r][c]->printVars(out);
			}
		}
	}
}

uint Board::rows()
{
	return _grid.size();
}

uint Board::cols()
{
	return _grid[0].size();
}

//add a Creature at a given row and column
void Board::addCreature(const uint& r, const uint& c, const uint& e)
{
	addCreature(r, c, e, deque<Creature::Instruction>());
}

void Board::addCreature(const uint& r, const uint& c, const uint& e, const deque<Creature::Instruction>& instructions)
{
	uint modR = mod(r, _grid.size());
	uint modC = mod(c, _grid[modR].size());
	removeCreature(r, c);
	_grid[modR][modC] = new Creature(modR, modC, e, instructions);
}

//return the creature at row r and column c
Creature* Board::getCreatureAt(const uint& r, const uint& c)
{
	uint modR = mod(r, _grid.size());
	uint modC = mod(c, _grid[modR].size());
	return _grid[modR][modC];
}

//move Creature at position (r, c) by dr and dc, respectively.
//return the new coordinates of the creature
//If the destination coordinates have a Creature already, do nothing.
pair<uint, uint> Board::moveCreature(const uint& r, const uint& c, const int& dr, const int& dc)
{
	int modR = mod(r, _grid.size());
	int modC = mod(c, _grid[modR].size());
	uint rows = _grid.size();
	uint cols = _grid[r].size();
	uint newR = mod(modR+dr, rows);
	uint newC = mod(modC+dc, cols);
	if ( _grid[newR][newC] == nullptr)
	{
		_grid[newR][newC] = _grid[modR][modC];
		_grid[modR][modC] = nullptr;
		return pair<uint, uint>(newR, newC);
	}

	return pair<uint, uint>(modR, modC);
}

//remove a Creature at a given row and column
void Board::removeCreature(const uint& r, const uint& c)
{
	uint modR = mod(r, _grid.size());
	uint modC = mod(c, _grid[modR].size());
	delete _grid[modR][modC];
	_grid[modR][modC] = nullptr;
}

//make all the Creatures take their turn
void Board::runTurn ()
{
	_turn = !_turn;

	for (int r = 0; r < _grid.size(); ++r)
	{
		for (int c = 0; c < _grid[r].size(); ++c)
		{
			Creature* creature = _grid[r][c];
			if (creature != nullptr)
			{
				creature->takeTurn(_turn, *this);
			}
		}
	}

//	print(cout);
}
