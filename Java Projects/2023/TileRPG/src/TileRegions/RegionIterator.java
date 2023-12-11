package TileRegions;

import java.util.Iterator;
import java.util.NoSuchElementException;

import Game.Tile;

class RegionIterator implements Iterator<Tile> {
	
	private Iterator<? extends TileRegion> _regionIterator;
	private Iterator<Tile> _currentTileIterator;
	
	public RegionIterator (Iterable<? extends TileRegion> regions) {
		_regionIterator = regions.iterator();
	}
	
	@Override
	public boolean hasNext() {
		while ((_currentTileIterator == null || !_currentTileIterator.hasNext())
				&& _regionIterator.hasNext())
			_currentTileIterator = _regionIterator.next().iterator();
		return _currentTileIterator.hasNext();
	}
	
	@Override
	public Tile next() {
		if (hasNext())
			return _currentTileIterator.next();
		throw new NoSuchElementException();
	}
}
