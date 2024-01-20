package Mobs;

import java.awt.Graphics2D;
import java.util.Objects;

import Game.Tile;
import UI.ColoredShape;

public class Mob{
	private String _name;
	private ColoredShape _shape;
	private Tile _tile;
	private int _health, _maxHealth;
	
	Mob (ColoredShape shape, int maxHealth) {
		_shape = shape; _health = maxHealth; _maxHealth = maxHealth;
	}
	Mob (Mob other) {
		_name = other._name; _shape = other._shape; _health = other._health; _maxHealth = other._maxHealth;
	}
	
	public Mob getState() {
		return new Mob(this);
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
