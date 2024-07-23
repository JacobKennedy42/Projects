package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import Board.Tile;
import Board.Tile.TileCondition;

public class RadiatingLinesRegion implements TileRegion {
	
	private LinkedList<LineRegion> _lines;
	private TileCondition _selectionCondition;
	
	public RadiatingLinesRegion (Tile origin, int distance, boolean includeOrigin) {
		this (origin, distance, includeOrigin, Tile.NON_BLOCKING);
	}
	
	public RadiatingLinesRegion (Tile origin, int distance, boolean includeOrigin, TileCondition selectionCondition) {
		_selectionCondition = selectionCondition;
		_lines = selectTiles(origin, distance, includeOrigin);
	}
	
	private LinkedList<LineRegion> selectTiles (Tile origin, int distance, boolean includeOrigin) {
		LinkedList<LineRegion> lines = new LinkedList<LineRegion>();
		if (includeOrigin)
			lines.add(new LineRegion(origin, _selectionCondition));
		for (Tile neighbor : origin.getNeighbors())
			lines.addLast(new LineRegion(origin, neighbor, distance, false, _selectionCondition));
		return lines;
	}
	
	public int getDistanceFromOrigin (Tile targetTile) {
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
	
	public boolean contains (Tile soughtTile) {		
		for (LineRegion line : _lines)
			if (line.contains(soughtTile))
				return true;
		return false;
	}
	
	public void colorTilesToBase () {
		for (LineRegion line : _lines)
			line.colorTilesToBase();
	}
	
	public void colorTilesTo (Color color) {
		for (LineRegion line : _lines)
			line.colorTilesTo(color);
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return new RegionIterator(_lines);
	}
}
