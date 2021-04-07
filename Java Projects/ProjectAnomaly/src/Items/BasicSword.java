package Items;

import java.awt.Color;

import static Game.Surface.*;
import Mobs.Mob;

//Basic sword
public class BasicSword extends Item
{	
	//TODO: eventually delete this and make abstract Weapon, Sword, etc classes.
	
	//attack damage
	private int atkDmg;
	
	public BasicSword ()
	{
		this(0, 0, 1, 1);
	}
	
	public BasicSword (int inX, int inY, int inWidth, int inHeight)
	{
		super (inX, inY, inWidth, inHeight);
		
		atkDmg = 1;
	}

//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayRect(this, .5f, .5f, .25f, .25f, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, .375f, .75f, .125f, .125f, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, .75f, .375f, .125f, .125f, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, .75f, .75f, .125f, .125f, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, .875f, .875f, .125f, .125f, WOOD_BROWN));
//		displayElements.add(new DisplayRect(this, .375f, .375f, .25f, .25f, Color.gray));
//		displayElements.add(new DisplayRect(this, .25f, .25f, .25f, .25f, Color.gray));
//		displayElements.add(new DisplayRect(this, .125f, .125f, .25f, .25f, Color.gray));
//		displayElements.add(new DisplayRect(this, 0, 0, .25f, .25f, Color.gray));
//	}	

	public boolean use (Mob inMob)
	{
		Mob frontMob = inMob.findFrontTile().getMob();
		
		//If there is a mob in front, damage it
		if (frontMob != null)
		{
			frontMob.damage(atkDmg);
		}
		
		return true;
	}
}
