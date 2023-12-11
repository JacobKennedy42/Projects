package Game;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Mobs.Mob;
import UI.ColoredRectangle;
import UI.ColoredShape;

public class Tile {
	private ColoredShape _tileShape;
	private Color _baseColor;
	private Mob _mob;
	private LinkedList<Tile> _neighbors;
	
	public Tile _previousTile;	//used for pathfinding algorithms
	
	private static final Color DEFAULT_COLOR = new Color(210, 180, 140);	//tan
	private static final Color SLOW_TILE_COLOR = new Color (180, 120, 80);	//brown
	public static final int TILE_WIDTH = 20, TILE_HEIGHT = 20, TILE_GAP = 2;
	
	@FunctionalInterface
	private static interface Effect {
		public void apply (Mob mob);
	}
	private static final Effect SLOW_TILE = (Mob mob) -> {
		if (mob != null)
			mob.setMovementLeft(0);
	};
	private Effect _onEnterEffect;
	private void triggerOnEnterEffect () {
		if (_onEnterEffect != null)
			_onEnterEffect.apply(_mob);
	}
	public boolean makeSlowTile () {
		if (_onEnterEffect == SLOW_TILE)
			return false;
		
		_onEnterEffect = SLOW_TILE;
		_baseColor = SLOW_TILE_COLOR;
		return true;
	}
	
	@FunctionalInterface
	private static interface TileConditionStatement {
		public boolean tileFitsCondition (Tile tile);
	}
	public static class TileCondition {
		TileConditionStatement _condition;
		
		TileCondition (TileConditionStatement condition) {
			_condition = condition;
		}
		
		public boolean tileFitsCondition (Tile tile) {
			return _condition.tileFitsCondition(tile);
		}
		
		public TileCondition and (TileCondition otherCondition) {
			TileConditionStatement andCondition = (Tile tile) ->
				_condition.tileFitsCondition(tile) && otherCondition._condition.tileFitsCondition(tile); 
			return new TileCondition(andCondition);
		}
		
		public TileCondition or (TileCondition otherCondition) {
			TileConditionStatement andCondition = (Tile tile) ->
				_condition.tileFitsCondition(tile) || otherCondition._condition.tileFitsCondition(tile); 
			return new TileCondition(andCondition);
		}
	}
	
	public static final TileCondition ALL = new TileCondition ((Tile tile) -> true);
	public static final TileCondition NONE = new TileCondition ((Tile tile) -> false);
 	public static final TileCondition NON_BLOCKING = new TileCondition((Tile tile) -> tile != null && !tile.isBlocking());
 	public static final TileCondition NON_SLOW_TILE = new TileCondition((Tile tile) -> tile != null && tile._onEnterEffect != SLOW_TILE);
	public static final TileCondition HAS_PLAYER_MOB = new TileCondition((Tile tile) -> tile != null && tile.hasPlayerMob()); 
	public static final TileCondition IS_CONTAINED_IN (Collection<Tile> tiles) {
		return new TileCondition((Tile tile) -> tiles.contains(tile));
	}

	
	public Tile () {
		_baseColor = DEFAULT_COLOR;
		_tileShape = new ColoredRectangle(TILE_WIDTH-TILE_GAP, TILE_HEIGHT-TILE_GAP, _baseColor);
	}
	private Tile (Tile other) {
		setState(other);
	}
	
	public Tile getState () {
		return new Tile(this);
	}
	
	public void setState (Tile other) {
		setState(other._mob, other._baseColor, other._onEnterEffect);
	}
	private void setState (Mob mob, Color baseColor, Effect onEnterEffect) {
		Mob mobCopy = mob == null ? null : new Mob(mob);
		setMob(mobCopy);
		_baseColor = baseColor;
		if (_tileShape != null)
			colorTo(_baseColor);
		_onEnterEffect = onEnterEffect;
	}
	
	public Collection<ColoredShape> getShapes () {
		Collection<ColoredShape> shapes = new LinkedList<ColoredShape>();
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
	
	public Tile getNeighborOppositeOf (Tile directionTile) {
		return getNeighbor(directionTile, 3);
	}
	public Tile getNeighborLeftOf (Tile directionTile) {
		return getNeighbor(directionTile, 5);
	}
	public Tile getNeighborRightOf (Tile directionTile) {
		return getNeighbor(directionTile, 1);
	}
	public Tile getNeighborOppositeLeftOf (Tile directionTile) {
		return getNeighbor(directionTile, 4);
	}
	public Tile getNeighborOppositeRightOf (Tile directionTile) {
		return getNeighbor(directionTile, 2);
	}
	private Tile getNeighbor (Tile directionTile, int clockwiseTurns) {
		int index = _neighbors.indexOf(directionTile);
		if (index == -1)
			throw new RuntimeException("given tile not adjacent to called tile");
		return _neighbors.get((index+clockwiseTurns) % 6);
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
		
		triggerOnEnterEffect();
		otherTile.triggerOnEnterEffect();
	}

	private void setMob (Mob mob) {
		_mob = mob;
		if (mob != null)
			mob.setTile(this);
	}
	
	public void colorTo (Color color) {
		_tileShape.setColor(color);
	}
	
	public void colorToBase () {
		_tileShape.setColor(_baseColor);
	}
	
	public static void colorTo (Collection<Tile> tiles, Color color) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorTo(color);
	}
	
	public static void colorToDefault (Collection<Tile> tiles) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorToBase();
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
		Tile flankingTile = target.getNeighborOppositeOf(this);
		if (flankingTile == null || flankingTile.getMob() == null)
			return false;
		return getMob().getAllegiance() == flankingTile.getMob().getAllegiance();
	}
	
	public boolean doNonPlayerAction () {
		if (!hasNonPlayerMob())
			return false;
		return getMob().doNonPlayerAction();
	}
	
	public boolean hasMovableMob () {
		return hasPlayerMob() && getMob().getMovementLeft() > 0;
	}
	
	public boolean hasActionableMob () {
		return hasPlayerMob() && getMob().getAvailableActions().size() > 0;
	}
	
	public void nextTurnReset () {
		if (_mob != null)
			_mob.nextTurnReset();
	}
	
	public static int xToCenterX (int x) {
		return x + (TILE_WIDTH/2);
	}
	public static int yToCenterY (int y) {
		return y + (TILE_HEIGHT/2);
	}
	
	public boolean equals (Tile other) {
		return other != null
				&& Objects.equals(_mob, other._mob)
				&& _baseColor.equals(other._baseColor)
				&& _onEnterEffect == other._onEnterEffect;
	}
	
	public static Tile randomChooseFrom (List<Tile> tiles) {
		if (tiles == null || tiles.size() <= 0)
			return null;
		int randomIndex = (int) (Math.random() * tiles.size());
		return tiles.get(randomIndex);
	}
	
	public static List<Tile> randomlyOrder (List<Tile> tiles) {
		if (tiles == null)
			return null;
		
		List<Tile> tilesCopy = new LinkedList<Tile>();
		for (Tile tile : tiles)
			tilesCopy.add(tile);
		tiles = tilesCopy;								//copied to not modify argument
		
		List<Tile> randomList = new LinkedList<Tile>();
		while (tiles.size() > 0) {
			int randomIndex = (int) (Math.random() * tiles.size());
			randomList.add(tiles.remove(randomIndex));
		}
		return randomList;
	}
}
