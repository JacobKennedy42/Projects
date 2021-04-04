package Mobs;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.Color;

import Game.*;
import static Game.Game.Direction;
import static Game.Surface.*;
import Tiles.Tile;

//player class
public class Player extends Mob
{	
	//The player's inventory
	private Inventory inventory;
//	private ArrayList<InventorySlot> inventory = new ArrayList<InventorySlot>();
	
	//What inventory slot is currently selected
	private int currentSlot = 0;
	//The tile that was directly in front of the player after a move
	private Tile frontTile;
	
	public Player ()
	{
		this(0, 0, 1, 1);
		
//		super();
//		
//		isSolid = true;
//		timePerMove = 6;//250000000;
//		glowRange = 2;
//		
//		//set up the inventory
//		setUpInventory();
		
//		frontTile = findFrontTile();
	}
	
	public Player (int inX, int inY, int inWidth, int inHeight)
	{
		super(inX, inY, inWidth, inHeight);
		
		isSolid = true;
		timePerMove = 6;//250000000;
		glowRange = 2;
		
		inventory = new Inventory(10);
		
		//set up the inventory
//		setUpInventory();
		
//		frontTile = findFrontTile();
	}

//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayEllipse(this, 0, 0, 1, 1, Color.red));
//		displayElements.add(new DisplayEllipse(this, .125f, .75f, .25f, .25f, Color.black));
//		displayElements.add(new DisplayEllipse(this, .625f, .75f, .25f, .25f, Color.black));
//		
//		//TODO: delete this
//		float[] xPoints = {.25f, .5f, .75f};
//		float[] yPoints = {.25f, .75f, .25f};
//		displayElements.add(new DisplayPoly(xPoints, yPoints, Color.green));
//	}
	
	@Override
//	public boolean move (int inDirection)
	public boolean move (Direction inDirection)
	{	
		boolean output = super.move(inDirection);
		
		//only interact with the now-front block if the player actually moved from one tile to another
		if (output)
		{
			frontTile = super.findFrontTile();
//			System.out.println(getFrontTile());
//			System.out.println(inDirection);
		}
		
		return output;
	}
	
//	//Move the mob in a given direction
//	public void move (int inDirection)
//	{
//		//get the tile the player is currently on and the tile in front of the player
//		Tile currentTile = Game.tiles.get(y).get(x);
//		Tile frontTile = findFrontTile();
//
//		//show that the player is no longer within the old tile
//		currentTile.setMob(null);
//		
//		//only walk forward if there is a tile in front of the player and that tile is walkable
//		if (frontTile != null && frontTile.isWalkable())
//		{
//			//move left
//			if (inDirection == 1)
//			{
//    			x--;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveHorizontally(-1);
//    			}
//			}
//			
//			//move up
//			else if (inDirection == 2)
//			{		
//    			y--;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveVertically(-1);
//    			}
//			}
//			
//			//move right
//			else if (inDirection == 3)
//			{
//    			x++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveHorizontally(1);
//    			}
//			}
//			
//			//move down
//			else if (inDirection == 4)
//			{
//    			y++;
//    			for (DisplayElement element: displayElements)
//    			{
//    				element.moveVertically(1);
//    			}
//			}	
//		
//			if (glowRange > 0)
//			{
//				currentTile.removeLightSource(this);
//				frontTile.addLightSource(this);
//			}
//			
//			nextMoveTime = Game.clock + timePerMove;
//		}
//		
//		//show that the player is now within the new tile
//		Game.tiles.get(y).get(x).setMob(this);
//	}
	
	@Override
//	public void setRotation (int inDirection)
	public void setRotation (Direction inDirection)
	{
		if (rotation != inDirection)
		{
			super.setRotation(inDirection);
			frontTile = findFrontTile();
			
//			rotation = inDirection;
//			
//			//Make the eyes face left
//			if (inDirection == Direction.LEFT)
//			{
//				displayElements.get(1).setPos(x, y + (height / 8.0f));
//				displayElements.get(2).setPos(x, y + (5 * height / 8.0f));
//				
//				if (x > 0)
//				{
//					frontTile = Game.tiles.get(y).get(x-1);
//				}
//				else
//				{
//					frontTile = null;
//				}
//			}
//			
//			//Make the eyes face up
//			else if (inDirection == Direction.UP)
//			{
//				displayElements.get(1).setPos(x + (width / 8.0f), y);
//				displayElements.get(2).setPos(x + ((5 * width) / 8.0f), y);
//				
//				if (y > 0)
//				{
//					frontTile = Game.tiles.get(y-1).get(x);
//				}
//				else
//				{
//					frontTile = null;
//				}
//			}
//			
//			//Make the eyes face right
//			else if (inDirection == Direction.RIGHT)
//			{
//				displayElements.get(1).setPos(x + ((3 * width) / 4.0f), y + (height / 8.0f));
//				displayElements.get(2).setPos(x + ((3 * width) / 4.0f), y + (5 * height / 8.0f));
//				
//				if (x < Game.BOARD_SIZE - 1)
//				{
//					frontTile = Game.tiles.get(y).get(x + 1);
//				}
//				else
//				{
//					frontTile = null;
//				}
//			}
//			
//			//Make the eyes face down
//			else if (inDirection == Direction.DOWN)
//			{
//				displayElements.get(1).setPos(x + (width / 8.0f), y + ((3 * height) / 4.0f));
//				displayElements.get(2).setPos(x + ((5 * width) / 8.0f), y + ((3 * height) / 4.0f));
//				
//				if (y < Game.BOARD_SIZE - 1)
//				{
//					frontTile = Game.tiles.get(y + 1).get(x);
//				}
//				else
//				{
//					frontTile = null;
//				}
//			}
			
//			System.out.println(getFrontTile());
		}
	}
	
	//Set the next move time (used to make it so that when the user presses a direction key, the player moves instantly)
	public void setNextMoveTime (long inTime)
	{
		nextMoveTime = inTime;
	}
	
	@Override
	protected void die()
	{
		image = images.get("Zombie.png");
//		displayElements.get(0).setColor(Surface.ZOMBIE_GREEN);
//		displayElements.get(1).setColor(Color.red);
//		displayElements.get(2).setColor(Color.red);
	}
	
//	public int getSlot ()
//	{
//		return currentSlot;
//	}
//	
//	public void setSlot (int inSlot)
//	{
////		inventory.get(currentSlot).setSelected(false);
////		currentSlot = inSlot;
////		inventory.get(currentSlot).setSelected(true);
//
//		inventory.setSlot(inSlot);
//	}

	//return the player's inventory
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public Tile getFrontTile ()
	{
		return frontTile;
	}
	
	public void setFrontTile (Tile inTile)
	{
		frontTile = inTile;
	}
	
//	public ArrayList<InventorySlot> getInventory()
//	{
//		return inventory;
//	}
	
//	//initializes the inventory
//	private void setUpInventory ()
//	{
//		for (int i = 0; i < 10; i++)
//		{
//			inventory.add(new InventorySlot(Game.BOARD_SIZE + 2 + (i % 5) * 4, 22 + (i / 5) * 4));
//		}
//		
//		setSlot(0);
//	}

	
	//use the item in the selected inventory slot
	public void useItem ()
	{
		inventory.useItem(this);
		
//		InventorySlot slot = inventory.get(currentSlot);
//		GameObject item = slot.getItem();
//		if (item != null)
//		{
//			//Use the item. If it is successfully used, and it is consumable, subtract 1 from the inventory slot
//			if (item.use() && item.isConsumable())
//			{
//				slot.subtract(1);
//			}
//		}
	}
	
//	//return the tile in front of the player
//	public Tile findFrontTile ()
//	{
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
//	}
	
	//Makes the player break the block in front of them
	public void breakBlock ()
	{
		Tile tempTile = findFrontTile();
	
		//TODO: make an isBreakable variable in Tile and check based on that, not based on isSolid
		if (tempTile != null && tempTile.isSolid())
		{
//			addItem (tempTile.drop(), 1);
			inventory.addItem(tempTile.drop(), 1);
			tempTile.breakBlock();
		}
	}

	
//	//adds an item to the inventory
//	public void addItem (GameObject inItem, int inQuantity)
//	{
//		if (inItem != null)
//		{
//			InventorySlot itemSlot = null;
//			
//			InventorySlot slot;
//			//check to see if there already is an inventory slot of the given type (or an open slot, in case there isn't a slot of the given type)
//			for (int i = 0; i < inventory.size() && itemSlot == null; i++)
//			{
//				slot = inventory.get(i);
//				if (slot.getItem() == null || slot.getItem().sameTypeAs(inItem))
//				{
//					 itemSlot = slot;
//				}
//			}
//			
//			//If there is a slot with the item, add to that slot
//			if (itemSlot != null)
//			{
//				itemSlot.add(inItem, inQuantity);
//			}
//		}
//	}
	
	//subtracts an item from the inventory (Makes sure the slot is valid)
//	public void subtractItem (GameObject inItem, int inQuantity)
//	{
//		InventorySlot tempSlot = null;
//		
//		//TODO: review all of the inventory methods to make sure they make sense
//		
//		InventorySlot slot;
//		for (int i = 0; i < inventory.size() && tempSlot == null; i++)
//		{
//			slot = inventory.get(i);
//			if (slot.getItem().sameTypeAs(inItem))
//			{
//				tempSlot = slot;
//			}
//		}
//		
//		//if the correct item was found and there is enough of the item to subtract, subtract the item
//		if (tempSlot != null && tempSlot.getQuanity() >= inQuantity)
//		{
//			tempSlot.subtract(inQuantity);
//		}
//	}
	
//	//subtracts an item from the inventory (assumes the slot has been pre-checked)
//	public void subtractItem (int inQuantity, InventorySlot inSlot)
//	{
//		InventorySlot tempSlot = inSlot;
//		
//		tempSlot.subtract(inQuantity);
//	}
	
	//update the player (don't do anything, this is handled in the Game class)
	@Override
	public void update ()
	{
		//NOTHING!!!!!!!!!!!!!!!!!!!!!
	}
	
//	//draw the contents of the player's inventory
//	private void drawInventory (Graphics2D g2d)
//	{
//		for (InventorySlot slot : inventory)
//		{
//			slot.draw(g2d);
//		}
//	}

//	//resize the inventory icons
//	public void resizeInventory (float scaleFactor)
//	{
//		for (InventorySlot slot: inventory)
//		{
//			slot.resize(scaleFactor);
//		}
//	}
	
//	//resize the player and any other displayed elements the player has (like their inventory)
//	@Override
//	public void resize (float scaleFactor)
//	{	
//		super.resize(scaleFactor);
//		
//		inventory.resize(scaleFactor);
//	}

	@Override
	public GameObject drop()
	{
		//Do nothing.
		return null;
		//TODO: make player drop their inventory when dead.
	}
	
	@Override
	//draw the player and player related things, like the player's inventory
	public void draw (Graphics2D g2d)
	{
		super.draw(g2d);
		
		inventory.draw(g2d);
		//TODO: also resize whatever the frontTile is displaying when zooming
		if (frontTile != null)
		{
			frontTile.drawWhenInFront(g2d);
		}
	}
}
