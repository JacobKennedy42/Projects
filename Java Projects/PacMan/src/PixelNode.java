/**
 * @(#)PixelNode.java
 *
 *
 * @author 
 * @version 1.00 2016/7/16
 */
 import javax.swing.*;
 import java.awt.*;
 import java.lang.Double;

//Pixel wrapper class (lets pixels behave like nodes while pathfinding)
public class PixelNode 
{
	//The pixel the node is wrapping
	private Pixel myPixel;
	
	//Where the node came from
	private PixelNode cameFrom;

	//How far the node is from the start.
	private double gScore;
	
	//How long the path from start to goal that crosses this node is
	private double fScore = Double.MAX_VALUE;
	
	//The total distance away the pixel is from any mobs.
	double totalDist;
	
	public PixelNode ()
	{
		
	}
	
    public PixelNode(Pixel inPixel) 
    {
    	myPixel = inPixel;
    }
    
    public PixelNode (Pixel inPixel, double inDist)
    {
    	myPixel = inPixel;
    	totalDist = inDist;
    }
    
    public String getColorString()
    {
    	return myPixel.getColor().toString();
    }
    
    public Color getColor()
    {
    	return myPixel.getColor();
    }
    
    public void setColor (Color color)
    {
    	myPixel.setColor(color);
    }
    
    public PixelNode getCameFrom ()
    {
    	return cameFrom;
    }
    
    public void setCameFrom (PixelNode inNode)
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
    
    public double getDist()
    {
    	return totalDist;
    }
    
    public void setDist (double inDist)
    {
    	totalDist = inDist;
    }
    
    public double getFScore()
    {
    	return fScore;
    }
    
    public int getX()
    {
    	return myPixel.getPos().GetX();
    }
    
    public int getY()
    {
    	return myPixel.getPos().GetY();
    }
    
    public String getPosString ()
    {
    	return myPixel.getPos().toString();
    }
    
    public Pixel getPixel()
    {
    	return myPixel;
    }
}