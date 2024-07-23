package Mobs;

import java.awt.Graphics2D;
import java.util.Objects;

import Board.Tile;
import UI.ColoredShape;

public class Mob {
	private String _name;
	private ColoredShape _shape;
	private Tile _tile;
	private int _health, _maxHealth;

	protected int _eotHealth, _eotMaxHealth;

	private Mob _isPreviewOf;
	
	Mob (ColoredShape shape, int maxHealth) {
		setState(shape, maxHealth);
	}
	Mob (Mob other) {
		setState(other);
	}
	
	public Mob getState() {
		return new Mob(this);
	}

	private void setState (ColoredShape shape, int maxHealth, int health) {
		_shape = shape;
		_health = health;
		_maxHealth = maxHealth;
		_eotHealth = health;
		_eotMaxHealth = maxHealth;
		_isPreviewOf = null;
	}
	private void setState (ColoredShape shape, int maxHealth) {
		setState(shape, maxHealth, maxHealth);
	}
	private void setState (Mob other) {
		setState(other._shape, other._maxHealth, other._health);
		_name = other._name;
		_isPreviewOf = other._isPreviewOf;
	}

	public Mob makePreviewCopy() {
		Mob copy = getState();
		// TODO: children give null as getState. How do we deal with this? probable override makePreviewCopy in compoundMob to give null if child and make previews for children if parent, then place parent on board like normal
		// System.out.println(copy);
		copy._isPreviewOf = this;
		return copy;
	}

	public void applyPreview () {
		if (_isPreviewOf == null)
			throw new RuntimeException("Trying to update original Mob when this Mob is not a preview");
		_isPreviewOf.updatePreviewDisplay(this);
	}
	protected void updatePreviewDisplay (Mob preview) {
		_eotHealth = preview._health;
		_eotMaxHealth = preview._maxHealth;
	}
	
	public void setName (String name) {
		_name = name;
	}
	
	public boolean canFitIn (Tile tile) {
		return !tile.hasMob();
	}
	
	public Tile getTile () {
		return _tile;
	}
	
	public void setTile (Tile tile) {
		if (tile != null && tile.getMob() != this)
			throw new RuntimeException("Tile that mob is trying to link to is not already linked to mob."
									 + "Link the tile to the mob, then link the mob to the tile.");
		_tile = tile;
	}
	
	public void damage (int delta) {
		if (delta < 0)
			throw new RuntimeException("Delta cannot be negative");
		incrementHealth(delta *-1);
	}
	
	public void heal (int delta) {
		if (delta < 0)
			throw new RuntimeException("Delta cannot be negative");
		incrementHealth(delta);
	}
	
	protected void incrementHealth (int delta) {
		_health = _health+delta >= _maxHealth ? _maxHealth : _health+delta;
		if (_health <= 0)
			die();
	}
	
	protected int getHealth () {
		return _health;
	}
	protected int getMaxHealth () {
		return _maxHealth;
	}
	
	protected void die () {
		if (getTile() != null)
			getTile().detachMob();
	}
	
	public void draw (Graphics2D g, int centerX, int centerY) {
		_shape.draw(g, centerX, centerY);
	}
	
	protected void setShape (ColoredShape shape) {
		_shape = shape;
	}
	
	@Override
	public String toString() {
		return _name+statsString();
				
	}
	protected String statsString() {
		return "\nLife: "+getHealth()+"/"+getMaxHealth();
	}
	
	public boolean equals (Mob other) {
		return other != null
				&& _name.equals(other._name)
				&& Objects.equals(_shape, other._shape)
				&& _health == other._health
				&& _maxHealth == other._maxHealth;
	}
}
