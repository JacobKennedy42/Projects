/**
 * @(#)OrangeGhost.java
 *
 *
 * @author 
 * @version 1.00 2016/8/19
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

//Ghost that moves randomly once near the player
public class OrangeGhost extends Ghost
{
	//test
	private Pixel tempPixel;
	private Color tempColor;

    public OrangeGhost() 
    {
    	super();
    }
    
    public OrangeGhost(int inX, int inY, JPanel inPixel) throws InterruptedException
    {
    	super (inX, inY, inPixel);
    }
    
    public void Move() throws InterruptedException
    {
		//This creates path towards the player, updating to the player's change of position (very inefficient
		// for now).
    	while (path.size() != 0)
    	{
    		path.remove(0);
    	}
    	
    	//If within 8 pixels of the player, move randomly
    	if (pos.GetX() - player.getPos().GetX() < 4 &&
    		player.getPos().GetX() - pos.GetX() < 4 &&
    		pos.GetY() - player.getPos().GetY() < 4 &&
    		player.getPos().GetY() - pos.GetY() < 4)
    	{
    		path.add(RandomMove());
    	}
    	
    	//Otherwise
    	else
    	{
    		CreatePath(player);
    	}
    		
		TimeUnit.MILLISECONDS.sleep(200);
		
		if (path.size() != 0)
		{
			//Move towards the target. (via replacement)
    		SwitchPixels();
		}
		
		/*
		//test
		if (path.size() != 0)
		{
			if (tempColor == null)
			{
				tempColor = Color.black;
				tempPixel = path.get(0);
			}
			
			tempPixel.setColor(tempColor);
			tempPixel = path.get(0);
			tempColor = tempPixel.getColor();
			tempPixel.setColor(Color.green);
		}
		*/
    }
    
    //moves randomly in a direction that has no wall
    public Pixel RandomMove () throws InterruptedException
    {
    	ArrayList<String> options = CheckSides();
    	int rand = (int)(Math.random() * options.size());
    	String dir = "";
    	
    	dir = options.get(rand);
    	
    	if (dir.equals("up"))
    	{
    		return PacMan.grid.get(pos.GetY() - 1).get(pos.GetX());
    	}
    	
    	else if(dir.equals("down"))
    	{
    		return PacMan.grid.get(pos.GetY() + 1).get(pos.GetX());
    	}
    	
    	else if(dir.equals("left"))
    	{
    		return PacMan.grid.get(pos.GetY()).get(pos.GetX() - 1);
    	}
    	
    	else if(dir.equals("right"))
    	{
    		return PacMan.grid.get(pos.GetY()).get(pos.GetX() + 1);
    	}
    	
    	return null;
    }
    
    public ArrayList<String> CheckSides ()
    {
    	ArrayList<String> options = new ArrayList<String>();
    	
    	//left side if possible
    	if (pos.GetX() > 0 &&
    		(PacMan.grid.get(pos.GetY()).get(pos.GetX() - 1).getColor().equals(Color.black) ||
    		 PacMan.grid.get(pos.GetY()).get(pos.GetX() - 1).getColor().equals(Color.white)))
    	{
    		options.add("left");
    	}
    	
    	//right side if possible
    	if (pos.GetX() < PacMan.SIZEX - 1 &&
    		(PacMan.grid.get(pos.GetY()).get(pos.GetX() + 1).getColor().equals(Color.black) ||
    		 PacMan.grid.get(pos.GetY()).get(pos.GetX() + 1).getColor().equals(Color.white)))
    	{
    		options.add("right");
    	}
    	
    	//upper side if possible
    	if (pos.GetY() > 0 &&
    		(PacMan.grid.get(pos.GetY() - 1).get(pos.GetX()).getColor().equals(Color.black) ||
    	     PacMan.grid.get(pos.GetY() - 1).get(pos.GetX()).getColor().equals(Color.black)))
    	{
    		options.add("up");
    	}
    	
    	//lower side if possible
    	if (pos.GetY() < PacMan.SIZEY - 1 && 
    		(PacMan.grid.get(pos.GetY() + 1).get(pos.GetX()).getColor().equals(Color.black) ||
    	     PacMan.grid.get(pos.GetY() + 1).get(pos.GetX()).getColor().equals(Color.black)))
    	{
    		options.add("down");
    	}
    	
    	return options;
    }
}