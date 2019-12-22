/**
 * @(#)Line.java
 *
 *
 * @author 
 * @version 1.00 2016/8/10
 */

import java.lang.Double;

//An infinite line
public class Line 
{
	private double slope;
	private double yInt;
	
	//For vertical lines
	private double xInt;

    public Line() 
    {
    	slope = 1;
    	yInt = 0;
    }
    
    public Line (double inSlope, double inYInt)
    {
    	slope = inSlope;
    	yInt = inYInt;
    }
    
    public Line (Coord one, Coord two)
    {		
    	//distance formula (adjusted to allow vertical lines with undefined slopes)
    	if (one.GetX() - two.GetX() != 0)
    	{
    		//distance formula
    		slope = ((double) one.GetY() - two.GetY()) / ((double) one.GetX() - two.GetX());
    		
	    	//y - mx = b
	    	yInt = one.GetY() - (slope * one.GetX());
    	}
    	
    	//if vertical...
    	else
    	{
    		slope = Double.MAX_VALUE;
    		xInt = one.GetX();
    	}
    }
    
    public void SetLine (double inSlope, double inYInt)
    {
    	slope = inSlope;
    	yInt = inYInt;
    }
    
    public void SetLine (Coord one, Coord two)
    {
    	if (one.GetX() - two.GetX() != 0)
    	{
	    	//distance formula
	    	slope = ((double) one.GetY() - two.GetY()) / ((double) one.GetX() - two.GetX());
	    	
	    	//b = y - mx
	    	yInt = one.GetY() - (slope * one.GetX());
    	}
    	
    	//if vertical...
    	else
    	{
    		slope = Double.MAX_VALUE;
    		xInt = one.GetX();
    	}
    }
    
    public double GetSlope ()
    {
    	return slope;
    }
    
    public void SetSlope (double inSlope)
    {
    	slope = inSlope;
    }
    
    public double GetY (int x)
    {
    	if (slope == Double.MAX_VALUE)
    	{
    		return Double.MAX_VALUE;
    	}
    	
    	//y = mx + b
    	return (slope * x) + yInt;
    }
    
    public double GetX (int y)
    {
    	if (slope == Double.MAX_VALUE)
    	{
    		return xInt;
    	}
    	
    	//x = (y - b) / m
    	return (y - yInt) / slope;
    }
    
    public String toString ()
    {
    	if (slope == Double.MAX_VALUE)
    	{
    		return "x = " + xInt;
    	}
    	
    	else if (yInt < 0)
    	{
    		double tempInt = yInt * -1;
    		return "y = " + slope + "x - " + tempInt;
    	}
    	
    	return "y = " + slope + "x + " + yInt;
    }
}