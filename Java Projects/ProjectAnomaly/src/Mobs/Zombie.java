package Mobs;

import java.awt.Color;
import java.lang.Math;
import java.util.ArrayList;

import Game.*;
import Game.Game.Direction;

import static Game.Surface.*;
import Tiles.Tile;

public class Zombie extends Mob
{	
	private int sightRange;
	
	public Zombie ()
	{
		this (0, 0, 1, 1);
		
//		super ();
//		
//		isSolid = true;
//		timePerMove = 64;//2000000000;
	}
	
	public Zombie (int inX, int inY, int inWidth, int inHeight)
	{
		super(inX, inY, inWidth, inHeight);
		
		isSolid = true;
		timePerMove = 64;//2000000000;
		sightRange = 4;
	}
	
//	protected void setDisplay ()
//	{
//		displayElements= new ArrayList<DisplayElement> ();
//		displayElements.add(new DisplayEllipse(this, 0, 0, 1, 1, ZOMBIE_GREEN));
//		displayElements.add(new DisplayEllipse(this, .125f, .75f, .25f, .25f, Color.red));
//		displayElements.add(new DisplayEllipse(this, .625f, .75f, .25f, .25f, Color.red));
//	}
	
	//update the zombie
	public void update ()
	{	
		//move the zombie randomly (exclude directions that are blocks)
		if (Game.clock > nextMoveTime)
		{	
			//try to find the player
			Tile playerTile = Game.tiles.get(Game.player.getY()).get(Game.player.getX());
			Tile pathTile = Game.tiles.get(y).get(x).nextPathTile(playerTile, sightRange);
			
			//If the zombie found the player, make them move towards the player
			if (pathTile != null)
			{	
				//find out which direction to turn
				ArrayList<Direction> dirs = Direction.getDirs();
				Direction dir = null;
				for (int i = 0; dir == null && i < dirs.size(); i++)
				{
					Direction tempDir = dirs.get(i);
					if (getAdjacentTile(tempDir) == pathTile)
					{
						dir = tempDir;
					}
				}
//				int dir = 0;
//				for (int i = 1; dir == 0 && i < 5; i++)
//				{
//					if (getAdjacentTile(i) == pathTile)
//					{
//						dir = i;
//					}
//				}
				
//				//turn in the direction of the path. If the player is in front of the zombie, attack the player. Otherwise, move
//				//along the path
//				setRotation(dir);
				
				//if the player is not in front of the zombie, move towards them
//				if (pathTile != playerTile)
				if (findFrontTile() != playerTile)
				{
					setRotation(dir);
					move (dir);
				}
				//if the player is in front of the zombie, attack them
				else
				{
					Game.player.damage(1);
				}
			}
			
//			Player player = Game.player;
//			
//			//The tile the zombie was previously on
//			Tile srcTile = Game.tiles.get(y).get(x);
//			//The tile that the zombie will go to next
//			Tile destTile; /*= findPath(srcTile, Game.tiles.get(player.getY()).get(player.getX()),
//					new ArrayList<Tile>());*/
//			
////			//If the player is not in range, then move randomly
////			if (destTile == null)
////			{
//				int randDir = randomDirection();
//				Tile destTile;
////			}
//			
			//If the player has not been found, move randomly
			else
			{
//				int randDir = randomDirection();
				Direction randDir = randomDirection();

				//If there is a direction that the zombie can go, move the zombie that direction
//				if (randDir != 0)
				if (randDir != null)
				{
					move(randDir);
					setRotation(randDir);
				}
			}
			
//			
//			//Set the rotation of the mob
//			if (destTile.getX() < srcTile.getX())
//			{
//				setRotation(1);
//			}
//			else if (destTile.getX() > srcTile.getX())
//			{
//				setRotation(3);
//			}
//			else if (destTile.getY() < srcTile.getY())
//			{
//				setRotation(2);
//			}
//			else if (destTile.getY() > srcTile.getY())
//			{
//				setRotation(4);
//			}
//			
////			//The ways the mob can potentially go
////			ArrayList<Integer> options = new ArrayList<Integer>();
////			
////			if (x > 0 &&
////				Game.tiles.get(y).get(x - 1).isWalkable())
////			{
////				//mob can move left
////				options.add(1);
////			}
////			
////			if (y > 0 && 
////				Game.tiles.get(y - 1).get(x).isWalkable())
////			{
////				//mob can move up
////				options.add(2);
////			}
////			
////			if (x < Game.BOARD_SIZE - 1 && 
////	    		Game.tiles.get(y).get(x + 1).isWalkable())
////			{
////				//mob can move right
////				options.add(3);
////			}
////			
////			if (y < Game.BOARD_SIZE - 1 && 
////    			Game.tiles.get(y + 1).get(x).isWalkable())
////			{
////				//mob can move down
////				options.add(4);
////			}
////			
////			//choose an option randomly (if there is one available) and move in that direction
////			if (options.size() > 0)
////			{
////				move(options.get((int) (Math.random() * options.size())));
////			}
//			
			//set the next move time (only allow moves during an update check)
			nextMoveTime = Game.clock + timePerMove;
		}
	}

	
//	//finds a path from src to dest and return the next tile in that path, ie the next tile that the mob sould go onto. The
//	//zombie is dumb so it might not be the shortest path.
//	//TODO: need to make this breadth, not depth
//	private Tile findPath(Tile src, Tile dest, ArrayList<Tile> visited)
//	{
//		visited.add(src);
//		src.wasVisited = true;
//		
//		//check the adjacent tiles
//		//TODO: try to do something similar to this with addLightSource, removeLightSource, and other recursive tile stuff, rather
//		//than have those long if statements
//		Tile neighbor;
//		for (int i = 1; i < 5; i++)
//		{
//			neighbor = getAdjacentTile(i);
//			//If the neighbor has not already been visited and is walkable
//			if (neighbor != null && !neighbor.wasVisited && neighbor.isWalkable())
//			{				
//				//if the neighbor is the dest, return neighbor (if the src is right next to dest, then the next tile in the path
//				//would go onto the dest)
//				if (neighbor == dest)
//				{	
//					//clear the wasVisiteds
//					//TODO: probably make a static method in tile for this, like clearVisited(ArrayList<Tile> visited)
//					for (Tile tile: visited)
//					{
//						tile.wasVisited = false;
//					}
//					
//					return neighbor;
//				}
//				
//				//If the neighbor is in sight range, try to find a path to dest through the neighbor 
//				else if (neighbor.getX() >= x - sightRange && neighbor.getX() <= x + sightRange &&
//						 neighbor.getY() >= y - sightRange && neighbor.getY() <= y + sightRange)
//				{	
//					Tile pathTile = findPath(neighbor, dest, visited);
//					//If the neighbor returns a path tile to dest, then return that neighbor, since going to it will get you one
//					//step closer to dest.
//					if (pathTile != null)
//					{
//						return neighbor;
//					}
//				}
//			}
//		}
//		
//		//no path found
//		return null;
//	}
	
//	@Override
//	public boolean move(int inDirection)
//	{
//		//show that the mob is no longer within the old tile
//		Game.tiles.get(y).get(x).setMob(null);
//		
//		//rotate the mob in the correct direction
//		setRotation(inDirection);
//		
//		//move left
//		if (inDirection == 1)
//		{
//			x--;
//			for (DisplayElement element: displayElements)
//			{
//				element.moveHorizontally(-1);
//			}
//		}
//		
//		//move up
//		else if (inDirection == 2)
//		{
//			y--;
//			for (DisplayElement element: displayElements)
//			{
//				element.moveVertically(-1);
//			}
//		}
//		
//		//move right
//		else if (inDirection == 3)
//		{
//			x++;
//			for (DisplayElement element: displayElements)
//			{
//				element.moveHorizontally(1);
//			}
//		}
//		
//		//move down
//		else if (inDirection == 4)
//		{
//			y++;
//			for (DisplayElement element: displayElements)
//			{
//				element.moveVertically(1);
//			}
//		}
//		
//		//show that the mob is now within the new tile
//		Game.tiles.get(y).get(x).setMob(this);
//		
//		//In zombie, this is only called if there was a valid move option
//		return true;
//	}
	
	//Return a random direction (that has a walkable tile)
//	public int randomDirection ()
	public Direction randomDirection ()
	{
		//The ways the mob can potentially go
//		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<Direction> options = new ArrayList<Direction>();
		
		if (x > 0 &&
			Game.tiles.get(y).get(x - 1).isWalkable())
		{
			//mob can move left
//			options.add(1);
			options.add(Direction.LEFT);
		}
		
		if (y > 0 && 
			Game.tiles.get(y - 1).get(x).isWalkable())
		{
			//mob can move up
//			options.add(2);
			options.add(Direction.UP);
		}
		
		if (x < Game.BOARD_SIZE - 1 && 
    		Game.tiles.get(y).get(x + 1).isWalkable())
		{
			//mob can move right
//			options.add(3);
			options.add(Direction.RIGHT);
		}
		
		if (y < Game.BOARD_SIZE - 1 && 
			Game.tiles.get(y + 1).get(x).isWalkable())
		{
			//mob can move down
//			options.add(4);
			options.add(Direction.DOWN);
		}
		
		//choose an option randomly (if there is one available) and move in that direction
		if (options.size() > 0)
		{
			return options.get((int) (Math.random() * options.size()));
		}
		
		//no valid direction
		return null;
	}
	
	//Move the zombie to a certain tile
	//TODO: maybe use this in mob instead of move(int)
//	public void move (Tile newTile)
//	{	
//		//show that the mob is no longer within the old tile
//		Game.tiles.get(y).get(x).setMob(null);
//		
//		Tile currentTile = Game.tiles.get(y).get(x);
//		int dx = newTile.getX() - currentTile.getX();
//		int dy = newTile.getY() - currentTile.getY();
//		
//		x += dx;
//		y += dy;
//		for (DisplayElement element: displayElements)
//		{
//			element.move(dx, dy);
//		}
//		
//		//show that the mob is now within the new tile
//		Game.tiles.get(y).get(x).setMob(this);
//	}
	
//	@Override
////	public void setRotation (int inDirection)
//	public void setRotation (Direction inDirection)
//	{
////		super.setRotation (inDirection);
//		
////		//Make the eyes face left
////		if (inDirection == Direction.LEFT)
////		{
////			displayElements.get(1).setPos(x, y + (height / 8.0f));
////    		displayElements.get(2).setPos(x, y + (5 * height / 8.0f));
////		}
////		
////		//Make the eyes face up
////		else if (inDirection == Direction.UP)
////		{
////			displayElements.get(1).setPos(x + (width / 8.0f), y);
////    		displayElements.get(2).setPos(x + ((5 * width) / 8.0f), y);
////		}
////		
////		//Make the eyes face right
////		else if (inDirection == Direction.RIGHT)
////		{
////			displayElements.get(1).setPos(x + ((3 * width) / 4.0f), y + (height / 8.0f));
////    		displayElements.get(2).setPos(x + ((3 * width) / 4.0f), y + (5 * height / 8.0f));
////		}
////		
////		//Make the eyes face down
////		else if (inDirection == Direction.DOWN)
////		{
////			displayElements.get(1).setPos(x + (width / 8.0f), y + ((3 * height) / 4.0f));
////    		displayElements.get(2).setPos(x + ((5 * width) / 8.0f), y + ((3 * height) / 4.0f));
////		}
//	}

	@Override
	public GameObject drop()
	{
		//drop nothing (for now)
		return null;
	}
}
