#include <stdio.h>
#include <time.h>
#include <iostream>
//#include <random>
#include <cmath>

#include "Game.h"

using namespace Eigen;
using namespace std;

static const int INPUT_SIZE = BOARD_SQRD;
static const int NUM_HIDDEN = 2;//3;
static const int HIDDEN_SIZE = BOARD_SIZE;//BOARD_SQRD*4;//BOARD_SQRD / 2;
static const int OUTPUT_SIZE = 4;

//The number of transitions stored
static const int TRANS_STORED = 1000000;

//The probability that the net will make a random choice, rather than doing the perceived best choice
double epsilon = .95;//.1;
//the minimum epsilon can be
static const double EP_MIN = .05;
//the rate at which epsilon decays towards its min.
//epsilon = start - (decay * actionsTaken)	until epsilon <= end
//aproximates: epsilon = end + (start - end)(e^(-actionsTaken * decay))
static const double EP_DECAY = .0025;//.000001;

//weight put on future rewards
static const double GAMMA = .8;
//update the evaluator every UPDATE_EVAL turns
static const int UPDATE_EVAL = 10000;//10;

//The weight the algorithm gives to past gradients when changing the weights and biases
static const double MOMENTUM = .9; //Set to 0 to mimic regular sgd
static const double ONE_SUB_MOM = 1 - MOMENTUM;
//The rate at which the net changes the weights and biases
static const double LEARN_RATE = .1;
//The minimum expected gradient squared (added to prevent dividing by zero)
static const double MIN_GRAD = .01;

static const int NUM_GAMES = 100000;

//save the neural net after this many games (if it has improved)
static const int SAVE_RATE = 100;

//The number of transition that need to be stored before the net starts making decisions and learning
static const int REPLAY_START_SIZE = 50000;
//number of transitions that are picked when adjusting the net.
static const int BATCH_SIZE = 32;

//used to make random doubles between -START_RANGE and START_RANGE for the weights and biases
static const double START_RANGE = 5.;
uniform_real_distribution<double> randWaB (-START_RANGE, START_RANGE);
//used to make random values between 0 and 1, to decide with the net will do the best perceived action or a random action
uniform_real_distribution<double> randVal (0., 1.);
//used to get a random action
uniform_int_distribution<int> randAction (0, OUTPUT_SIZE - 1);


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

//holds a game state, the action the net took, the resulting reward, and the resulting game state
struct Transition
{
	VectorXd prevState;
	int action;
	double reward;
	VectorXd nextState;

	Transition (){/*Do Nothing*/}

	Transition (VectorXd& inPrev, int inAct, double inRe, VectorXd& inNext)
	{
		prevState = inPrev;
		action = inAct;
		reward = inRe;
		nextState = inNext;
	}

	void dbPrintPrev ()
	{
		for (int i = 0; i < BOARD_SQRD; ++i)
		{
			prevState(i) == EMPTY ? printf("_") : prevState(i) == PLAYER ? printf("O"): printf("X");
			if (i % BOARD_SIZE == BOARD_SIZE - 1)
			{
				printf("\n");
			}
		}
	}

	void dbPrintNext ()
	{
		for (int i = 0; i < BOARD_SQRD; ++i)
		{
			nextState(i) == EMPTY ? printf("_") : nextState(i) == PLAYER ? printf("O"): printf("X");
			if (i % BOARD_SIZE == BOARD_SIZE - 1)
			{
				printf("\n");
			}
		}
	}

	void dbPrint()
	{
		dbPrintPrev();
		printf("act:%d, re:%lf", action, reward);
		dbPrintNext();
	}
};

struct Layer
{
	MatrixXd weights;
	VectorXd biases;
	VectorXd vals;
	Layer* prev;

	MatrixXd sumdCdws;
	VectorXd sumdCdbs;
	//If making input, input does not need sums. Output only needs one dCda
	VectorXd sumdCdas;

	//store the expected values of the squared gradients. used for the momentum algorithm
	MatrixXd eGradW;
	VectorXd eGradB;

	Layer (){/*Do Nothing*/}

	Layer (Layer* inPrev, int numNodes)
	{
		prev = inPrev;
		int prevSize = prev->vals.size();
		weights = MatrixXd(numNodes, prevSize);
		biases = VectorXd(numNodes);
		vals = VectorXd::Zero(numNodes);
		sumdCdws = MatrixXd::Zero(numNodes, prevSize);
		sumdCdbs = VectorXd::Zero(numNodes);
		sumdCdas = VectorXd::Zero(numNodes);
		eGradW = MatrixXd::Zero(numNodes, prevSize);
		eGradB = VectorXd::Zero(numNodes);

		//randomize initial weights and biases
		for (int r = 0; r < numNodes; ++r)
		{
			for (int c = 0; c < prevSize; ++c)
			{
				weights(r, c) = randWaB(re);

			}
			biases(r) = randWaB(re);
		}

	}

	//TODO: make InputLayer and OutputLayer classes that override relevant methods like findDerivs, if it doesn't effect performance
	//used for input layer
	Layer (int numNodes)
	{
		vals = VectorXd(numNodes);
	}

	//Set the vals to a given vector, then store them (used for inputing data into the net)
	void setVals (VectorXd& inVals)
	{
		vals = inVals;
	}

	//calculate this layer's vals.
	void calcVals()
	{
		vals = weights*(prev->vals);
		vals += biases;
		sig(vals);
	}

	//Find dCdws, dCdbs and dCdas. Modifies the dCdas of the previous layer
	void findDerivs ()
	{
		//zero out the dCdas of the previous layer. Should not accumulate between runs.
		prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

		double dCdb, dCdw;
		double absPlusOne;
		for (int i = 0; i < sumdCdas.size(); ++i)
		{
			// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
			absPlusOne = abs(vals(i)) + 1;
			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
			sumdCdbs(i) += dCdb;

//			//momentum algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//			//b -= E[g]_t
//			absPlusOne = abs(vals(i)) + 1;
//			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
//			edCdbs(i) = (MOMENTUM * edCdbs(i)) + (LEARN_RATE * dCdb);
//			sumdCdbs(i) += edCdbs(i);

//			//RMSprop algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//			//b -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//			absPlusOne = abs(vals(i)) + 1;
//			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
//			edCdbs(i) = (MOMENTUM * edCdbs(i)) + (ONE_SUB_MOM * dCdb * dCdb);
//			sumdCdbs(i) += (LEARN_RATE * dCdb) / sqrt(edCdbs(i) + MIN_GRAD);

			for (int j = 0; j < weights.cols(); ++j)
			{
				// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
				sumdCdws(i, j) += dCdb * prev->vals(j);

//				//momentum algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//				//w -= E[g]_t
//				dCdw = dCdb * prev->vals(j);
//				edCdws(i, j) = (MOMENTUM * edCdws(i, j)) + (LEARN_RATE * dCdw);
//				sumdCdws(i, j) += edCdws(i, j);

//				//RMSprop algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//				//w -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//				dCdw = dCdb * prev->vals(j);
//				edCdws(i, j) = (MOMENTUM * edCdws(i, j)) + (ONE_SUB_MOM * dCdw * dCdw);
//				sumdCdws(i, j) += (LEARN_RATE * dCdw) / sqrt(edCdws(i, j) + MIN_GRAD);

				// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
				prev->sumdCdas(j) += dCdb * weights(i, j);
			}
		}
	}

	//Like findDerivs, but does not update dCdas of the previous layer.
	//Used to efficiently find the derivs of the last hidden layer, since the dCdas of the input layer are never used.
	void findLastDerivs ()
	{
		double dCdb, dCdw;
		double absPlusOne;
		for (int i = 0; i < sumdCdas.size(); ++i)
		{
			// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
			absPlusOne = abs(vals(i)) + 1;
			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
			sumdCdbs(i) += dCdb;

//			//momentum algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//			//b -= E[g]_t
//			absPlusOne = abs(vals(i)) + 1;
//			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
//			edCdbs(i) = (MOMENTUM * edCdbs(i)) + (LEARN_RATE * dCdb);
//			sumdCdbs(i) += edCdbs(i);

//			//RMSprop algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//			//b -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//			absPlusOne = abs(vals(i)) + 1;
//			dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
//			edCdbs(i) = (MOMENTUM * edCdbs(i)) + (ONE_SUB_MOM * dCdb * dCdb);
//			sumdCdbs(i) += (LEARN_RATE * dCdb) / sqrt(edCdbs(i) + MIN_GRAD);

			for (int j = 0; j < weights.cols(); ++j)
			{
				// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
				sumdCdws(i, j) += dCdb * prev->vals(j);

//				//momentum algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//				//w -= E[g]_t
//				dCdw = dCdb * prev->vals(j);
//				edCdws(i, j) = (MOMENTUM * edCdws(i, j)) + (LEARN_RATE * dCdw);
//				sumdCdws(i, j) += edCdws(i, j);

//				//RMSprop algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//				//w -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//				dCdw = dCdb * prev->vals(j);
//				edCdws(i, j) = (MOMENTUM * edCdws(i, j)) + (ONE_SUB_MOM * dCdw * dCdw);
//				sumdCdws(i, j) += (LEARN_RATE * dCdw) / sqrt(edCdws(i, j) + MIN_GRAD);
			}
		}
	}

	//Adjust the weights and biases based on the average dCdws and dCdbs
	void adjustVals (int batchSize)
	{
		double avW;
		double avB;

		for (int r = 0; r < weights.rows(); ++r)
		{
			for (int c = 0; c < weights.cols(); ++c)
			{
//				weights(r, c) -= LEARN_RATE * (sumdCdws(r, c) / batchSize);

//				//momentum algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//				//w -= E[g]_t
//				avW = sumdCdws(r, c) / batchSize;
//				eGradW(r, c) = (MOMENTUM * eGradW(r, c)) + (LEARN_RATE * avW);
//				weights(r, c) -= eGradW(r, c);

				//RMSprop algorithm:
				//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
				//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
				//w -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
				avW = sumdCdws(r, c) / batchSize;
				eGradW(r, c) = (MOMENTUM * eGradW(r, c)) * (ONE_SUB_MOM * avW * avW);
				weights(r, c) -= (LEARN_RATE * avW) / sqrt(eGradW(r,c) + MIN_GRAD);
			}

//			biases(r) -= LEARN_RATE * (sumdCdbs(r) / batchSize);

//			//momentum algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//			//b -= E[g]_t
//			avB = sumdCdbs(r) / batchSize;
//			eGradB(r) = (MOMENTUM * eGradB(r)) + (LEARN_RATE * avB);
//			biases(r) -= eGradB(r);

			//RMSprop algorithm:
			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
			//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
			//b -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
			avB = sumdCdbs(r) / batchSize;
			eGradB(r) = (MOMENTUM * eGradB(r)) + (ONE_SUB_MOM * avB * avB);
			biases(r) -= (LEARN_RATE * avB) / sqrt(eGradB(r) + MIN_GRAD);
		}

		//zero out the sumdCdws and sumdCdbs once finished
		sumdCdws = MatrixXd::Zero(sumdCdws.rows(), sumdCdws.cols());
		sumdCdbs = VectorXd::Zero(sumdCdbs.size());
	}
};

struct OutputLayer : Layer
{
	OutputLayer (){/*Do Nothing*/}

	//call the super constructor
	OutputLayer (Layer* inPrev, int numNodes) : Layer(inPrev, numNodes) {}

	//Find the dCdws, dCdbs and dCdas of the output layer (done since the output layer should only have 1 dCda)
	//Takes in the node of the chosen action
	void findDerivs (int actionIndex)
	{
		double absPlusOne;
		double dCdb, dCdw;

		//zero out the dCdas of the previous layer. Should not accumulate between runs.
		prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

//		// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//		absPlusOne = abs(vals(actionIndex)) + 1;
//		dCdb = sumdCdas(actionIndex) / (absPlusOne * absPlusOne);
//		sumdCdbs(actionIndex) += dCdb;

		//TODO: net seems for work when the abs^2 is just replaced by some positive number. Might just be that the abs^2 acts like a low learning rate. should try using method below and tune the learning rate
		// dC/db = dC/da * da/db = 2(a_i-a*) * 1
		dCdb = sumdCdas(actionIndex);
		sumdCdbs(actionIndex) += dCdb;

		//TODO: get rid of absPlusOne here and see what happens
		//momentum algorithm:
		//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
		//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
		//b -= E[g]_t
//		absPlusOne = abs(vals(actionIndex)) + 1;
//		dCdb = sumdCdas(actionIndex) / (absPlusOne * absPlusOne);

//		//momentum algorithm:
//		//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//		//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//		//b -= E[g]_t
//		dCdb = sumdCdas(actionIndex);
//		edCdbs(actionIndex) = (MOMENTUM * edCdbs(actionIndex)) + (LEARN_RATE * dCdb);
//		sumdCdbs(actionIndex) += edCdbs(actionIndex);

//		//RMSprop algorithm:
//		//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//		//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//		//b -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//		dCdb = sumdCdas(actionIndex);
//		edCdbs(actionIndex) = (MOMENTUM * edCdbs(actionIndex)) + (ONE_SUB_MOM * dCdb * dCdb);
//		sumdCdbs(actionIndex) += (LEARN_RATE * dCdb) / sqrt(edCdbs(actionIndex) + MIN_GRAD);

		for (int i = 0; i < weights.cols(); ++i)
		{
			// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			sumdCdws(actionIndex, i) += dCdb * prev->vals(i);

//			//momentum algorithm:
//			//g_t = dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//			//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (LEARN_RATE * g_t)
//			//w -= E[g]_t
//			dCdw = dCdb * prev->vals(i);
//			edCdws(actionIndex, i) = (MOMENTUM * edCdws(actionIndex, i)) + (LEARN_RATE * dCdw);
//			sumdCdws(actionIndex, i) += edCdws(actionIndex, i);

//			//RMSprop algorithm:
//			//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//			//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
//			//w -= (LEARN_RATE * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
//			dCdw = dCdb * prev->vals(i);
//			edCdws(actionIndex, i) = (MOMENTUM * edCdws(actionIndex, i)) + (ONE_SUB_MOM * dCdw * dCdw);
//			sumdCdws(actionIndex, i) += (LEARN_RATE * dCdw) / sqrt(edCdws(actionIndex, i) + MIN_GRAD);

			// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
			prev->sumdCdas(i) += dCdb * weights(actionIndex, i);
		}

		//Once done, zero out this layer's sumdCdas
		sumdCdas = VectorXd::Zero(sumdCdas.size());
	}

	//calculate this layer's vals. For output layer, do not apply sig
	void calcVals()
	{
		vals = weights*(prev->vals);
		vals += biases;
	}
};

struct NeuralNet
{
	Layer inputLayer;
	Layer hiddenLayers[NUM_HIDDEN];
//	Layer outputLayer;
	OutputLayer outputLayer;

	//Makes neural net using const values
	NeuralNet ()
	{
		inputLayer = Layer(INPUT_SIZE);
		hiddenLayers[0] = Layer(&inputLayer, HIDDEN_SIZE);
		for (int i = 1; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i] = Layer(&hiddenLayers[i-1], HIDDEN_SIZE);
		}
		outputLayer = OutputLayer(&hiddenLayers[NUM_HIDDEN - 1], OUTPUT_SIZE);
	}

	//Push the data through the net to set all the node values
	void pushForward (VectorXd& inData)
	{
		inputLayer.setVals(inData);
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i].calcVals();
		}
		outputLayer.calcVals();
	}

	//Process the data and guess which action to take. Save the vals, for when we backpropogat (for vectors, = makes a copy vector, ie does not pass a pointer value)
	//Returns the action taken (the index of the output node it chose)
	int pickAction (VectorXd& inData)
	{
		//push the data through the net and get the output values
//		inputLayer.setVals(inData);
//		for (int i = 0; i < NUM_HIDDEN; ++i)
//		{
//			hiddenLayers[i].calcVals();
//		}
//		outputLayer.calcVals();
		pushForward(inData);

		int bestAction = 0;
		for (int action = 0; action < OUTPUT_SIZE; ++action)
		{
			if (outputLayer.vals(action) > outputLayer.vals(bestAction))
			{
				bestAction = action;
			}
		}
		return bestAction;
	}

	//Like pickAction, but with a chance of choosing a random action (for exploration purposes)
	int ePickAction (VectorXd& inData)
	{
		//push the data through the net and get the output values
//		inputLayer.setVals(inData);
//		for (int i = 0; i < NUM_HIDDEN; ++i)
//		{
//			hiddenLayers[i].calcVals();
//		}
//		outputLayer.calcVals();

		//decay epsilon if it hasn't reached the minimum
		if (epsilon >= EP_MIN)
		{
			epsilon -= EP_DECAY;
		}

		pushForward(inData);

		//With a probability of epsilon, pick a random action. Otherwise, pick the best perceived action
		double rand = randVal(re);
		if (rand > epsilon)
		{
			//TODO: call pickAction in here if that doesn't slow performance
			int bestAction = 0;
			for (int action = 0; action < OUTPUT_SIZE; ++action)
			{
				if (outputLayer.vals(action) > outputLayer.vals(bestAction))
				{
					bestAction = action;
				}
			}

			return bestAction;
		}
		return randAction(re);
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
	void backpropogate (VectorXd& inState, int action, double actual)
	{
		//TODO: propably input a state and push forward here, since you need the vals in order to backprop properly

		//push the data forward (to get the node vals needed to find the derivs)
		pushForward(inState);

		//dC/da = 2(a - a*)
		outputLayer.sumdCdas(action) = 2 * (outputLayer.vals(action) - actual);

		//Find the derivs of all the layers
		outputLayer.findDerivs(action);
		for (int i = NUM_HIDDEN - 1; i > 0; --i)
		{
			hiddenLayers[i].findDerivs();
		}
		hiddenLayers[0].findLastDerivs();
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

	//copy the weights and biases of a given net into this net
	void copyNet (NeuralNet& origNet)
	{
		for (int i = 0; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i].weights = origNet.hiddenLayers[i].weights;
			hiddenLayers[i].biases = origNet.hiddenLayers[i].biases;
		}
		outputLayer.weights = origNet.outputLayer.weights;
		outputLayer.biases = origNet.outputLayer.biases;
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

//hold all the transitions made by the net
Transition transitions[TRANS_STORED];
//current length of transitions
int transLen = 0;

//get a random sample of transitions and store them in the given array
void getBatch(Transition inArray[])
{
	int tempIndices[transLen];
	int tempLen = 0;
	while(tempLen < transLen)
	{
		tempIndices[tempLen] = tempLen;
		++tempLen;
	}

	int randIndex;
	for (int i = 0; i < BATCH_SIZE; ++i)
	{
		//put a random tranaction into the batch, then remove that transition (move the last trans index into the chosen slot, then decrement the indices length)
		randIndex = (int) (randVal(re) * tempLen);
		inArray[i] = transitions[tempIndices[randIndex]];
		tempIndices[randIndex] = tempIndices[--tempLen];
	}
}

int main ()
{
	printf("board size:%d, num hidden:%d, hidden size:%d\n", BOARD_SIZE, NUM_HIDDEN, HIDDEN_SIZE);

	//used for testing
	int score = 0;
	int bestScore = 2147483647; //Some very big number

	NeuralNet player = NeuralNet();
	NeuralNet evaluator = NeuralNet();
	evaluator.copyNet(player);

	//current and next board states
	VectorXd currentState;
	VectorXd nextState;

	int action;
	int reward;
	int transIndex = 0;
	int turnTotal = 1;

	Transition batch[BATCH_SIZE];

	time_t startTime = time(NULL);

	//play the games
	for (int game = 1; game <= NUM_GAMES; ++game)
//	for (int game = 1; bestScore > 2*SAVE_RATE; ++game)
	{
		//TODO: for games where the previous game states matter, like for velocity, need to input prior game states as well (but for Minesweeper you just need the current state)
		//TODO: may need to make a start game function
		resetBoard();
		//TODO: I don't think I need these to be set to zero
		action = 0;
		reward = 0;

		currentState = board;

		//TODO: need to populate the replay memory with random actions before letting the net learn

		//do each turn of the game
		while (!isFinished)
		{
			//the net looks at the game state, chooses an action, and gets a reward
			action = player.ePickAction(currentState);
			reward = movePlayer(action);

			//record the following game state. If the game has ended, put a null vector (size 0 vector) instead
			if (!isFinished)
			{
				nextState = board;
			}
			else
			{
				nextState = VectorXd(0);
			}
			//store the transition
			transitions[transIndex] = Transition(currentState, action, reward, nextState);
			transIndex = (transIndex + 1) % TRANS_STORED;
			if (transLen < TRANS_STORED)
			{
				++transLen;
			}

			//make current state into next state
			currentState = nextState;

			//If there are enough stored transitions, take a random batch and process them
			if (transLen >= BATCH_SIZE)
			{
				//get a random batch of tranactions
				getBatch(batch);

				//backpropogate for each transition in the batch
				Transition* currentTrans;
				double actual;
				for (int i = 0; i < BATCH_SIZE; ++i)
				{
//					player.printWaB();
//					currentTrans->dbPrint();

					currentTrans = &batch[i];
					actual = currentTrans->reward;
					//If the transition is not game-ending, have the evaluator guess the future reward and add it to the actual reward
					if (currentTrans->nextState.size() != 0)
					{
						actual += GAMMA * evaluator.outputLayer.vals(evaluator.pickAction(currentTrans->nextState));
					}
					//Find the derivs of the weights and biases
					player.backpropogate(currentTrans->prevState, currentTrans->action, actual);
				}
				//adjust the weights and biases by subtracting the derivs
				player.adjustVals(BATCH_SIZE);
			}

			//update the evaluator every few turns
//			if (game % UPDATE_EVAL == 0)
			if (turnTotal % UPDATE_EVAL == 0)
			{
				evaluator.copyNet(player);
			}

			++turnTotal;
			++score;
		}

		if (game % SAVE_RATE == 0)
		{
			if (score < bestScore)
			{
				bestScore = score;
			}
//			printf("eps:%lf, ", epsilon);
			printf("av turns: %lf\n", score / (double) SAVE_RATE);
			score = 0;
		}
	}

	player.dbPrintWaB();

	printf("Complete. Time: %ld seconds\n", time(NULL) - startTime);
}
