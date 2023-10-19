package TileRegions;

import java.awt.Color;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import Game.Tile;
import Game.Pair;

class FreeFormRegion implements TileRegion {
	
	private LinkedList<Pair<Tile, Integer>> _tiles;
	
	public FreeFormRegion () {
		_tiles = new LinkedList<Pair<Tile, Integer>>();
	}
	
	public void colorTilesToDefault () {
		for (Pair<Tile, Integer> td: _tiles)
			td.first.colorToDefault();
	}
	
	public void colorTilesTo (Color color) {
		for (Pair<Tile, Integer> td: _tiles)
			td.first.colorTo(color);
	}
	
	public int getDistanceFromOrigin (Tile targetTile) {
		Pair<Tile, Integer> td = getTileAndDistance(targetTile);
		if (td == null)
			return -1;
		return td.second;
	}
	
	public boolean add (Pair<Tile, Integer> tileAndDistance) {
		return _tiles.add(tileAndDistance);
	}
	
	public Pair<Tile, Integer> removeFirst () {
		try 							 {return _tiles.removeFirst();}
		catch (NoSuchElementException e) {return null;}
	}
	
	public boolean remove (Pair<Tile, Integer> tileAndDistance) {
		return _tiles.remove(tileAndDistance);
	}
	
	public boolean remove (Tile soughtTile) {
		return _tiles.remove(getTileAndDistance(soughtTile));
	}
	
	public int size () {
		return _tiles.size();
	}
	
	public boolean contains (Tile soughtTile) {
		return getTileAndDistance(soughtTile) != null;
	}
	
	private Pair<Tile, Integer> getTileAndDistance (Tile soughtTile) {
		if (soughtTile == null)
			return null;
		for (Pair<Tile, Integer> td : _tiles)
			if (td.first == soughtTile)
				return td;
		return null;
	}
}
