package Mobs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Game.Tile;
import Items.Action;
import Items.Item;
import TileRegions.EmanationRegion;
import TileRegions.TileRegion;
import UI.ColoredPie;
import UI.ColoredShape;

public abstract class CreatureMob extends Mob {
	private Item _weapon;
	private int _movementLeft, _speed;
	private int _mana, _maxMana;
	private int _actionsLeft;
	private int _incomingAttackDamage;

	private int _shields;
	private int _regen;

	protected CreatureMob(ColoredShape shape, Item weapon, int speed, int maxHealth) {
		this(shape, weapon, speed, maxHealth, 0);
	}

	protected CreatureMob(ColoredShape shape, Item weapon, int speed, int maxHealth, int maxMana) {
		super(shape, maxHealth);
		_movementLeft = speed;
		_speed = speed;
		_mana = maxMana;
		_maxMana = maxMana;
		_actionsLeft = 1;
		_incomingAttackDamage = 0;
		_shields = 0;
		_regen = 0;
		setWeapon(weapon);
	}

	protected CreatureMob(CreatureMob other) {
		super(other);
		_movementLeft = other._movementLeft;
		_speed = other._speed;
		_mana = other._mana;
		_maxMana = other._maxMana;
		_actionsLeft = other._actionsLeft;
		_incomingAttackDamage = other._incomingAttackDamage;
		_shields = other._shields;
		_regen = other._regen;
		setWeapon(other.getWeapon());
	}

	@Override
	public abstract Mob getState();

	public Item getWeapon() {
		return _weapon;
	}

	protected void setWeapon(Item weapon) {
		if (weapon == null)
			return;
		_weapon = new Item(weapon);
	}

	public List<Action> getAvailableActions() {
		List<Action> availableActions = new LinkedList<Action>();
		if (getActionsLeft() <= 0)
			return availableActions;

		for (Action action : getWeapon().getActions())
			if (action.costSatisfied(getTile()))
				availableActions.add(action);
		return availableActions;
	}

	public Action[] getAllActions() {
		return getWeapon().getActions();
	}

	public int getMovementLeft() {
		return _movementLeft;
	}

	public void decrementMovement(int distance) {
		setMovementLeft(getMovementLeft() - distance);
	}

	public void setMovementLeft(int movementLeft) {
		_movementLeft = movementLeft;
	}

	public TileRegion getMovementTilesInRange() {
		return new EmanationRegion(getTile(), 2, getMovementLeft(), false, Tile.NON_BLOCKING,
				Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE));
	}

	public int getSpeed() {
		return _speed;
	}

	public int getMana() {
		return _mana;
	}

	public void incrementMana(int delta) {
		_mana = _mana + delta >= getMaxMana() ? getMaxMana() : _mana + delta;
	}

	private int getMaxMana() {
		return _maxMana;
	}

	public int getActionsLeft() {
		return _actionsLeft;
	}

	public void decrementActionsLeft(int delta) {
		_actionsLeft -= delta;
	}

	private void resetActionsLeft() {
		_actionsLeft = 1;
	}

	@Override
	public void damage(int delta) {
		if (delta < 0)
			throw new RuntimeException("Delta cannot be negative");

		int preventedDamage = delta < getShields() ? delta : getShields();
		incrementShields(-1 * preventedDamage);
		delta -= preventedDamage;
		super.damage(delta);
	}

	protected void applyRegen() {
		incrementHealth(_regen);
	}

	public void incrementPoison(int delta) {
		if (delta < 0)
			throw new RuntimeException("Delta cannot be negative");
		_regen -= delta;
	}
	public void incrementRegen(int delta) {
		if (delta < 0)
			throw new RuntimeException("Delta cannot be negative");
		_regen += delta;
	}
	protected int getRegen () {
		return _regen;
	}

	public void incrementIncomingAttackDamage(int delta) {
		_incomingAttackDamage += delta;

		if (_incomingAttackDamage < 0)
			throw new RuntimeException("Next turn health loss should not be negative");
	}

	private int getIncomingAttackDamage() {
		return _incomingAttackDamage;
	}

	private void resetIncomingAttackDamage() {
		_incomingAttackDamage = 0;
	}

	public void incrementShields(int delta) {
		_shields += delta;
	}

	private void resetShields() {
		_shields = 0;
	}

	protected int getShields() {
		return _shields;
	}

	public abstract boolean isFriendlyTo(Mob other);

	protected Collection<Tile> getAllPlayers() {
		return getTile().getAllPlayers();
	}

	protected Collection<Tile> getAllEnemies() {
		return getTile().getAllEnemies();
	}

	public void startTurn() {
		resetIncomingAttackDamage();
		resetShields();
		setMovementLeft(getSpeed());
		resetActionsLeft();
	}

	public void endTurn() {
		applyRegen();
		clearTurnEffects();
	}

	protected void clearTurnEffects() {
		clearPowerModifiers();
	}

	private void clearPowerModifiers() {
		if (getWeapon() != null)
			getWeapon().clearPowerModifier();
	}

	public void incrementPowerModifiers(int delta) {
		getWeapon().incrementPowerModifier(delta);
	}

	public abstract void hover();

	public abstract void dehover();

	@Override
	public void draw(Graphics2D g, int centerX, int centerY) {
		drawHealthOutline(g, centerX, centerY);
		drawLostHealthOutline(g, centerX, centerY);
		drawShieldOutline(g, centerX, centerY);
		super.draw(g, centerX, centerY);
	}

	private void drawHealthOutline(Graphics2D g, int centerX, int centerY) {
		int twelfths = getHealth() * 12 / getMaxHealth();
		ColoredPie outline = makePie(twelfths, healthOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private int getNetHealth() {
		int netHealth = getHealth() + getRegen();
		netHealth = netHealth < 0 ? 0 : netHealth > getMaxHealth() ? getMaxHealth() : netHealth;
		netHealth = (netHealth + getShields()) - getIncomingAttackDamage();
		netHealth = netHealth < 0 ? 0 : netHealth > getMaxHealth() ? getMaxHealth() : netHealth;
		return netHealth;
	}

	private void drawLostHealthOutline(Graphics2D g, int centerX, int centerY) {
		int netHealth = getNetHealth();
		int offsetTwelfths = netHealth * 12 / getMaxHealth();
		int healthTwelfths = getHealth() * 12 / getMaxHealth();
		int spanTwelfths = healthTwelfths - offsetTwelfths;
		ColoredPie outline = makePie(offsetTwelfths, spanTwelfths, lostHealthOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private void drawShieldOutline(Graphics2D g, int centerX, int centerY) {
		int netShields = getShields() - getIncomingAttackDamage();
		netShields = netShields >= 0 ? netShields : 0;

		int twelfths = netShields * 12 / getMaxHealth();
		twelfths = twelfths <= 12 ? twelfths : 12;
		ColoredPie outline = makePie(twelfths, shieldOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private static ColoredPie makePie(int twelfths, Color color) {
		twelfths = twelfths % 13;
		twelfths = twelfths < 0 ? twelfths + 13 : twelfths;
		int degrees = 30 * twelfths;
		int diameter = (Tile.TILE_WIDTH - Tile.TILE_GAP) * 3 / 4;
		return new ColoredPie(diameter, diameter, degrees, color);
	}

	private static ColoredPie makePie(int offsetTwelfths, int spanTwelfths, Color color) {
		offsetTwelfths = offsetTwelfths % 13;
		offsetTwelfths = offsetTwelfths < 0 ? offsetTwelfths + 13 : offsetTwelfths;
		spanTwelfths = spanTwelfths % 13;
		spanTwelfths = spanTwelfths < 0 ? spanTwelfths + 13 : spanTwelfths;
		int offset = 30 * offsetTwelfths;
		int degrees = 30 * spanTwelfths;
		int diameter = (Tile.TILE_WIDTH - Tile.TILE_GAP) * 3 / 4;
		return new ColoredPie(diameter, diameter, degrees, offset, color);
	}

	protected abstract Color healthOutlineColor();

	protected abstract Color lostHealthOutlineColor();

	protected abstract Color shieldOutlineColor();

	@Override
	protected String statsString() {
		String shieldString = getShields() > 0 ? "  Shields: " + getShields() : "";
		String endOfTurnHealthDeltaString = getRegen() > 0 ? "\nRegen: " + getRegen()
				: getRegen() < 0 ? "\nPoison: " + (getRegen() * -1) : "";
		String healthLossString = _incomingAttackDamage > 0 ? "\nIncoming Attack Damage: " + _incomingAttackDamage : "";
		String manaString = getMaxMana() > 0 ? "\nMana Left: " + getMana() + "/" + _maxMana : "";
		String weaponString = getWeapon() != null ? "\n\n" + getWeapon().toString() : "";

		return super.statsString() + shieldString + healthLossString + endOfTurnHealthDeltaString + "\nActions Left: "
				+ getActionsLeft() + "\nMovement Left: " + getMovementLeft() + "/" + getSpeed() + manaString
				+ weaponString;
	}

	public boolean equals(CreatureMob other) {
		return super.equals(other)
				&& Objects.equals(getWeapon(), other.getWeapon())
				&& getMovementLeft() == other.getMovementLeft()
				&& getSpeed() == other.getSpeed()
				&& getMana() == other.getMana()
				&& getMaxMana() == other.getMaxMana()
				&& getActionsLeft() == other.getActionsLeft()
				&& getIncomingAttackDamage() == other.getIncomingAttackDamage()
				&& getShields() == other.getShields()
				&& getRegen() == other.getRegen();
	}
}
