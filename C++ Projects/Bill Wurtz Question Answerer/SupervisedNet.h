#include <stdio.h>
#include <time.h>
#include <iostream>
#include <random>
#include <cmath>
#include <Eigen/Dense>

using namespace Eigen;
using namespace std;

//Neural net dimensions
static const int DEFAULT_INPUT_SIZE = 16;//BOARD_SQRD;
static const int NUM_HIDDEN = 3;//3;
static const int HIDDEN_SIZE = 64;//16;
static const int DEFAULT_OUTPUT_SIZE = 4;

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
static const double DEFAULT_LEARN_RATE = .0005;
static const double LEARN_STEP = .00005;//.00001;
static double learn_rate = DEFAULT_LEARN_RATE;//.0001; RMS: ~.001
//number max cost per character a test can have before adding a new sample to the test samples
static const double COST_THRESH = .01;
//The minimum expected gradient squared (added to prevent dividing by zero)
static const double MIN_GRAD = .01;

static const int NUM_GAMES = 100000;

//save the neural net after this many games (if it has improved)
static const int SAVE_RATE = 100;

//The number of transition that need to be stored before the net starts making decisions and learning
static const int REPLAY_START_SIZE = 50000;
//number of transitions that are picked when adjusting the net.
static const int BATCH_SIZE = 32;

//files from which the samples are read
static const string Q_FILE = "questions.html";
static const string A_FILE = "answers.html";
//directory where the save files are stored
static const string S_DIR = "saves";

static const char NULL_CHAR = 0;

random_device rd;
default_random_engine re(rd());

//used to make random doubles between -START_RANGE and START_RANGE for the weights and biases
static const double START_RANGE = 5.;
uniform_real_distribution<double> randWaB (-START_RANGE, START_RANGE);
//used to make random values between 0 and 1, to decide with the net will do the best perceived action or a random action
uniform_real_distribution<double> randVal (0., 1.);
//used to get a random action
uniform_int_distribution<int> randAction (0, DEFAULT_OUTPUT_SIZE - 1);


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

class NeuralNet
{
private:
	class Layer
	{
	//TODO: find a way to make these private
//	private:
	public:
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

//	public:
		Layer (){/*Do Nothing*/}
		Layer (Layer* inPrev, int numNodes);
		Layer (int numNodes);
		Layer (Layer* inPrev, ifstream& inFile);
		void setVals (VectorXd& inVals);
		void calcVals();
		void findDerivs ();
		void findLastDerivs ();
		void adjustVals (int batchSize);
		void resize(const int& numNodes);
		void prevResize(const int& numPrevNodes);
		void saveLayer(ofstream& inFile);
		void loadLayer(ifstream& inFile);
	};

	class OutputLayer : public Layer
	{
	public:
		OutputLayer (){/*Do Nothing*/}
		//call the super constructor
		OutputLayer (Layer* inPrev, int numNodes) : Layer(inPrev, numNodes) {}
		OutputLayer (Layer* inPrev, ifstream& inFile) : Layer(inPrev, inFile) {}
		void findDerivs (int actionIndex);
		void findDerivs ();
		void calcVals();
	};

	Layer inputLayer;
	Layer hiddenLayers[NUM_HIDDEN];
	OutputLayer outputLayer;

public:

	class Sample
	{
	public:
		VectorXd input;
		VectorXd label;

		Sample() = default;
		Sample (const string& inInput, const string& inLabel);
		VectorXd& stringToVector (const string& inString, VectorXd& inVector);
		void dbPrint();
		void resizeInput(const int& inNum);
		void resizeLabel(const int& inNum);
		int totalSize();
	};

	NeuralNet ();
	NeuralNet (const string& fileName);
	void saveNet (const string& fileName);
	void loadNet (const string& fileName);
	void pushForward (VectorXd& inData);
	void pushForward (Sample& inSample);
	int pickAction (VectorXd& inData);
	int ePickAction (VectorXd& inData);
	void backpropogate (VectorXd& inState, int action, double actual);
	void adjustVals (int batchSize);
	void copyNet (NeuralNet& origNet);
	double runTest (Sample& inSample);
	double runTests (vector<Sample>& inSamples, const int& numTests);
	void dbPrintVals ();
	void dbPrintWaB ();
	void dbPrintDerivs ();
	void dbPrintResult (Sample& inSample);
};

vector<NeuralNet::Sample*> getRandBatch(vector<NeuralNet::Sample>& inSamples);
string getNextSegment (ifstream& inFile);
int partition (vector<NeuralNet::Sample>& inSamples, int lo, int hi);
void sortSamples (vector<NeuralNet::Sample>& inSamples, int lo, int hi);
void sortSamples (vector<NeuralNet::Sample>& inSamples);
