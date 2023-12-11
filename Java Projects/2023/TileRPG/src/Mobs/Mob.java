package Mobs;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Game.Tile;
import Items.*;
import Items.Item.ItemFactory;
import TargetingAIs.AI;
import TargetingAIs.Closest;
import TileRegions.EmanationRegion;
import TileRegions.TileRegion;
import UI.ColoredEllipse;
import UI.ColoredShape;

public class Mob {
	private Tile _tile;
	private String _name;
	private Allegiance _allegiance;
	private ColoredShape _shape;
	private AI _targetingAI;
	private Item _weapon;
	private int _speed, _health, _mana, _movementLeft, _actionsLeft;
	
	public enum Allegiance {PLAYER, NUETRAL, HOSTILE, INANIMATE}
	
	public static class MobFactory {
		
		private static final HashMap<MobLabel, Mob> _mobCatalogue = new HashMap<MobLabel, Mob>();
		
		private static void put (MobLabel label, Mob mob) {
			_mobCatalogue.put(label, mob);
			mob._name = label.toString();
		}
		
		public static Mob get (MobLabel label) {
			return new Mob(_mobCatalogue.get(label));
		}
		
		public static List<Mob> get (Iterable<MobLabel> labels) {
			List<Mob> mobs = new LinkedList<Mob>();
			for (MobLabel label : labels)
				mobs.add(get(label));
			return mobs;
		}
		
		static {
			put(MobLabel.SWORDSMAN,	SWORDSMAN);
			put(MobLabel.ARCHER,	ARCHER);
			put(MobLabel.ROGUE,		ROGUE);
			put(MobLabel.WIZARD,	WIZARD);
			put(MobLabel.DRUID, 	DRUID);
			
			put(MobLabel.ENEMY,		ENEMY);
			
			put(MobLabel.WALL,		WALL);
		}

		private MobFactory () {}
		
	}
	
	private static final Mob SWORDSMAN = new Mob(
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.white),
			new Item(ItemFactory.get(ItemLabel.SWORD)),
			2,
			3);
	private static final Mob ARCHER = new Mob(
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.yellow),
			new Item(ItemFactory.get(ItemLabel.BOW)),
			2,
			2);
	private static final Mob ROGUE = new Mob(
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.gray),
			new Item(ItemFactory.get(ItemLabel.DAGGER)),
			3,
			2);
	private static final Mob WIZARD = new Mob(
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.blue),
			new Item(ItemFactory.get(ItemLabel.FIRE_WAND)),
			2,
			1,
			2);
	private static final Mob DRUID = new Mob(
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.green),
			new Item(ItemFactory.get(ItemLabel.EARTH_ROD)),
			2,
			2,
			2);
	
	private static final Mob ENEMY = new Mob (
			Allegiance.HOSTILE,
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.red),
			new Closest(Tile.NON_BLOCKING.or(Tile.HAS_PLAYER_MOB),
						Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE),
						Tile.HAS_PLAYER_MOB),
			new Item(ItemFactory.get(ItemLabel.FIST)),
			2,
			2);
	
	private static final Mob WALL = new Mob (
			new ColoredEllipse(Tile.TILE_WIDTH-Tile.TILE_GAP, Tile.TILE_HEIGHT-Tile.TILE_GAP, Color.black),
			1);
	
	
	private Mob (ColoredShape shape, int health) {
		this(Allegiance.INANIMATE, shape, null, null, 0, health, 0, 0, 0);
	}
	private Mob (ColoredShape shape, Item weapon, int speed, int health) {
		this(Allegiance.PLAYER, shape, null, weapon, speed, health, 0);
	}
	private Mob (ColoredShape shape, Item weapon, int speed, int health, int mana) {
		 this(Allegiance.PLAYER, shape, null, weapon, speed, health, mana, speed, 1);
	}
	private Mob (Allegiance allegiance, ColoredShape shape, AI targetingAI, Item weapon, int speed, int health) {
		 this(allegiance, shape, targetingAI, weapon, speed, health, 0, speed, 1);
	}
	private Mob (Allegiance allegiance, ColoredShape shape, AI targetingAI, Item weapon, int speed, int health, int mana) {
		 this(allegiance, shape, targetingAI, weapon, speed, health, mana, speed, 1);
	}
	private Mob (Allegiance allegiance, ColoredShape shape, AI targetingAI, Item weapon, int speed, int health, int mana, int movementLeft, int actionsLeft) {
		 _allegiance = allegiance; _shape = shape; _targetingAI = targetingAI; _speed = speed;  _health = health; _mana = mana; _movementLeft = movementLeft; _actionsLeft = actionsLeft;
		 setWeapon(weapon);
	}
	public Mob (Mob other) {
		this(other._allegiance, other._shape, other._targetingAI, other._weapon, other._speed, other._health, other._mana, other._movementLeft, other._actionsLeft);
		_name = other._name;
	}
	
	public boolean isPlayerControlled () {
		return _allegiance == Allegiance.PLAYER;
	}
	
	public Allegiance getAllegiance() {
		return _allegiance;
	}
	
	public Item getWeapon () {
		return _weapon;
	}
	
	private void setWeapon (Item weapon) {
		if (weapon == null)
			return;
		_weapon = new Item(weapon);
	}
	
	public ColoredShape getShape() {
		return _shape;
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
		return new EmanationRegion(_tile, 2, _movementLeft, false, Tile.NON_BLOCKING, Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE));
	}
	
	public void decrementActionsLeft (int delta) {
		_actionsLeft -= delta;
	}
	
	public boolean doNonPlayerAction () {
		if (_targetingAI == null)
			return false;
		
		Tile destination = _targetingAI.getNextMovementTile(getTile(), _speed);
		moveToEmptyTile(destination, 0);
		
		LinkedList<Tile> nearByPlayers = new LinkedList<Tile>();
		for (Tile neighbor : getTile().getNeighbors())
			if (neighbor != null && neighbor.fitsCondition(Tile.HAS_PLAYER_MOB))
				nearByPlayers.add(neighbor);
		if (nearByPlayers.size() > 0) {
			int rand = (int) (Math.random() * nearByPlayers.size());
			Tile target = nearByPlayers.get(rand);
			_weapon.getActions()[0].doAction(getTile(), target);
		}
		
		return true;
	}
	
	public void incrementHealth (int deltaHealth) {
		_health += deltaHealth;
		if (_health <= 0)
			die();
	}

	public int getMana () {
		return _mana;
	}
	
	public void incrementMana (int delta) {
		_mana += delta;
	}
	
	public void decrementMovement (int distance) {
		_movementLeft -= distance;
	}
	public void setMovementLeft (int movementLeft) {
		_movementLeft = movementLeft;
	}
	public int getMovementLeft() {
		return _movementLeft;
	}
	
	public List<Action> getAvailableActions () {
		List<Action> availableActions = new LinkedList<Action>();
		if (_actionsLeft <= 0)
			return availableActions;
		
		for (Action action : _weapon.getActions())
			if (action.costSatisfied(_tile))
				availableActions.add(action);
		return availableActions;
	}
	
	public Action[] getAllActions() {
		return _weapon.getActions();
	}
	
	public void nextTurnReset () {
		_movementLeft = _speed;
		_actionsLeft = 1;
	}
	
	private void die () {
		_tile.clearTileOfMob();
	}
	
	@Override
	public String toString() {
		String manaString = _mana > 0 ? "\nMana Left: "+_mana : "";
		String weaponString = _weapon != null ? "\n\n"+_weapon.toString() : "";
		return _name+
				"\nLife: "+_health+
				"\nActions Left: "+_actionsLeft+
				"\nMovement Left: "+_movementLeft+
				manaString+
				weaponString;
	}
	
	public boolean equals (Mob other) {
		return other != null
				&& _name.equals(other._name)
				&& _allegiance == other._allegiance
				&& Objects.equals(_shape, other._shape)
				&& Objects.equals(_targetingAI, other._targetingAI)
				&& Objects.equals(_weapon, other._weapon)
				&& _speed == other._speed
				&& _health == other._health
				&& _mana == other._mana
				&& _movementLeft == other._movementLeft
				&& _actionsLeft == other._actionsLeft;
	}
}
