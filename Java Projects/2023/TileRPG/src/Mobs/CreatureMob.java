package Mobs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Board.Tile;
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

	private int _shields;
	private int _regen;

	private int _eotShields;

	protected CreatureMob(ColoredShape shape, Item weapon, int speed, int maxHealth) {
		this(shape, weapon, speed, maxHealth, 0);
	}

	protected CreatureMob(ColoredShape shape, Item weapon, int speed, int maxHealth, int maxMana) {
		super(shape, maxHealth);
		setState(speed, maxMana, weapon);
	}

	protected CreatureMob(CreatureMob other) {
		super(other);
		setState(other);
	}

	private void setState (int speed, int movementLeft, int maxMana, int mana, int actionsLeft, int shields, int regen, Item weapon){
		_movementLeft = movementLeft;
		_speed = speed;
		_mana = mana;
		_maxMana = maxMana;
		_actionsLeft = actionsLeft;
		_shields = shields;
		_eotShields = shields;
		_regen = regen;
		setWeapon(weapon);
	}
	private void setState (int speed, int maxMana, Item weapon) {
		setState(speed, speed, maxMana, maxMana, 1, 0, 0, weapon);
	}
	private void setState (CreatureMob other) {
		setState(other._speed, other._movementLeft, other._maxMana, other._mana, other._actionsLeft, other._shields, other._regen, other._weapon);
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
	protected void updatePreviewDisplay (Mob preview) {
		super.updatePreviewDisplay(preview);
		if (!(preview instanceof CreatureMob))
			throw new RuntimeException("preview is not a CreatureMob");
		
		CreatureMob previewCreature = (CreatureMob) preview;
		_eotShields = previewCreature._shields;
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
		drawLostShieldOutline(g, centerX, centerY);
		super.draw(g, centerX, centerY);
	}

	private void drawHealthOutline(Graphics2D g, int centerX, int centerY) {
		int twelfths = getHealth() * 12 / getMaxHealth();
		ColoredPie outline = makePie(twelfths, healthOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private void drawLostHealthOutline(Graphics2D g, int centerX, int centerY) {
		int offsetTwelfths = _eotHealth * 12 / getMaxHealth();
		int healthTwelfths = getHealth() * 12 / getMaxHealth();
		int spanTwelfths = healthTwelfths - offsetTwelfths;
		ColoredPie outline = makePie(offsetTwelfths, spanTwelfths, lostHealthOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private void drawShieldOutline(Graphics2D g, int centerX, int centerY) {
		if (_eotHealth < getHealth())	//don't draw shields if any damage will get through them, since we want to show health lost
			return;

		int twelfths = getShields() * 12 / getMaxHealth();
		twelfths = twelfths <= 12 ? twelfths : 12;
		ColoredPie outline = makePie(twelfths, shieldOutlineColor());
		outline.draw(g, centerX, centerY);
	}

	private void drawLostShieldOutline (Graphics2D g, int centerX, int centerY) {
		if (_eotHealth < getHealth())	//don't draw shields if any damage will get through them, since we want to show health lost
			return;

		int offsetTwelfths = _eotShields * 12 / getMaxHealth();
		offsetTwelfths = offsetTwelfths <= 12 ? offsetTwelfths : 12;
		int shieldTwelfths = getShields() * 12 / getMaxHealth();
		shieldTwelfths = shieldTwelfths <= 12 ? shieldTwelfths : 12;
		int spanTwelfths = shieldTwelfths - offsetTwelfths;
		ColoredPie outline = makePie(offsetTwelfths, spanTwelfths, lostHealthOutlineColor());
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
		int dHealth = getHealth() - _eotHealth;
		String healthLossString = dHealth > 0 ? "\nIncoming Attack Damage: " + dHealth : "";
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
				&& getShields() == other.getShields()
				&& getRegen() == other.getRegen();
	}
}
