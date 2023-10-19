package TargetingAIs;

import java.util.LinkedList;

import Game.Tile;

public interface AI {
	public LinkedList<Tile> getPathToTarget (Tile origin);
}
