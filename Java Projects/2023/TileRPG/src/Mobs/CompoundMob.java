package Mobs;

import Game.Tile;
import Items.Item;
import TargetingAIs.AI;
import UI.ColoredShape;

public abstract class CompoundMob extends EnemyMob {
	protected CompoundMob _parent;
	protected CompoundMob[] _children;
	private int _rotation;	//0 is top-left, the rotates clockwise
	
	protected CompoundMob (ColoredShape shape, AI targetingAI, Item weapon, int speed, int maxHealth) {
		super(shape, targetingAI, weapon, speed, maxHealth);
		_children = initChildren();
		_rotation = 0;
	}
	protected CompoundMob (CompoundMob other) {
		super(other);
		_children = copyChildrenOf(other);
		_rotation = other._rotation;
	}
	
	@Override
	public Mob getState () {
		if (_parent != null)
			return null;
		return makeCopy();
	}
	protected abstract CompoundMob makeCopy();
	private CompoundMob makeCopy (CompoundMob parent) {
		CompoundMob copy = makeCopy();
		copy._parent = parent;
		return copy;
	}
	
	protected CompoundMob[] initChildren () {
		return new CompoundMob[0];
	}
	private CompoundMob[] copyChildrenOf (CompoundMob other) {
		CompoundMob[] otherChildren = other._children;
		CompoundMob[] copy = new CompoundMob[otherChildren.length];
		for (int i = 0; i < otherChildren.length; ++i)
			if (otherChildren[i] != null)
				copy[i] = (CompoundMob) otherChildren[i].makeCopy(this);
		return copy;
	}
	
	@Override
	public void setTile (Tile tile) {
		super.setTile(tile);
		placeChildren();
	}
	private void placeChildren () {
		if (getTile() == null || getTile().getNeighbors() == null)
			return;
		
		for (int i = 0; i < _children.length; ++i)
			if (_children[i] != null)
				getNeighborAt(i).attachMob(_children[i]);
	}
	
	protected void setRotation (int rotation) {
		if (rotation < 0 || rotation > 6)
			throw new RuntimeException("Rotation cannot be less than 0 or more than 6");
		_rotation = rotation;
	}
	
	private CompoundMob getRootParent () {
		if (_parent == null)
			return this;
		return _parent.getRootParent();
	}
	protected boolean sharesRootParentWith (Mob other) {
		if (other == null || !(other instanceof CompoundMob))
			return false;
		return getRootParent() == ((CompoundMob) other).getRootParent();
	}
	
	@Override
	public boolean canFitIn (Tile tile) {
		if (tile == null || (tile.hasMob() && !sharesRootParentWith(tile.getMob())))
			return false;
		for (int i = 0; i < _children.length; ++i)
			if (_children[i] != null
					&& !_children[i].canFitIn(getNeighborAt(tile, i)))
				return false;
		return true;
	}
	
	private Tile getNeighborAt (int index) {
		return getNeighborAt(getTile(), index);
	}
	private Tile getNeighborAt (Tile tile, int index) {
		index = (index + _rotation) % 6;
		return tile.getNeighbors().get(index);
	}
	protected int getRotationTo (Tile neighbor) {
		int rotation = getTile().getNeighbors().indexOf(neighbor);
		if (rotation == -1)
			throw new RuntimeException("parameter is not a neighbor of this mob");
		return rotation;
	}
	
	@Override
	protected void die () {
		super.die();
		detachFromParent();
		detachFromChildren();
	}
	
	private void detachFromParent () {
		if (_parent == null)
			return;
		_parent.removeChild(this);
		_parent = null;
	}
	private void detachFromChildren () {
		for (int i = 0; i < _children.length; ++i)
			if (_children[i] != null)
				_children[i].detachFromParent();
		_children = new CompoundMob[0];
	}
	private void removeChild (CompoundMob child) {
		for (int i = 0; i < _children.length; ++i)
			if (_children[i] == child) {
				_children[i] = null;
				return;
			}
	}
	
	public boolean equals (CompoundMob other) {
		return super.equals(other)
				&& _rotation == other._rotation;
	}
}
