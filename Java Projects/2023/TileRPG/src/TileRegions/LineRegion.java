package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import Board.Tile;
import Board.Tile.TileCondition;

public class LineRegion implements TileRegion {

	private Tile _origin;
	private boolean _includeOrigin;
	private LinkedList<Tile> _tiles;
	
	public LineRegion (Tile origin, TileCondition selectionCondition) {
		_origin = origin;
		_includeOrigin = true;
		_tiles = new LinkedList<Tile>();
		if (origin.fitsCondition(selectionCondition))
			_tiles.addFirst(origin);
	}
	
	public LineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition) {
		this (origin, directionTile, distance, includeOrigin, selectionCondition, 1);
	}
	
	public LineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition, int piercing) {
		_origin = origin;
		_includeOrigin = includeOrigin;
		_tiles = makeLine(origin, directionTile, distance, includeOrigin, selectionCondition, piercing);
	}
	
	private LinkedList<Tile> makeLine (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition, int piercing) {
		LinkedList<Tile> lineSoFar;
		if (distance <= 0 || directionTile == null)
			lineSoFar = new LinkedList<Tile>();
		else if (!origin.fitsCondition(selectionCondition) && origin != _origin)	//_origin should not count as pierced
			if (piercing > 1)
				lineSoFar = makeLine(directionTile,
					directionTile.getNeighborOppositeOf(origin),
					distance - 1,
					true,
					selectionCondition,
					piercing - 1);
			else
				lineSoFar = new LinkedList<Tile>();
		else
			lineSoFar = makeLine(directionTile,
									directionTile.getNeighborOppositeOf(origin),
									distance - 1,
									true,
									selectionCondition,
									piercing);
		
		if (includeOrigin
				&& (origin.fitsCondition(selectionCondition) || piercing > 0))
			lineSoFar.addFirst(origin);
		
		return lineSoFar;
	}
	
	public int getDistanceFromOrigin (Tile targetTile) {
		int index = _tiles.indexOf(targetTile);
		return indexToDistance(index);
	}
	
	private int indexToDistance (int index) {
		if (index == -1 || _includeOrigin)
			return index;
		return index+1;		//since origin is not in list, offset distance by 1
	}
	private int distanceToIndex (int distance) {
		if (distance == -1 || _includeOrigin)
			return distance;
		return distance-1;
	}
	
	public Tile getNeighborTowardsOrigin (Tile targetTile) {
		int distance = getDistanceFromOrigin(targetTile);
		if (distance >= 2)
			return _tiles.get(distanceToIndex(distance)-1);
		if (distance == 1)
			return _origin;
		return null;
	}
	
	public boolean contains (Tile soughtTile) {
		return _tiles.contains(soughtTile);
	}
	
	public void colorTilesToBase () {
		for (Tile tile : _tiles)
			if (tile != null)
				tile.colorToBase();
	}
	
	public void colorTilesTo (Color color) {
		for (Tile tile : _tiles)
			if (tile != null)
				tile.colorTo(color);
	}
	
	public Tile getLastTile () {
		if (_tiles.size() == 0)
			return null;
		return _tiles.getLast();
	}
	public Tile getSecondToLast () {
		if (_tiles.size() < 2)
			return null;
		return _tiles.get(_tiles.size()-2);
	}
	
	public int numTiles () {
		return _tiles.size();
	}

	@Override
	public Iterator<Tile> iterator() {
		return _tiles.iterator();
	}
}
