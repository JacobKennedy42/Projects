package TargetingAIs;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Game.Tile;
import Game.Tile.TileCondition;
import Items.Action;
import TileRegions.EmanationRegion;
import TileRegions.TileRegion;

public class Closest implements AI {
	
	private TileCondition _propogationCondition;
	
	public Closest (TileCondition propogationCondition) {
		_propogationCondition = propogationCondition;
	}
	
//	public List<Tile> getMovementPath (Tile origin, int movementPerTurn, Action action) {
//		Collection<Tile> soughtTiles = getSoughtTiles(origin.getAllPlayers(), action);
	public List<Tile> getMovementPath (Tile origin, int movementPerTurn, Collection<Tile> soughtTiles) {
//		Collection<Tile> soughtTiles = getSoughtTiles(origin.getAllPlayers(), action);
		EmanationRegion movementRegion = new EmanationRegion(origin, movementPerTurn,
				Tile.HAS_SPACE_FOR(origin.getMob()).or(Tile.HAS_PLAYER_MOB),
				_propogationCondition,
				Tile.IS_CONTAINED_IN(soughtTiles));
		Tile destination = movementRegion.getClosestOuterTileWith(Tile.IS_CONTAINED_IN(soughtTiles));
		
		return movementRegion.getMovementPath(destination);
	}
	
//	private Collection<Tile> getSoughtTiles (Collection<Tile> playerTiles, Action action) {
//		HashSet<Tile> soughtTiles =  new HashSet<Tile>();
//		for (Tile target : playerTiles)
//			soughtTiles.addAll(action.getAreaOfInfluence(target));	//Assumes that if player can hit enemy, enemy can hit player
//		return soughtTiles;
//	}
	
	public Tile getTargetFrom (Tile origin, Action action) {
		if (origin == null)
			return null;
		
		TileRegion inRangeTiles = action.getTilesInRangeFrom(origin);
		for (Tile tile : inRangeTiles)
			if (tile != null && tile.fitsCondition(Tile.HAS_PLAYER_MOB))
				return tile;
		return null;
	}
	
	public boolean equals (Closest other) {
		return other != null
				&&_propogationCondition == other._propogationCondition;
	}
}
