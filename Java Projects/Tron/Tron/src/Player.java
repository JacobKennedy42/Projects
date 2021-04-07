/**
 * @(#)Player.java
 *
 *
 * @author 
 * @version 1.00 2016/6/19
 */
 import javax.swing.*;
 import java.awt.*;
 import java.util.ArrayList;
 import java.lang.Math;

public class Player 
{
	private Color color;
	private Coord pos = new Coord();
	private ArrayList<Coord> trail = new ArrayList<Coord>();
	private int length = 3;
	private int counter = 0;
	private final int COUNTER_RATE = 50;

    public Player() 
    {
    	color = Color.white;
    	pos.Set(Tron.SIZE / 2, Tron.SIZE / 2);
    }
    
    public Player (Color inColor, Coord inPos)
    {
    	color = inColor;
    	pos = inPos;
    }
    
    public void Display()
    {
    	Tron.grid.get(pos.GetY()).remove(pos.GetX());
    	Tron.pane.remove(pos.GetX() + (pos.GetY() * Tron.SIZE));
    	
    	JPanel tempPixel = new JPanel();
    	tempPixel.setBackground(color);
    	
    	Tron.grid.get(pos.GetY()).add(pos.GetX(), tempPixel);
    	//System.out.println(pos.GetY());
    	Tron.pane.add(tempPixel, pos.GetX() + (pos.GetY() * Tron.SIZE));
    	
    	Tron.GUI.setVisible(true);
    }
    
    public boolean Move()
    {
    	UpdateTrail();
    	
    	ArrayList<String> options = CheckSides();
    	int rand = (int)(Math.random() * options.size());
    	String dir = "";
    	
    	if(options.size() == 0)
    	{
    		if(color.equals(Color.orange))
    		{
    			System.out.println("Hero Wins.");
    		}
    		
    		else
    		{
    			System.out.println("Villain Wins.");
    		}
    		
    		return false;
    	}
    	
    	dir = options.get(rand);
    	
    	if (dir.equals("up"))
    	{
    		pos.YDown();
    	}
    	
    	else if(dir.equals("down"))
    	{
    		pos.YUp();
    	}
    	
    	else if(dir.equals("left"))
    	{
    		pos.XDown();
    	}
    	
    	else if(dir.equals("right"))
    	{
    		pos.XUp();
    	}
    	
    	Display();
    	
    	return true;
    }
    
    public ArrayList<String> CheckSides ()
    {
    	ArrayList<String> options = new ArrayList<String>();
    	
    	//left side if possible
    	if (pos.GetX() > 0 && Tron.grid.get(pos.GetY()).get(pos.GetX() - 1).getBackground().equals(Color.black))
    	{
    		options.add("left");
    	}
    	
    	//right side if possible
    	if (pos.GetX() < Tron.SIZE - 1 && Tron.grid.get(pos.GetY()).get(pos.GetX() + 1).getBackground().equals(Color.black))
    	{
    		options.add("right");
    	}
    	
    	//upper side if possible
    	if (pos.GetY() > 0 && Tron.grid.get(pos.GetY() - 1).get(pos.GetX()).getBackground().equals(Color.black))
    	{
    		options.add("up");
    	}
    	
    	//lower side if possible
    	if (pos.GetY() < Tron.SIZE - 1 && Tron.grid.get(pos.GetY() + 1).get(pos.GetX()).getBackground().equals(Color.black))
    	{
    		options.add("down");
    	}
    	
    	return options;
    }
    
    public void UpdateTrail()
    {
    	trail.add(new Coord(pos.GetX(), pos.GetY()));
    	
    	if (trail.size() >= length)
    	{
    		Coord tempCoord = trail.remove(0);
    		
    		Tron.grid.get(tempCoord.GetY()).remove(tempCoord.GetX());
    		Tron.pane.remove(tempCoord.GetX() + (tempCoord.GetY() * Tron.SIZE));
    		
    		JPanel tempPixel = new JPanel();
    		tempPixel.setBackground(Color.black);
    		
    		Tron.grid.get(tempCoord.GetY()).add(tempCoord.GetX(), tempPixel);
	    	//System.out.println(pos.GetY());
	    	Tron.pane.add(tempPixel, tempCoord.GetX() + (tempCoord.GetY() * Tron.SIZE));
    	}
    	
    	counter++;
    	if (counter >= COUNTER_RATE)
    	{
    		counter = 0;
    		length++;
    	}
    }
}