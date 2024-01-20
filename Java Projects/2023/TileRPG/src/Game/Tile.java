package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Mobs.CompoundMob;
import Mobs.CreatureMob;
import Mobs.EnemyMob;
import Mobs.Mob;
import Mobs.PlayerMob;
import TileRegions.LineRegion;
import TileRegions.TileRegion;
import UI.Canvas.CanvasTileCallback;
import UI.ColoredPolygon;
import UI.ColoredShape;

public class Tile {
	private ColoredShape _outline;
	private Color _outlineColor;
	private ColoredShape _tileShape;
	private Color _baseColor;
	private Mob _mob;
	private LinkedList<Tile> _neighbors;
	
	public Tile _previousTile;	//used for pathfinding algorithms
	
	private static final Color DEFAULT_COLOR = new Color(210, 180, 140);	//tan
	public static final Color SLOW_TILE_COLOR = new Color (180, 120, 80);	//brown
	private static final Color DEFAULT_OUTLINE_COLOR = new Color (180, 120, 80, 0);	//transparent
	public static final int TILE_WIDTH = 40, TILE_HEIGHT = (int)(TILE_WIDTH * (Math.sqrt(3)/2))+1, TILE_GAP = TILE_WIDTH/10;
	
	@FunctionalInterface
	private static interface Effect {
		public void apply (CreatureMob mob);
	}
	private static final Effect SLOW_TILE = (CreatureMob mob) -> {
		if (mob != null)
			mob.setMovementLeft(0);
	};
	private Effect _onEnterEffect;
	private void triggerOnEnterEffect () {
		if (_onEnterEffect != null && hasCreatureMob())
			_onEnterEffect.apply(getCreatureMob());
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
 	public static final TileCondition NON_BLOCKING = new TileCondition((Tile tile) -> tile != null && !tile.hasMob());
 	public static final TileCondition NON_SLOW_TILE = new TileCondition((Tile tile) -> tile != null && tile._onEnterEffect != SLOW_TILE);
	public static final TileCondition HAS_PLAYER_MOB = new TileCondition((Tile tile) -> tile != null && tile.hasPlayerMob());
	public static final TileCondition HAS_ENEMY_MOB = new TileCondition((Tile tile) -> tile != null && tile.hasEnemyMob());
	public static final TileCondition HAS_SPACE_FOR (Mob mob) {
		return new TileCondition((Tile tile) -> mob.canFitIn(tile));
	}
	public static final TileCondition IS_CONTAINED_IN (TileRegion tiles) {
		return new TileCondition((Tile tile) -> tiles.contains(tile));
	}
	public static final TileCondition IS_CONTAINED_IN (Collection<Tile> tiles) {
		return new TileCondition((Tile tile) -> tiles.contains(tile));
	}

	
	public Tile () {
		_baseColor = DEFAULT_COLOR;
		_outlineColor = DEFAULT_OUTLINE_COLOR;
		_outline = makeHexagon(DEFAULT_OUTLINE_COLOR, TILE_WIDTH+TILE_GAP);
		_tileShape = makeHexagon(_baseColor, TILE_WIDTH - TILE_GAP);
	}
	private Tile (Tile other) {
		setState(other);
	}
	
	private static ColoredPolygon makeHexagon (Color color, int width) {
		double xUnit = width/2;
		double yUnit = xUnit / Math.sqrt(3);
		int[] xPoints = new int[] {(int) xUnit, 0, 0, (int) xUnit, (int) (2*xUnit), (int) (2*xUnit)};
		int[] yPoints = new int[] {0, (int) yUnit, (int) (3*yUnit), (int) (4*yUnit), (int) (3*yUnit), (int) yUnit};
		return new ColoredPolygon(xPoints, yPoints, color);
	}
	
	public Tile getState () {
		return new Tile(this);
	}
	
	public void setState (Tile other) {
		setState(other._mob, other._baseColor, other._outlineColor, other._onEnterEffect);
	}
	private void setState (Mob mob, Color baseColor, Color outlineColor, Effect onEnterEffect) {
		Mob mobCopy = mob == null ? null : mob.getState();
		attachMob(mobCopy);
		_baseColor = baseColor;
		if (_tileShape != null)
			colorTo(_baseColor);
		_outlineColor = outlineColor;
		if (_outline != null)
			colorOutlineTo(outlineColor);
		_onEnterEffect = onEnterEffect;
	}
	
	public void draw (Graphics2D g, int centerX, int centerY) {
		_outline.draw(g, centerX, centerY);
		_tileShape.draw(g, centerX, centerY);
		if (_mob != null)
			_mob.draw(g, centerX, centerY);
	}
	
	public void setNeighbors (List<Tile> neighbors) {
		if (_neighbors != null)
			throw new RuntimeException("Neighbors can only be set once");
		
		_neighbors = new LinkedList<Tile>(neighbors);
	}
	
	public List<Tile> getNeighbors () {
		return _neighbors;
	}
	
	public boolean isNeighboring (Tile tile) {
		return _neighbors.contains(tile);
	}

	public boolean fitsCondition (TileCondition condition) {
		return condition.tileFitsCondition(this);
	}
	
	public List<Tile> getTilesWith (TileCondition condition) {
		List<Tile> soughtTiles = new LinkedList<Tile>();
		List<Tile> visitedTiles = new LinkedList<Tile>();
		List<Tile> unvisitedTiles = new LinkedList<Tile>();
		unvisitedTiles.add(this);
		
		Tile currentTile;
		while (unvisitedTiles.size() > 0) {
			currentTile = unvisitedTiles.remove(0);
			if (currentTile == null)
				continue;
			visitedTiles.add(currentTile);
			if (currentTile.fitsCondition(condition))
				soughtTiles.add(currentTile);
			for (Tile neighbor : currentTile.getNeighbors())
				if (!visitedTiles.contains(neighbor) && !unvisitedTiles.contains(neighbor))
					unvisitedTiles.add(neighbor);
		}
		
		return soughtTiles;
	}
	
	public Collection<Tile> getAllPlayers () {
		return getTilesWith(Tile.HAS_PLAYER_MOB);
	}
	public Collection<Tile> getAllEnemies () {
		return getTilesWith(Tile.HAS_ENEMY_MOB);
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

	public Mob getMob() {
		return _mob;
	}
	
	public CreatureMob getCreatureMob() {
		if (!hasCreatureMob())
			throw new RuntimeException("Tile does not contain a CreatureMob");
		return (CreatureMob) getMob();
	}
	public EnemyMob getEnemyMob() {
		if (!hasEnemyMob())
			throw new RuntimeException("Tile does not contain an EnemyMob");
		return (EnemyMob) getMob();
	}
	public PlayerMob getPlayerMob() {
		if (!hasPlayerMob())
			throw new RuntimeException("Tile does not contain a PlayerMob");
		return (PlayerMob) getMob();
	}
		
	public boolean moveMobToEmptyTile (Tile destinationTile, int distance) {
		if (_mob == null
				|| !hasCreatureMob()
				|| destinationTile == null
				|| destinationTile.getMob() != null)
			return false;
		
		getCreatureMob().decrementMovement(distance);
		swapMobsWith(destinationTile);
		return true;
	}
	
	private void swapMobsWith(Tile otherTile) {
		Mob tempMob = otherTile.getMob();
		otherTile.attachMob(_mob);
		attachMob(tempMob);
		
		triggerOnEnterEffect();
		otherTile.triggerOnEnterEffect();
	}

	public void detachMob () {
		if (_mob == null)
			return;
		_mob.setTile(null);
		_mob = null;
	}
	public void attachMob (Mob mob) {
		detachMob();
		
		_mob = mob;
		if (mob == null)
			return;
		if (mob.getTile() != null)
			mob.getTile().detachMob();
		mob.setTile(this);
	}
	
	public void pushMobAway(Tile target, int distance) {
		if (!isNeighboring(target))
			throw new RuntimeException("target must be neighboring tile");
		if (!target.hasMob() || target.hasCompoundMob())
			return;
		
		Tile directionTile = target.getNeighborOppositeOf(this);
		LineRegion pushPath = new LineRegion(target, directionTile, distance, false, Tile.NON_BLOCKING);
		Tile lastTile = pushPath.getLastTile();
		if (lastTile != null && lastTile.hasMob()) {
			if (pushPath.getSecondToLast() != null)
				target.moveMobToEmptyTile(pushPath.getSecondToLast(), 0);
			target.getMob().damage(distance);
			lastTile.getMob().damage(distance);
			return;
		}
		target.moveMobToEmptyTile(lastTile, 0);
		if (pushPath.numTiles() < distance)
			target.getMob().damage(distance);
	}
	
	public void colorTo (Color color) {
		_tileShape.setColor(color);
	}
	
	public void colorToBase () {
		_tileShape.setColor(_baseColor);
	}
	
	public static void colorTo (Iterable<Tile> tiles, Color color) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorTo(color);
	}
	
	public static void colorToBase (Iterable<Tile> tiles) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorToBase();
	}
	
	public void colorOutlineTo(Color color) {
		_outlineColor = color;
		_outline.setColor(_outlineColor);
	}
	
	public void colorOutlineToBase() {
		_outlineColor = DEFAULT_OUTLINE_COLOR;
		_outline.setColor(_outlineColor);
	}
	
	public static void colorOutlineTo (Iterable<Tile> tiles, Color color) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorOutlineTo(color);
	}
	
	public static void colorOutlineToBase (Iterable<Tile> tiles) {
		for (Tile tile : tiles)
			if (tile != null)
				tile.colorOutlineToBase();
	}
	
	public boolean hasPlayerMob () {
		return getMob() instanceof PlayerMob;
	}
	public boolean hasEnemyMob () {
		return getMob() instanceof EnemyMob;
	}
	public boolean hasCreatureMob () {
		return getMob() instanceof CreatureMob;
	}
	public boolean hasCompoundMob() {
		return getMob() instanceof CompoundMob;
	}
	public boolean hasMob () {
		return getMob() != null;
	}
	
	public boolean isFlanking (Tile target) {
		if (!isNeighboring(target))
			return false;
		Tile flankingTile = target.getNeighborOppositeOf(this);
		if (!hasCreatureMob() || flankingTile == null || !flankingTile.hasCreatureMob())
			return false;
		return getCreatureMob().isFriendlyTo(flankingTile.getCreatureMob());
	}
	
	public void hover (CanvasTileCallback callback) {
		callback.callback(this);
		if (hasCreatureMob())
			getCreatureMob().hover();
	}
	
	public void dehover (CanvasTileCallback callback) {
		if (hasCreatureMob())
			getCreatureMob().dehover();
	}
	
	public boolean hasMovableMob () {
		return hasPlayerMob() && getCreatureMob().getMovementLeft() > 0;
	}
	
	public boolean hasActionableMob () {
		return hasPlayerMob() && getCreatureMob().getAvailableActions().size() > 0;
	}
	
	public boolean equals (Tile other) {
		return other != null
				&& Objects.equals(_mob, other._mob)
				&& _baseColor.equals(other._baseColor)
				&& _outlineColor.equals(other._outlineColor)
				&& _onEnterEffect == other._onEnterEffect;
	}
}
