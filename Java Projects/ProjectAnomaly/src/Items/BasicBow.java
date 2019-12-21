package Items;

import Mobs.Mob;

public class BasicBow extends Item
{
	public BasicBow ()
	{
		super(0, 0, 1, 1);
	}
	
	//TODO: needs to return true if an arrow was actually shot
	public boolean use (Mob inMob)
	{
		System.out.println("pew");
		
		//shoot an arrow in front of the mob
		Arrow arrow = new Arrow (inMob.findFrontTile(), inMob.getRotation());
		return true;
		
	}
}
