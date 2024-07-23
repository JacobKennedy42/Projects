package Mobs;

import java.awt.Color;
import java.util.List;

import Board.Tile;
import Items.Item;
import Items.ItemLabel;
import Items.Item.ItemFactory;
import TargetingAIs.Closest;


class GiantWorm extends CompoundMob{
	protected GiantWorm () {
		super(MobFactory.makeCircle(Color.black),
				null,
				null,
				1,
				5);
		setName("GIANT WORM");
	}
	protected GiantWorm (int bodyLength) {
		this();
		_children = initChildren(bodyLength);
	}
	private GiantWorm (GiantWorm other) {
		super(other);
	}
	@Override
	public CompoundMob makeCopy() {
		return new GiantWorm(this);
	}
	
	private CompoundMob[] initChildren (int bodyLength) {
		if (bodyLength <= 1)
			return new CompoundMob[0];
		
		CompoundMob[] children = new CompoundMob[6];
		children[3] = new GiantWorm(bodyLength-1);
		return children;
	}
	
	protected GiantWorm getChild () {
		for (int i = 0; i < _children.length; ++i)
			if (_children[i] != null)
				return (GiantWorm) _children[i];
		return null;
	}
	
	private void rotateTowards (Tile tile) {
		super.setRotation(super.getRotationTo(tile));
		rotateChildTowards(getTile());
	}
	protected void rotateChildTowards (Tile tile) {
		GiantWorm child = getChild();
		if (child != null)
			child.rotateTowards(tile);	
	}
	
	@Override
	protected void die () {
		transformChildToHead();
		super.die();
	}
	private void transformChildToHead () {
		GiantWorm child = getChild();
		if (child != null)
				child.transformToHead();
	}
	private void transformToHead () {
		GiantWormHead head = new GiantWormHead(this);
		getTile().attachMob(head);
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n" +
				"""
				Will trample through players.
				On Death: previous body becomes a head
				""";
				
	}
	
	public static class GiantWormHead extends GiantWorm {
		public GiantWormHead () {
			super();
			initHead();
		}
		private GiantWormHead (GiantWorm formerBody) {
			super(formerBody);
			initHead();
		}
		private GiantWormHead (GiantWormHead other) {
			super(other);
		}
		private void initHead () {
			setShape(MobFactory.makeCircle(Color.red));
			_targetingAI = new Closest(Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE));
			setWeapon(new Item(ItemFactory.get(ItemLabel.MARTIAL_ARTS)));
		}
		@Override
		public CompoundMob makeCopy() {
			return new GiantWormHead(this);
		}
		
		@Override
		protected CompoundMob[] initChildren () {
			CompoundMob[] children = new CompoundMob[6];
			children[3] = new GiantWorm(3);
			return children;
		}
		
		@Override
		public boolean canFitIn (Tile tile) {
			//This is a bad hack. If getTile() is null, then worm has not been placed yet and should consider children. Otherwise, moves like it has no children, and the children follow
			if (getTile() == null)
				return super.canFitIn(tile);

			return tile != null && !isFriendlyTo(tile.getMob());
		}
		
		@Override
		public boolean doAction () {
			Tile destination = getTrampleDestination();
			while (getMovementLeft() > 0 && destination != null) {
				trampleStep (destination);
				destination = getTrampleDestination();
				decrementMovement(1);
			}
			return true;
		}
		private Tile getTrampleDestination () {
			List<Tile> movementPath = _targetingAI.getMovementPath(getTile(), getSpeed(), getAllPlayers());
			if (movementPath == null || movementPath.size() <= 1)
				return null;
			return movementPath.get(1);
		}
		private void trampleStep (Tile destination) {
			if (destination.hasMob())
				getChosenAction().doAction(getTile(), destination, getTile());
			if (!destination.hasMob())		//action might push mob out of destination
				moveTo(destination);	
		}
		private void moveTo (Tile tile) {
			super.setRotation(super.getRotationTo(tile));
			rotateChildTowards(getTile());
			tile.attachMob(this);
		}
	}
}
