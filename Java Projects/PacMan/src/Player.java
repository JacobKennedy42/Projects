/**
 * @(#)Player.java
 *
 *
 * @author 
 * @version 1.00 2016/7/15
 */

//the player (pacman)
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.lang.Math;

public class Player extends Mob
{
    public Player() 
    {
    	super();
    }
    
    public Player (int inX, int inY, JPanel inPixel)
    {
    	super(inX, inY, inPixel);
    }
    
    public void Move() throws InterruptedException
    {
    	//CHANGE
    	Pixel ghost = Search(Color.red, RADIUS);
    	
    	if (ghost == null)
    	{
    		ghost = Search(Color.pink, RADIUS);
    	}
    	
    	if (ghost == null)
    	{
    		ghost = Search(Color.cyan, RADIUS);
    	}
    	
    	if (ghost == null)
    	{
    		ghost = Search(Color.orange, RADIUS);
    	}
    	
    	//if there are ghosts around, run away from them.
    	if (ghost != null)
    	{
    		//discard the previous path
    		while (path.size() != 0)
    		{
    			path.remove(0);
    		}
    		
    		//make an escape path
    		CreatePath(FindEscape(RADIUS, ghost));
    	}
    	
    	//if there is currently no path, make one to the nearest pellet.
    	else if (path.size() == 0)
    	{
    		//Search(Color.white, PacMan.SIZEY).setColor(Color.green);
    		CreatePath(Search(Color.white, PacMan.SIZEY));
    	}
		
		TimeUnit.MILLISECONDS.sleep(200);

		//Move towards the target. (via replacement) (make this method SwitchPixels)
		SwitchPixels();
    	
    	//System.out.println(pathPixel.getPos().toString());
    	
    	
    }
    
	//Find an escape point
	public Pixel FindEscape (int range, Pixel ghost)
	{
		//List of all evaluated pixels
		ArrayList<PixelNode> closedSet = new ArrayList<PixelNode>();
		
		//Evaluate each pixel in radius of the player
		for (int r = pos.GetY() - range; r <= pos.GetY() + range; r++)
		{
			for (int c = pos.GetX() - range; c <= pos.GetX() + range; c++)
			{
				//if the pixel tested exists in the grid...
				if ((r > -1 && r < PacMan.SIZEY) &&
					(c > -1 && c < PacMan.SIZEX))
				{
					Pixel current = PacMan.grid.get(r).get(c);
					double totalDist;
					
					//If the pixel tested is not a wall...
					if (current.getColor() != Color.blue)
					{
						//Evaluate the pixel by adding its distances from the player and the ghost
						totalDist = FindDist(current, this) + FindDist(current, ghost);
						
						//Add the evaluated node to the set
						closedSet.add(new PixelNode (current, totalDist));
					}
				}
			}
		}
		
		return FindFarthest(closedSet);
	}
	
	//Find the node in a list of nodes that is the farthest from a group of mobs
	public Pixel FindFarthest (ArrayList<PixelNode> set)
	{
		double farthestDist = 0;
		PixelNode currentFarthest = new PixelNode();
		
		for (PixelNode node : set)
		{
			if (node.getDist() > farthestDist)
			{
				farthestDist = node.getDist();
				currentFarthest = node;
			}
		}
		
		//currentFarthest.setColor(Color.green);
		
		return currentFarthest.getPixel();
	}
}