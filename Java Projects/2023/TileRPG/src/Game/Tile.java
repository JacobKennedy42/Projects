package Game;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;

import Mobs.Mob;
import Mobs.Mob.MobState;

public class Tile {
	private FilledShape _tileShape;
	private Mob _mob;
	private LinkedList<Tile> _neighbors;
	
	private static final Color DEFAULT_COLOR = new Color(210, 180, 140);	//tan
	public static final int TILE_WIDTH = 20, TILE_HEIGHT = 20, TILE_GAP = 2;
	@FunctionalInterface
	public static interface TileCondition {
		public boolean tileFitsCondition (Tile tile);
	}
	
	public static final TileCondition ALL = (Tile tile) -> true;
 	public static final TileCondition NON_BLOCKING = (Tile tile) -> tile != null && !tile.isBlocking();
	public static final TileCondition HAS_PLAYER_MOB = (Tile tile) -> tile != null && tile.hasPlayerMob();
	public static final TileCondition IS_CONTAINED_IN (Collection<Tile> tiles) {
		return (Tile tile) -> tiles.contains(tile);
	}
	
	public static class TileState {
		private Mob.MobState _mobState;
		
		public TileState (Mob mob) {
			if (mob != null)
				_mobState = mob.getState();
		}
		
		public Mob.MobState getMobState () {
			return _mobState;
		}
		
		public boolean equals (TileState other) {
			if (other == null)
				return false;
			if (_mobState == null)
				return other._mobState == null;
			return _mobState.equals(other._mobState);
		}
	}
	
	public Tile () {
		_tileShape = new FilledShape(new Rectangle(TILE_WIDTH-TILE_GAP, TILE_HEIGHT-TILE_GAP), DEFAULT_COLOR);
	}
	
	public Tile.TileState getState () {
		return new Tile.TileState(getMob());
	}
	
	public void setState (Tile.TileState newState) {
		if (newState == null || newState.getMobState() == null)
			setMob(null);
		else
			setMob(new Mob (newState.getMobState()));
	}
	
	public Collection<FilledShape> getShapes () {
		Collection<FilledShape> shapes = new LinkedList<FilledShape>();
		shapes.add(_tileShape);
		if (_mob != null)
			shapes.add(_mob.getShape());
		return shapes;
	}
	
	public void setNeighbors (Collection<Tile> neighbors) {
		_neighbors = new LinkedList<Tile>(neighbors);
	}
	
	public Collection<Tile> getNeighbors () {
		return _neighbors;
	}
	
	public boolean isNeighboring (Tile tile) {
		return _neighbors.contains(tile);
	}

	public boolean fitsCondition (TileCondition condition) {
		return condition.tileFitsCondition(this);
	}
	
	public Tile getTileOppositeFrom (Tile tile) {
		int index = _neighbors.indexOf(tile);
		if (index == -1)
			throw new RuntimeException("given tile not adjacent to called tile");
		return _neighbors.get((index+3) % 6);
	}
	
	public void placeNewMob (Mob newMob) {
		if (newMob.getTile() != null)
			throw new RuntimeException("Placed mob must not be linked to a tile yet");
		
		_mob = newMob;
		_mob.setTile(this);
	}

	public Mob getMob() {
		return _mob;
	}
	
	public boolean isBlocking () {
		return getMob() != null;
	}
	
	public void clearTileOfMob() {
		setMob(null);
	}
	
	public boolean moveMobToEmptyTile (Tile destinationTile, int distance) {
		if (_mob == null
			|| destinationTile == null
			|| destinationTile.getMob() != null)
			return false;
		_mob.decrementMovement(distance);
		swapMobsWith(destinationTile);
		return true;
	}
	
	private void swapMobsWith(Tile otherTile) {
		Mob tempMob = otherTile.getMob();
		otherTile.setMob(_mob);
		setMob(tempMob);
	}

	private void setMob (Mob mob) {
		_mob = mob;
		if (mob != null)
			mob.setTile(this);
	}
	
	public void colorTo (Color color) {
		_tileShape._color = color;
	}
	
	public void colorToDefault () {
		_tileShape._color = DEFAULT_COLOR;
	}
	
	public static void colorTo (Collection<Tile> tiles, Color color) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorTo(color);
	}
	
	public static void colorToDefault (Collection<Tile> tiles) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorToDefault();
	}
	
	public boolean hasPlayerMob () {
		if (getMob() == null)
			return false;
		
		return getMob().isPlayerControlled();
	}
	
	public boolean hasNonPlayerMob () {
		if (getMob() == null)
			return false;
		return !getMob().isPlayerControlled();
	}
	
	public boolean isFlanking (Tile target) {
		if (!isNeighboring(target))
			return false;
		Tile flankingTile = target.getTileOppositeFrom(this);
		if (flankingTile == null || flankingTile.getMob() == null)
			return false;
		return getMob().getAllegiance() == flankingTile.getMob().getAllegiance();
	}
	
	public boolean doPlayerActionOn (Tile targetTile) {
		if (!hasPlayerMob())
			return false;
		return getMob().doAction(targetTile);
	}
	
	public boolean doNonPlayerAction () {
		if (!hasNonPlayerMob())
			return false;
		return getMob().doAction(null);
	}
	
	public boolean hasMovableMob () {
		if (getMob() == null)
			return false;
		
		return getMob().getMovementLeft() > 0;
	}
	
	public boolean hasMobWithActionsLeft () {
		if (getMob() == null)
			return false;
		
		return getMob().getActionsLeft() > 0;
	}
	
	public void nextTurnReset () {
		if (_mob != null)
			_mob.nextTurnReset();
	}
}
