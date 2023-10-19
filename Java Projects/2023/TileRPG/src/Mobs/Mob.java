package Mobs;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.Objects;

import Game.FilledShape;
import Game.Tile;
import Items.*;
import Items.Item.ItemLibrary;
import TargetingAIs.AI;
import TargetingAIs.Closest;
import TileRegions.EmanationRegion;
import TileRegions.TileRegion;

public class Mob {
	private Tile _tile;
	private MobState _state;
	
	public enum Allegiance {PLAYER, NUETRAL, HOSTILE}
	
	public class MobLibrary {
		public static final MobState SWORDSMAN = new MobState (
				"SWORDSMAN",
				new FilledShape(new Ellipse2D.Float(0, 0, Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP), Color.white),
				new Item(ItemLibrary.SWORD),
				2,
				3);
		public static final MobState ARCHER = new MobState (
				"ARCHER",
				new FilledShape(new Ellipse2D.Float(0, 0, Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP), Color.yellow),
				new Item(ItemLibrary.BOW),
				2,
				1);
		public static final MobState ROGUE = new MobState (
				"ROGUE",
				new FilledShape(new Ellipse2D.Float(0, 0, Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP), Color.green),
				new Item(ItemLibrary.DAGGER),
				3,
				2);
		public static final MobState WIZARD = new MobState (
				"WIZARD",
				new FilledShape(new Ellipse2D.Float(0, 0, Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP), Color.blue),
				new Item(ItemLibrary.FIRE_WAND),
				2,
				1);
		
		public static final MobState ENEMY = new MobState (
				"ENEMY",
				Allegiance.HOSTILE,
				new FilledShape(new Ellipse2D.Float(0, 0, Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP), Color.red),
				new Closest(Tile.ALL, Tile.NON_BLOCKING, Tile.HAS_PLAYER_MOB),
				new Item(ItemLibrary.FIST),
				1,
				3);
	}
	
	public static class MobState {
		public String _label;
		public Allegiance _allegiance;
		public FilledShape _shape;
		public AI _targetingAI;
		public Item _weapon;
		public int _speed, _health, _movementLeft, _actionsLeft;
		
		public MobState (String label, FilledShape shape, Item weapon, int speed, int health) {
			 this(label, Allegiance.PLAYER, shape, null, weapon, speed, health, speed, 1);
		}
		
		public MobState (String label, Allegiance allegiance, FilledShape shape, AI targetingAI, Item weapon, int speed, int health) {
			 this(label, allegiance, shape, targetingAI, weapon, speed, health, speed, 1);
		}
		
		public MobState (String label, Allegiance allegiance, FilledShape shape, AI targetingAI, Item weapon, int speed, int health, int movementLeft, int actionsLeft) {
			 _label = label; _allegiance = allegiance; _shape = shape; _targetingAI = targetingAI; _weapon = weapon; _speed = speed;  _health = health; _movementLeft = movementLeft; _actionsLeft = actionsLeft;
		}
		
		public MobState (MobState otherState) {
			this(otherState._label, otherState._allegiance, otherState._shape, otherState._targetingAI, new Item(otherState._weapon), otherState._speed, otherState._health, otherState._movementLeft, otherState._actionsLeft);
		}
		
		public boolean equals (MobState other) {
			return other != null
					&& _label.equals(other._label)
					&& _allegiance == other._allegiance
					&& Objects.equals(_shape, other._shape)
					&& Objects.equals(_targetingAI, other._targetingAI)
					&& Objects.equals(_weapon, other._weapon)
					&& _speed == other._speed
					&& _health == other._health
					&& _movementLeft == other._movementLeft
					&& _actionsLeft == other._actionsLeft;
		}
	}
	
	public Mob (MobState state) {
		setState(state);
	}
	
	public MobState getState() {
		return new MobState(_state);
	}
	
	private void setState (MobState state) {
		_state = new MobState(state);
	}
	
	public boolean isPlayerControlled () {
		return _state._allegiance == Allegiance.PLAYER;
	}
	
	public Allegiance getAllegiance() {
		return _state._allegiance;
	}
	
	public Item getWeapon () {
		return _state._weapon;
	}
	
	public FilledShape getShape() {
		return _state._shape;
	}

	public Tile getTile () {
		return _tile;
	}
	
	public void setTile (Tile tile) {
		if (tile.getMob() != this)
			throw new RuntimeException("Tile that mob is trying to link to is not already linked to mob."
									 + "Link the tile to the mob, then link the mob to the tile.");
		
		_tile = tile;
	}
	
	private boolean moveToEmptyTile (Tile destination, int distance) {
		return getTile().moveMobToEmptyTile(destination, distance);
	}
	
	public TileRegion getMovementTilesInRange () {
		return new EmanationRegion(_tile, _state._movementLeft, false);
	}
	
	public TileRegion getActionTilesInRange () {
		return _state._weapon.getTilesInRangeFrom(_tile);
	}
	
	public boolean doAction (Tile targetTile) {
		if (_state._allegiance == Allegiance.PLAYER)
			return doWeaponAction(targetTile);
		return doNonPlayerAction();
	}
	
	private boolean doWeaponAction (Tile targetTile) {
		if (targetTile == null
				|| targetTile.getMob() == this
				|| !_state._weapon.doActionOnMob(_tile, targetTile))	//returns if action is successfully performed
			return false;
			
		--_state._actionsLeft;
		return true;
	}
	
	private boolean doNonPlayerAction () {
		LinkedList<Tile> pathToTarget = _state._targetingAI.getPathToTarget(getTile());
		if (pathToTarget.size() <= 0)
			return false;
		
		int distance = _state._speed <= pathToTarget.size()-2 ? _state._speed : pathToTarget.size()-2;
		Tile destination = pathToTarget.get(distance);
		moveToEmptyTile(destination, distance);
		if (distance == pathToTarget.size()-2) {
			Tile target = pathToTarget.get(pathToTarget.size()-1);
			doWeaponAction(target);
		}
		return true;
	}
	
	public void incrementHealth (int deltaHealth) {
		_state._health += deltaHealth;
		if (_state._health <= 0)
			die();
	}
	
	public void decrementMovement (int distance) {
		_state._movementLeft -= distance;
	}

	public int getMovementLeft() {
		return _state._movementLeft;
	}
	
	public int getActionsLeft() {
		return _state._actionsLeft;
	}
	
	public void nextTurnReset () {
		_state._movementLeft = _state._speed;
		_state._actionsLeft = 1;
	}
	
	private void die () {
		_tile.clearTileOfMob();
	}
	
	@Override
	public String toString() {
		return _state._label+
				"\nLife: "+_state._health+
				"\nActions Left: "+_state._actionsLeft+
				"\nMovement Left: "+_state._movementLeft+
				"\n\n"+_state._weapon.toString();
	}
}
