package Tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Game.*;
import static Game.Surface.*;

import Items.BasicBow;
import Items.BasicSword;

public class Chest extends Tile
{
//	ArrayList<InventorySlot> inventory;
	
	//The chest's inventory
	Inventory inventory;
	
	public Chest ()
	{
		this (0, 0);
		
//		super();
//		isSolid = true;
//		setUpInventory(5);
	}
	
	public Chest (int inX, int inY)
	{
		super(inX, inY);
		isSolid = true;
		inventory = new Inventory(5, images.get("chestSlot.png"), images.get("chestSelectRing.png"), Game.BOARD_SIZE + 2, 46);
//		setUpInventory(5);
		
		//by default, the chest spawns with a basic sword
		inventory.addItem(new BasicBow(), 1);
		inventory.addItem(new BasicSword(), 1);
	}
	
//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayRect(this, 0, 0, 1, 1, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, 0, 0, .25f, .25f, Color.yellow));
//		displayElements.add(new DisplayRect(this, .75f, 0, .25f, .25f, Color.yellow));
//		displayElements.add(new DisplayRect(this, .25f, .25f, .5f, .25f, Color.yellow));
//	}
	
	//return the chest's inventory
	public Inventory getInventory()
	{
		return inventory;
	}
	
//	//resize the chest and it's inventory
//	@Override
//	public void resize (float scaleFactor)
//	{
//		super.resize(scaleFactor);
//		
//		inventory.resize(scaleFactor);
//	}
	
	//TODO: make this drop the items inside, as well as the chest or maybe some chest-materials
	
//	//initializes the inventory
//	private void setUpInventory (int numSlots)
//	{
//		//TODO: put this in the inventory class
//		
//		inventory = new ArrayList<InventorySlot>();
//		
//		for (int i = 0; i < numSlots; i++)
//		{
//			inventory.add(new InventorySlot(Game.BOARD_SIZE + 2 + (i % 5) * 4, 46 - (i / 5) * 4,
//					new Color (185, 122, 87), Color.yellow));
//		}
//	}
	
	@Override
	public void drawWhenInFront (Graphics2D g2d)
	{
		//draw the chest's inventory
		inventory.draw(g2d);
		
//		for (InventorySlot slot : inventory)
//		{
//			slot.draw(g2d);
//		}
	}
}
