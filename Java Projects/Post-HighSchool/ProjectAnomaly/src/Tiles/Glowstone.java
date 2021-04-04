package Tiles;

import java.awt.Color;
import java.util.ArrayList;

import static Game.Surface.*;

public class Glowstone extends Ore
{
	
	public Glowstone ()
	{
		this (0, 0);
		
//		super();
//		isSolid = true;
//		glowRange = 1;
//		veinChance = .1f;
//		veinGrowth = 0;//.5f;
	}
	
	public Glowstone (int inX, int inY)
	{
		super(inX, inY);
		isSolid = true;
		glowRange = 1;
		veinChance = .1f;
		veinGrowth = .5f;
	}
	
//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayRect(this, 0, 0, 1, 1, Color.gray));
//		displayElements.add(new DisplayRect(this, 0, 0, .5f, .5f, Color.green));
//		displayElements.add(new DisplayRect(this, .5f, .5f, .5f, .5f, Color.green));
//	}	
}
