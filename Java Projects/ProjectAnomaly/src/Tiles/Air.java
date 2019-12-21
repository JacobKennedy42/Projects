package Tiles;

import java.awt.Color;
import java.util.ArrayList;

import Game.*;

import static Game.Surface.*;

public class Air extends Tile
{	
	public Air ()
	{
		this (0, 0);
//		super();
	}
	
	public Air (int inX, int inY)
	{
		super(inX, inY);
	}
	
//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayRect(this, 0, 0, 1, 1, Color.lightGray));
//	}

	public GameObject drop ()
	{
		//error. Air blocks should never drop
		throw new Error("air block trying to drop.\n");
//		return null;
	}
	
	public boolean use()
	{
		//error. Air blocks should never be in the inventory in the first place.
		throw new Error("trying to use an air block.\n");
	}
}
