package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import Game.Tile;
import Game.Tile.TileCondition;

public class ConeLineRegion implements TileRegion {

	private LinkedList<LineRegion> _lines;
	private TileCondition _selectionCondition;
	
	public ConeLineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin) {
		this (origin, directionTile, distance, includeOrigin, Tile.NON_BLOCKING);
	}
	public ConeLineRegion (Tile origin, Tile directionTile, int distance, boolean includeOrigin, TileCondition selectionCondition) {
		_selectionCondition = selectionCondition;
		_lines = selectTiles(origin, directionTile, distance, includeOrigin);
	}
	
	private LinkedList<LineRegion> selectTiles (Tile origin, Tile directionTile, int distance, boolean includeOrigin) {
		LinkedList<LineRegion> lines = new LinkedList<LineRegion>();
		if (includeOrigin)
			lines.add(new LineRegion(origin, _selectionCondition));
		Tile leftTile = origin.getNeighborOppositeLeftOf(directionTile);
		lines.addLast(new LineRegion(origin, leftTile, distance, false, _selectionCondition));
		Tile rightTile = origin.getNeighborOppositeRightOf(directionTile);
		lines.addLast(new LineRegion(origin, rightTile, distance, false, _selectionCondition));
		return lines;
	}
	
	@Override
	public int getDistanceFromOrigin(Tile targetTile) {
		for (LineRegion line : _lines)
			if (line.contains(targetTile))
				return line.getDistanceFromOrigin(targetTile);
		return -1;
	}
	
	@Override
	public Tile getNeighborTowardsOrigin (Tile targetTile) {
		for (LineRegion line : _lines) {
			Tile neighbor = line.getNeighborTowardsOrigin(targetTile);
			if (neighbor != null)
				return neighbor;
		}
		return null;	
	}

	@Override
	public boolean contains(Tile soughtTile) {
		for (LineRegion line : _lines)
			if (line.contains(soughtTile))
				return true;
		return false;
	}

	@Override
	public void colorTilesToBase() {
		for (LineRegion line : _lines)
			line.colorTilesToBase();
	}

	@Override
	public void colorTilesTo(Color color) {
		for (LineRegion line : _lines)
			line.colorTilesTo(color);
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return new RegionIterator(_lines);
	}
}
