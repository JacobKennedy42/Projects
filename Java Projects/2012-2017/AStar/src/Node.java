/**
 * @(#)Node.java
 *
 *
 * @author 
 * @version 1.00 2016/6/30
 */
 import javax.swing.*;
 import java.awt.*;
 import java.lang.Double;

public class Node 
{
	//The node's display panel
	private JPanel myPixel;
	
	//Where the node came from
	private Node cameFrom;
	
	//How far the node is from the start.
	private double gScore;
	
	//How long the path from start to goal that crosses this node is
	private double fScore = Double.MAX_VALUE;
	
	//Coordinate position of the node
	private Coord pos;
	
	public Node()
	{
		
	}
	
    public Node(JPanel inPixel, Coord inCoord) 
    {
    	myPixel = inPixel;
    	pos = inCoord;
    }
    
    public String getColorString()
    {
    	return myPixel.getBackground().toString();
    }
    
    public Color getColor()
    {
    	return myPixel.getBackground();
    }
    
    public void setColor (Color color)
    {
    	myPixel.setBackground(color);
    }
    
    public Node getCameFrom ()
    {
    	return cameFrom;
    }
    
    public void setCameFrom (Node inNode)
    {
    	cameFrom = inNode;
    }
    
    public void setGScore (double score)
    {
    	gScore = score;
    }
    
    public double getGScore()
    {
    	return gScore;
    }
    
    public void setFScore (double score)
    {
    	fScore = score;
    }
    
    public double getFScore()
    {
    	return fScore;
    }
    
    public int getX()
    {
    	return pos.GetX();
    }
    
    public int getY()
    {
    	return pos.GetY();
    }
    
    public String getPosString ()
    {
    	return pos.toString();
    }
}