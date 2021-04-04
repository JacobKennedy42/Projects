package Items;

import Game.GameObject;
import Tiles.Tile;

public abstract class Item extends GameObject
{
//	//General item type
//	public static final int ITEM_TYPE = 2;
//	//item types
//	public static final int SWORD1_TYPE;
	
	//initialize the item types
//	static
//	{
//		int i = 0;
//		SWORD1_TYPE = i++;
//	}
	
	public Item (int inX, int inY, int inWidth, int inHeight)
	{
		super (inX, inY, inWidth, inHeight);
	}
	
	public Item (Tile inTile)
	{
		this(inTile, 1, 1);
	}
	
	//create an item on a given Tile
	public Item (Tile inTile, int inWidth, int inHeight)
	{
		width = inWidth;
		height = inHeight;
		place(inTile);
	}
	
//	public int getSuperType()
//	{
//		return ITEM_TYPE;
//	}
	
	public GameObject drop()
	{
		throw new Error("trying to have an item drop another item");
	}
	
	//update the item (does nothing by default)
	public void update()
	{
		//Do nothing
	}
	
	//place the item on a tile
	//TODO: maybe put this in Game, as placeItem
	public void place(Tile inTile)
	{
		if (inTile != null && inTile.isWalkable())
		{
			x = inTile.getX();
			y = inTile.getY();
			inTile.getItems().add(this);
		}
	}
}
