package Game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//import Game.Surface.DisplayElement;
import static Game.Game.Direction;
import static Game.Surface.*;
import Mobs.Mob;
import Tiles.Tile;

//class for the general game elements
public abstract class GameObject
{	
//	//The display elements that are displayed when this game element is drawn.
//	protected ArrayList<DisplayElement> displayElements;
	
	//the image of the gameObject that is displayed
	protected BufferedImage image;
	
	protected boolean isSolid;
	//Does this object leave your inventory when you use it?
	protected boolean isConsumable;
	
	//these are measured in tile units, not panel tiles
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	//How far this object's light reaches (if it does glow)
	protected int glowRange;
	
	public GameObject ()
	{
		this(0, 0, 1, 1);
		
//		x = 0;
//		y = 0;
//		width = 1;
//		height = 1;
//		
//		setDisplay();
	}
	
	public GameObject (int inX, int inY)
	{
		this(inX, inY, 1, 1);
		
//		x = inX;
//		y = inY;
//		width = 1;
//		height = 1;
//		
//		setDisplay();
	}
	
	public GameObject (int inX, int inY, int inWidth, int inHeight)
	{
		x = inX;
		y = inY;
		width = inWidth;
		height = inHeight;
	
		initImage();
//		setDisplay();
	}
	
	//Set the starting image (by default, <classname>.png)
	protected void initImage ()
	{
		image = images.get(getClass().getSimpleName() + ".png");
	}
	
//	//change the size of the tile (used for zooming in or out)
//	public void resize (float scaleFactor)
//	{	
//		for (DisplayElement element: displayElements)
//		{
//			element.resize(scaleFactor);
//		}
//	}
	
//	//returns the general type of the object (Tile, mob, etc)
//	public abstract int getSuperType();
//	
//	//the type of the object
//	public abstract int getType ();
	
//	//method that changes the tile's display elements based on it's type
//	protected void setDisplay ()
//	{	
//		//Make the object look like the object it's supposed to look like
//		displayElements = Surface.createDisplayList(getSuperType(), getType(), x, y, width, height);
//	}
	
//	protected abstract void setDisplay();
	
	public boolean isConsumable ()
	{
		return isConsumable;
	}
	
	public boolean isSolid ()
	{
		return isSolid;
	}
	
	public int getX()
	{
		return x;
	}
	
	public void setX(int inX)
	{
		x = inX;
//		setDisplay();
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setY(int inY)
	{
		y = inY;
//		setDisplay();
	}
	
	public int getWidth ()
	{
		return width;
	}
	
	public int getHeight ()
	{
		return height;
	}
	
	//set both x and y (a little more efficient than doing setX then setY)
	public void setPos (int inX, int inY)
	{
		x = inX;
		y = inY;
//		setDisplay();
	}
	
	public int getGlowRange()
	{
		return glowRange;
	}
	
	//return the adjacent tile in a given direction (in radians)
//	public Tile getAdjacentTile (int inDirection)
	public Tile getAdjacentTile (Direction inDirection)
	{
		//Note to self: don't use &&'s. If for example rotation = 1, but x < 0, then you want it to end, not go to the next if
		//statement
		if (inDirection == Direction.LEFT)
		{
			if (x > 0)
			{
				return Game.tiles.get(y).get(x-1);
			}
		}
		
		else if (inDirection == Direction.UP)
		{
			if (y > 0)
			{
				return Game.tiles.get(y-1).get(x);
			}
		}
		
		else if (inDirection == Direction.RIGHT)
		{
			if (x < Game.BOARD_SIZE - 1)
			{
				return Game.tiles.get(y).get(x+1);
			}
		}
		
		else if (inDirection == Direction.DOWN)
		{
			if (y < Game.BOARD_SIZE - 1)
			{
				return Game.tiles.get(y+1).get(x);
			}
		}
		
		return null;
	}
	
	//what the element drops when you destroy it (like when you break a block or kill a mob)
	public abstract GameObject drop();
	
	//what the element does when you try to use it in your inventory.
	//Takes in the mob that tries to use it, (weapons need mob position, potions need mob health, etc.).
	//returns true if the object was successfully used, false if otherwise
	public abstract boolean use(Mob inMob);
	
	//return the filepath to the images folder
	protected String getImageDir ()
	{
		StringBuilder output = new StringBuilder();
		Class current = getClass();
		while (!current.getSimpleName().equals("GameObject"))
		{
			output.insert(0, "/" + current.getSimpleName());
			current = current.getSuperclass();
		}
		output.insert(0, "Images");
		return output.toString();
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void draw (Graphics2D g2d)
	{
//		for (DisplayElement element: displayElements)
//		{
//			element.draw(g2d);
//		}
		g2d.drawImage(getImage(), x * Game.tileSize, y * Game.tileSize, width * Game.tileSize, height * Game.tileSize,null);
	}
	
	//How the object is drawn when in an inventory (by default, work's identical to draw, though I wish I could just call the GamObject class's draw() without overriding)
	public void drawInInv(Graphics2D g2d)
	{
		g2d.drawImage(getImage(), x * Game.tileSize, y * Game.tileSize, width * Game.tileSize, height * Game.tileSize,null);
	}

	
//	//returns if this game object is the same type as a given object
//	public boolean sameTypeAs (GameObject inObj)
//	{
//		return inObj != null && getSuperType() == inObj.getSuperType() && getType() == inObj.getType();
//	}

//	//returns if the input object is the same class as this object
//	public boolean isSameClass (Object obj)
//	{
//		//TODO: test to make sure the inheritence works as expected with this
//		
//		//check if the object is an instance of the same class
//		Class c = this.getClass();
//		return c.isInstance(obj);
//	}
}