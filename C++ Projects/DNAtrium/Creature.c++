#include "DNAtrium.h"
#include <algorithm>

//random 32-bit number generator
mt19937 Creature::rg(chrono::system_clock::now().time_since_epoch().count());

void Creature::setPC(const unsigned int& pc)
{
	if (_instructions.size() > 0)
	{
		_pc = pc % _instructions.size();
	}
}

void Creature::incrementPC()
{
	setPC(_pc+1);
}

//add a set of instructions to this creature's instructions at n
void Creature::addInstructions(uint b, uint e, uint n, const deque<Instruction>& other)
{
	b = b % (other.size()+1);
	e = e % (other.size()+1);
	n = n % (_instructions.size()+1);

	if (b < e)
	{

		if (e-b+_instructions.size() <= INST_LIMIT)
		{
			_instructions.insert(_instructions.begin()+n, other.begin()+b, other.begin()+e);
		}
		else
		{
			//amount of space left in the instruction list to insert more instructions
			int gap = INST_LIMIT - _instructions.size();
			//fill in the available space
			if (gap > 0)
			{
				_instructions.insert(_instructions.begin()+n, other.begin()+b, other.begin()+(b+gap));
			}
			//then replace old instructions with new instructions
			int i = (n + gap) % _instructions.size();
			for (int otherI = b+gap; otherI < e; ++otherI)
			{
				_instructions[i] = other[otherI];
				i = (i + 1) % _instructions.size();
			}
		}
	}
}

//remove an instruction at a given line n
void Creature::removeInstruction(uint n)
{
	if (_instructions.size()>0)
	{
		n = n % _instructions.size();
		_instructions.erase(_instructions.begin()+n);
	}
}

//remove a range of instructions from b to e (inclusive, exclusive)
void Creature::removeInstructions(uint b, uint e)
{
	if (_instructions.size()>0)
	{
		b = b % _instructions.size();
		e = e % (_instructions.size() + 1);

		if (b < e)
		{
			_instructions.erase(_instructions.begin()+b, _instructions.begin()+e);
		}
	}
}

//swap two instructions in the creature's instruction list
void Creature::swapInstructions(uint a, uint b)
{
	if (_instructions.size() > 0 && a != b)
	{
		a = a % _instructions.size();
		b = b % _instructions.size();
		swap(_instructions[a], _instructions[b]);
	}
}

//randomize a random instruction
void Creature::mutateInstruction()
{
	if (_instructions.size()>0)
	{
		_instructions[rg()%_instructions.size()].mutate();
	}
}

//remove the creature from the board
void Creature::die(Board& board)
{
	board.removeCreature(_r, _c);
}

//turn the Creature by a given number of quarter turns.
//positive qTurns rotate the Creature clockwise
//negative qTurns rotate the Creature counterclockwise
void Creature::rotate(const int& qTurns)
{
	_dir = mod(_dir+qTurns, 4);
}

//Get the coordinates that are in front of the creature
pair<int, int> Creature::getFrontCoords ()
{
	return pair<int, int>(_r + (_dir%2) * ((_dir/2)*2 - 1), _c + (1-(_dir%2)) * ((_dir/2)*2 - 1));
}

//Move the creature forward on the board
void Creature::moveForward(Board& board)
{
	pair<uint, uint> newCoords = board.moveCreature(_r, _c, (_dir%2) * ((_dir/2)*2 - 1), (1-(_dir%2)) * ((_dir/2)*2 - 1));
	_r = newCoords.first;
	_c = newCoords.second;
}

//get the creature that is in front of this creature
Creature* Creature::getFront(Board& board)
{
	pair<int, int> frontCoords = getFrontCoords();
	return board.getCreatureAt(frontCoords.first, frontCoords.second);
}

//create an empty creature at a given row and column
Creature::Creature(const int& r, const int& c, const int& e) : Creature(r, c, e, deque<Instruction>()) {}

//create a creature given a given set of instructions
Creature::Creature(const int& r, const int& c, const int& e, const deque<Instruction>& instructions) : _turn(0), _pc(0), _energy(e), _dir(3), _instructions(instructions), _r(r), _c(c) {}

void Creature::takeTurn(const bool& turn, Board& board)
{
	if (_turn != turn)
	{
		//If the Creature ran out of energy, kill it
		if (_energy <= 0)
		{
			die(board);
			return;
		}

		unsigned int count = 0;

		while (_turn != turn)
		{
			//if the Creature gets caught in a loop or has no instructions, kill it
			if (count >= _instructions.size())
			{
				die(board);
				return;
			}

			_instructions[_pc%_instructions.size()].run(*this, board);
			++count;
		}
	}
}

void Creature::print(ostream& out)
{
	string arrows = "<^>v";
	out << arrows[_dir];
}

void Creature::printVars(ostream& out)
{
	out << "r, c: " << _r << ", " << _c << endl;
	out << "turn: " << _turn << endl;
	out << "pc: " << _pc << endl;
	out << "energy: " << _energy << endl;
	out << "instructions:" << endl;
	for (uint i = 0; i < _instructions.size(); ++i)
	{
		out << " " << i << ": ";
		string instruction = _instructions[i].toString(*this);
		if (i == _pc)
		{
			instruction.insert(instruction.find('\n'), "\t\t<==");
		}
		out << instruction;
	}

//	TODO: still need to convert InstLabels to strings for if_probe
}
