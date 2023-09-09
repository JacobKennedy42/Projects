/**
 * @(#)Ghost.java
 *
 *
 * @author 
 * @version 1.00 2016/7/26
 */
import javax.swing.*;
import java.awt.*;

public abstract class Ghost extends Mob
{
	protected Player player;
	
	//The color of the pixel the ghost is sitting upon.
	Color spaceColor = Color.black;
	
    public Ghost()
    {
    	super ();
    }
    
    public Ghost (int inX, int inY, JPanel inPixel) throws InterruptedException
    {
    	super(inX, inY, inPixel);
    }
    
    //move the mob
    public abstract void Move() throws InterruptedException;
    
    //Switches two adjacent pixels (override the mob SwitchPixels method to account for the spaceColor).
	public void SwitchPixels ()
	{
		//The adjacent pixel
		Pixel pathPixel = path.remove(path.size() - 1);
    	
    	//Switch the two Pixels' positions on the grid.
    	PacMan.grid.get(getPos().GetY()).set(getPos().GetX(), pathPixel);
    	PacMan.grid.get(pathPixel.getPos().GetY()).set(pathPixel.getPos().GetX(), this);
    	
    	//Switch the two pixel's JPanels
    	JPanel tempPixel = pathPixel.getPixel();
    	Color tempColor = tempPixel.getBackground();
    	Color myColor = getColor();
    	pathPixel.setPixel(getPixel());
    	setPixel(tempPixel);
    	setColor (myColor);
    	pathPixel.setColor (spaceColor);
    	spaceColor = tempColor;
    		    	
    	//Switch the two pixels' positions
    	Coord tempPos = pathPixel.getPos();
    	pathPixel.setPos(getPos());
    	setPos(tempPos);
	}
	
	public void LockOnPlayer() throws InterruptedException
	{
		player = (Player) Search(Color.yellow, PacMan.SIZEY);
	}
}