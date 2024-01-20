package TileRegions;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Game.Tile;
import Game.Tile.TileCondition;

class Layer implements TileRegion {
	private List<Tile> _tiles;
	private Layer _previousLayer;
	
	public Layer () {
		_tiles = new LinkedList<Tile>();
	}
	
	public Layer (Tile origin) {
		this();
		_tiles.add(origin);
		origin._previousTile = null;
	}
	
	private Layer (Layer previousLayer) {
		this();
		_previousLayer = previousLayer;
	}
	
	public boolean contains (Tile soughtTile) {
		return _tiles.contains(soughtTile);
	}
	
	public List<Tile> getTilesWith (TileCondition soughtCondition) {
		List<Tile> foundTiles = new LinkedList<Tile>();
		for (Tile tile : _tiles)
			if (tile.fitsCondition(soughtCondition))
				foundTiles.add(tile);
		return foundTiles;
	}
	
	public void removeTilesWith (TileCondition removalCondition) {
		List<Tile> toBeRemoved = new LinkedList<Tile>();
		for (Tile tile : _tiles)
			if (tile.fitsCondition(removalCondition))
				toBeRemoved.add(tile);
		_tiles.removeAll(toBeRemoved);
	}
	
	private boolean add (Tile tile) {
		return _tiles.add(tile);
	}
	
	public int size () {
		return _tiles.size();
	}
	
	public Layer makeNextLayer (TileCondition selectionCondition, TileCondition propagationCondition) {
		return makeNextLayer(1, selectionCondition, propagationCondition);
	}
	
	public Layer makeNextLayer (int thickness, TileCondition selectionCondition, TileCondition propagationCondition) {
		Layer nextLayer = new Layer(this);
		List<Tile> unpropagatedTiles = _tiles;
		List<Tile> selectedTiles;
		for (int i = 0; i < thickness && unpropagatedTiles.size() > 0; ++i){
			selectedTiles = propagateTiles(nextLayer, unpropagatedTiles, selectionCondition, propagationCondition);
			for (Tile tile : selectedTiles)
				nextLayer.add(tile);
			unpropagatedTiles = selectedTiles;
		}
		return nextLayer;
	}
	
	private List<Tile> propagateTiles (Layer nextLayer, List<Tile> unpropagatedTiles, TileCondition selectionCondition, TileCondition propagationCondition) {
		List<Tile> selectedTiles = new LinkedList<Tile>();
		for (Tile tile : unpropagatedTiles)
			if (tile.fitsCondition(propagationCondition)
					|| _tiles.contains(tile))		//tiles in this layer should propagate
				for (Tile neighbor : tile.getNeighbors())
					if (neighbor != null
							&& neighbor.fitsCondition(selectionCondition)
							&& !this.contains(neighbor)
							&& (_previousLayer == null || !_previousLayer.contains(neighbor))
							&& !nextLayer.contains(neighbor)) {
						neighbor._previousTile = tile;
						selectedTiles.add(neighbor);
					}
		return selectedTiles;
	}
	
	public List<Tile> createPathFromOriginTo (Tile targetTile, boolean includeTarget) {
		if (!_tiles.contains(targetTile))
			throw new RuntimeException("Target tile must be contained in called Layer.");
		
		LinkedList<Tile> path = new LinkedList<Tile>();
		Tile currentTile = targetTile;
		if (!includeTarget)
			currentTile = currentTile._previousTile;
		while (currentTile != null) {
			path.addFirst(currentTile);
			currentTile = currentTile._previousTile;
		}
		return path;
	}
	
	@Override
	public Tile getNeighborTowardsOrigin (Tile targetTile) {
		if (!_tiles.contains(targetTile))
			throw new RuntimeException("Target tile must be contained in called Layer.");
		
		return targetTile._previousTile;
	}
	
	@Override
	public int getDistanceFromOrigin(Tile targetTile) {
		if (!_tiles.contains(targetTile))
			throw new RuntimeException("Target tile must be contained in called Layer.");
		
		int distance = 0;
		Tile currentTile = targetTile;
		while (currentTile._previousTile != null) {
			++distance;
			currentTile = currentTile._previousTile;
		}
		return distance;
	}
	
	@Override
	public void colorTilesToBase () {
		for (Tile tile : _tiles)
			tile.colorToBase();
	}
	
	@Override
	public void colorTilesTo (Color color) {
		for (Tile tile : _tiles)
			tile.colorTo(color);
	}

	@Override
	public Iterator<Tile> iterator() {
		return _tiles.iterator();
	}
}
