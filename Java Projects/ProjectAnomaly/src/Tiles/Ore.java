package Tiles;

import java.util.ArrayList;

import Game.*;
import static Game.Game.Direction;
//import Game.Ore;

public abstract class Ore extends Tile 
{
	protected float veinChance;
	protected float veinGrowth;
	
	public Ore()
	{
		this (0, 0);
//		super();
	}
	
	public Ore(int inX, int inY)
	{
		super(inX, inY);
	}
	
	//recursively makes a vein of the ore be transforming adjacent tiles into ore
	public void makeVein()
	{
		ArrayList<Tile> visited = new ArrayList<Tile>();
		makeVeinHelper(x, y, visited);
		
		clear(visited);
	}
	
	//recursive helper for makeVein(). (origX and origY are where the first ore was placed, the "origin" of the vein)
	private void makeVeinHelper (int origX, int origY, ArrayList<Tile> visited)
	{
		//Look at the adjacent tiles and try to make them ores
		Tile adjTile;
		ArrayList<Direction> dirs = Direction.getDirs();
//		for (int i = 1; i < 5; i++)
		for (int i = 0; i < dirs.size(); i++)
		{
//			adjTile = getAdjacentTile(i);
			adjTile = getAdjacentTile(dirs.get(i));
			
			//only place ores on unvisited stone tiles
			if (adjTile != null  && adjTile.getClass().getSimpleName().equals("Stone") && !getWasVisited(adjTile))
			{
				//place the ore
				if (Math.random() < (veinGrowth / Surface.getDist(adjTile.getX(), adjTile.getY(), origX, origY)))
				{
					Ore newOre = (Ore) getBlankCopy();
					Game.placeTile(newOre, adjTile.getX(), adjTile.getY());
					newOre.makeVeinHelper(origX, origY, visited);
				}
				//don't place the ore
				else
				{
					setWasVisited(adjTile, true);
					visited.add(adjTile);
				}
			}
		}
	}
	
//    //recursively makes a vein of the ore be transforming adjacent tiles into ore (origX and origY are where the first ore was
//    //placed, the "origin" of the vein)
//    public void makeVein (int origX, int origY, ArrayList<Tile> visitedTiles)
//    {
////    	wasVisited = true;
////    	visitedTiles.add(this);
////    	
////    	Game.placeTile(getBlankCopy(), inX, inY);
////    	
//    	for (int i = x - 1; i <= x + 1; i++)
//		{
//			for (int j = y - 1; j <= y + 1; j++)
//			{
//				//Make sure the tile is in the board, non-diagonal, and in the sight range
//				if (i >= 0 && i < Game.BOARD_SIZE &&
//					j >= 0 && j < Game.BOARD_SIZE &&
//					!(i == x && j == y) &&
//					(i == x || j == y))
//				{
//					Tile tempTile = Game.tiles.get(j).get(i);
//					
//					//TODO: figure out why having a very small vein chance still gives large veins. Might be rounding error
//					
//					//Chance of an ore block being placed next to this ones
//					if (tempTile.getType() == STONE_TYPE &&
//						!tempTile.wasVisited)
//					{
//						tempTile.wasVisited = true;
//						visitedTiles.add(tempTile);
//						
//						if (Math.random() < (veinGrowth / Surface.getDist(i, j, origX, origY)))
//						{
//							Ore newOre = (Ore) getBlankCopy();
//							Game.placeTile(newOre, i, j);
//							newOre.makeVein(origX, origY, visitedTiles);
//						}
//					}
//				}
//			}
//		}
//    }
    
    
}
