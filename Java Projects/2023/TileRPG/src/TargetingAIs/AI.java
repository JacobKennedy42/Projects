package TargetingAIs;

import java.util.Collection;
import java.util.List;

import Game.Tile;
import Items.Action;

public interface AI {
//	public List<Tile> getMovementPath (Tile origin, int movementPerTurn, Action action);
	public List<Tile> getMovementPath (Tile origin, int movementPerTurn, Collection<Tile> soughtTiles);
	public Tile getTargetFrom (Tile origin, Action action);
}
