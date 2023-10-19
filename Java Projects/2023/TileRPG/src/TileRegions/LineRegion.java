package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import Game.Tile;
import Game.Tile.TileCondition;

class LineRegion implements TileRegion {

	private LinkedList<Tile> _tiles;
	
	public LineRegion (Tile origin, TileCondition selectionCondition) {
		_tiles = new LinkedList<Tile>();
		if (origin.fitsCondition(selectionCondition))
			_tiles.add(origin);
	}
	
	public LineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition) {
		this (origin, directionTile, distance, includeOrigin, selectionCondition, 1);
	}
	
	public LineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition, int piercing) {
		_tiles = makeLine(origin, directionTile, distance, includeOrigin, selectionCondition, piercing);
	}
	
	private LinkedList<Tile> makeLine (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition, int piercing) {
		LinkedList<Tile> lineSoFar;
		if (distance <= 0 || directionTile == null)
			lineSoFar = new LinkedList<Tile>();
		else if (!origin.fitsCondition(selectionCondition))
			if (piercing > 0)
				lineSoFar = makeLine(directionTile,
					directionTile.getTileOppositeFrom(origin),
					distance - 1,
					true,
					selectionCondition,
					piercing - 1);
			else
				lineSoFar = new LinkedList<Tile>();
		else
			lineSoFar = makeLine(directionTile,
									directionTile.getTileOppositeFrom(origin),
									distance - 1,
									true,
									selectionCondition,
									piercing);
		
		if (includeOrigin
				&& (origin.fitsCondition(selectionCondition) || piercing >= 0))
			lineSoFar.add(origin);
		else if (distance <= 0 || directionTile == null)
			lineSoFar.add(null);	//null added so line length can still be used as distance from origin
		
		return lineSoFar;
	}
	
	public int getDistanceFromOrigin (Tile targetTile) {
		return _tiles.indexOf(targetTile);
	}
	
	public boolean contains (Tile soughtTile) {
		return _tiles.contains(soughtTile);
	}
	
	public void colorTilesToDefault () {
		for (Tile tile : _tiles)
			if (tile != null)
				tile.colorToDefault();
	}
	
	public void colorTilesTo (Color color) {
		for (Tile tile : _tiles)
			if (tile != null)
				tile.colorTo(color);
	}
	

	@Override
	public Iterator<Tile> iterator() {
		return _tiles.iterator();
	}
}
