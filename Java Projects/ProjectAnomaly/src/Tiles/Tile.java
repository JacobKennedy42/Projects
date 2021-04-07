package Tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;

import Game.Game;
import static Game.Game.Direction;
import Game.GameObject;
//import Game.Surface.DisplayElement;
import static Game.Surface.*;
import Mobs.Mob;
import Items.Item;

//tile class
public abstract class Tile extends GameObject implements Cloneable
{
	
//	//TODO: move general types to game object
//	//General tile type
//	public static final int TILE_TYPE = 0;
////	//specific tile types
////	public static enum Type{AIR, STONE, GLOWSTONE, CHEST};
//	
//	//tile types (potentially a lot of finals)
//	public static final int AIR_TYPE; public static final int STONE_TYPE; public static final int GLOWSTONE_TYPE; public static final int CHEST_TYPE;
//	
//	//initialize the tile types
//	static
//	{
//		int i = 0;
//		AIR_TYPE = i++;
//		STONE_TYPE = i++;
//		GLOWSTONE_TYPE = i++;
//		CHEST_TYPE = i++;
//	}
//	
//	//The type of tile
//	protected int type;
	
//	//What the tile gives when it's broken
//	private int outputType;
	
	//What kind of tile this tile turns into when you break it (when null, should just give an air block)
//	private int breakType;
	private String breakType = "Air";
	
//	//The chance that, when making a vein, this block will make another ore vein next to it.
//	private float veinChance;
	
	//variables used for searching through tiles
	protected boolean wasVisited;
	protected Tile prev;
	protected Tile next;
	
	//the light sources that light this tile
	private ArrayList<GameObject> lightSources = new ArrayList<GameObject>();
	
	//the mob that currently resides in the tile
	private Mob mob = null;
	
	//The item(s) that this tile holds
	private ArrayList<Item> items = new ArrayList<Item>();
	
	//TODO: delete this
	static int test = 0;
	
	//Air tile by default
	public Tile ()
	{
		this(0, 0);
		
//		super();
//		isConsumable = true;
//		setFloorType (0);
	}
	
	public Tile (int inX, int inY)
	{
		super(inX, inY);
		isConsumable = true;
//		setFloorType (0);
	}
	
//	public Tile (int inType)
//	{
//		super();
//		setType (inType);
//		setFloorType (0);
//	}
	
//	public Tile (int inX, int inY, int inType)
//	{
//		x = inX;
//		y = inY;
//		width = 1;
//		height = 1;
//		
//		setType (inType);
//		setFloorType (0);
//	}
	
//	//method that changes the tile's characteristics based on it's type
//	protected void setType (int inType)
//	{
////		if (inType != AIR_TYPE)
////		{
////			throw new Error ("setting tile to unspecified type.\n");
////		}
//		
//		type = inType;
//		//Make the tile look like the tile it's supposed to look like
//		displayElements = Surface.createDisplayList(TILE_TYPE, inType, x, y, width, height);
//		
//////		//Air
//////		if (inType == AIR_TYPE)
//////		{
////			isSolid = false;
////			glowRange = 0;
////			removeLightSource(this);
//////			setOutput (AIR_TYPE);
//////		}
//		
////		//Stone
////		else if (inType == STONE_TYPE)
////		{
////			isSolid = true;
////			glowRange = 0;
////			removeLightSource(this);
////			setOutput (STONE_TYPE);
////		}
////		
////		//Glowstone
////		else if (inType == GLOWSTONE_TYPE)
////		{
////			isSolid = true;
////			//setColor(Color.gray);
////			//setDetailColor(Color.green);
////			glowRange = 1;
////			addLightSource(this);
////			setOutput (GLOWSTONE_TYPE);
////			veinChance = 0.0000000000000000000001f;
////		}
//	}

	
//	public int getOutput ()
//	{
//		return outputType;
//	}
	
//	public void setOutput (int inType)
//	{
//		outputType = inType;
//	}
	
//	public int getFloorType ()
//	{
//		return floorType;
//	}
//	
//	public void setFloorType (int inType)
//	{
//		floorType = inType;
//	}

//    //Places an ore block (recursion to make a vein)
//    public void placeOre (Ore inOre, int inX, int inY)
//    {
//    	ArrayList<Tile> visitedTiles = new ArrayList<Tile>();
//    	placeTile(inOre, inX, inY);
//    	inOre.makeVein(inX, inY, visitedTiles);
//    	
//    	//after your done placing the ore vein, clear the wasVisited's from all of the visited tiles
//    	for (Tile tile : visitedTiles)
//    	{
//    		tile.wasVisited = false;
//    	}
//    	
////    	setType(inType);
////    	
////    	for (int i = x - 1; i <= x + 1; i++)
////		{
////			for (int j = y - 1; j <= y + 1; j++)
////			{
////				//Make sure the tile is in the board, non-diagonal, and in the sight range
////				if (i >= 0 && i < Game.BOARD_SIZE &&
////					j >= 0 && j < Game.BOARD_SIZE &&
////					!(i == x && j == y) &&
////					(i == x || j == y))
////				{
////					Tile tempTile = Game.tiles.get(j).get(i);
////					
////					//Chance of an ore block being placed next to this ones
////					if (tempTile.getType() == 1 &&
////						Math.random() < (veinChance / Surface.getDist(x, y, inX, inY)))
////					{
////						tempTile.placeOre(inX, inY, inType);
////					}
////				}
////			}
////		}
//    }
    
//    //recursively makes a vein of the ore be transforming adjacent tiles into ore (origX and origY are where the first ore was
//    //placed, the "origin" of the vein)
//    //TODO: put this in the ore class
//    private void makeVein (int origX, int origY, ArrayList<Tile> visitedTiles)
//    {
////    	wasVisited = true;
////    	visitedTiles.add(this);
////    	
////    	Game.placeTile(getBlankCopy(), inX, inY);
////    	
//    	for (int i = x - 1; i <= x + 1; i++)
//		{
//			for (int j = y - 1; j <= y + 1; j++)
//			{
//				//Make sure the tile is in the board, non-diagonal, and in the sight range
//				if (i >= 0 && i < Game.BOARD_SIZE &&
//					j >= 0 && j < Game.BOARD_SIZE &&
//					!(i == x && j == y) &&
//					(i == x || j == y))
//				{
//					Tile tempTile = Game.tiles.get(j).get(i);
//					
//					//TODO: figure out why having a very small vein chance still gives large veins. Might be rounding error
//					
//					//Chance of an ore block being placed next to this ones
//					if (tempTile.getType() == STONE_TYPE &&
//						!tempTile.wasVisited)
//					{
//						tempTile.wasVisited = true;
//						visitedTiles.add(tempTile);
//						
//						if (Math.random() < (veinChance / Surface.getDist(i, j, origX, origY)))
//						{
//							Ore newOre = (Ore) getBlankCopy();
//							Game.placeTile(newOre, i, j);
//							newOre.makeVein(origX, origY, visitedTiles);
//						}
//					}
//				}
//			}
//		}
//    }
	
	//TODO: might actually just put all the images in one map in surface
	
	protected void loadImages()
	{
		
	}
	
	//TODO: might want to have air block appear by default and override, rather than use reflection, for reasons of performance, code robustness (less likely this shit breaks), and so you can more easily do other stuff with breaking blocks, like deal damage to player
    //break this block (replace it with another block.)
	public void breakBlock()
    {
//    	Tile newTile;
    	
    	//TODO: Idea: type to tile repository. There is a class that is assigned a Tile. A Converter. The repository is and array
    	//of converters. The repository maps type to the tile, so when given a type, it spits out the corresponding tile.
    	//This way you can just store type and use the repository to get tiles from it. Probably should undo alot of the stuff i
    	// did to the inventory. Maybe use backup (though I don't really want to).
    	//Need to make slots hold type, get rid of the isSameType in GameObject. Don't get rid of use (when using an item, get
    	//the type, make the item, then call use() on that item)
    	//Maybe for the repository try the <E> thing, or <E extends GameObject>
    	
    	
////    	//Replace this block with an air block
////    	if (breakType == AIR_TYPE)
////    	{
//    		newTile = new Air(x, y);
//    		Game.placeTile(newTile, x, y);
////    	}
			try
			{
				//Replace this tile with another tile, as specified by the breakType field
    			Tile newTile = (Tile) Class.forName("Tiles." + breakType).newInstance();
    			Game.placeTile(newTile, x, y);
			}
			catch (IllegalAccessException | InstantiationException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
    }
    
	//Java doesn't let subclasses access the protected members of other instances unless you are the same type, so this lets
	//tiles access the protected members of other tiles, no matter the subclass (this feels stupid)
	protected boolean getWasVisited (Tile inTile)
	{
		return inTile.wasVisited;
	}
	
	protected void setWasVisited (Tile inTile, boolean inBool)
	{
		inTile.wasVisited = inBool;
	}
	
	//clear the pathfinding variables of used tiles
	protected void clear(ArrayList<Tile> affected)
	{
//		System.out.println("clearing");
		
		for (Tile tile : affected)
		{
			tile.prev = null;
			tile.next = null;
			tile.wasVisited = false;
		}
		
//		System.out.println("cleared");
	}
	
	//find a path from this tile to another, and return the next tile in that path. Only looks in a given range for dest. Clears
	//the path variables afterwards.
	public Tile nextPathTile (Tile dest, int inRange)
	{
//		System.out.println("nextPathTile");
		
		//first check if this tile is the dest tile
		if (this == dest)
		{
			//already at dest, so if you move to a tile, you should move to this tile
			return this;
		}
		
		LinkedList<Tile> unProcessed = new LinkedList<Tile>();
		ArrayList<Tile> affected = new ArrayList<Tile>();
		
		unProcessed.add(this);
		//do not let other tiles try to process this
		wasVisited = true;
		affected.add(this);
		
		Tile currentTile;
		//process the unProcessed tiles until the dest tile is found.
//		for (Tile currentTile = this; currentTile != null/*unProcessed.isEmpty()*/; currentTile = unProcessed.removeFirst())
		while (!unProcessed.isEmpty())
		{
			currentTile = unProcessed.removeFirst();
			
//			System.out.println("current: " + currentTile.getX() + " " + currentTile.getY());
//			currentTile.changeColor(Color.blue);
			
		
//			System.out.println("Before forloop");
			
			//check the current tile's adjacent tiles.
			Tile adjTile;
			ArrayList<Direction> dirs = Direction.getDirs();
//			for (int i = 1; i < 5; i++)
			for (int i = 0; i < dirs.size(); i++)
			{	
//				adjTile = currentTile.getAdjacentTile(i);
				adjTile = currentTile.getAdjacentTile(dirs.get(i));
				
//				System.out.println("Before if");
				
				//Check if there is a nonsolid, nonvisited adjacent tile and that it is in range
				if (adjTile != null && !adjTile.isSolid() && !adjTile.wasVisited &&
					adjTile.x >= x - inRange && adjTile.x <= x + inRange && //TODO: probably make this condition a method, use in addLightSource
					adjTile.y >= y - inRange && adjTile.y <= y + inRange)
				{
//					System.out.println("adj: " + adjTile.getX() + " " + adjTile.getY());

					//set the adjacent tile's previous tile to this one. Thus adjTile has been affected and so must be cleared at
					//the end
					adjTile.prev = currentTile;
					adjTile.wasVisited = true;
					affected.add(adjTile);
					
					//if dest was found, create the path
					if (adjTile == dest)
					{
//						System.out.println("dest found");
						
						//TODO: need to handle if the Tile returned has a mob in it. Path should be able to go though mobs.
						
//						adjTile.changeColor(Color.green);
						
						createPath(adjTile);
						
						//The next tile in the path
						Tile nextTile = next;
						
//						nextTile.changeColor(Color.magenta);
						
						//After everything, clear the pathfinding variables
						clear(affected);
						
						return nextTile;
					}
					
					//If there is an adjacent tile that is in range and has not been visited, put it in unProcessed and set it's prev
					//to this tile.
					else
					{
//						System.out.println("dest not found");
						
//						adjTile.changeColor(Color.red);
						
						unProcessed.add(adjTile);
					}
				}
				
//				System.out.println("After if");
//				System.out.println(unProcessed.isEmpty());
			}
		}
		
//		System.out.println("After forloop");
		
		//no path found
		clear(affected);
		return null;
	}
   
	//Create the path to a given destTile by setting the next's of the tiles
	private static void createPath(Tile destTile)
	{
//		System.out.println("making path");
		
		Tile currentTile = destTile;
		while (currentTile.prev != null)
		{
//			System.out.println("current: " + currentTile.prev.getX() + " " + currentTile.prev.getY());
//			currentTile.prev.changeColor(Color.pink);
			
			currentTile.prev.next = currentTile;
			currentTile = currentTile.prev;
		}
		
//		System.out.println("made path");
	}
	
    public ArrayList<GameObject> getLightSources ()
	{
		return lightSources;
	}
	
	//adds a light source to this tile and nearby Game.tiles (uses recursion)
	public void addLightSource (GameObject inSource)
	{
		//Make sure to only add the light source if it isn't already in lightSources
		if (!lightSources.contains(inSource))
		{
    		lightSources.add(inSource);
    		
    		//Only reveal this tile's surroundings if it is not solid or if it is the light source
			if (!isSolid || this == inSource)
    		{
	    		for (int i = x - 1; i <= x + 1; i++)
				{
					for (int j = y - 1; j <= y + 1; j++)
					{
						//Make sure the tile is in the board, non-diagonal, and in the sight range
						if (i >= 0 && i < Game.BOARD_SIZE &&
							j >= 0 && j < Game.BOARD_SIZE &&
							!(i == x && j == y) &&
							(i == x || j == y) &&
							i <= inSource.getX() + inSource.getGlowRange() && i >= inSource.getX() - inSource.getGlowRange() &&
							j <= inSource.getY() + inSource.getGlowRange() && j >= inSource.getY() - inSource.getGlowRange())
						{
							Tile tempTile = Game.tiles.get(j).get(i);
							
							tempTile.addLightSource(inSource);
						}
					}
				}
    		}
		}
	}
	
	//Removes a light source from this tile and nearby Game.tiles (uses recursion)
	public void removeLightSource (GameObject inSource)
	{
		//Make sure to only remove the light source if it is in lightSources
		if (lightSources.remove(inSource))//lightSources.contains(inSource))
		{
//    		lightSources.remove(inSource);
    		
    		for (int i = x - 1; i <= x + 1; i++)
			{
				for (int j = y - 1; j <= y + 1; j++)
				{
					//Make sure the tile is in the board, non-diagonal, and in the sight range
					if (i >= 0 && i < Game.BOARD_SIZE &&
						j >= 0 && j < Game.BOARD_SIZE &&
						!(i == x && j == y) &&
						(i == x || j == y))
					{
						Tile tempTile = Game.tiles.get(j).get(i);
						
						tempTile.removeLightSource(inSource);
					}
				}
			}
		}
	}
	
	//Get the lightsources of the adjacent tiles. return them as a list
	public ArrayList<GameObject> getNearbyLightSources ()
	{
		ArrayList<GameObject> output = new ArrayList<GameObject>();
		
		//check the surrounding Game.tiles' light sources
		for (int i = x - 1; i <= x + 1; i++)
		{
			for (int j = y - 1; j <= y + 1; j++)
			{
				//Make sure the tile is in the board and non-diagonal
				if (i >= 0 && i < Game.BOARD_SIZE &&
					j >= 0 && j < Game.BOARD_SIZE &&
					!(i == x && j == y) &&
					(i == x || j == y))
				{
					Tile tempTile = Game.tiles.get(j).get(i);
					
					for (GameObject source : tempTile.getLightSources())
					{
						if (!output.contains(source))
						{
							output.add(source);
						}
					}
				}
			}
		}
		
		return output;
	}
	
	//updates this tile's lightSources based on those of the surrounding Game.tiles
	public void updateLightSources ()
	{
		/*
		//First, remove previous light sources
		while (lightSources.size() > 0)
		{
			lightSources.remove(0);
		}
		*/
		/*
		//check the surrounding Game.tiles' light sources and add them if in range
		for (int i = x - 1; i <= x + 1; i++)
		{
			for (int j = y - 1; j <= y + 1; j++)
			{
				//Make sure the tile is in the board and non-diagonal
				if (i >= 0 && i < Game.BOARD_SIZE &&
					j >= 0 && j < Game.BOARD_SIZE &&
					!(i == x && j == y) &&
					(i == x || j == y))
				{
					Tile tempTile = Game.tiles.get(j).get(i);
					
					for (GameObject source : tempTile.getLightSources())
					{
						//add the light source if this tile is close enough to it
						if (!lightSources.contains(source) &&
							x <= source.getX() + source.getGlowRange() && x >= source.getX() - source.getGlowRange() &&
							y <= source.getY() + source.getGlowRange() && y >= source.getY() - source.getGlowRange())
						{
							addLightSource (source);
						}
					}
				}
			}
		}
		*/
//		ArrayList<GameObject> tempSources = new ArrayList<GameObject>();
//		
//		//check the surrounding Game.tiles' light sources
//		for (int i = x - 1; i <= x + 1; i++)
//		{
//			for (int j = y - 1; j <= y + 1; j++)
//			{
//				//Make sure the tile is in the board and non-diagonal
//				if (i >= 0 && i < Game.BOARD_SIZE &&
//					j >= 0 && j < Game.BOARD_SIZE &&
//					!(i == x && j == y) &&
//					(i == x || j == y))
//				{
//					Tile tempTile = Game.tiles.get(j).get(i);
//					
//					for (GameObject source : tempTile.getLightSources())
//					{
//						if (!tempSources.contains(source))
//						{
//							tempSources.add(source);
//						}
//					}
//				}
//			}
//		}
		
		ArrayList<GameObject> nearbySources = getNearbyLightSources();
		
		//remove and reset the light sources
		for (GameObject source : nearbySources)
		{
			Tile sourceTile = Game.tiles.get(source.getY()).get(source.getX());
			sourceTile.removeLightSource(source);
			sourceTile.addLightSource(source);
		}
	}
	
	//get the mob that currently resides in the tile
	public Mob getMob ()
	{
		return mob;
	}
	
	//reset what mob is currently residing in the tile
	public void setMob (Mob inMob)
	{
		mob = inMob;
	}
	
	//return the items that this tile holds
	public ArrayList<Item> getItems()
	{
		return items;
	}
	
	//update the items
	public void updateItems()
	{
		//make a copy, since updating the items may remove them from the list
		ArrayList<Item> tempList = (ArrayList<Item>) items.clone();
		
//		for (Item item : items)
		for (Item item : tempList)
		{
			item.update();
		}
	}
	
	//returns if the tile can be walked on by a terrestrial mob
	public boolean isWalkable ()
	{
		return !isSolid && mob == null;
	}
	
	//drop a copy of this tile by default
	public GameObject drop ()
	{
		return getBlankCopy();
	}
	
	//default use of a tile. Just place a copy of this tile in front of the player
	public boolean use (Mob inMob)
	{	
		return inMob.placeBlock(getBlankCopy());
	}
	
	//return a blank copy of this tile
 	public Tile getBlankCopy ()
	{
		try {
			return getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			return (Tile) clone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//		}
		
		return null;
	}
	
	@Override
	public void draw (Graphics2D g2d)
	{
		//if the tile is lit (or in an inventory), draw it and the items it holds as normal
		if (lightSources.size() > 0)
		{
			super.draw(g2d);
			for (Item item : items)
			{
				item.draw(g2d);
			}
		}
		
		//If it's not, draw it as a black square
		else
		{
			g2d.drawImage(images.get("darkTile.png"), x * Game.tileSize, y * Game.tileSize, width * Game.tileSize, height * Game.tileSize,null);
			
//			g2d.setPaint(Color.black);
//			g2d.fill(displayElements.get(0).getShape());
		}
	}
	
	//draws something when this tile is in front of the player (ex: draw chest inventory when in front of the player)
	public void drawWhenInFront (Graphics2D g2d)
	{
		//Do nothing
	}

/////////////////////////////////
// used for testing with tiles //
/////////////////////////////////
	
//	public void changeColor (Color inColor)
//	{
//		for (DisplayElement e : displayElements)
//		{
//			e.setColor(inColor);
//		}
//	}
	
	public static boolean clearCheck()
	{
		for (ArrayList<Tile> row: Game.tiles)
		{
			for (Tile tile : row)
			{
				if (tile.wasVisited || tile.prev != null || tile.next != null)
				{
					System.out.println("check FAILED!");
					return false;
				}
			}
		}
		
		System.out.println("check passed");
		return true;
	}
}