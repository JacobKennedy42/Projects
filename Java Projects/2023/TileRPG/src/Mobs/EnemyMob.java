package Mobs;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import Board.Tile;
import Items.Action;
import Items.Item;
import TargetingAIs.AI;
import UI.ColoredShape;

public class EnemyMob extends CreatureMob{
	protected AI _targetingAI;
	private int _chosenActionIndex;
	private Tile _targetedPlayer;
	
	EnemyMob (ColoredShape shape, AI targetingAI, Item weapon, int speed, int maxHealth) {
		this(shape, targetingAI, weapon, speed, maxHealth, 0);
	}
	EnemyMob (ColoredShape shape, AI targetingAI, Item weapon, int speed, int maxHealth, int maxMana) {
		super(shape, weapon, speed, maxHealth, maxMana);
		_targetingAI = targetingAI;
	}
	protected EnemyMob (EnemyMob other) {
		super(other);
		_targetingAI = other._targetingAI;
		_chosenActionIndex = other._chosenActionIndex;
		targetPlayer(other._targetedPlayer);
	}
	
	@Override
	public Mob getState() {
		return new EnemyMob(this);
	}
	
	@Override
	public boolean isFriendlyTo (Mob other) {
		return other instanceof EnemyMob;
	}
	
	protected Action getChosenAction() {
		if (getWeapon() == null)
			return null;
		return getAllActions()[_chosenActionIndex];
	}
	
	private Collection<Tile> getSoughtTiles() {
		HashSet<Tile> soughtTiles =  new HashSet<Tile>();
		for (Tile target : getAllPlayers())
			soughtTiles.addAll(getChosenAction().getAreaOfInfluence(target));	//Assumes that if player can hit enemy, enemy can hit player
		return soughtTiles;
	}
	
	private Tile getMovementDestination() {
		List<Tile> movementPath = _targetingAI.getMovementPath(getTile(), getSpeed(), getSoughtTiles());
		if (movementPath == null || movementPath.size() == 0)
			return null;
		return movementPath.get(movementPath.size()-1);
	}
	
	private void moveTowardsTarget () {
		Tile destination = getMovementDestination();
		if (destination != null)
			destination.attachMob(this);
	}
	
	public boolean doAction () {
		if (_targetingAI == null)
			return false;
		
		moveTowardsTarget();
		
		Tile target = _targetingAI.getTargetFrom(getTile(), getChosenAction());
		getChosenAction().doAction(getTile(), target);
		
		return true;
	}
	
	private Tile getTargetedPlayer () {
		if (_targetingAI == null)
			return null;
		
		Tile destination = getMovementDestination();
		Tile target = _targetingAI.getTargetFrom(destination, getChosenAction());
		return target;
	}
	
	public void updateTargetedPlayers () {
		targetPlayer(getTargetedPlayer());
	}
	
	public void unTargetPlayer () {
		if (getChosenAction() != null)
			getChosenAction().revertPreview(getTile(), _targetedPlayer);
		_targetedPlayer = null;
	}
	
	private void targetPlayer (Tile targetedPlayer) {
		if (_targetingAI == null)
			return;
		
		unTargetPlayer();
		_targetedPlayer = targetedPlayer;
		if (getChosenAction() != null)
			getChosenAction().applyPreview(getTile(), _targetedPlayer);
	}
	
	public boolean isTargetingPlayer (Tile playerTile) {
		return Objects.equals(_targetedPlayer, playerTile);
	}
	
	@Override
	public void hover () {
	}
	
	@Override
	public void dehover () {}
	
	@Override
	public void startTurn () {
		super.startTurn();
		chooseRandomAction();
		updateTargetedPlayers();
	}
	
	@Override
	public void endTurn () {
		applyRegen();
		if (getHealth() <= 0)
			return;
		
		unTargetPlayer();
		doAction();
		clearTurnEffects();
	}
	
	@Override
	public void incrementPowerModifiers(int delta) {
		unTargetPlayer();
		super.incrementPowerModifiers(delta);
		updateTargetedPlayers();
	}
	
	@Override
	protected void die() {
		unTargetPlayer();
		super.die();
	}
	
	private void chooseRandomAction () {
		if (getWeapon() != null)
			_chosenActionIndex = (int)(Math.random() * getAllActions().length);
	}
	
	@Override
	protected Color healthOutlineColor() {
		return Color.red;
	}
	
	@Override
	protected Color lostHealthOutlineColor() {
		return Color.pink;
	}
	
	@Override
	protected Color shieldOutlineColor () {
		return Color.orange;
	}
	
	public boolean equals (EnemyMob other) {
		return super.equals(other)
				&& Objects.equals(_targetingAI, other._targetingAI)
				&& Objects.equals(_chosenActionIndex, other._chosenActionIndex)
				&& Objects.equals(_targetedPlayer, other._targetedPlayer);
	}
}
