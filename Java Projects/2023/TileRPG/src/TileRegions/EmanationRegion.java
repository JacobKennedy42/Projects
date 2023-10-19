package TileRegions;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import Game.Pair;
import Game.Tile;
import Game.Tile.TileCondition;

public class EmanationRegion implements TileRegion {
	
	private LinkedList<Layer> _layers;
	private TileCondition _selectionCondition;
	private TileCondition _propagationCondition;
	
	private LinkedList<Layer> selectTiles (Tile origin, int radius, boolean includeOrigin) {
		LinkedList<Layer> layers = new LinkedList<Layer>();
		Layer previousLayer = null;
		Layer currentLayer = new Layer(origin);
		Layer nextLayer;
		
		do {
			layers.add(currentLayer);
			nextLayer = currentLayer.makeNextLayer(previousLayer, _selectionCondition, _propagationCondition);
			previousLayer = currentLayer;
			currentLayer = nextLayer;
		} while (layers.size() <= radius && currentLayer.size() > 0);
		
		if (!includeOrigin)
			layers.set(0, new Layer());
		
		return layers;	
	}
	
	private Layer getOuterLayer () {
		return _layers.getLast();
	}

	public Collection<Tile> getOuterTilesWith (TileCondition soughtCondition) {
		return getOuterLayer().getTilesWith(soughtCondition);
	}
	
	public boolean addLayer () {
		Layer previousLayer = _layers.size() > 1 ? _layers.get(_layers.size()-2) : null;
		Layer newLayer = getOuterLayer().makeNextLayer(previousLayer, _selectionCondition, _propagationCondition);
		if (newLayer.size() > 0)
			return _layers.add(newLayer);
		return false;
	}

	public int numLayers () {return _layers.size();}
	
	public EmanationRegion (Tile origin, boolean includeOrigin) {
		this(origin, 1, includeOrigin, Tile.NON_BLOCKING);
	}
	
	public EmanationRegion (Tile origin, boolean includeOrigin, TileCondition selectionCondition) {
		this(origin, 1, includeOrigin, selectionCondition);
	}
	
	public EmanationRegion (Tile origin, boolean includeOrigin, TileCondition selectionCondition, TileCondition propagationCondition) {
		this(origin, 1, includeOrigin, selectionCondition, propagationCondition);
	}
	
	public EmanationRegion (Tile origin, int radius, boolean includeOrigin) {
		this(origin, radius, includeOrigin, Tile.NON_BLOCKING);
	}
	
	public EmanationRegion (Tile origin, int radius, boolean includeOrigin, TileCondition selectionCondition) {
		this(origin, radius, includeOrigin, selectionCondition, Tile.NON_BLOCKING);
	}
	
	public EmanationRegion (Tile origin, int radius, boolean includeOrigin, TileCondition selectionCondition, TileCondition propagationCondition) {
		_selectionCondition = selectionCondition;
		_propagationCondition = propagationCondition;
		_layers = selectTiles(origin, radius, includeOrigin);
	}
	
	public LinkedList<Tile> createPathFromOriginTo (Tile destination) {
		LinkedList<Tile> path = new LinkedList<Tile>();
		Tile closestTile = destination;
		for (int distanceFromOrigin = getDistanceFromOrigin(closestTile); distanceFromOrigin >= 0; --distanceFromOrigin) {
			path.addFirst(closestTile);
			closestTile = getNextClosest(closestTile, distanceFromOrigin);
		}
		return path;
	}
	
	private Tile getNextClosest (Tile currentClosest, int currentDistanceFromOrigin) {
		if (currentClosest == null)
			return null;
		
		for (Tile neighbor : currentClosest.getNeighbors())
			if (_layers.get(currentDistanceFromOrigin - 1).contains(neighbor))
				return neighbor;
		return null;
	}
	
	public boolean contains (Tile soughtTile) {
		for (Layer layer : _layers)
			if (layer.contains(soughtTile))
				return true;
		return false;
	}
	
	public int getDistanceFromOrigin(Tile targetTile) {
		int radius = 0;
		for (Layer layer : _layers) {
			if (layer.contains(targetTile))
				return radius;
			++radius;
		}
		return -1;
	}
	
	public void colorTilesToDefault () {
		for (Layer layer : _layers)
			layer.colorTilesToDefault();
	}
	
	public void colorTilesTo (Color color) {
		for (Layer layer : _layers)
			layer.colorTilesTo(color);
	}
	
	private static class EmanationRegionIterator implements Iterator<Tile> {

		Iterator<Layer> _layerIterator;
		Iterator<Tile> _currentTileIterator;
		
		public EmanationRegionIterator (Collection<Layer> layers) {
			_layerIterator = layers.iterator();
			_currentTileIterator = getNextTileIterator();
		}
		
		private Iterator<Tile> getNextTileIterator () {
			if (_layerIterator.hasNext())
				return _layerIterator.next().iterator();
			throw new NoSuchElementException();
		}
		
		@Override
		public boolean hasNext() {
			return _currentTileIterator.hasNext() || _layerIterator.hasNext();
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
		return new EmanationRegionIterator(_layers);
	}
}
