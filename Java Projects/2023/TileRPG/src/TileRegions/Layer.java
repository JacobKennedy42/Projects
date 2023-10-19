package TileRegions;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import Game.Tile;
import Game.Tile.TileCondition;

class Layer implements Iterable<Tile> {
	private Collection<Tile> _tiles;
	
	public Layer () {
		_tiles = new LinkedList<Tile>();
	}
	
	public Layer (Tile origin) {
		this();
		_tiles.add(origin);
	}
	
	public boolean contains (Tile soughtTile) {
		return _tiles.contains(soughtTile);
	}
	
	public Collection<Tile> getTilesWith (TileCondition soughtCondition) {
		Collection<Tile> foundTiles = new LinkedList<Tile>();
		for (Tile tile : _tiles)
			if (tile.fitsCondition(soughtCondition))
				foundTiles.add(tile);
		return foundTiles;
	}
	
	private boolean add (Tile tile) {
		return _tiles.add(tile);
	}
	
	public int size () {
		return _tiles.size();
	}
	
	public Layer makeNextLayer (Layer previousLayer, TileCondition selectionCondition, TileCondition propagationCondition) {
		Layer nextLayer = new Layer();
		for (Tile tile : _tiles)
			if (tile.fitsCondition(propagationCondition)
				|| _tiles.size() == 1)						//In the case the tile is the origin, it should propagate
				for (Tile neighbor : tile.getNeighbors())
					if (neighbor != null
							&& neighbor.fitsCondition(selectionCondition)
							&& !this.contains(neighbor)
							&& (previousLayer == null || !previousLayer.contains(neighbor))
							&& !nextLayer.contains(neighbor))
						nextLayer.add(neighbor);
		return nextLayer;
	}
	
	public void colorTilesToDefault () {
		for (Tile tile : _tiles)
			tile.colorToDefault();
	}
	
	public void colorTilesTo (Color color) {
		for (Tile tile : _tiles)
			tile.colorTo(color);
	}

	
	@Override
	public Iterator<Tile> iterator() {
		return _tiles.iterator();
	}
}
