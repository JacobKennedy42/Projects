package TileRegions;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import Game.Tile;
import Game.Tile.TileCondition;

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
	
	public boolean contains (Tile soughtTile) {
		for (LineRegion line : _lines)
			if (line.contains(soughtTile))
				return true;
		return false;
	}
	
	public void colorTilesToDefault () {
		for (LineRegion line : _lines)
			line.colorTilesToDefault();
	}
	
	public void colorTilesTo (Color color) {
		for (LineRegion line : _lines)
			line.colorTilesTo(color);
	}
	
	private static class RadiatingLineRegionIterator implements Iterator<Tile> {
		Iterator<LineRegion> _lineIterator;
		Iterator<Tile> _currentTileIterator;
		
		public RadiatingLineRegionIterator (Collection<LineRegion> lines) {
			_lineIterator = lines.iterator();
			_currentTileIterator = getNextTileIterator();
		}
		
		private Iterator<Tile> getNextTileIterator () {
			if (_lineIterator.hasNext())
				return _lineIterator.next().iterator();
			throw new NoSuchElementException();
		}

		@Override
		public boolean hasNext() {
			return _currentTileIterator.hasNext() || _lineIterator.hasNext();
		}

		@Override
		public Tile next() {
			if (!_currentTileIterator.hasNext())
				_currentTileIterator = getNextTileIterator();
			if (_currentTileIterator.hasNext())
				return _currentTileIterator.next();
			throw new NoSuchElementException();
		}
		
		
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return new RadiatingLineRegionIterator(_lines);
	}
}
