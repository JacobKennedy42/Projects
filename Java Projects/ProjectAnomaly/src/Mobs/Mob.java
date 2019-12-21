package Mobs;

import java.awt.Graphics2D;

import java.lang.Math;

import Game.Game;
import static Game.Game.Direction;
import Game.GameObject;
import Game.Surface;
//import Game.Surface.DisplayElement;
import Tiles.Tile;

//mob class
public abstract class Mob extends GameObject
{	
//	//general mob type
//	public static final int MOB_TYPE = 1;
//	//mob types (potentially a lot of finals)
//	public static final int PLAYER_TYPE; public static final int ZOMBIE_TYPE;
//	
//	//initialize the mob types
//	static
//	{
//		int i = 0;
//		PLAYER_TYPE = i++;
//		ZOMBIE_TYPE = i++;
//	}
//	
//	//the type of mob this is
//	protected int type;
//	
//	//1 = left, 2 = up, 3 = right, 4 = down
//	protected int rotation;
	protected Direction rotation;
	
	//The amount the mob must wait per move
	protected int timePerMove;
	//The time at which the mob may move again
	protected long nextMoveTime;
	
	//The mob's stats
	protected int health;
	
	public Mob ()
	{
		this(0, 0, 1, 1);
		
//		super();
//		setRotation (4);
//		
//		Game.tiles.get(y).get(x).setMob(this);
	}
	
	public Mob (int inX, int inY, int inWidth, int inHeight)
	{
		this(inX, inY, inWidth, inHeight, 1);
	}
	
	public Mob (int inX, int inY, int inWidth, int inHeight, int inHealth)
	{
		super(inX, inY, inWidth, inHeight);
		
		setRotation (Direction.UP);
		
		health = inHealth;
		
//		Game.tiles.get(y).get(x).setMob(this);
		//TODO: if glowRange > 0, tempTile.addLightSource(this)
	}
	
	//Move the mob in a given direction
//	public void move (int inDirection)
//	{	
//		//show that the mob is no longer within the old tile
//		Game.tiles.get(y).get(x).setMob(null);
//		
//		//move left
//		if (inDirection == 1)
//		{
//			if (x > 0 && 
//	        	!Game.tiles.get(y).get(x - 1).isSolid() &&
//	        	Game.tiles.get(y).get(x - 1).getMob() == null)
//	    		{
//	    			x--;
//	    			for (DisplayElement element: displayElements)
//	    			{
//	    				element.moveHorizontally(-1);
//	    			}
//	    			
//	    			nextMoveTime = Game.clock + timePerMove;
//	    		}
//		}
//		
//		//move up
//		else if (inDirection == 2)
//		{
//			if (y > 0 && 
//	    		!Game.tiles.get(y - 1).get(x).isSolid() &&
//	        	Game.tiles.get(y - 1).get(x).getMob() == null)
//    		{
//    			y--;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveVertically(-1);
//    			}
//    			
//    			nextMoveTime = Game.clock + timePerMove;
//    		}
//		}
//		
//		//move right
//		else if (inDirection == 3)
//		{
//    		if (x < Game.BOARD_SIZE - 1 && 
//            	!Game.tiles.get(y).get(x + 1).isSolid() &&
//	        	Game.tiles.get(y).get(x + 1).getMob() == null)
//    		{
//    			x++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveHorizontally(1);
//    			}
//    			
//    			nextMoveTime = Game.clock + timePerMove;
//    		}
//		}
//		
//		//move down
//		else if (inDirection == 4)
//		{
//    		if (y < Game.BOARD_SIZE - 1 && 
//				!Game.tiles.get(y + 1).get(x).isSolid() &&
//	        	Game.tiles.get(y + 1).get(x).getMob() == null)
//    		{
//    			y++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveVertically(1);
//    			}
//    			
//    			nextMoveTime = Game.clock + timePerMove;
//    		}
//		}
//		
//		//show that the mob is now within the new tile
//		Game.tiles.get(y).get(x).setMob(this);
//	}
	
	//Move the mob in a given direction. returns whether or not the mob actually moved
//	public boolean move (int inDirection)
	public boolean move (Direction inDirection)
	{
		boolean output = false;
		
		//get the tile the player is currently on and the tile in front of the player
		Tile currentTile = Game.tiles.get(y).get(x);
		Tile frontTile = getAdjacentTile(inDirection);

		//show that the player is no longer within the old tile
		currentTile.setMob(null);
		
		//only walk forward if there is a tile in front of the player and that tile is walkable
		if (frontTile != null && frontTile.isWalkable())
		{
			//move left
			if (inDirection == Direction.LEFT)
			{
    			x--;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.move(-1, 0);
//    			}
			}
			
			//move up
			else if (inDirection == Direction.UP)
			{		
    			y--;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.move(0, -1);
//    			}
			}
			
			//move right
			else if (inDirection == Direction.RIGHT)
			{
    			x++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.move(1, 0);
//    			}
			}
			
			//move down
			else if (inDirection == Direction.DOWN)
			{
    			y++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.move(0, 1);
//    			}
			}	
		
			if (glowRange > 0)
			{
				currentTile.removeLightSource(this);
				frontTile.addLightSource(this);
			}

			nextMoveTime = Game.clock + timePerMove;		
			output = true;
		}
//		System.out.println(" move time: " + nextMoveTime);
//		System.out.println(" clock:     " + Game.clock);
		
		
//		System.out.println("next move time in " + (nextMoveTime - Game.clock));
		
//		System.out.println(" move time: " + nextMoveTime);
//		System.out.println(" clock:     " + Game.clock);
		
		//show that the player is now within the new tile
		Game.tiles.get(y).get(x).setMob(this);
		
//		System.out.println("next move time in " + (nextMoveTime - Game.clock));
//		System.out.println(" move time: " + nextMoveTime);
//		System.out.println(" clock:     " + Game.clock);
		
		return output;
	}
	
	//Set the mob's rotation (in radians, starting upward and going clockwise)
//	public void setRotation (int inDirection)
	public void setRotation (Direction inDirection)
	{	
		//TODO
//		//rotate the image
//		if (rotation != null)
//		{
//			image = Surface.rotateImage(rotation.val(), inDirection.val(), image);
//		}
//		else
//		{
//			image = Surface.rotateImage(0, inDirection.val(), image);
//		}
		
		rotation = inDirection;
	}
	
	//return the mob's rotation
//	public int getRotation ()
	public Direction getRotation ()
	{
		return rotation;
	}
	
//	//set the mob's type and adjust variables accordingly
//	public void setType (int inType)
//	{
//		displayElements = Surface.createDisplayList(1, inType, x, y, width, height);
//		
//		//Player = 0
//		if (inType == 0)
//		{
//			isSolid = true;
//			timePerMove = 250000000;
//			glowRange = 2;
//		}
//		
//		//Zombie = 1
//		if (inType == 1)
//		{
//			isSolid = true;
//			timePerMove = 250000000;
//		}
//	}
	    	
	//return the time needed per move
	public int getTimePerMove ()
	{
		return timePerMove;
	}
	
	//Return the ticks until the next move
	public long getNextMoveTime ()
	{
		return nextMoveTime;
	}
	
	//damage the mob by inDamage amount
	public void damage (int inDamage)
	{
		health -= inDamage;
		
		//if health is <= 0, kill the mob
		if (health <= 0)
		{
			die();
		}
	}
	
	//makes the mob go away forever ='(
	protected void die ()
	{
		//get rid of this mob's references
		Tile currentTile = Game.tiles.get(y).get(x); 
		currentTile.setMob(null);
		Game.mobs.remove(this);
		currentTile.removeLightSource(this);
	}
	
	//return the tile in front of the mob
 	public Tile findFrontTile ()
	{
		return getAdjacentTile (rotation);
		
//		//Note to self: don't use &&'s. If for example rotation = 1, but x < 0, then you want it to end, not go to the next if
//		//statement
//		if (rotation == 1)
//		{
//			if (x > 0)
//			{
//				return Game.tiles.get(y).get(x - 1);
//			}
//		}
//		
//		else if (rotation == 2)
//		{
//			if (y > 0)
//			{
//				return Game.tiles.get(y - 1).get(x);
//			}
//		}
//		
//		else if (rotation == 3)
//		{
//			if (x < Game.BOARD_SIZE - 1)
//			{
//				return Game.tiles.get(y).get(x + 1);
//			}
//		}
//		
//		else if (rotation == 4)
//		{
//			if (y < Game.BOARD_SIZE - 1)
//			{
//				return Game.tiles.get(y + 1).get(x);
//			}
//		}
//		
//		return null;
	}
	
//	//return the adjacent tile in a given direction
//	public Tile getAdjacentTile (int inDirection)
//	{
//		//Note to self: don't use &&'s. If for example rotation = 1, but x < 0, then you want it to end, not go to the next if
//		//statement
//		if (inDirection == 1)
//		{
//			if (x > 0)
//			{
//				return Game.tiles.get(y).get(x - 1);
//			}
//		}
//		
//		else if (inDirection == 2)
//		{
//			if (y > 0)
//			{
//				return Game.tiles.get(y - 1).get(x);
//			}
//		}
//		
//		else if (inDirection == 3)
//		{
//			if (x < Game.BOARD_SIZE - 1)
//			{
//				return Game.tiles.get(y).get(x + 1);
//			}
//		}
//		
//		else if (inDirection == 4)
//		{
//			if (y < Game.BOARD_SIZE - 1)
//			{
//				return Game.tiles.get(y + 1).get(x);
//			}
//		}
//		
//		return null;
//	}
	
	//Places a block in front of the mob
	//returns true if the block is placed and false if not
	public boolean placeBlock (Tile inTile)
	{	
		Tile tempTile = findFrontTile();
		
		//If there is a non-solid tile, place the block
		if (tempTile != null && !tempTile.isSolid() && tempTile.getMob() == null)
		{
			Game.placeTile(inTile, tempTile.getX(), tempTile.getY());
			return true;
		}
		
		return false;
	}
	
	//what the mob does when the clock tick updates it (on some mobs like the Player, this does nothing)
	public abstract void update ();
	
	@Override
	public boolean use (Mob inMob)
	{
		//do nothing (assuming mobs can't be kept in the inventory, though that may change =) )
		return false;
	}
	
	@Override
	//draw the mob
	public void draw (Graphics2D g2d)
	{
		//If the mob is lit, draw it
		if (glowRange > 0 || Game.tiles.get(y).get(x).getLightSources().size() > 0)
		{
//			super.draw(g2d);
			int convertedX = x*Game.tileSize;
			int convertedY = y*Game.tileSize;
			int convertedW = width*Game.tileSize;
			int convertedH= height*Game.tileSize;
			g2d.drawImage(getImage(), convertedX, convertedY, convertedX + convertedW, convertedY + convertedH,
					(int)(getImage().getWidth() * rotation.val()), 0, (int) (getImage().getWidth() * (rotation.val() + Direction.INTERVAL)), image.getHeight(), null);
		}
	}
	
	@Override
	public void drawInInv(Graphics2D g2d)
	{
		int convertedX = x*Game.tileSize;
		int convertedY = y*Game.tileSize;
		int convertedW = width*Game.tileSize;
		int convertedH= height*Game.tileSize;
		g2d.drawImage(getImage(), convertedX, convertedY, convertedX + convertedW, convertedY + convertedH,
				(int)(getImage().getWidth() * rotation.val()), 0, (int) (getImage().getWidth() * (rotation.val() + Direction.INTERVAL)), image.getHeight(), null);
	}
}
