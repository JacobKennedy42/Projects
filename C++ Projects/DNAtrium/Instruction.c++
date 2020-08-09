#include "DNAtrium.h"

//library of instruction functions
const unordered_map<InstLabel, function<void(Creature&, Board&, const vector<uint>&)>> Creature::Instruction::instLib
{
//	//spend this Creature's action doing nothing
//	{WAIT, [](Creature& creature, Board& board, const vector<uint>& args){
//		creature.incrementPC();
//		creature._turn = !creature._turn;
//	}},
	//Move the Creature in the direction that they are facing
	{MOVE, [](Creature& creature, Board& board, const vector<uint>& args){
		creature.moveForward(board);
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//turn the Creature clockwise/counter-clockwise
	{TURN_LEFT, [](Creature& creature, Board& board, const vector<uint>& args){
		creature.rotate(-1);
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	{TURN_RIGHT, [](Creature& creature, Board& board, const vector<uint>& args){
		creature.rotate(1);
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//Increase the Creature's energy by a random amount. Might mutate the creature
	{GROW, [](Creature& creature, Board& board, const vector<uint>& args){
		uint gained = rg();
		creature._energy+=(gained%MAX_GROW)+1;
//		creature.swapInstructions(creature._pc, rg());
		if (gained%10000==0)
		{
			creature.mutateInstruction();
		}
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//Take energy from a Creature in front of this creature
	//If there is no creature, lose energy instead
	{EAT, [](Creature& creature, Board& board, const vector<uint>& args){
		Creature* prey = creature.getFront(board);
		if (prey != nullptr)
		{
			if (prey->_energy > MAX_EAT)
			{
				creature._energy+=MAX_EAT;
				prey->_energy-=MAX_EAT;
			}
			else
			{
				creature._energy+=prey->_energy;
				prey->die(board);
			}
		}
		else
		{
			--creature._energy;
		}
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//Give energy to the creature in front of this creature
	{GIVE, [](Creature& creature, Board& board, const vector<uint>& args){
		Creature* prey = creature.getFront(board);
		if (prey != nullptr)
		{
			++prey->_energy;
		}
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//create a creature in front of this creature that has a given energy and a portion of this creature's instructions
	{BIRTH, [](Creature& creature, Board& board, const vector<uint>& args){
		int childEnergy = mod(args[2], creature._energy) + 1;
		if (creature.getFront(board) == nullptr)
		{
			pair<int, int> frontCoords = creature.getFrontCoords();
			board.addCreature(frontCoords.first, frontCoords.second, childEnergy);
			Creature* child = creature.getFront(board);
			child->addInstructions(args[0], args[1], 0, creature._instructions);
		}
		creature._energy -= childEnergy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//Insert a potion of this creature's instructions into another creature
	{INJECT, [](Creature& creature, Board& board, const vector<uint>& args){
		Creature* prey = creature.getFront(board);
		if (prey != nullptr)
		{
			prey->addInstructions(args[0], args[1], args[2], creature._instructions);
		}
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//Delete a range instructions from this creature
	{DELETE, [](Creature& creature, Board& board, const vector<uint>& args){
		creature.removeInstructions(args[0], args[1]);
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//randomizes a random instruction in the creature in front of this creature
	{MUTATE, [](Creature& creature, Board& board, const vector<uint>& args){
		Creature* prey = creature.getFront(board);
		if (prey != nullptr)
		{
			prey->mutateInstruction();
		}
		--creature._energy;
		creature.incrementPC();
		creature._turn = !creature._turn;
	}},
	//set the pc to a specific line in the instructions
	{GOTO, [](Creature& creature, Board& board, const vector<uint>& args){
		creature.setPC(args[0]);
	}},
	//have a 50% of either setting the pc to a given line, or going to the next line
	{IF_RAND, [](Creature& creature, Board& board, const vector<uint>& args){
		if (rg()%2)
		{
			creature.setPC(args[0]);
		}
		else
		{
			creature.incrementPC();
		}
	}},
	//Go to a specified line if there is a creature in front of this creature. Goto the next line otherwise
	{IF_FRONT, [](Creature& creature, Board& board, const vector<uint>& args){
		if (creature.getFront(board) != nullptr)
		{
			creature.setPC(args[0]);
		}
		else
		{
			creature.incrementPC();
		}
	}},
	//go to a specified line if a given line of the creature in front matches a given label
	{IF_PROBE, [](Creature& creature, Board& board, const vector<uint>& args){
		Creature* prey = creature.getFront(board);
		if (prey != nullptr &&
			prey->_instructions.size() > 0 &&
			prey->_instructions[args[0]%prey->_instructions.size()]._label == static_cast<InstLabel>(args[1]%instLib.size()))
		{
			creature.setPC(args[2]);
		}
		else
		{
			creature.incrementPC();
		}
	}}
};

//Fill the library keys vector with the keys from the Instruction Library
const vector<InstLabel> Creature::Instruction::initLibKeys ()
{
	vector<InstLabel> output;
	output.reserve(instLib.size());
	for (auto e : instLib)
	{
		output.push_back(e.first);
	}
	return output;
}
const vector<InstLabel> Creature::Instruction::libKeys(initLibKeys());


//functions to print instructions and their arguments
const unordered_map<InstLabel, function<string(const Creature&, const vector<uint>&)>> Creature::Instruction::printInst
{
	{WAIT, [](const Creature&, const vector<uint>&){
		return "WAIT\n";
	}},
	{MOVE, [](const Creature&, const vector<uint>&){
		return "MOVE\n";
	}},
	{TURN_LEFT,  [](const Creature&, const vector<uint>&){
		return "TURN_LEFT\n";
	}},
	{TURN_RIGHT, [](const Creature&, const vector<uint>&){
		return "TURN_RIGHT\n";
	}},
	{GROW, [](const Creature&, const vector<uint>&){
		return "GROW\n";
	}},
	{EAT, [](const Creature&, const vector<uint>&){
		return "EAT\n";
	}},
	{GIVE, [](const Creature&, const vector<uint>&){
		return "GIVE\n";
	}},
	{BIRTH, [](const Creature& creature, const vector<uint>& args){
		if(creature._energy > 0)
		{
			return "BIRTH\n\tfrom: " + to_string((args[0] % (creature._instructions.size()+1))) + "\n\tto:   " + to_string((args[1] % (creature._instructions.size()+1))) + "\n\twith: " + to_string((args[2] % creature._energy)+1) + " energy\n";
		}
		return "BIRTH\n\tfrom: " + to_string((args[0] % (creature._instructions.size()+1))) + "\n\tto:   " + to_string((args[1] % (creature._instructions.size()+1))) + "\n\twith: NO energy\n";
	}},
	{INJECT, [](const Creature& creature, const vector<uint>& args){
		return "INJECT\n\tfrom: " + to_string((args[0] % (creature._instructions.size()+1))) + "\n\tto:   " + to_string((args[1] % (creature._instructions.size()+1))) + "\n\tat: " + to_string(args[2]) + "\n";
	}},
	{DELETE, [](const Creature& creature, const vector<uint>& args){
		return "DELETE\n\tfrom: " + to_string((args[0] % (creature._instructions.size()+1))) + "\n\tto:   " + to_string((args[1] % (creature._instructions.size()+1))) + "\n";
	}},
	{MUTATE, [](const Creature&, const vector<uint>&){
		return "MUTATE\n";
	}},
	{GOTO, [](const Creature& creature, const vector<uint>& args){
		return "GOTO: " + to_string((args[0] % (creature._instructions.size()))) + "\n";
	}},
	{IF_RAND, [](const Creature& creature, const vector<uint>& args){
		return "IF_RAND: " + to_string((args[0] % (creature._instructions.size()))) + "\n";
	}},
	{IF_FRONT, [](const Creature& creature, const vector<uint>& args){
		return "IF_FRONT: " + to_string((args[0] % (creature._instructions.size()))) + "\n";
	}},
	{IF_PROBE, [](const Creature& creature, const vector<uint>& args){
		return "IF_PROBE: " + to_string((args[0] % (creature._instructions.size()))) + "\n\tline: " + to_string(args[0]) + "\n\tinst: " + to_string(args[1]%instLib.size()) + "\n";
	}}
//	{WAIT, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "WAIT" << endl;
//	}},
//	{MOVE, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "MOVE" << endl;
//	}},
//	{TURN_LEFT,  [](const Creature&, const vector<uint>&, ostream& out){
//		out << "TURN_LEFT" << endl;
//	}},
//	{TURN_RIGHT, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "TURN_RIGHT" << endl;
//	}},
//	{GROW, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "GROW" << endl;
//	}},
//	{EAT, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "EAT" << endl;
//	}},
//	{GIVE, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "GIVE" << endl;
//	}},
//	{BIRTH, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "BIRTH\n";
//		out << "\tfrom: " << (args[0] % (creature._instructions.size()+1)) << "\n";
//		out << "\tto:   " << (args[1] % (creature._instructions.size()+1)) << "\n";
//		out << "\twith: " << (args[2] % (creature._energy+1)) << " energy" << endl;
//	}},
//	{INJECT, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "INJECT\n";
//		out << "\tfrom: " << (args[0] % (creature._instructions.size()+1)) << "\n";
//		out << "\tto:   " << (args[1] % (creature._instructions.size()+1)) << "\n";
//		out << "\tat:   " << args[2] << endl;
//	}},
//	{DELETE, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "DELETE\n";
//		out << "\tfrom: " << (args[0] % (creature._instructions.size()+1)) << "\n";
//		out << "\tto:   " << (args[1] % (creature._instructions.size()+1)) << endl;
//	}},
//	{MUTATE, [](const Creature&, const vector<uint>&, ostream& out){
//		out << "MUTATE" << endl;
//	}},
//	{GOTO, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "GOTO " << (args[0] % creature._instructions.size()) << endl;
//	}},
//	{IF_RAND, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "IF_RAND " << (args[0] % creature._instructions.size()) << endl;
//	}},
//	{IF_FRONT, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "IF_FRONT " << (args[0] % creature._instructions.size()) << endl;
//	}},
//	{IF_PROBE, [](const Creature& creature, const vector<uint>& args, ostream& out){
//		out << "IF_PROBE " << (args[2] % creature._instructions.size()) << "\n";
//		out << "\tline: " << args[0] << "\n";
//		out << "\tinst: " << static_cast<InstLabel>(args[1]%instLib.size()) << endl;
//	}}
};

//make a random list of args of a given size
vector<uint> Creature::Instruction::randArgs(uint numArgs)
{
	vector<uint> output;
	output.reserve(numArgs);
	for (int i = 0; i < numArgs; ++i)
	{
		output.push_back(rg());
	}
	return output;
}

//get a random key for the instlib.
InstLabel Creature::Instruction::randKey ()
{
	return libKeys[rg() % libKeys.size()];
}

Creature::Instruction::Instruction() {mutate();}

Creature::Instruction::Instruction(InstLabel label) : Instruction(label, randArgs(NUM_ARGS)) {}

Creature::Instruction::Instruction(InstLabel label, const vector<uint>& args) : _label(label), _args(args) {}

//run this instruction's function.
void Creature::Instruction::run(Creature& creature, Board& board)
{
	instLib.at(_label)(creature, board, _args);
}

//give this instruction a random function with random arguments
void Creature::Instruction::mutate()
{
	_label = randKey();
	_args = randArgs(NUM_ARGS);
}

//convert the instruction into a string
string Creature::Instruction::toString(const Creature& creature)
{
	return printInst.at(_label)(creature, _args);
}
