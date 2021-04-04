package Items;

import java.awt.Graphics2D;

import Game.Game;
import static Game.Game.Direction;
import Mobs.Mob;
import Tiles.Tile;

public class Arrow extends Item
{
	protected Direction rotation;
	
	private int atkDmg;
	
	//The amount the mob must wait per move
	protected int timePerMove;
	//The time at which the mob may move again
	protected long nextMoveTime;
	
	public Arrow (Tile inTile)
	{
		this(inTile, Direction.UP);
	}
	
	public Arrow (Tile inTile, Direction inDirection)
	{
		super(inTile, 1, 1);
		setRotation(inDirection);
		timePerMove = 32;
		nextMoveTime = Game.clock + timePerMove;
		atkDmg = 1;
	}
	
	public void setRotation (Direction inDirection)
	{	
		rotation = inDirection;
	}
	
	public void update()
	{
		//TODO: still needs tuning, like when mobs run into the arrow, or when an arrow is shot point blank at a mob
//		sdakhjhb
		
		if (Game.clock > nextMoveTime)
		{
			//If there is a mob in front of the arrow, damage it. Otherwise, move the arrow
			Tile frontTile = getAdjacentTile(rotation);
			Mob frontMob = null;
			if (frontTile != null)
			{
				frontMob = frontTile.getMob();
			}
			
			if (frontMob != null)
			{
				//damage the mob, then remove this arrow
				frontMob.damage(atkDmg);
				Game.tiles.get(y).get(x).getItems().remove(this);
			}
			else
			{
				move();
			}
			
			//set the next move time (only allow moves during an update check)
			nextMoveTime = Game.clock + timePerMove;
		}
	}
	
	public void move ()
	{	
		Tile currentTile = Game.tiles.get(y).get(x);
		Tile frontTile = getAdjacentTile(rotation);
		
		currentTile.getItems().remove(this);
		
		//only move forward if there is a tile in front of the arrow and that tile is walkable
		if (!(frontTile == null || frontTile.isSolid()))
		{
			//move left
			if (rotation == Direction.LEFT)
			{
    			x--;
			}
			
			//move up
			else if (rotation == Direction.UP)
			{		
    			y--;
			}
			
			//move right
			else if (rotation == Direction.RIGHT)
			{
    			x++;
			}
			
			//move down
			else if (rotation == Direction.DOWN)
			{
    			y++;
			}	
		
			if (glowRange > 0)
			{
				currentTile.removeLightSource(this);
				frontTile.addLightSource(this);
			}
			
			//put the arrow on the next tile, if there is one
			frontTile.getItems().add(this);
		}
	}
	
	public boolean use (Mob inMob)
	{
		//Do nothing
		return false;
	}
	
	@Override
	public void draw(Graphics2D g2d)
	{
		//If the arrow is lit, draw it
		if (glowRange > 0 || Game.tiles.get(y).get(x).getLightSources().size() > 0)
		{
			int convertedX = x*Game.tileSize;
			int convertedY = y*Game.tileSize;
			int convertedW = width*Game.tileSize;
			int convertedH= height*Game.tileSize;
			g2d.drawImage(getImage(), convertedX, convertedY, convertedX + convertedW, convertedY + convertedH,
					(int)(getImage().getWidth() * rotation.val()), 0, (int) (getImage().getWidth() * (rotation.val() + Direction.INTERVAL)), image.getHeight(), null);
		}
	}
}
