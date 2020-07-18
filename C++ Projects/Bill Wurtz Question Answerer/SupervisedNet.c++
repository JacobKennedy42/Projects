#include <stdio.h>
#include <time.h>
#include <sys/stat.h>
#include <iostream> //cout, endl, flush
#include <fstream> //ifstream
#include <vector> //vector
#include <limits> //numeric_limits::max()
//#include <random>
#include <algorithm> //random_shuffle
#include <cmath> //isnan
#include <Eigen/Dense> //MatrixXd, VectorXd
#include "SupervisedNet.h"

//#include "Game.h"

//using namespace Eigen;
//using namespace std;
//
//static const int DEFAULT_INPUT_SIZE = BOARD_SQRD;
//static const int NUM_HIDDEN = 2;//3;
//static const int HIDDEN_SIZE = BOARD_SIZE;//BOARD_SQRD*4;//BOARD_SQRD / 2;
//static const int DEFAULT_OUTPUT_SIZE = 4;
//
////The number of transitions stored
//static const int TRANS_STORED = 1000000;
//
////The probability that the net will make a random choice, rather than doing the perceived best choice
//double epsilon = .95;//.1;
////the minimum epsilon can be
//static const double EP_MIN = .05;
////the rate at which epsilon decays towards its min.
////epsilon = start - (decay * actionsTaken)	until epsilon <= end
////aproximates: epsilon = end + (start - end)(e^(-actionsTaken * decay))
//static const double EP_DECAY = .0025;//.000001;
//
////weight put on future rewards
//static const double GAMMA = .8;
////update the evaluator every UPDATE_EVAL turns
//static const int UPDATE_EVAL = 10000;//10;
//
////The weight the algorithm gives to past gradients when changing the weights and biases
//static const double MOMENTUM = .9; //Set to 0 to mimic regular sgd
//static const double ONE_SUB_MOM = 1 - MOMENTUM;
////The rate at which the net changes the weights and biases
//static const double learn_rate = .1;
////The minimum expected gradient squared (added to prevent dividing by zero)
//static const double MIN_GRAD = .01;
//
//static const int NUM_GAMES = 100000;
//
////save the neural net after this many games (if it has improved)
//static const int SAVE_RATE = 100;
//
////The number of transition that need to be stored before the net starts making decisions and learning
//static const int REPLAY_START_SIZE = 50000;
////number of transitions that are picked when adjusting the net.
//static const int BATCH_SIZE = 32;
//
////used to make random doubles between -START_RANGE and START_RANGE for the weights and biases
//static const double START_RANGE = 5.;
//uniform_real_distribution<double> randWaB (-START_RANGE, START_RANGE);
////used to make random values between 0 and 1, to decide with the net will do the best perceived action or a random action
//uniform_real_distribution<double> randVal (0., 1.);
////used to get a random action
//uniform_int_distribution<int> randAction (0, DEFAULT_OUTPUT_SIZE - 1);
//
//
////Apply sigmoid to vector vals
////This is a fast approximation of the actual sigmoid function.
////Original sigmoid, range 0 to 1: 1 / (1 + e^-x)
////Approximation, range -1 to 1: x / (1 + |x|)
//static void sig (VectorXd& inV)
//{
//	for (int i = 0; i < inV.size(); ++i)
//	{
//		inV(i) = inV(i) / (1 + abs(inV(i)));
//	}
//}
//static double sig (double inVal)
//{
//	return inVal / (1 + abs(inVal));
//}

////holds a game state, the action the net took, the resulting reward, and the resulting game state
//struct Transition
//{
//	VectorXd prevState;
//	int action;
//	double reward;
//	VectorXd nextState;
//
//	Transition (){/*Do Nothing*/}
//
//	Transition (VectorXd& inPrev, int inAct, double inRe, VectorXd& inNext)
//	{
//		prevState = inPrev;
//		action = inAct;
//		reward = inRe;
//		nextState = inNext;
//	}
//
//	void dbPrintPrev ()
//	{
//		for (int i = 0; i < BOARD_SQRD; ++i)
//		{
//			prevState(i) == EMPTY ? printf("_") : prevState(i) == PLAYER ? printf("O"): printf("X");
//			if (i % BOARD_SIZE == BOARD_SIZE - 1)
//			{
//				printf("\n");
//			}
//		}
//	}
//
//	void dbPrintNext ()
//	{
//		for (int i = 0; i < BOARD_SQRD; ++i)
//		{
//			nextState(i) == EMPTY ? printf("_") : nextState(i) == PLAYER ? printf("O"): printf("X");
//			if (i % BOARD_SIZE == BOARD_SIZE - 1)
//			{
//				printf("\n");
//			}
//		}
//	}
//
//	void dbPrint()
//	{
//		dbPrintPrev();
//		printf("act:%d, re:%lf", action, reward);
//		dbPrintNext();
//	}
//};

//struct Layer
//{
//	MatrixXd weights;
//	VectorXd biases;
//	VectorXd vals;
//	Layer* prev;
//
//	MatrixXd sumdCdws;
//	VectorXd sumdCdbs;
//	//If making input, input does not need sums. Output only needs one dCda
//	VectorXd sumdCdas;
//
//	//store the expected values of the squared gradients. used for the momentum algorithm
//	MatrixXd eGradW;
//	VectorXd eGradB;
//
//	Layer (){/*Do Nothing*/}

//convert the characters of a string into a vector and store it in the given vector
VectorXd& NeuralNet::Sample::stringToVector (const string& inString, VectorXd& inVector)
{
	inVector.resize(inString.size());
	for (int c = 0; c < inVector.size(); ++c)
	{
//		//convert ascii char into double bewteen -1 and 1, inclusive
//		inVector[c] = ((2*inString[c]) / 127.0) - 1;
		inVector[c] = inString[c];
	}
	return inVector;
}

NeuralNet::Sample::Sample (const string& inInput, const string& inLabel)
{
	stringToVector(inInput, input);
	stringToVector(inLabel, label);
}

//resize the input vector. Added coefficients are set to null
void NeuralNet::Sample::resizeInput (const int& inNum)
{
	int oldSize = input.size();
	input.conservativeResize(inNum);

	for (int i = oldSize; i < input.size(); ++i)
	{
		input(i) = NULL_CHAR;
	}
}

//resize the output vector. Added coefficients are set to null
void NeuralNet::Sample::resizeLabel (const int& inNum)
{
	int oldSize = label.size();
	label.conservativeResize(inNum);

	for (int i = oldSize; i < label.size(); ++i)
	{
		label(i) = NULL_CHAR;
	}
}

//give the total size of the input and label
int NeuralNet::Sample::totalSize()
{
	return input.size() + label.size();
}

//print the sample's vector values for debugging purposes
void NeuralNet::Sample::dbPrint ()
{
	cout << "input: ";
	for (int i = 0; i < input.size(); ++i)
	{
		cout << input[i] << " ";
	}
	cout << endl;

	cout << "label: ";
	for (int i = 0; i < label.size(); ++i)
	{
		cout << label[i] << " ";
	}
	cout << endl;
}

NeuralNet::Layer::Layer (Layer* inPrev, int numNodes) : prev(inPrev)
{
//	prev = inPrev;
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
NeuralNet::Layer::Layer (int numNodes)
{
	vals = VectorXd(numNodes);
}

//create the layer by loading in the weights and biases from the given file
NeuralNet::Layer::Layer (Layer* inPrev, ifstream& inFile) : prev(inPrev)
{
	loadLayer(inFile);

	int rows = weights.rows();
	int cols = weights.cols();

	vals = VectorXd::Zero(rows);
	sumdCdws = MatrixXd::Zero(rows, cols);
	sumdCdbs = VectorXd::Zero(rows);
	sumdCdas = VectorXd::Zero(rows);
	eGradW = MatrixXd::Zero(rows, cols);
	eGradB = VectorXd::Zero(rows);
}

//Set the vals to a given vector, then store them (used for inputing data into the net)
void NeuralNet::Layer::setVals (VectorXd& inVals)
{
	vals = inVals;
}

//calculate this layer's vals.
void NeuralNet::Layer::calcVals()
{
	vals = weights*(prev->vals);
	vals += biases;
	sig(vals);
}

//Find dCdws, dCdbs and dCdas. Modifies the dCdas of the previous layer
void NeuralNet::Layer::findDerivs ()
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

		for (int j = 0; j < weights.cols(); ++j)
		{
			// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			sumdCdws(i, j) += dCdb * prev->vals(j);

			// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
			prev->sumdCdas(j) += dCdb * weights(i, j);
		}
	}
}

//Like findDerivs, but does not update dCdas of the previous layer.
//Used to efficiently find the derivs of the last hidden layer, since the dCdas of the input layer are never used.
void NeuralNet::Layer::findLastDerivs ()
{
	double dCdb, dCdw;
	double absPlusOne;
	for (int i = 0; i < sumdCdas.size(); ++i)
	{
		// dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
		absPlusOne = abs(vals(i)) + 1;
		dCdb = sumdCdas(i) / (absPlusOne * absPlusOne);
		sumdCdbs(i) += dCdb;

		for (int j = 0; j < weights.cols(); ++j)
		{
			// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			sumdCdws(i, j) += dCdb * prev->vals(j);
		}
	}
}

//Adjust the weights and biases based on the average dCdws and dCdbs
void NeuralNet::Layer::adjustVals (int batchSize)
{
	double avW;
	double avB;

	for (int r = 0; r < weights.rows(); ++r)
	{
		for (int c = 0; c < weights.cols(); ++c)
		{
//				//Standard gradient descent (SGD) algorithm
//				weights(r, c) -= learn_rate * (sumdCdws(r, c) / batchSize);

//				//momentum algorithm:
//				//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
//				//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (learn_rate * g_t)
//				//w -= E[g]_t
//				avW = sumdCdws(r, c) / batchSize;
//				eGradW(r, c) = (MOMENTUM * eGradW(r, c)) + (learn_rate * avW);
//				weights(r, c) -= eGradW(r, c);

			//RMSprop algorithm:
			//g_t = dC/dw = dC/da * da/dz * dz/dw = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
			//w -= (learn_rate * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
			avW = sumdCdws(r, c) / batchSize;
			eGradW(r, c) = (MOMENTUM * eGradW(r, c)) * (ONE_SUB_MOM * avW * avW);
			weights(r, c) -= (learn_rate * avW) / sqrt(eGradW(r,c) + MIN_GRAD);
		}

//			//Standard gradient descent (SGD) algorithm
//			biases(r) -= learn_rate * (sumdCdbs(r) / batchSize);

//			//momentum algorithm:
//			//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
//			//E[g]_t = (MOMENTUM * E[g]_(t-1)) + (learn_rate * g_t)
//			//b -= E[g]_t
//			avB = sumdCdbs(r) / batchSize;
//			eGradB(r) = (MOMENTUM * eGradB(r)) + (learn_rate * avB);
//			biases(r) -= eGradB(r);

		//RMSprop algorithm:
		//g_t = dC/db = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * 1
		//E[g^2]_t = (MOMENTUM * E[g^2]_(t-1)) + ((1 - MOMENTUM) * (g_t)^2)
		//b -= (learn_rate * g_t) / sqrt(E[g^2]_t + MIN_GRAD)
		avB = sumdCdbs(r) / batchSize;
		eGradB(r) = (MOMENTUM * eGradB(r)) + (ONE_SUB_MOM * avB * avB);
		biases(r) -= (learn_rate * avB) / sqrt(eGradB(r) + MIN_GRAD);
	}

	//zero out the sumdCdws and sumdCdbs once finished
	sumdCdws = MatrixXd::Zero(sumdCdws.rows(), sumdCdws.cols());
	sumdCdbs = VectorXd::Zero(sumdCdbs.size());
}

//resize this layer (adds or subtracts rows from all vectors and matrices in the layer via conservative resize)
void NeuralNet::Layer::resize (const int& numNodes)
{
	int oldNumNodes = weights.rows();

	weights.conservativeResize(numNodes, NoChange);
	biases.conservativeResize(numNodes);
	vals.conservativeResize(numNodes);
	sumdCdws.conservativeResize(numNodes, NoChange);
	sumdCdbs.conservativeResize(numNodes);
	sumdCdas.conservativeResize(numNodes);
	eGradW.conservativeResize(numNodes, NoChange);
	eGradB.conservativeResize(numNodes);

	//initialize the new values (random weights and biases, otherwise set to zero)
	for (int r = oldNumNodes; r < numNodes; ++r)
	{
		for (int c = 0; c < weights.cols(); ++c)
		{
			weights(r, c) = randWaB(re);
			sumdCdws(r,c) = 0;
			eGradW(r,c) = 0;
		}
		biases(r) = randWaB(re);
		vals(r) = 0;
		sumdCdbs(r) = 0;
		sumdCdas(r) = 0;
		eGradB(r) = 0;
	}
}

//TODO: this is an ugly method. Assumes that the previous layer changed size
//Assumedly the number of nodes in the previous layer has changed, so add or subtract columns from each matrix in this layer
void NeuralNet::Layer::prevResize (const int& numPrevNodes)
{
	int oldPrevNodes = weights.cols();

	weights.conservativeResize(NoChange, numPrevNodes);
	sumdCdws.conservativeResize(NoChange, numPrevNodes);
	eGradW.conservativeResize(NoChange, numPrevNodes);

	//initialize the new values (random weights, otherwise set to zero)
	for (int r = 0; r < weights.rows(); ++r)
	{
		for (int c = oldPrevNodes; c < weights.cols(); ++c)
		{
			weights(r, c) = randWaB(re);
			sumdCdws(r,c) = 0;
			eGradW(r,c) = 0;
		}
	}
}

//save the weights and biases of this layer into a given file
void NeuralNet::Layer::saveLayer (ofstream& inFile)
{
	//save the rows and cols of the layer
	unsigned long rows = weights.rows();
	unsigned long cols = weights.cols();
	inFile.write(reinterpret_cast<char *>(&rows), sizeof(rows));
	inFile.write(reinterpret_cast<char *>(&cols), sizeof(cols));

	//save the values of the weights and biases
	for (int r = 0; r < weights.rows(); ++r)
	{
		for (int c = 0; c < weights.cols(); ++c)
		{
			inFile.write(reinterpret_cast<char *>(&(weights(r, c))), sizeof(weights(r, c)));
		}
	}
	for (int r = 0; r < biases.size(); ++r)
	{
		inFile.write(reinterpret_cast<char *>(&(biases(r))), sizeof(biases(r)));
	}
}

//load the weights and biases from a file into the layer
void NeuralNet::Layer::loadLayer (ifstream& inFile)
{
	//get the number of rows and cols from the file, and make the weights and biases to those dimensions
	unsigned long rows;
	unsigned long cols;
	inFile.read(reinterpret_cast<char *>(&(rows)), sizeof(rows));
	inFile.read(reinterpret_cast<char *>(&(cols)), sizeof(cols));
	weights = MatrixXd(rows, cols);
	biases = VectorXd(rows);

	for (int r = 0; r < weights.rows(); ++r)
	{
		for (int c = 0; c < weights.cols(); ++c)
		{
			inFile.read(reinterpret_cast<char *>(&(weights(r, c))), sizeof(weights(r, c)));
		}
	}
	for (int r = 0; r < biases.size(); ++r)
	{
		inFile.read(reinterpret_cast<char *>(&(biases(r))), sizeof(biases(r)));
	}
}

//};

//struct OutputLayer : Layer
//{
//	OutputLayer (){/*Do Nothing*/}

//call the super constructor
//NeuralNet::OutputLayer::OutputLayer (Layer* inPrev, int numNodes) : Layer(inPrev, numNodes) {}

//Find the dCdws, dCdbs and dCdas of the output layer (done since the output layer should only have 1 dCda)
//Takes in the node of the chosen action
void NeuralNet::OutputLayer::findDerivs (int actionIndex)
{
	double dCdb, dCdw;

	//zero out the dCdas of the previous layer. Should not accumulate between runs.
	prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

	// dC/db = dC/da * da/db = 2(a_i-a*) * 1
	dCdb = sumdCdas(actionIndex);
	sumdCdbs(actionIndex) += dCdb;

	for (int i = 0; i < weights.cols(); ++i)
	{
		// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
		sumdCdws(actionIndex, i) += dCdb * prev->vals(i);

		// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
		prev->sumdCdas(i) += dCdb * weights(actionIndex, i);
	}

	//Once done, zero out this layer's sumdCdas
	sumdCdas = VectorXd::Zero(sumdCdas.size());
}

//find the dCda's, dCdb's and dCdw's of this layer, for all nodes in this layer.
void NeuralNet::OutputLayer::findDerivs()
{
	double dCdb, dCdw;

	//zero out the dCdas of the previous layer. Should not accumulate between runs.
	prev->sumdCdas = VectorXd::Zero(prev->sumdCdas.size());

	//find the derivs for each node in this layer
	for (int i = 0; i < sumdCdas.size(); ++i)
	{
		// dC/db = dC/da * da/db = 2(a_i-a*) * 1
		dCdb = sumdCdas(i);
		sumdCdbs(i) += dCdb;

		//for each node in the previous layer
		for (int j = 0; j < weights.cols(); ++j)
		{
			// dC/dw = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * a_i-1 = dC/db * a_i-1
			sumdCdws(i, j) += dCdb * prev->vals(j);

			// dC/da = dC/da * da/dz * dz/db = 2(a_i-a*) * 1/(1+|a_i|)^2 * w_j = dC/db * w_j
			prev->sumdCdas(j) += dCdb * weights(i, j);
		}
	}

	//Once done, zero out this layer's sumdCdas
	sumdCdas = VectorXd::Zero(sumdCdas.size());
}

//calculate this layer's vals. For output layer, do not apply sig
void NeuralNet::OutputLayer::calcVals()
{
	vals = weights*(prev->vals);
	vals += biases;
}
//};

//struct NeuralNet
//{
//	Layer inputLayer;
//	Layer hiddenLayers[NUM_HIDDEN];
//	OutputLayer outputLayer;

//Makes neural net using const values
NeuralNet::NeuralNet ()
{
	inputLayer = Layer(DEFAULT_INPUT_SIZE);
	hiddenLayers[0] = Layer(&inputLayer, HIDDEN_SIZE);
	for (int i = 1; i < NUM_HIDDEN; ++i)
	{
		hiddenLayers[i] = Layer(&hiddenLayers[i-1], HIDDEN_SIZE);
	}
	outputLayer = OutputLayer(&hiddenLayers[NUM_HIDDEN - 1], DEFAULT_OUTPUT_SIZE);
}

//Make a neural net based on the information in a given file
NeuralNet::NeuralNet (const string& fileName)
{
	ifstream saveFile;
	saveFile.open(fileName);

	inputLayer = Layer(DEFAULT_INPUT_SIZE);
	//load the net from the file if the file exists
	if (saveFile.peek() != EOF)
	{
		cout << "loading net from file " << fileName << endl;
		hiddenLayers[0] = Layer (&inputLayer, saveFile);
		for (int i = 1; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i] = Layer(&hiddenLayers[i-1], saveFile);
		}
		outputLayer = OutputLayer(&hiddenLayers[NUM_HIDDEN - 1], saveFile);
	}
	//otherwise, create the net with random weights and biases
	else
	{
		cout << "could not find file: " << fileName << "\tmaking new net" << endl;

		hiddenLayers[0] = Layer(&inputLayer, HIDDEN_SIZE);
		for (int i = 1; i < NUM_HIDDEN; ++i)
		{
			hiddenLayers[i] = Layer(&hiddenLayers[i-1], HIDDEN_SIZE);
		}
		outputLayer = OutputLayer(&hiddenLayers[NUM_HIDDEN - 1], DEFAULT_OUTPUT_SIZE);
	}

	saveFile.close();
}

//save the neural net weights and values to a given file
void NeuralNet::saveNet (const string& fileName)
{
	ofstream saveFile;
	saveFile.open(fileName);

//	//write the metadata of the net, so that it can be reconstructed
//	saveFile << inputLayer.vals.size();
//	saveFile << outputLayer.vals.size();

	//write the weights and biases
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		hiddenLayers[i].saveLayer(saveFile);
	}
	outputLayer.saveLayer(saveFile);

	saveFile.flush();
	saveFile.close();

	cout << "saved net" << endl;
}

//Push the data through the net to set all the node values
void NeuralNet::pushForward (VectorXd& inData)
{
	inputLayer.setVals(inData);
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		hiddenLayers[i].calcVals();
	}
	outputLayer.calcVals();
}

//push forward using a sample. Makes sure that the size of the sample and the first hidden layer match,
// and the same for the label and the output layer
void NeuralNet::pushForward (Sample& inSample)
{
	//if the input layer is too small for the input, add more nodes to the input layer
	if (inSample.input.size() > hiddenLayers[0].weights.cols())
	{
		hiddenLayers[0].prevResize(inSample.input.size());
	}
	//If the input layer is too big, add null values to the input
	else if (inSample.input.size() < hiddenLayers[0].weights.cols())
	{
		inSample.resizeInput(hiddenLayers[0].weights.cols());
	}
	//if the output layer is too small for the label, add more nodes to the output layer
	if (inSample.label.size() > outputLayer.vals.size())
	{
		outputLayer.resize(inSample.label.size());
	}
	//If the output layer is too big, add null values to the label
	else if (inSample.label.size() < outputLayer.vals.size())
	{
		inSample.resizeLabel(outputLayer.vals.size());
	}

	pushForward(inSample.input);
}

//Process the data and guess which action to take. Save the vals, for when we backpropogat (for vectors, = makes a copy vector, ie does not pass a pointer value)
//Returns the action taken (the index of the output node it chose)
int NeuralNet::pickAction (VectorXd& inData)
{
	//push the data through the net and get the output values
	pushForward(inData);

	int bestAction = 0;
	for (int action = 0; action < outputLayer.vals.size(); ++action)
	{
		if (outputLayer.vals(action) > outputLayer.vals(bestAction))
		{
			bestAction = action;
		}
	}
	return bestAction;
}

//Like pickAction, but with a chance of choosing a random action (for exploration purposes)
int NeuralNet::ePickAction (VectorXd& inData)
{
	//decay epsilon if it hasn't reached the minimum
	if (epsilon >= EP_MIN)
	{
		epsilon -= EP_DECAY;
	}

	//push the data through the net and get the output values
	pushForward(inData);

	//With a probability of epsilon, pick a random action. Otherwise, pick the best perceived action
	double rand = randVal(re);
	if (rand > epsilon)
	{
		//TODO: call pickAction in here if that doesn't slow performance
		int bestAction = 0;
		for (int action = 0; action < outputLayer.vals.size(); ++action)
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
void NeuralNet::backpropogate (VectorXd& inState, int action, double actual)
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

//like the above backpropogate, only takes in an input and compares the output to what it should be, then sets the layer derivatives.
//returns the total cost per character (how much each output character deviates from what the character should be)
double NeuralNet::runTest (Sample& inSample)
{
//	//if the input layer is too small for the input, add more nodes to the input layer
//	if (inSample.input.size() > hiddenLayers[0].weights.cols())
//	{
//		hiddenLayers[0].prevResize(inSample.input.size());
//	}
//	//If the input layer is too big, add null values to the input
//	else if (inSample.input.size() < hiddenLayers[0].weights.cols())
//	{
//		inSample.resizeInput(hiddenLayers[0].weights.cols());
//	}
//	//if the output layer is too small for the label, add more nodes to the output layer
//	if (inSample.label.size() > outputLayer.vals.size())
//	{
//		outputLayer.resize(inSample.label.size());
//	}
//	//If the output layer is too big, add null values to the label
//	else if (inSample.label.size() < outputLayer.vals.size())
//	{
//		inSample.resizeLabel(outputLayer.vals.size());
//	}
//
//	pushForward(inSample.input);
	pushForward(inSample);

	double totalCost = 0;

	//TODO: lowering learning rate helped a lot, now see if this thing can guess the answers

	//set the sumdCdas of the output layer
	double diff;
	for (int i = 0; i < outputLayer.vals.size(); ++i)
	{
		diff = (outputLayer.vals(i) - inSample.label(i));
		outputLayer.sumdCdas(i) = 2 * diff;
		totalCost += abs(diff);//diff * diff;
	}

	//Find the derivs of all the layers
	outputLayer.findDerivs();
	for (int i = NUM_HIDDEN - 1; i > 0; --i)
	{
		hiddenLayers[i].findDerivs();
	}
	hiddenLayers[0].findLastDerivs();

	//return the cost per character
	return totalCost/outputLayer.vals.size();
}

//adjust the weights and biases of each layer based on their average derivs
void NeuralNet::adjustVals (int batchSize)
{
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		hiddenLayers[i].adjustVals(batchSize);
	}
	outputLayer.adjustVals(batchSize);
}

//copy the weights and biases of a given net into this net
void NeuralNet::copyNet (NeuralNet& origNet)
{
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		hiddenLayers[i].weights = origNet.hiddenLayers[i].weights;
		hiddenLayers[i].biases = origNet.hiddenLayers[i].biases;
	}
	outputLayer.weights = origNet.outputLayer.weights;
	outputLayer.biases = origNet.outputLayer.biases;
}

//run a number of tests and return the average cost per character
double NeuralNet::runTests (vector<Sample>& inSamples, const int& numTests)
{
	vector<Sample*> batch;
	//the total test averages
	double totalTestAvs = 0;
	for (int test = 0; test < numTests; ++test)
	{
		//the total cost accrued in this test
		double testTotal = 0;
		batch = getRandBatch(inSamples);
		for (Sample* sample : batch)
		{
			testTotal += runTest(*sample);
		}
		adjustVals(batch.size());
		//add the average cost for each sample of this test
		double batchAv = testTotal / batch.size();
		totalTestAvs += batchAv;

		//if the net stops converging, reset the learning rate
		if (batchAv > 128) //TODO: make the 128 into a static const
		{
			cout << "resetting learning rate" << endl;
			learn_rate = DEFAULT_LEARN_RATE;
		}
	}

	//return the average of the test averages
//	cout << "average cost per sample: " << (totalTestAvs / numTests) << endl;

	return totalTestAvs / numTests;
}

//Print the vals of the layers
void NeuralNet::dbPrintVals ()
{
	printf("Vals:\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].vals.size(); ++j)
		{
			printf("%lf ", hiddenLayers[i].vals(j));
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.vals.size(); ++i)
	{
		printf("%lf ", outputLayer.vals(i));
	}
	printf("\n");
}

//print the weights and biases of each layer
void NeuralNet::dbPrintWaB ()
{
	printf("**********biases**********\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].biases.size(); ++j)
		{
			printf("%lf ", hiddenLayers[i].biases(j));
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.biases.size(); ++i)
	{
		printf("%lf ", outputLayer.biases(i));
	}
	printf("\n**********weights**********\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].weights.rows(); ++j)
		{
			for (int k = 0; k < hiddenLayers[i].weights.cols(); ++k)
			{
				printf("%lf ", hiddenLayers[i].weights(j,k));
			}
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.weights.rows(); ++i)
	{
		for (int j = 0; j < outputLayer.weights.cols(); ++j)
		{
			printf("%lf ", outputLayer.weights(i, j));
		}
	}
	printf("\n");
}

//print the derivs of the layers
void NeuralNet::dbPrintDerivs ()
{
	printf("**********dCdas**********\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].sumdCdas.size(); ++j)
		{
			printf("%lf ", hiddenLayers[i].sumdCdas(j));
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.sumdCdas.size(); ++i)
	{
		printf("%lf ", outputLayer.sumdCdas(i));
	}
	printf("\n**********dCdbs**********\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].sumdCdbs.size(); ++j)
		{
			printf("%lf ", hiddenLayers[i].sumdCdbs(j));
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.sumdCdbs.size(); ++i)
	{
		printf("%lf ", outputLayer.sumdCdbs(i));
	}
	printf("\n**********dCdws**********\n");
	for (int i = 0; i < NUM_HIDDEN; ++i)
	{
		for (int j = 0; j < hiddenLayers[i].sumdCdws.rows(); ++j)
		{
			for (int k = 0; k < hiddenLayers[i].sumdCdws.cols(); ++k)
			{
				printf("%lf ", hiddenLayers[i].sumdCdws(j,k));
			}
		}
		printf("\n");
	}
	for (int i = 0; i < outputLayer.sumdCdws.rows(); ++i)
	{
		for (int j = 0; j < outputLayer.sumdCdws.cols(); ++j)
		{
			printf("%lf ", outputLayer.sumdCdws(i, j));
		}
	}
	printf("\n");
}

//print the sample input, label, and the net's output
void NeuralNet::dbPrintResult (Sample& inSample)
{
	cout << "input: ";
	unsigned char tempChar;
	for (int i = 0; i < inSample.input.size(); ++i)
	{
		tempChar = inSample.input(i) + .5;
		if (tempChar > 31)
		{
			cout << tempChar;
		}
	}
	cout << endl;

	cout << "label: ";
	for (int i = 0; i < inSample.label.size(); ++i)
	{
		tempChar = inSample.label(i) + .5;
		if (tempChar > 31)
		{
			cout << tempChar;
		}
	}
	cout << endl;

	cout << "output:";
	pushForward(inSample);
	for (int i = 0; i < outputLayer.vals.size(); ++i)
	{
		tempChar = outputLayer.vals(i) + .5;
		if (tempChar > 31)
		{
			cout << tempChar;
		}
//		else
//		{
//			cout << " ";
//		}
	}
	cout << endl;
}
//};

//return a random subset of given collection of samples
vector<NeuralNet::Sample*> getRandBatch (vector<NeuralNet::Sample>& inSamples)
{
	//make an array of pointers to each of the samples
	vector<NeuralNet::Sample*> pointers (inSamples.size());
	for (int i = 0; i < pointers.size(); ++i)
	{
		pointers[i] = &(inSamples[i]);
	}

	//get a random subset of the samples
	random_shuffle(begin(pointers), end(pointers));

	//return the all samples if there aren't enough for a whole batch
	if (pointers.size() <= BATCH_SIZE)
	{
		return pointers;
	}

	pointers.resize(BATCH_SIZE);
	return pointers;
}

//Get the next question/answer segment from the given file. Segment consists of all characters before a double newline and/or an EOF
//advances the file stream iterator to after the segment
//returns an empty string if no segment was found
string getNextSegment (ifstream& inFile)
{
	string temp;
	string segment;
	if (inFile.peek() != '\n' && inFile.peek() != EOF)
	{
		getline (inFile, temp);
		segment.append(temp);
	}

	while (inFile.peek() != '\n' && inFile.peek() != EOF)
	{
		getline (inFile, temp);
		segment.append('\n' + temp);
	}
	//skip over the newline at the end of the question
	if (inFile.peek() != EOF)
	{
		inFile.get();
	}

	return segment;
}

//partition function for the sortSamples function.
//the goal is to put the pivot value in the right spot and have lower values to the left of the pivot and higher values to the right.
//return the index of the pivot
int partition (vector<NeuralNet::Sample>& inSamples, int lo, int hi)
{
	int pivot = inSamples[hi].totalSize();
	int small = lo; //index to put the next number less than the pivot
	NeuralNet::Sample temp;
	for (int i = lo; i < hi; ++i)
	{
		if (inSamples[i].totalSize() < pivot)
		{
			//put the small number in the 'small' portion of the vector
			temp = inSamples[small];
			inSamples[small] = inSamples[i];
			inSamples[i] = temp;

			++small;
		}
	}

	//place the pivot at the end of the small portion. All values before it should be smaller, and all values after it should be greater than or equal
	temp = inSamples[small];
	inSamples[small] = inSamples[hi];
	inSamples[hi] = temp;

	return small;
}

//recursive helper method for sortSamples.
//lo and hi are the lower and upper bounds (includive) to be sorted
void sortSamples (vector<NeuralNet::Sample>& inSamples, int lo, int hi)
{
	if (lo < hi)
	{
		int pivotIndex = partition(inSamples, lo, hi);
		sortSamples(inSamples, lo, pivotIndex - 1);
		sortSamples(inSamples, pivotIndex + 1, hi);
	}
}

//sort the samples from smallest total size to largest using quicksort
void sortSamples (vector<NeuralNet::Sample>& inSamples)
{
	sortSamples(inSamples, 0, inSamples.size() - 1);
}

int main ()
{
	ifstream qFile;
	ifstream aFile;
	qFile.open(Q_FILE);
	aFile.open(A_FILE);

	//get the samples from the files and put them in a vector
	vector<NeuralNet::Sample> samples;
	string temp;
//	for (int i = 0; i < 3; ++i)
	while (qFile.peek() != EOF && aFile.peek() != EOF)
	{
		samples.push_back(NeuralNet::Sample (getNextSegment(qFile), getNextSegment(aFile)));
	}
	//sort the samples by length
	sortSamples(samples);

	vector<NeuralNet::Sample> testSamples; //subsection of samples used for tests
//	for (int i = 0; i < 5; ++i)
//	for (int i = 0; i < samples.size(); ++i)
//	{
//		testSamples.push_back(samples[i]);
//	}
//	testSamples.push_back(samples[2]);
//	testSamples.push_back(samples[3]);
	testSamples.push_back(samples[0]);

	//TODO: output stagnates when learning rate is too low. Make it so that if the cost stagnates and is above an "acceptable" threshold (like above 1), then increase the learning rate gradually.

	//try to make the neuralNet by reading from the file. If the file doesn't exist, initialize the net with random values
	mkdir(S_DIR.c_str(), 0777);
	string fileName = S_DIR + "/H" + to_string(NUM_HIDDEN) + "x" + to_string(HIDDEN_SIZE) + "L" + to_string(DEFAULT_LEARN_RATE);
	NeuralNet answerer(fileName);
//	double minCost = numeric_limits<double>::max();
	double lastCost = 0; //the cost of the last test
	double testCost;

	cout << "How many samples do you want to learn before showing the results? : " << flush;
	int numSamples;
	cin >> numSamples;

	//run the tests
	while (testSamples.size() <= numSamples && testSamples.size() < samples.size())
//	for (int i = 0; i < 200; ++i)
	{
		testCost = answerer.runTests(testSamples, 100);
//		testCost = answerer.runTests(samples, 50);
		cout << "average cost per character: " << testCost << endl;

		//once the average cost is low enough, save the net and add another sample
		if (testCost < COST_THRESH)
//		if (testCost < minCost)
		{
//			minCost = testCost;
			answerer.saveNet(fileName);
			//add another sample to the test samples
			if (testSamples.size() < samples.size())
			{
				learn_rate = DEFAULT_LEARN_RATE;
				lastCost = 0;
				testSamples.push_back(samples[testSamples.size()]);
				cout << "*** number of samples increased to " << testSamples.size() << " ***" << endl;
			}
		}
		//if the net stagnates, gradually increase the learning rate
		else if (testCost - .5 <= lastCost && testCost + .5 >= lastCost) //TODO: make the range a static constant
		{
			learn_rate += LEARN_STEP;
			cout << " increased learning rate to " << learn_rate << endl;
		}
		else
		{
			lastCost = testCost;
		}
	}
//	//save the net after the tests
//	answerer.saveNet(fileName); //TODO: might want to get rid of this

	//print out some of the results
//	for (int i = 0; i < 5; ++i)
	for (int i = 0; i < testSamples.size(); ++i)
	{
		answerer.dbPrintResult(testSamples[i]);
//		samples[i].dbPrint();
//		answerer.dbPrintWaB();
//		answerer.dbPrintVals();
	}


	qFile.close();
	aFile.close();
}
