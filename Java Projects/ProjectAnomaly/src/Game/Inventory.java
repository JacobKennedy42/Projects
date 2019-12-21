package Game;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import static Game.Surface.*;
import Mobs.Mob;

//invetory class for things that need to hold items
public class Inventory
{
	private InventorySlot[] slots;
	private int selectedSlot = 0;
	
	public Inventory (int numSlots)
	{
		this(numSlots, images.get("defaultSlot.png"), images.get("defaultSelectRing.png"));
	}
	
	public Inventory (int numSlots, BufferedImage backdrop, BufferedImage selectRing)
	{
		this(numSlots, backdrop, selectRing, Game.BOARD_SIZE + 2, 22);
		
//		slots = new InventorySlot[numSlots];
//		for (int i = 0; i < numSlots; i++)
//		{
//			slots[i] = new InventorySlot(Game.BOARD_SIZE + 2 + (i % 5) * 4, 22 + (i / 5) * 4, backdropColor, ringColor);
//		}
//		
//		selectedSlot = 0;
	}
	
	//inX and inY denote the top left corner of where the inventory will be displayed
	public Inventory (int numSlots, BufferedImage backdrop, BufferedImage selectRing, int inX, int inY)
	{
		slots = new InventorySlot[numSlots];
		for (int i = 0; i < numSlots; i++)
		{
			slots[i] = new InventorySlot(inX + (i % 5) * 4, inY + (i / 5) * 4, backdrop, selectRing);
		}
		
		setSlot(0);
	}
	
	//get which slot is currently selected
	public int getSlot()
	{
		return selectedSlot;
	}
	
	public void setSlot(int inSlot)
	{
//		if (inSlot >= slots.length)
//		{
//			selectedSlot = slots.length - 1;
//		}
//		else
//		{
//			selectedSlot = inSlot;
//		}
		
		if (inSlot >= slots.length)
		{
			inSlot = slots.length-1;
		}
		slots[selectedSlot].select(false);
		selectedSlot = inSlot;
		slots[selectedSlot].select(true);
	}
	
	//get the item of the currently selected slot
	public GameObject getItem ()
	{
		return slots[selectedSlot].getItem();
	}
	
	//put an item(s) into the inventory's currently selected slot
	private void setItem (GameObject inItem, int inQuantity)
	{
		slots[selectedSlot].add(inItem, inQuantity);
	}
	
	//get the number of items in the currently selected slot
	private int getQuantity()
	{
		return slots[selectedSlot].getQuanity();
	}
	
	//add an item to the inventory. try to put the item in a slot that already has the item. If there is none, try to put it in
	//the currently selected item slot. If that slot has another item in it, the try to put it in the earliest open slot
	public void addItem (GameObject inItem, int inQuantity)
	{
		if (inItem != null)
		{
			InventorySlot openSlot = null;
			
			InventorySlot tempSlot;
			//first try to find an item slot that with the same type of item. Also find the earliest open slot.
			for (int i = 0; i < slots.length; i++)
			{
				tempSlot = slots[i];
				
				//set the open slot if it hasn't been already
				if (tempSlot.getItem() == null)
				{
					if (openSlot == null)
					{
						openSlot = tempSlot;
					}
				}
				
				//slot found. Add the item to it and then get out of the method
				else if (tempSlot.getItem().getClass().equals(inItem.getClass()))
				{
					tempSlot.add(inItem, inQuantity);
					return;
				}
			}
			
			//No slot found with the same item. Try to put it in the currently selected slot
			tempSlot = slots[selectedSlot];
			if (tempSlot.canHold(inItem))
			{
				tempSlot.add(inItem, inQuantity);
				return;
			}
			
			//Can't put item in currently selected slot. Try to put in in the earliest open slot
			if (openSlot != null)
			{
				openSlot.add(inItem);
			}
		}
	}
	
	//swap the items in this inventory's currently selected slot with the item in the given inventory's selected slot
	public void swap (Inventory inInv)
	{
		GameObject tempItem = inInv.getItem();
		int tempQuantity = inInv.getQuantity();
		inInv.setItem(getItem(), getQuantity());
		setItem(tempItem, tempQuantity);
	}
	
	//use the currently selected item. Takes in the mob that is trying to use the item
	public void useItem (Mob inMob)
	{
		GameObject item = slots[selectedSlot].getItem();
		if (item != null)
		{
			//Use the item. If it is successfully used, and it is consumable, subtract 1 from the inventory slot
			if (item.use(inMob) && item.isConsumable())
			{
				slots[selectedSlot].subtract(1);
			}
		}
	}
	
//	//resize the inventory
//	public void resize (float scaleFactor)
//	{		
//		InventorySlot slot;
//		for (int i = 0; i < slots.length; i++)
//		{
//			slot = slots[i];
//			slot.resize(scaleFactor);
//		}
//	}
	
	public void draw (Graphics2D g2d)
	{
		for (int i = 0; i < slots.length; i++)
		{
//			if (i != selectedSlot)
//			{
				slots[i].draw(g2d);
//			}
//			else
//			{
//				slots[i].drawSelected(g2d);
//			}
		}
	}
	

	private static class InventorySlot
	{	
//		//what is displayed in the slot
//		private ArrayList<DisplayElement> displayElements = new ArrayList<DisplayElement>();
		
		//what the slot holds and how much
		//int itemType;
		private GameObject item;
		private int quantity;
		
		//is this slot selected
		private boolean isSelected;
		
//		//Is this slot the currently selected slot
//		private boolean isSelected;
		
		//where and how the slot should be displayed
		private int x;
		private int y;
		private int width;
		private int height;
		
//		//the backdrop and select backdrop for the inventory slot
//		private DisplayElement backdrop;
//		private DisplayElement selectRing;
//		private DisplayElement selectBackdrop;
		
		BufferedImage image;
		BufferedImage selectImage;
		
		//TODO: get rid of most of these constructors
		
		private InventorySlot(int inX, int inY)
		{
//			setQuantity (0);
//			x = inX;
//			y = inY;
//			setBackdrops();
			
			this (inX, inY, null, 0);
		}
		
		private InventorySlot (int inX, int inY, GameObject inItem, int inQuantity)
		{
			this (inX, inY, inItem, inQuantity, images.get("defaultSlot.png"), images.get("defaultSelectRing.png"));
		}
		
		private InventorySlot (int inX, int inY, BufferedImage backdrop, BufferedImage selectRing)
		{
			this (inX, inY, null, 0, backdrop, selectRing);
		}
		
		private InventorySlot (int inX, int inY, GameObject inItem, int inQuantity, BufferedImage backdrop, BufferedImage selectRing)
		{
			x = inX;
			y = inY;
			width = 3;
			height = 3;
			isSelected = false;
			item = inItem;
			if (item != null)
			{
				item.setPos(x + 1, y + 1);
			}
			setQuantity (inQuantity);
//			setBackdrops(backdropColor, ringColor);
			
			image = backdrop;
			selectImage = selectRing;
//			image = images.get("defaultSlot.png");
//			selectImage = images.get("defaultSelectRing.png");
		}
		
//		//set up the slot's backdrops (default white and red)
//		private void setBackdrops ()
//		{
//			setBackdrops (Color.white, Color.red);
//		}
		
//		//set up the backdrops using the two given colors
//		private void setBackdrops (Color backdropColor, Color ringColor)
//		{
//			backdrop = new DisplayRect (x, y, 3, 3, backdropColor);
//			selectRing = new DisplayRect (x, y, 3, 3, ringColor);
//			selectBackdrop = new DisplayRect (x + .5f, y + .5f, 2, 2, backdropColor);
//			
////			backdrop = new DisplayElement (new Rectangle2D.Float(x * Game.tileSize, y * Game.tileSize,
////					3 * Game.tileSize, 3 * Game.tileSize), backdropColor);
////			selectRing = new DisplayElement (new Rectangle2D.Float(x * Game.tileSize, y * Game.tileSize,
////					3 * Game.tileSize, 3 * Game.tileSize), ringColor);
////			selectBackdrop = new DisplayElement (new Rectangle2D.Float((x + .5f) * Game.tileSize, (y + .5f) * Game.tileSize,
////					2 * Game.tileSize, 2 * Game.tileSize), backdropColor);
//		}
		
//		public int getType ()
//		{
//			return itemType;
//		}
		
//		public void setType (int inType)
//		{
//			itemType = inType;
//			
//			displayElements = Surface.createDisplayList(0, inType, x, y, 1, 1);
//		}
	//	
		//set whether this slot is selected
		private void select(boolean inBool)
		{
			isSelected = inBool;
		}
		
		private GameObject getItem ()
		{
			return item;
		}
		
		private int getQuanity ()
		{
			return quantity;
		}
		
		private void setQuantity (int inQuantity)
		{
			if (inQuantity == 0)
			{
				item = null;
			}
			
			quantity = inQuantity;
		}
		
//		private boolean isSelected()
//		{
//			return isSelected;
//		}
//		
//		private void setSelected (boolean inBool)
//		{
//			isSelected = inBool; 
//		}
		
		//Add one of an item (evict the existing item if it is of a different class)
		private void add (GameObject inObj)
		{
			add (inObj, 1);
		}
		
		//TODO: invetory is buggy, find out why
		//Add multiple of an item (evict the existing item if it is of a different class)
		private void add (GameObject inObj, int inNum)
		{	
			if (item == null || inObj == null || !item.getClass().equals(inObj.getClass()))
			{
				item = inObj;
				if (inObj != null)
				{
					item.setPos(x + 1, y + 1);
				}
				quantity = 0;
			}
			
			quantity += inNum;
		}
		
		private void add (int inQuantity)
		{
			quantity += inQuantity;
		}
		
		private void subtract (int inQuantity)
		{
			if (quantity > 0)
			{
				quantity -= inQuantity;
				
				if (quantity < 0)
				{
					quantity = 0;
				}
				
				if (quantity == 0)
				{
					item = null;
				}
			}
		}
		
		//can this inventory slot hold a given item?
		private boolean canHold (GameObject inItem)
		{
			return item == null || item.getClass().equals(inItem.getClass());
		}
		
//		private void resize (float scaleFactor)
//		{
//			backdrop.resize(scaleFactor);
//			selectRing.resize(scaleFactor);
//			selectBackdrop.resize(scaleFactor);
//			
//			if (item != null)
//			{
//				for (DisplayElement element: item.displayElements)
//				{
//					element.resize(scaleFactor);
//				}
//			}
//		}
		
		private void draw (Graphics2D g2d)
		{
////			//draw the backdrop of the slot
////			if (isSelected)
////			{
////				selectRing.draw(g2d);
////				selectBackdrop.draw(g2d);
////			}
////			else
////			{
//				backdrop.draw(g2d);
////			}
//			
//			//draw the item, if the is one
//			if (item != null)
//			{
//				for (DisplayElement element: item.displayElements)
//				{
//					element.draw(g2d);
//				}
//			}
			
			g2d.drawImage(image, x * Game.tileSize, y * Game.tileSize, width * Game.tileSize, height * Game.tileSize,null);
			if (isSelected)
			{
				g2d.drawImage(selectImage, x * Game.tileSize, y * Game.tileSize, width * Game.tileSize, height * Game.tileSize,null);
			}
			if (item != null)
			{
//				item.draw(g2d);
				item.drawInInv(g2d);
			}
		}
		
//		//draw the slot in with the ring around it
//		private void drawSelected (Graphics2D g2d)
//		{
//			selectRing.draw(g2d);
//			selectBackdrop.draw(g2d);
//			
//			//draw the item, if the is one
//			if (item != null)
//			{
//				for (DisplayElement element: item.displayElements)
//				{
//					element.draw(g2d);
//				}
//			}
//		}
	}
}
