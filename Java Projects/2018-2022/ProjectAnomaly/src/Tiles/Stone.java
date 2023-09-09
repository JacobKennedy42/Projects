package Tiles;

import java.awt.Color;
import java.util.ArrayList;

import Game.*;

import static Game.Surface.*;

public class Stone extends Tile
{
	
	public Stone ()
	{
		this (0, 0);
		
//		super();
//		isSolid = true;
	}
	
	public Stone (int inX, int inY)
	{
		super(inX, inY);
		isSolid = true;
	}

//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayRect(this, 0, 0, 1, 1, Color.gray));
//	}
}
