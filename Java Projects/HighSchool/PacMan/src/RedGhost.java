/**
 * @(#)RedGhost.java
 *
 *
 * @author 
 * @version 1.00 2016/7/26
 */
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

//Ghost that chases directly after the player.
public class RedGhost extends Ghost
{
	//test
	private Pixel tempPixel;
	private Color tempColor;
	
	public RedGhost ()
	{
		super();
	}

    public RedGhost(int inX, int inY, JPanel inPixel) throws InterruptedException
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
    		
    	CreatePath(player);
    		
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
}