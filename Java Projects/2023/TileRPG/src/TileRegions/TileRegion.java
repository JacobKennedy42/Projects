package TileRegions;

import java.awt.Color;

import Game.Tile;

public interface TileRegion extends Iterable<Tile> {
	public int getDistanceFromOrigin(Tile targetTile);
	public boolean contains (Tile soughtTile);
	public void colorTilesToDefault ();
	public void colorTilesTo (Color color);
}
