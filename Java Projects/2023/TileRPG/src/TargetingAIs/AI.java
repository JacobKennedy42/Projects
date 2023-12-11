package TargetingAIs;

import Game.Tile;

public interface AI {
	public Tile getNextMovementTile (Tile origin, int movementPerTurn);
}
