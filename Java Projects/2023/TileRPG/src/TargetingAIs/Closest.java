package TargetingAIs;

import java.util.List;

import Game.Tile;
import Game.Tile.TileCondition;
import TileRegions.EmanationRegion;

public class Closest implements AI {
	
	private TileCondition _selectionCondition;
	private TileCondition _propagationCondition;
	private TileCondition _soughtCondition;
	
	public Closest (TileCondition selectionCondition, TileCondition propagationCondition, TileCondition soughtCondition) {
		_selectionCondition = selectionCondition;
		_propagationCondition = propagationCondition;
		_soughtCondition = soughtCondition;
	}
	
	public Tile getNextMovementTile (Tile origin, int movementPerTurn) {
		EmanationRegion region = new EmanationRegion (origin, movementPerTurn, _selectionCondition, _propagationCondition, _soughtCondition);
		List<Tile> foundTiles = region.getOuterTilesWith(_soughtCondition);
		if (foundTiles.size() == 0)
			return null;
		Tile targetTile = Tile.randomChooseFrom(foundTiles);
		return region.getNextMovementTile(targetTile);
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
