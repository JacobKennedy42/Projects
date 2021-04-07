/**
 * @(#)Coord.java
 *
 *
 * @author 
 * @version 1.00 2016/6/19
 */


public class Coord 
{
	private int x;
	private int y;
	
    public Coord() 
    {
    	x = 0;
    	y = 0;
    }
    
    public Coord(int inX, int inY)
    {
    	x = inX;
    	y = inY;
    }
    
    public void SetX(int inX)
    {
    	x = inX;
    }
    
    public void SetY(int inY)
    {
    	y = inY;
    }
    
    public void Set (int inX, int inY)
    {
    	x = inX;
    	y = inY;
    }
    
    public void YUp ()
    {
    	y++;
    }
    
    public void YDown ()
    {
    	y--;
    }
    
    public void XUp ()
    {
    	x++;
    }
    
    public void XDown ()
    {
    	x--;
    }
    
    public int GetX()
    {
    	return x;
    }
    
    public int GetY()
    {
    	return y;
    }
    
    public String toString ()
    {
    	return "(" + x + "," + y + ")";
    }
    
    public Coord Plus (Coord other)
    {
    	Coord sum = new Coord (x + other.GetX(), y + other.GetY());
    	return sum;
    }
    
    public Coord Minus (Coord other)
    {
    	Coord diff = new Coord (x - other.GetX(), y - other.GetY());
    	return diff;
    }
}