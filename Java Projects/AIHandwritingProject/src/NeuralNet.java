import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

//A network with layers of connected nodes, with the first layer being the input and the last layer being the output
public class NeuralNet 
{
	private Node[] firstHidden;
	private Node[] secondHidden;
	private Node[] outputLayer;
	
	private int numHiddenNodes = 16;
	//0-9 (plus a node for total nonsense images, if there are random images)
	private int numOutputNodes;
		
	//make the net with random values
	public NeuralNet (int inOutput, Node[] inInput)
	{
		numOutputNodes = inOutput;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inInput);
		initLayer(secondHidden, firstHidden);
		initLayer(outputLayer, secondHidden);
	}
	
	//make the net with values read from a file
	public NeuralNet (DataInputStream inStream, int inOutput, Node[] inInput) throws IOException
	{
		numOutputNodes = inOutput;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inInput, inStream);
		initLayer(secondHidden, firstHidden, inStream);
		initLayer(outputLayer, secondHidden, inStream);
	}
	
	//make a net with random values and a given amount of hidden nodes
	public NeuralNet (int inOutput, int inHidden, Node[] inInput)
	{
		numOutputNodes = inOutput;
		numHiddenNodes = inHidden;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inInput);
		initLayer(secondHidden, firstHidden);
		initLayer(outputLayer, secondHidden);
	}
	
	//make a net with values read from a file and a given amount of hidden nodes
	public NeuralNet (DataInputStream inStream, int inOutput, int inHidden, Node[] inInput) throws IOException
	{
		numOutputNodes = inOutput;
		numHiddenNodes = inHidden;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inInput, inStream);
		initLayer(secondHidden, firstHidden, inStream);
		initLayer(outputLayer, secondHidden, inStream);
	}
	
	//make a net with random values, and whose input is the output of another net
	public NeuralNet (int inOutput, NeuralNet inNet)
	{
		numOutputNodes = inOutput;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inNet.outputLayer);
		initLayer(secondHidden, firstHidden);
		initLayer(outputLayer, secondHidden);
	}	
	
	//make the net with values read from a file, and whose input is the output of another net
	public NeuralNet (DataInputStream inStream, int inOutput, NeuralNet inNet) throws IOException
	{
		numOutputNodes = inOutput;
		
		firstHidden = new Node[numHiddenNodes];
		secondHidden = new Node[numHiddenNodes];
		outputLayer = new Node[numOutputNodes];
		
		initLayer(firstHidden, inNet.outputLayer, inStream);
		initLayer(secondHidden, firstHidden, inStream);
		initLayer(outputLayer, secondHidden, inStream);
	}
	
	//fill the layer with nodes and connect these nodes to those of the previous layer
	private void initLayer (Node[] inLayer, Node[] prevLayer)
	{
		for (int i = 0; i < inLayer.length; i++)
		{
			inLayer[i] = new Node(prevLayer);
		}
	}
	
	//fill the layer with nodes, connect the nodes to the previous layer, and read the weights and biases from the file 
	private void initLayer (Node[] inLayer, Node[] prevLayer, DataInputStream inStream) throws IOException
	{
		for (int i = 0; i < inLayer.length; i++)
		{
			inLayer[i] = new Node(prevLayer, inStream);
		}
	} 
	
	//looks at an image, compares results to what the image actually is, and finds the average cost function on the
	//current net
	public boolean runTest (int inLabel)
	{
//		float[] guess;
//		float[] desired;
//		//the differences between the guess and the desired result
//		float[] diffs;
		
//		//guess, then compare the guess to the right answer
//		guess = makeGuess();
//		desired = makeDesired(inLabel);
//		diffs = findDiffs(guess, desired);
//		//once the guess and answer are compared, determine how the image wants to change the net
//		backpropogate (getdCdas(diffs));
				
		backpropogate(getdCdas(findDiffs(makeGuess(), makeDesired(inLabel))));
		
		//return costFunction(diffs);
		return isCorrect(inLabel);
	}
	
	//guess what the image is (or, if the writer, guess how the given number is drawn)
	public float[] makeGuess()
	{	
		findVals(firstHidden);
		findVals(secondHidden);
		return findVals(outputLayer);
	}
	
	//find the values of the nodes in a layer based on what the previous layer's values are
	private float[] findVals (Node[] inLayer)
	{
		float[] output = new float[inLayer.length];
		
		for (int i = 0; i < inLayer.length; i++)
		{
			output[i] = inLayer[i].findVal();
		}
		
		return output;
	}
	
	//makes the desired guess output array based on the image's label
	private float[] makeDesired (int inLabel)
	{
		float[] output = new float[numOutputNodes];
		output[inLabel] = 1f;
		return output;
	}
	
	//find the differences between the guess and the desired result
	private static float[] findDiffs (float[] inGuess, float[] inDesired)
	{
		float[] output = new float[inGuess.length];
		for (int i = 0; i < output.length; i++)
		{
			output[i] = inGuess[i] - inDesired[i];
		}
		return output;
	}	
	
	//finds the cost function given the difference between guess and the desired result
	private float costFunction (float[] inDiffs)
	{
		float cost = 0;
		for (int i = 0; i < inDiffs.length; i++)
		{
			cost += Math.pow((inDiffs[i]), 2);
		}
		return cost;
	}
	
	//backpropogate using the current dCda's in the output layer (used when this net is connected to another)
	public void backpropogate ()
	{
		//find the derives every layer in backwards order
		for (int i = 0; i < numOutputNodes; i++)
		{
			outputLayer[i].findDerivs();
			outputLayer[i].resetdCda();
		}
		for (int i = 0; i < numHiddenNodes; i++)
		{
			secondHidden[i].findDerivs();
			secondHidden[i].resetdCda();
		}
		for (int i = 0; i < numHiddenNodes; i++)
		{
			firstHidden[i].findDerivs();
			firstHidden[i].resetdCda();
		}
	}
	
	//once the cost of the net has been found for a given image example, find the changes needed to all the weights and biases in
	//all the nodes, given the derivative of the cost function in relation to the nodes in the last layer.
	private void backpropogate (float[] indCdas)
	{
		
		//set the dCdas of the output layer and adjust their weights, biases, and the dCda's of the previous nodes
		for (int i = 0; i < numOutputNodes; i++)
		{
			outputLayer[i].setdCda(indCdas[i]);
			outputLayer[i].findDerivs();
		}
		
		//find the derivatives of the weights, biases, etc. of the inner layers in backwards order, then reset their dCda's to
		//prepare for the next batch
		for (int i = 0; i < numHiddenNodes; i++)
		{
			secondHidden[i].findDerivs();
			secondHidden[i].resetdCda();
		}
		for (int i = 0; i < numHiddenNodes; i++)
		{
			firstHidden[i].findDerivs();
			firstHidden[i].resetdCda();
		}
	}
	
	//adjust the values of the weights and biases based on the average of their desired changes
	public void adjustVals ()
	{
		for (int i = 0; i < numOutputNodes; i++)
		{
			outputLayer[i].adjustVals();
		}
		
		for (int i = 0; i < numHiddenNodes; i++)
		{
			secondHidden[i].adjustVals();
			firstHidden[i].adjustVals();
		}
	}
	
	//finds the derivatives of the cost functions in relation to each output node, using the differences between guess and desired.
	//Returns a list of those derivatives.
	// dC/da = 2(a - y)     C = cost, a = node, y = desired
	private float[] getdCdas (float[] inDiffs)
	{
		float[] output = new float[numOutputNodes];
		for (int i = 0; i < output.length; i++)
		{
			output[i] = 2 * inDiffs[i];
		}
		return output;
	}
	
	//return the ouput layer
	public Node[] getOutput ()
	{
		return outputLayer;
	}
	
	//return true if what the net guessed (the most active node in the output layer) is the correct answer
	private boolean isCorrect (int inAnswer)
	{
		//get the most active node in the output layer
		int highestNode = 0;
		for (int i = 1; i < outputLayer.length; i++)
		{
			if (outputLayer[i].getVal() > outputLayer[highestNode].getVal())
			{
				highestNode = i;
			}
		}
		
//		System.out.println("guessed: " + highestNode + "  actual: " + inAnswer);
		
		//compare the most active node to the correct answer
		return highestNode == inAnswer;
	}
	
	//return the current cost value of the net for a given answer
	public float getCost (int inAnswer)
	{
		return costFunction(findDiffs(makeGuess(), makeDesired(inAnswer)));
	}
	
	//print out the values of the nodes
	public void printNet()
	{
		for (int i = 0; i < numHiddenNodes; i++)
		{
			System.out.print(firstHidden[i].toString() + " ");
		}
		System.out.print("\n");
		for (int i = 0; i < numHiddenNodes; i++)
		{
			System.out.print(secondHidden[i].toString() + " ");
		}
		System.out.print("\n");
		for (int i = 0; i < numOutputNodes; i++)
		{
			System.out.print(outputLayer[i].toString() + " ");
		}
		System.out.print("\n");
	}
	
	//write this net's weights and biases to the save file
	public void writeSave (DataOutputStream inStream) throws IOException
	{
		//write the weights and biases of the hidden layers
		for (int i = 0; i < numHiddenNodes; i++)
		{
			firstHidden[i].writeSave(inStream);
		}
		for (int i = 0; i < numHiddenNodes; i++)
		{
			secondHidden[i].writeSave(inStream);
		}
		//write the weights and biases of the output layer
		for (int i = 0; i < outputLayer.length; i++)
		{
			outputLayer[i].writeSave(inStream);
		}
	}
}
