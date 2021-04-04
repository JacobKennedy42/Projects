/**
 * @(#)Card.java
 *
 *
 * @author 
 * @version 1.00 2016/5/21
 */


public class Card 
{
	String suit;
	int val;
	
	public Card()
	{
		suit = "";
		val = -1;
	}
	
    public Card(String inSuit, int inVal) 
    {
    	suit = inSuit;
    	val = inVal;
    }
    
    
    public String GetSuit()
    {
    	return suit;
    }
    
    public int GetVal()
    {
    	return val;
    }
    
    public String Print()
    {
    	return GetSuit() + GetVal();
    }
    
}