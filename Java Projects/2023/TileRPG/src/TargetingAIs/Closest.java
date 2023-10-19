package TargetingAIs;

import java.util.Collection;
import java.util.LinkedList;

import Game.Tile;
import Game.Tile.TileCondition;
import TileRegions.EmanationRegion;

public class Closest implements AI {
	
	private TileCondition _selectionCondition;
	private TileCondition _propogationCondition;
	private TileCondition _soughtCondition;
	
	public Closest (TileCondition selectionCondition, TileCondition propogationCondition, TileCondition soughtCondition) {
		_selectionCondition = selectionCondition;
		_propogationCondition = propogationCondition;
		_soughtCondition = soughtCondition;
	}
	
	public LinkedList<Tile> getPathToTarget (Tile origin) {
		EmanationRegion region = new EmanationRegion(origin, false, _selectionCondition, _propogationCondition);
		Collection<Tile> foundTiles;
		while ((foundTiles = region.getOuterTilesWith(_soughtCondition)).size() <= 0
				&& region.addLayer());
		Tile targetTile = randomChooseTile(foundTiles);
		return region.createPathFromOriginTo(targetTile);
	}
	
	private Tile randomChooseTile (Collection<Tile> tiles) {
		if (tiles == null || tiles.size() <= 0)
			return null;
		
		int randomIndex = (int) (Math.random() * tiles.size());
		int i = 0;
		for (Tile tile : tiles) {
			if (i == randomIndex)
				return tile;
			++i;
		}
		
		throw new RuntimeException("Got to the end of chooseRandomTile");
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof Closest))
			return false;
		Closest otherAI = (Closest) other;
		return _selectionCondition == otherAI._selectionCondition
				&& _soughtCondition == otherAI._soughtCondition;
	}
}
