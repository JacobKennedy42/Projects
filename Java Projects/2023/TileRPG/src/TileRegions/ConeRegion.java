package TileRegions;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import Game.Tile;
import Game.Tile.TileCondition;

public class ConeRegion implements TileRegion {
	private LinkedList<ConeLineRegion> _coneLines;
	
	public ConeRegion (Tile origin, Tile directionTile, int distance, TileCondition selectionCondition) {
		_coneLines = selectTiles(origin, directionTile, distance, selectionCondition);
	}
	
	private LinkedList<ConeLineRegion> selectTiles (Tile origin, Tile directionTile, int distance, TileCondition selectionCondition) {
		LineRegion line = new LineRegion (origin, directionTile, distance, false, selectionCondition);
		LinkedList<ConeLineRegion> coneLines = new LinkedList<ConeLineRegion>();
		
		Tile previousTile = origin;
		for (Tile tile : line) {
			--distance;
			coneLines.add(new ConeLineRegion(tile, previousTile, distance, true, selectionCondition));
			previousTile = tile;
		}
		
		return coneLines;
	}
	
	@Override
	public int getDistanceFromOrigin (Tile targetTile) {
		for (ConeLineRegion coneLine : _coneLines)
			if (coneLine.contains(targetTile))
				return coneLine.getDistanceFromOrigin(targetTile) + _coneLines.indexOf(coneLine) + 1;
		return -1;
	}
	
	@Override
	public boolean contains (Tile soughtTile) {
		for (ConeLineRegion coneLine : _coneLines)
			if (coneLine.contains(soughtTile))
				return true;
		return false;
	}
	
	@Override
	public void colorTilesToBase () {
		for (ConeLineRegion coneLine : _coneLines)
			coneLine.colorTilesToBase();
	}
	
	@Override
	public void colorTilesTo (Color color) {
		for (ConeLineRegion coneLine : _coneLines)
			coneLine.colorTilesTo(color);
	}
	
	@Override
	public Tile getNeighborTowardsOrigin (Tile targetTile) {
		throw new RuntimeException("Not implemented XP");
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return new RegionIterator(_coneLines);
	}
}
