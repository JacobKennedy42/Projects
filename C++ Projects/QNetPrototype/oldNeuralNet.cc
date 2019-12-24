#include <stdio.h>
#include <iostream>
//#include <random>
#include <cmath>

#include "Game.h"

using namespace Eigen;
using namespace std;

static const int INPUT_SIZE = BOARD_SQRD;
static const int NUM_HIDDEN = 2;//5;
static const int HIDDEN_SIZE = BOARD_SQRD*2;//BOARD_SQRD / 2;
static const int OUTPUT_SIZE = 4;

static const int NUM_TURNS_STORED = 2;//20;

//Lowest reward coefficient
static const double LOWEST_COEFF = pow(.8, NUM_TURNS_STORED-1);

static const int NUM_GAMES = 10000000;

//vars used to make random doubles between -5 and 5
uniform_real_distribution<double> unif (-5., 5.);
//random_device rd;
//default_random_engine re(rd());

//stores the vals of the last NUM_STATES_STORED turns of the game. Cyclical, current state is stored in turnCounter % NUM_STATES_STORED.
//index [#][NUM_HIDDEN+1] stores the output vals, ie the predicted total score after NUM_STATES_STORED turns
//VectorXd storedStates[NUM_STATES_STORED][NUM_HIDDEN + 2];

//the number of turns taken
static int turnCounter = 0;

//The following arrays are stored cyclically, as in index = turnCounter % NUM_TURNS_STORED. Since they are static arrays, their values are set to 0 by default
//Actions that the net has taken.
static int actions[NUM_TURNS_STORED];
//Rewards given to each player at each turn.
static double rewards[NUM_TURNS_STORED];

//The total amount of points accrued from all stored turns
static double accrued = 0;

//TODO:delete this after testing
double totalCost = 0;
double totalReward = 0;

//TODO: need to sig the accrued when comparing it to the output vals
//TODO: also need to store which action was picked on a given turn
//TODO: right in here step by step how this works (but without the math)

//Apply sigmoid to vector vals
//This is a fast approximation of the actual sigmoid function.
//Original sigmoid, range 0 to 1: 1 / (1 + e^-x)
//Approximation, range -1 to 1: x / (1 + |x|)
static void sig (VectorXd& inV)
{
	for (int i = 0; i < inV.size(); ++i)
	{
		inV(i) = inV(i) / (1 + abs(inV(i)));
	}
}
static double sig (double inVal)
{
	return inVal / (1 + abs(inVal));
}

//start the game by resst the board and the game variables
void startGame ()
{
	resetBoard();

	turnCounter = 0;
	accrued = 0;
	//zero out the rewards and the actions
	for (int i = 0; i < NUM_TURNS_STORED; ++i)
	{
		actions[i] = 0;
		rewards[i] = 0;
	}
}

class Layer
{
	public:

	MatrixXd weights;
	VectorXd biases;
	VectorXd vals;
	Layer* prev;

	MatrixXd sumdCdws;
	VectorXd sumdCdbs;
	VectorXd sumdCdas;

	//Stores that vals for when we want to backpropogate using vals from a past turn
	//turn values are stored in this array cyclically, as in index = turnCounter % NUM_STATES_STORED
	VectorXd storedVals[NUM_TURNS_STORED];

	Layer ()
	{
		//Do Nothing
	}

	Layer (Layer* inPrev, int numNodes)
	{
		prev = inPrev;
		int prevSize = prev->vals.size();
		weights = MatrixXd(numNodes, prevSize);
		biases = VectorXd(numNodes);
		vals = VectorXd(numNodes);
		sumdCdws = MatrixXd(numNodes, prevSize);
		sumdCdbs = VectorXd(numNodes);
		sumdCdas = VectorXd(numNodes);
		//TODO: make sure that vectors and matices are inited with zeros

		//randomize initial weights and biases
		for (int r = 0; r < numNodes; ++r)
		{
			for (int c = 0; c < prevSize; ++c)
			{
				weights(r, c) = unif(re);

			}
			biases(r) = unif(re);
		}

	}

	Layer (int numNodes)
	{
		vals = VectorXd(numNodes);
	}

	//Set the vals to a given vector, then store them (used for inputing data into the net)
	void setVals (VectorXd& inVals)
	{
		vals = inVals;
		storedVals[turnCounter % NUM_TURNS_STORED] = vals;
	}

	//calculate this layer's vals. Store those vals for a later use
	void calcVals()
	{
		vals = weights*(prev->vals);
		vals += biases;
		sig(vals);

		storedVals[turnCounter % NUM_TURNS_STORED] = vals;
//		printf("calcing vals\n");
//		for (int i = 0; i < vals.size(); ++i)
//		{
//			printf("%lf ", vals(i));
//		}
//		printf("\n");
	}

	//Find the dCdws, dCdbs and dCdas of the output layer (done since the output layer should only have 1 dCda)
	//Takes in the node of the chosen action
	//TODO: as an optimization, might make an OutputLayer class and have this overwrite it. That class would have a dCda field, not sumdCdas
	void findFirstDerivs (int actionIndex, int turnIndex)
//	void findFirstDerivs (int actionIndex)
	{
		//zero out the dCdas of the previous layer. Should not accumulate between runs.
		prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

		// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//		double absPlusOne = abs(vals(actionIndex)) + 1;
		double absPlusOne = abs(storedVals[turnIndex](actionIndex)) + 1;
		double dCdb = sumdCdas(actionIndex) / (absPlusOne * absPlusOne);
		sumdCdbs(actionIndex) += dCdb;
		for (int i = 0; i < weights.cols(); ++i)
		{
			// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			sumdCdws(actionIndex, i) += dCdb * prev->storedVals[turnIndex](i);
//			sumdCdws(actionIndex, i) += dCdb * prev->vals(i);
			// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
			prev->sumdCdas(i) += dCdb * weights(actionIndex, i);
		}

		//Once done, zero out this layer's sumdCdas
		sumdCdas = VectorXd::Zero(sumdCdas.size());
	}

	//Find dCdws, dCdbs and dCdas. Modifies the dCdas of the previous layer
	void findDerivs (int turnIndex)
//	void findDerivs ()
	{
		//zero out the dCdas of the previous layer. Should not accumulate between runs.
		prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

		double dCdb;
		double absPlusOne;
		for (int i = 0; i < sumdCdas.size(); ++i)
		{
			// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
			absPlusOne = abs(storedVals[turnIndex](i)) + 1;
//			absPlusOne = abs(vals(i)) + 1;
			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
			sumdCdbs(i) += dCdb;
			for (int j = 0; j < weights.cols(); ++j)
			{
				// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
				sumdCdws(i, j) += dCdb * prev->storedVals[turnIndex](j);
//				sumdCdws(i, j) += dCdb * prev->vals(j);
				// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
				prev->sumdCdas(j) += dCdb * weights(i, j);
			}
		}
	}

	//Like findDerivs, but does not update dCdas of the previous layer.
	//Used to efficiently find the derivs of the last hidden layer, since the dCdas of the input layer are never used.
	void findLastDerivs (int turnIndex)
//	void findLastDerivs ()
	{
		double dCdb;
		double absPlusOne;
		for (int i = 0; i < sumdCdas.size(); ++i)
		{
			//TODO: aren't we supposed to use stored vals for find derivs?
			//TODO: also need to use prev's stroed vals
			//TODO: change all of the find derivs
			//TODO: see if we need to use stored sumdC's also (probably not)

			// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			absPlusOne = abs(vals(i)) + 1;
			absPlusOne = abs(storedVals[turnIndex](i)) + 1;
			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
			sumdCdbs(i) += dCdb;
			for (int j = 0; j < weights.cols(); ++j)
			{
				// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
				sumdCdws(i, j) += dCdb * prev->storedVals[turnIndex](j);
//				sumdCdws(i, j) += dCdb * prev->vals(j);
			}
		}
	}

	//Adjust the weights and biases based on the average dCdws and dCdbs
	void adjustVals (int batchSize)
	{
		for (int r = 0; r < weights.rows(); ++r)
		{
			for (int c = 0; c < weights.cols(); ++c)
			{
				weights(r, c) -= sumdCdws(r, c) / batchSize;
			}

			biases(r) -= sumdCdbs(r) / batchSize;
		}

		//zero out the sumdCdws and sumdCdbs once finished
		sumdCdws = MatrixXd::Zero(sumdCdws.rows(), sumdCdws.cols());
		sumdCdbs = VectorXd::Zero(sumdCdbs.size());
	}
};


class NeuralNet
{
	public:

//	int inputSize;
//	int hiddenSize;
//	int outputSize;

	Layer inputLayer;
	Layer hiddenLayers[NUM_HIDDEN];
	Layer outputLayer;

//	double[] correct;

	//Makes neural net using const values
	NeuralNet ()
	{
		inputLayer = Layer(INPUT_SIZE);
		hiddenLayers[0] = Layer(&inputLayer, HIDDEN_SIZE);
		for (int i = 1; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i] = Layer(&hiddenLayers[i-1], HIDDEN_SIZE);
		}
		outputLayer = Layer(&hiddenLayers[NUM_HIDDEN - 1], OUTPUT_SIZE);
	}

	//Process the data and guess which action to take. Save the vals, for when we backpropogat (for vectors, = makes a copy vector, ie does not pass a pointer value)
	//Returns the action taken (the index of the output node it chose)
	int makeGuess (VectorXd& inData)
	{
		int stateIndex = turnCounter % NUM_TURNS_STORED;

//		inputLayer.vals = inData;
		inputLayer.setVals(inData);

		//TODO: make a setVals method that sets the vals and also puts them in stored vals

//		for (int i = 0; i < INPUT_SIZE; ++i)
//		{
//			printf("%lf ", inputLayer.vals(i));
//		}
//		printf("\n");

		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
//			printf("%d\n", i);
			hiddenLayers[i].calcVals();
		}
		outputLayer.calcVals();
//		return outputLayer.vals;


		//Treat the output nodes as a probability distribution and pick an action psuedo-randomly (since output vals range from -1 to 1, 1 is added to each node to make the distribution positive)

		//Find the sum of all the output nodes
		double sum = OUTPUT_SIZE; //short way of adding 1 to each node
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			sum += outputLayer.vals(i);
		}
		uniform_real_distribution<double> range (0, sum);
		double rand = range(re);

//		printf("output vals: ");
//		for (int i = 0; i < OUTPUT_SIZE; ++i)
//		{
//			printf("%lf ", outputLayer.vals(i));
//		}
//		printf("\nsum:%lf \n", sum);

		//choose an action (return a psuedo random node index from the probability range)
		sum = 0;
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			sum += outputLayer.vals(i) + 1; //+1 to make the value positive
			if (rand < sum)
			{
				return i;
			}
		}
	}

	/* Backpropogation Math:
	 * C - The total cost (we want this to be as low as possible)
	 * a - the value of a particular node
	 * a* - the corresponding value that the output node should have. the "correct" answer
	 * w - the value of a weight between a paricular node and a node in the previous layer
	 * b - the bias of a particular node
	 * o - the output layer. So a_io is a node in the output layer
	 * 
	 * C = sum( (a_i - a*)^2 ) for every ith node in the ouput layer
	 * dC/da = 2(a - a*)
	 * z = sum(w_j * a_j) + b for every jth node in the previous layer 
	 * a = 1 / (1 + e^(-z))
	 * dz/dw_i = a_j for the ith weight and the corresponding jth node in the previous layer
	 * dz/a_j = w_i for the jth node in the previous layer and the corresponding ith weight
	 * dz/db = 1
	 * da/dz = ((1+e^(-z))(0) - (1)(-e^(-z)))/(1+e^(-z))^2        Quotient Rule
	 *       = (e^(-z))/(1+e^(-z))
	 *       = 1/(1+e^(-z)) * (e^(-z))/(1+e^(-z))
	 *       = a * (1 - a)
	 * dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * a_i(1-a_i) * a_i-1 for node in layer i
	 * dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * a_i(1-a_i) * 1 for node in layer i
	 * dC/da_i-1 = dC/da_i * da_i/dz * dz/da_i-1 = 2(a_i-a*) * a_i(1-a_i) * w_j for node in layer i and corresponding weight j
	 */
	void backpropogate (int turnIndex)
	{
		//Find the total number of points gotten from the last NUM_TURNS_STORED turns
		//total points accrued = sum(.8^(T-t) * reward for turn T - t)
		//T = current turn number
		//0 <= t < NUM_TURNS_STORED

		int action = actions[turnIndex];

		//Find the dCda between the accrued points (adjusted to be between -1 and 1) and the net's prediction
		//only the dCda of the action taken needs to be computed
		outputLayer.sumdCdas(action) = 2 * (outputLayer.storedVals[turnIndex](action) - sig(accrued));

		totalCost += abs(outputLayer.sumdCdas(action));

//		printf("backprop. action:%d, prediction:%lf, accrued:%lf, sig accrued:%lf, cost:%lf\n", action, outputLayer.storedVals[turnIndex](action), accrued, sig(accrued), outputLayer.sumdCdas(action));

		//Find the derivs of all the layers
		outputLayer.findFirstDerivs(action, turnIndex);
		for (int i = NUM_HIDDEN - 1; i > 0; --i)
		{
			hiddenLayers[i].findDerivs(turnIndex);
		}
		hiddenLayers[0].findLastDerivs(turnIndex);
	}

	//Have the net take a turn. This involves learning from the turn NUM_TURNS_SAVED ago, then processing a new turn
	void takeTurn (VectorXd inData)
	{
//		printf("taking turn\n");

		int turnIndex = turnCounter % NUM_TURNS_STORED;

		if (turnCounter >= NUM_TURNS_STORED)
		{
			//Now that the net has seen how much reward it has gotten over a number of turns, figure out how much they need to change their decision making
			backpropogate(turnIndex);
		}

		//Input the data into the net and get it's resulting action
		int action = makeGuess(inData);

		//Store the action that the net took
		actions[turnIndex] = action;
		//Find the reward given for such an action
//		double newReward = clickTile(action);
		double newReward = movePlayer(action);
		totalReward += newReward;
//		printf("points:%lf\n", newReward);
		//Update the ammount of reward accrued. Remove the old reward, multiply all previous turn rewards by .8, then add the new reward
		accrued = ((accrued - LOWEST_COEFF * (rewards[turnIndex])) * .8) + newReward;
		//Store the reward given for such an action
		rewards[turnIndex] = newReward;

//		printf("index:%d, action:%d, reward:%lf, accrued:%lf\n", turnIndex, action, newReward, accrued);
//		printf("actions: ");
//		for (int i = 0; i < NUM_TURNS_STORED; ++i)
//		{
//			printf("%d ", actions[i]);
//		}
//		printf("\nrewards: ");
//		for (int i = 0; i < NUM_TURNS_STORED; ++i)
//		{
//			printf("%lf ", rewards[i]);
//		}
//		printf("\npredictions: ");
//		for (int i = 0; i < turnCounter+1; ++i)
//		{
//			printf("%lf ", outputLayer.storedVals[i](actions[i]));
//		}
//		printf("\n");

		//Move on to the next turn
		++turnCounter;
	}

	//adjust the weights and biases of each layer based on their average derivs
	void adjustVals (int batchSize)
	{
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i].adjustVals(batchSize);
		}
		outputLayer.adjustVals(batchSize);
	}

	//Print the vals of the layers
	void dbPrintVals ()
	{
		printf("Vals:\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", hiddenLayers[i].vals(j));
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			printf("%lf ", outputLayer.vals(i));
		}
		printf("\n");
	}

	//print the weights and biases of each layer
	void dbPrintWaB ()
	{
		printf("**********biases**********\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", hiddenLayers[i].biases(j));
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			printf("%lf ", outputLayer.biases(i));
		}
		printf("\n**********weights**********\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				for (int k = 0; k < hiddenLayers[i].prev->vals.size(); ++k)
				{
					printf("%lf ", hiddenLayers[i].weights(j,k));
				}
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", outputLayer.weights(i, j));
			}
		}
		printf("\n");
	}

	//print the derivs of the layers
	void dbPrintDerivs ()
	{
		printf("**********dCdas**********\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", hiddenLayers[i].sumdCdas(j));
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			printf("%lf ", outputLayer.sumdCdas(i));
		}
		printf("\n**********dCdbs**********\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", hiddenLayers[i].sumdCdbs(j));
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			printf("%lf ", outputLayer.sumdCdbs(i));
		}
		printf("\n**********dCdws**********\n");
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				for (int k = 0; k < hiddenLayers[i].prev->vals.size(); ++k)
				{
					printf("%lf ", hiddenLayers[i].sumdCdws(j,k));
				}
			}
			printf("\n");
		}
		for (int i = 0; i < OUTPUT_SIZE; ++i)
		{
			for (int j = 0; j < HIDDEN_SIZE; ++j)
			{
				printf("%lf ", outputLayer.sumdCdws(i, j));
			}
		}
		printf("\n");
	}
};

int main ()
{
	printf("hidSize:%d numHid:%d turnsStored:%d\n", HIDDEN_SIZE, NUM_HIDDEN, NUM_TURNS_STORED);

	NeuralNet testNet = NeuralNet();

	int totalTurns = 0;
	for (int game = 0; game < NUM_GAMES; ++game)
	{
		startGame();

		//Take turns until the game ends
		while (!isFinished)
		{

			testNet.takeTurn(board);
//			dbPrintBoard();
//			printf("\n");
		}

		//Game has ended. Backpropogate though all unprocessed turns
		//If the turn buffer has not been filled...
		if (turnCounter <= NUM_TURNS_STORED)
		{
			//Fill the empty buffer indices of the buffer with the end reward (0, since turns after the game-ending turn have no reward)
			for (int i = turnCounter; i < NUM_TURNS_STORED; ++i)
			{
				rewards[i] = 0;
			}
			//backpropogate all the remaining turns
			for (int i = 0; i < turnCounter; ++i)
			{
				testNet.backpropogate(i);
				//replace the reward with the end reward. Update accrued accordingly
				accrued = (accrued - LOWEST_COEFF * (rewards[i])) * .8;
				rewards[i] = 0;
			}
		}
		else
		{
			//Backpropogate all of the turns in the buffer that have not been processed
			int turnIndex;
			for (int i = turnCounter; i < turnCounter + NUM_TURNS_STORED; ++i)
			{
				turnIndex = i % NUM_TURNS_STORED;
				testNet.backpropogate(turnIndex);
				//replace the reward with the end reward. Update accrued accordingly
				accrued = (accrued - LOWEST_COEFF * (rewards[turnIndex])) * .8;
				rewards[turnIndex] = 0;
			}
		}

		//after the game has ended, adjust the net
		testNet.adjustVals(turnCounter);


		//TODO: figure out why the turn counter average starts so low
		totalTurns += turnCounter;

//		printf("%d\n", totalTurns);

		//TODO: make this a constant variable
		int batchSize = 100000;
		if (game % batchSize == batchSize - 1)
		{
			printf("turns:%lf, cost:%lf, reward:%lf\n", totalTurns / (double) batchSize, totalCost / (double) batchSize, totalReward / (double) batchSize);
//			printf("%lf\n", totalCost / (double) batchSize);
//			printf("%lf\n", totalReward / (double) batchSize);
			totalCost = 0;
			totalTurns = 0;
			totalReward = 0;
		}
	}
}
