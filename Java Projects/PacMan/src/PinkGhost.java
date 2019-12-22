/**
 * @(#)PinkGhost.java
 *
 *
 * @author 
 * @version 1.00 2016/8/1
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

//Ghost that moves to the location that the player is heading
public class PinkGhost extends Ghost
{
	//test
	private Pixel tempPixel;
	private Color tempColor;
	
	//the direction that the player is facing (default up).
	private Coord playerDir = new Coord (0, 1);
	
	//The last position that the player was in.
	private Coord lastPlayerPos;

    public PinkGhost() 
    {
    	super();
    }
    
    public PinkGhost(int inX, int inY, JPanel inPixel) throws InterruptedException
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
    		
    	//CreatePath(player);
    	CreatePath (FindPlayerDest());
    		
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
    
    //Find the destination that the player is heading towards.
    public Pixel FindPlayerDest ()
    {
    	
    	//Find the direction that the player is heading (default up if it hasn't moved).
    	if (lastPlayerPos == player.getPos())
    	{
    		playerDir.Set(0, 1);
    	}
    	
    	else
    	{
    		playerDir = player.getPos().Minus(lastPlayerPos);
    		lastPlayerPos = player.getPos();
    	}
    	
    	//find the pixel at the end of the player's directional path.
    	
    	Pixel currentPixel = player;
    	Coord nextCoord = currentPixel.getPos().Plus(playerDir);
    	Pixel nextPixel = PacMan.grid.get(nextCoord.GetY()).get(nextCoord.GetX());
    	
    	while (!nextPixel.getColor().equals(Color.blue))
    	{
    		currentPixel = nextPixel;
    		nextCoord = currentPixel.getPos().Plus(playerDir);
    		
    		//if the next pixel exists, evaluate it.
    		if ((nextCoord.GetX() > -1 && nextCoord.GetX() < PacMan.SIZEX) &&
    			(nextCoord.GetY() > -1 && nextCoord.GetY() < PacMan.SIZEY))
    		{
    			nextPixel = PacMan.grid.get(nextCoord.GetY()).get(nextCoord.GetX());	
    		}
    		
    		//otherwise, set the current pixel as the destination.
    		else
    		{
    			return currentPixel;
    		}
    	}
    	
    	return currentPixel;
    }
    
    public void LockOnPlayer() throws InterruptedException
	{
		super.LockOnPlayer();
		lastPlayerPos = player.getPos();
	}
}