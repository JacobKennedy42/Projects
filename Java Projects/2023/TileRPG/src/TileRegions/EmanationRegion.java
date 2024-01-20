package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Game.Tile;
import Game.Tile.TileCondition;

public class EmanationRegion implements TileRegion {
	
	private LinkedList<Layer> _layers;
	private TileCondition _selectionCondition;
	private TileCondition _propagationCondition;
	
	private LinkedList<Layer> selectTiles (Tile origin, int numLayers, int layerThickness, boolean includeOrigin) {
		LinkedList<Layer> layers = selectTiles(new Layer(origin), numLayers, layerThickness, Tile.NONE);
		if (!includeOrigin)
			layers.set(0, new Layer());
		return layers;
	}
	
	private LinkedList<Layer> selectTiles (Tile origin, int layerThickness, TileCondition soughtCondition) {
		return selectTiles(new Layer(origin), Integer.MAX_VALUE, layerThickness, soughtCondition);
	}
	
	private LinkedList<Layer> selectTiles (Layer startingLayer, int numLayers, int layerThickness, TileCondition soughtCondition) {
		LinkedList<Layer> layers = new LinkedList<Layer>();
		Layer nextLayer = startingLayer;
		
		do {	
			layers.add(nextLayer);
			nextLayer = nextLayer.makeNextLayer(layerThickness, _selectionCondition, _propagationCondition);
		} while (layers.size() < numLayers && nextLayer.size() > 0 && layers.getLast().getTilesWith(soughtCondition).size() <= 0);
		
		return layers;
	}
	
	private void removeTilesWith (TileCondition removalCondition) {
		for (Layer layer : _layers)
			layer.removeTilesWith(removalCondition);
	}
	
	public List<Tile> getMovementPath (Tile targetTile) {
		List<Tile> path = createPathFromOriginTo (targetTile, true);
		if (path == null || path.size() == 0)
			return null;
		if (path.size() == 1)
			return path;

		Layer originLayer = getOriginLayer();
		Layer firstLayer = getInnerLayer();
		Tile furthestTile = null;
		for (Tile tile : path) {
			if (!originLayer.contains(tile) && !firstLayer.contains(tile))
				break;
			furthestTile = tile;
		}
		return createPathFromOriginTo (furthestTile, true);
	}
	
	private Layer getOriginLayer () {
		if (_layers.size() < 1)
			return null;
		return _layers.get(0);
	}
	
	private Layer getInnerLayer () {
		if (_layers.size() < 2)
			return null;
		return _layers.get(1);
	}
	
	public List<Tile> getInnerTilesWith (TileCondition soughtCondition) {
		return getInnerLayer().getTilesWith(soughtCondition);
	}
	
	private Layer getOuterLayer () {
		return _layers.getLast();
	}

	public Tile getClosestOuterTileWith (TileCondition soughtCondition) {
		List<Tile> outerTiles = getOuterLayer().getTilesWith(soughtCondition);
		int closestDistance = Integer.MAX_VALUE;
		Tile closestTile = null;
		for (Tile tile : outerTiles) {
			int tileDistance = getDistanceFromOrigin(tile);
			if (tileDistance < closestDistance) {
				closestDistance = tileDistance;
				closestTile = tile;
			}
		}
		return closestTile;
	}
	
	public boolean addLayer () {
		Layer newLayer = getOuterLayer().makeNextLayer(_selectionCondition, _propagationCondition);
		if (newLayer.size() > 0)
			return _layers.add(newLayer);
		return false;
	}

	public int numLayers () {return _layers.size();}
	
	public int numTiles () {
		int sum = 0;
		for (Layer layer : _layers)
			sum += layer.size();
		return sum;
	}
	
	public EmanationRegion (Tile origin, int layerThickness, TileCondition selectionCondition, TileCondition propagationCondition, TileCondition soughtCondition) {
		_selectionCondition = selectionCondition;
		_propagationCondition = propagationCondition;
		_layers = selectTiles(origin, layerThickness, soughtCondition);
	}
	
	public EmanationRegion (Tile origin, int layerThickness, TileCondition selectionCondition, TileCondition propagationCondition, TileCondition soughtCondition, TileCondition removalCondition) {
		this (origin, layerThickness, selectionCondition, propagationCondition, soughtCondition);
		removeTilesWith(removalCondition);
	}
	
	public EmanationRegion (Tile origin, int numLayers, int layerThickness, boolean includeOrigin, TileCondition selectionCondition, TileCondition propagationCondition) {
		_selectionCondition = selectionCondition;
		_propagationCondition = propagationCondition;
		_layers = selectTiles(origin, numLayers, layerThickness, includeOrigin);
	}
	
	private List<Tile> createPathFromOriginTo (Tile targetTile, boolean includeTarget) {
		if (targetTile == null)
			return null;
		
		Layer targetLayer = getLayerContaining(targetTile);
		if (targetLayer == null)
			return null;
		return targetLayer.createPathFromOriginTo(targetTile, includeTarget);
	}
	
	public boolean contains (Tile soughtTile) {
		return getLayerContaining(soughtTile) != null;
	}
	
	public int getDistanceFromOrigin(Tile targetTile) {
		Layer targetLayer = getLayerContaining(targetTile);
		if (targetLayer == null)
			return -1;
		return targetLayer.getDistanceFromOrigin(targetTile);
	}
	
	public Tile getNeighborTowardsOrigin (Tile targetTile) {
		Layer targetLayer = getLayerContaining(targetTile);
		if (targetLayer == null)
			return null;
		return targetLayer.getNeighborTowardsOrigin(targetTile);
	}
	
	private Layer getLayerContaining (Tile targetTile) {
		for (Layer layer : _layers)
			if (layer.contains(targetTile))
				return layer;
		return null;
	}
	
	public void colorTilesToBase () {
		for (Layer layer : _layers)
			layer.colorTilesToBase();
	}
	
	public void colorTilesTo (Color color) {
		for (Layer layer : _layers)
			layer.colorTilesTo(color);
	}

	@Override
	public Iterator<Tile> iterator() {
		return new RegionIterator(_layers);
	}
}
