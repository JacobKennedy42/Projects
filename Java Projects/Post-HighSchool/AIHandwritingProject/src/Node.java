import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.naming.InitialContext;

//a node in the net
public class Node 
{
	//the nodes that feed into this node (if this node is in the first layer, inputNodes will = null)
	private Node[] inputNodes;
	//this node's weights and bias
	private float[] weights;
	private float bias;
	//the value of this node before the sigmoid function is applied (z is used during backpropogation)
	private float z;
	//this node's final value
	private float myVal;
	
	//the amount this node needs to change to best increase the cost function (used to then find the best way to decrease the
	//cost function). This is calculated either directly by the cost function or by the nodes in the layer after this one.
	private float dCda;
	
	//sum of all the derivatives dCdw and dCdb (used to calculate their average)
	private float[] sumdCdw;
	private float sumdCdb;
	
	Scanner input;
	
	//sets myVal explicitly, used for input layer
	public Node (float inVal)
	{
		input = new Scanner(System.in);
		
		myVal = inVal;
	}
	
	public Node (Node[] inNodes)
	{
		input = new Scanner(System.in);
		
		inputNodes = inNodes;
		weights = new float[inputNodes.length];
		sumdCdw = new float[inputNodes.length];
		
		randomizeVals();
	}
	
	public Node (Node[] inNodes, DataInputStream inStream) throws IOException
	{
		inputNodes = inNodes;
		weights = new float[inputNodes.length];
		sumdCdw = new float[inputNodes.length];
		
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = inStream.readFloat();
		}
		bias = inStream.readFloat();
	}
	
	//randomize the weights and bias
	private void randomizeVals ()
	{
		float range = 10;
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = (float) (Math.random() * range) - (range/2f);
		}
		bias = (float) (Math.random() * range) - (range/2f);
	}
	
	//finds and returns the value of this node based on its weights, bias, and the values of the previous node.
	//if the node has no input nodes, return -1
	// val = sigmoid(W1a1 + W2a2 + ... + Wnan + bias)
	public float findVal()
	{
		z = 0f;
		for (int i = 0; i < inputNodes.length; i++)
		{
			z += inputNodes[i].myVal * weights[i];
		}
		z += bias;
		myVal = sigmoid(z);
		
		return myVal;
	}
	
	public float getVal ()
	{
		return myVal;
	}
	
	//used to set the value of input nodes
	public void setVal (float inVal)
	{
		if (inputNodes == null)
		{
			myVal = inVal;
		}
		else
		{
			System.out.println("Values can only be accessed directly for input nodes.");
		}
	}
	
	//reset the node by setting dCda to 0
	public void resetdCda()
	{
		dCda = 0f;
	}
	
	//set dCda to a given amount (supposedly the value computed by the cost function)
	public void setdCda (float indCda)
	{
		dCda = indCda;
	}
	
	//perform the sigmoid function on a number
	private static float sigmoid (float inVal)
	{
		return 1f / (1f + (float) Math.pow(Math.E, -1f * inVal));
	}
	
	//find and return the derivative of this node's value in relation to its z value
	// a = this node's value, z = this node's value before sigmoid is applied
	// a = sig(z) = 1 / (1 + e^(-z))
	// da/dz = sig(z) * (1 - sig(z)) = a * (1 - a)
	private float getdadz ()
	{
//		//temp = e^(-z)
//		float temp = (float) Math.pow(Math.E, -1f * z);
//		//temp / ((1 + temp)^2)
//		return temp / ((float) Math.pow((1 + temp), 2));
		
//		//temp = sig(z)
//		float temp = sigmoid(z);
//		return temp * (1 - temp);
		
		//da/dz = a * (1 - a)
		return myVal * (1 - myVal);
	}
	
	//find the derivatives of the cost in relation to each of the weights, the bias, and the previous nodes, then adjusts the
	// weights and the bias by the negative of their derivative, while also incrementing the previous nodes by their derivatives
	// (this will act as their dC/da, or the amount that they should change).
	// a = this node's value, A = one of the previous node's value, C = cost function,
	// z = this node's value before sigmoid is applied, w = one of this node's weights, b = this node's bias
	// dz/dw = A, dz/db = 1, dz/dA = w
	// dC/db = dz/db * da/dz * dC/da
	// dC/dw = dz/dw * da/dz * dC/da
	// dC/dA = dz/dA * da/dz * dC/da
	public void findDerivs ()
	{
		//dC/db = dz/db * da/dz * dC/da = 1 * da/dz * dC/da
		float dCdb = getdadz() * dCda;
		//increment sumdCdb by dC/db
		sumdCdb += dCdb;
		
		//change the weight and dCda for each input node
		for (int i = 0; i < inputNodes.length; i++)
		{
			// dC/dA = dz/dA * da/dz * dC/da = w * dC/db
			//increment the previous node's dCda
			inputNodes[i].dCda += weights[i] * dCdb;
			
			//dC/dw = dz/dw * da/dz * dC/da = A * dC/db
			//increment sumdCdw by dC/dw
			sumdCdw[i] += inputNodes[i].myVal * dCdb;
		}
	}
	
	//once the derivatives have been calculated, adjust the weights and biases by the negative average of their respective
	//derivatives. Afterwards, clear both sumdCdb and sumdCdw to prepare for the new batch.
	public void adjustVals ()
	{
		bias -= sumdCdb / AIHandwritingProject.getBatchSize();
		sumdCdb = 0f;
		for (int i = 0; i < sumdCdw.length; i++)
		{
			weights[i] -= sumdCdw[i] / AIHandwritingProject.getBatchSize();
			sumdCdw[i] = 0f;
		}
	}
	
	public String toString()
	{
		return "" + myVal;
	}
	
	//write the weights and bias of the node to the file
	public void writeSave(DataOutputStream inStream) throws IOException
	{
		for (int i = 0; i < weights.length; i++)
		{
			inStream.writeFloat(weights[i]);
		}
		inStream.writeFloat(bias);
	}

}