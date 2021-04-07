/**
 * @(#)BlueGhost.java
 *
 *
 * @author 
 * @version 1.00 2016/8/10
 */
import javax.swing.*;
import java.awt.*;
import java.lang.Double;
import java.util.concurrent.TimeUnit;

//Ghost that tries to get on the other side of pacman (depending on where the red ghost is)
public class BlueGhost extends Ghost
{
	private RedGhost redGhost;
	//test
	private Pixel tempPixel;
	private Color tempColor;
	
    public BlueGhost() 
    {
    	super();
    }
    
    public BlueGhost(int inX, int inY, JPanel inPixel) throws InterruptedException
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
    		
    	CreatePath(FindFlank());
    		
		TimeUnit.MILLISECONDS.sleep(200);
		
		if (path.size() != 0)
		{
			//Move towards the target. (via replacement)
    		SwitchPixels();
		}
		
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
		
    }
    
    //Finds the side of the player opposite of the red ghost (flanks the player)
    public Pixel FindFlank ()
    {
    	//Create a line from the red ghost to the player;
    	Line line = new Line (redGhost.getPos(), player.getPos());
    	
    	//return the point that is on the other side of the player (line segment from red to destination, 
    	//with the player as a mid point). Makes sure that the destination pixel is in the grid and is an
    	//open space (the search algorithm may not work extremely accuratly. May be replaced later).
    	int tempX = (player.getPos().GetX() - redGhost.getPos().GetX()) + player.getPos().GetX();
    	
    	if (tempX < 0)
    	{
    		tempX = 0;
    	}
    	
    	else if (tempX >= PacMan.SIZEX)
    	{
    		tempX = PacMan.SIZEX - 1;
    	}
    	
    	int tempY = (int) line.GetY(tempX);
    	
    	if (tempY < 0)
    	{
    		tempY = 0;
    	}
    	
    	else if (tempY >= PacMan.SIZEY)
    	{
    		tempY = PacMan.SIZEY - 1;
    	}
    	
    	//destination so far (before making sure that it is not a wall or Mob)
    	Pixel currentDest = PacMan.grid.get(tempY).get(tempX);
    	
    	//if the current destination is a wall or mob
    	if (!currentDest.getColor().equals(Color.black) &&
    		!currentDest.getColor().equals(Color.white))
    	{
    		currentDest = currentDest.Search(Color.black, Color.white, PacMan.SIZEY);
    	}
    	
    	return currentDest;
    }
    
    public void LockOnPlayer() throws InterruptedException
    {
    	super.LockOnPlayer();
    	redGhost = (RedGhost) Search (Color.red, PacMan.SIZEY);
    }
}