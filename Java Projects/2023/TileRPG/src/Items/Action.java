package Items;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import Game.Tile;
import Game.Tile.TileCondition;
import Mobs.Mob.MobFactory;
import Mobs.MobLabel;
import TileRegions.ConeLineRegion;
import TileRegions.EmanationRegion;
import TileRegions.RadiatingLinesRegion;
import TileRegions.TileRegion;

public class Action {
	
	private static class Effect {
		private int _power;
		private Tile _origin, _target;
		private EffectFunction _func;
		
		@FunctionalInterface
		private static interface EffectFunction {
			public boolean apply (int power, Tile origin, Tile target);
		}
		
		public Effect (int power, Tile origin, Tile target, EffectFunction func) {
			_power = power; _origin = origin; _target = target; _func = func;
		}
		
		public boolean apply () {
			return _func.apply(_power, _origin, _target);
		}
		
		public Tile getTarget () {
			return _target;
		}
	}

	private static class ActionState {
		public int _power;
		public Tile _origin, _target, _directionTile;
		public LinkedList<Effect> _effects;
		
		public ActionState (int power, Tile origin, Tile target, Tile directionTile) {
			this (power, origin, target, directionTile, false);
		}
		
		public ActionState (int power, Tile origin, Tile target, Tile directionTile, boolean colorTargetsMode) {
			_power = power;
			_origin = origin;
			_target = target;
			_directionTile = directionTile;
			_effects = new LinkedList<Effect>();
		}
		
		public Collection<Tile> getTargets () {
			Collection<Tile> targets = new LinkedList<Tile>();
			for (Effect effect : _effects)
				if (!targets.contains(effect.getTarget()))
					targets.add(effect.getTarget());
			return targets;
		}
		
		public boolean applyEffects () {
			boolean isSuccessful = false;
			for (Effect effect : _effects)
				if (effect.apply())
					isSuccessful = true;
			return isSuccessful;
		}
	}
	
	public static interface Keyword {
		ActionState apply (ActionState state);
		ActionImage modifyImage (ActionImage image);
		public String toString();
		default boolean costSatisfied (Tile origin) {return true;} 
	}
	
	public static class DO_DAMAGE implements Keyword {
		public ActionState apply (ActionState state) {
			Effect.EffectFunction damageEffectFunc = (int power, Tile origin, Tile target) -> {
				if (target == null || target.getMob() == null)
					return false;
				target.getMob().incrementHealth(power * -1);
				return true;
			};
			
			state._effects.add(new Effect (state._power, state._origin, state._target, damageEffectFunc));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			image.addImage(ActionImage.TRIANGLE(Color.RED));
			return image;
		}
		public String toString () {
			return "Deal X damage";
		}
	}
	public static class SUMMON implements Keyword {
		MobLabel _mob;
		
		public SUMMON (MobLabel mob) {
			_mob = mob;
		}
		public ActionState apply (ActionState state) {
			Effect.EffectFunction summonFunc = (int power, Tile origin, Tile target) -> {
				if (target == null || target.getMob() != null)
					return false;
				target.placeNewMob(MobFactory.get(_mob));
				return true;
			};
			
			state._effects.add(new Effect (state._power, state._origin, state._target, summonFunc));
			return state;	
		}
		public ActionImage modifyImage (ActionImage image) {
			image.addImage(ActionImage.CIRCLE(Color.GREEN));
			return image;
		}
		public String toString() {
			return "Summon: "+_mob.toString();
		}
	}
	public static class MAKE_SLOW_TILE implements Keyword {
		
		public ActionState apply (ActionState state) {
			Effect.EffectFunction slowTileEffectFunc = (int power, Tile origin, Tile target) -> {
				if (target == null)
					return false;
				return target.makeSlowTile();
			};
			
			state._effects.add(new Effect(state._power, state._origin, state._target, slowTileEffectFunc));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			image.addImage(ActionImage.CIRCLE(Color.BLACK));
			return image;
		}
		public String toString () {
			return "Make Slow Tile";
		}
	}
	
	public static class INCREMENT_POWER implements Keyword {
		int _delta;
		
		public INCREMENT_POWER (int delta) {
			_delta = delta;
		}
		public ActionState apply (ActionState state) {
			state._power += _delta;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image;
		}
		public String toString () {
			char plusOrMinus = _delta > 0 ? '+' : '-';
			return ""+plusOrMinus+_delta+" X";
		}
	}
	public static class FLANKING implements Keyword {
		Keyword _modified;
		
		public FLANKING (Keyword modified) {
			_modified = modified;
		}
		
		public ActionState apply (ActionState state) {
			if (state._origin.isFlanking(state._target))
				return _modified.apply(state);
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image;
		}
		public String toString () {
			return "Flanking: "+_modified.toString();
		}
	}
	public static class RADIUS implements Keyword{
		Keyword _modified;
		int _radius;
		TileCondition _selectionCondition, _propogationCondition;
		
		public RADIUS (int radius, Keyword modified) {
			this(radius, modified, Tile.ALL, Tile.ALL);
		}
		public RADIUS (int radius, Keyword modified, TileCondition selectionCondition, TileCondition propogationCondition) {
			_radius = radius; _modified = modified; _selectionCondition = selectionCondition; _propogationCondition = propogationCondition;
		}
		public ActionState apply (ActionState state) {
			EmanationRegion area = new EmanationRegion (state._target, 2, _radius, true, _selectionCondition, _propogationCondition);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = _modified.apply(state);
			}
			state._target = oldTarget;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString() {
			return "Radius "+_radius+": "+_modified.toString();
		}
	}
	public static class V_LINE implements Keyword{
		Keyword _modified;
		int _distance;
		
		public V_LINE (int distance, Keyword modified) {
			_modified = modified; _distance = distance;
		}
		public ActionState apply (ActionState state) {
			ConeLineRegion area = new ConeLineRegion(state._target, state._directionTile, _distance, true, Tile.ALL);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = _modified.apply(state);
			}
			state._target = oldTarget;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString() {
			return "V-Line "+_distance+": "+_modified.toString();
		}
	}
	public static class MANA_GAIN implements Keyword {
		int _gain;
		
		public MANA_GAIN (int gain) {
			_gain = gain;
		}
		public ActionState apply(ActionState state) {
			Effect.EffectFunction manaGainFunc = (int power, Tile origin, Tile target) -> {
				if (origin == null || origin.getMob() == null)
					return false;
				origin.getMob().incrementMana(_gain);
				return true;
			};
			state._effects.add(new Effect (state._power, state._origin, state._target, manaGainFunc));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image;
		}
		public String toString() {
			return "Mana Gain: "+_gain;
		}
	}
	
	public static class MANA_COST implements Keyword{
		Keyword _modified;
		int _cost;
		
		public MANA_COST (int cost, Keyword modified) {
			_modified = modified; _cost = cost;
		}
		public ActionState apply(ActionState state) {
			if (!costSatisfied(state._origin))
				return state;
			
			Effect.EffectFunction manaCostFunc = (int power, Tile origin, Tile target) -> {
				if (origin == null || origin.getMob() == null)
					return false;
				origin.getMob().incrementMana(_cost * -1);
				return true;
			};
			state._effects.add(new Effect (state._power, state._origin, state._target, manaCostFunc));
			return _modified.apply(state);
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString() {
			return "Mana Cost: " + _cost + "\n" + _modified.toString();
		}
		public boolean costSatisfied (Tile origin) {
			return origin.getMob().getMana() >= _cost;
		}
	}

	private Keyword[] _keywords;
	private int _basePower;
	private int _baseRange;
	
	public Action (Keyword[] keywords, int basePower, int baseRange) {
		_keywords = keywords;
		_basePower = basePower;
		_baseRange = baseRange;
	}
	public Action (Action otherAction) {
		this(otherAction._keywords, otherAction._basePower, otherAction._baseRange);
	}
	
	private ActionState executeKeywords (Tile origin, Tile target, Tile directionTile) {
		ActionState state = new ActionState(_basePower, origin, target, directionTile);
		for (Keyword keyword : _keywords)
			state = keyword.apply(state);
		return state;
	} 
	
	private ActionImage getImage () {
		ActionImage image = new ActionImage();
		for (Keyword keyword : _keywords)
			keyword.modifyImage(image);
		return image;
	}
	
	public void draw (Graphics2D g, int x, int y) {
		getImage().draw(g, x, y);
	}
	
	public boolean doAction (Tile origin, Tile target) {
		return doAction(origin, target, null);
	}
	public boolean doAction (Tile origin, Tile target, Tile directionTile) {
		origin.getMob().decrementActionsLeft(1);
		ActionState state = executeKeywords(origin, target, directionTile);
		return state.applyEffects();
	}
	
	public boolean costSatisfied (Tile origin) {
		for (Keyword keyword : _keywords)
			if (!keyword.costSatisfied(origin))
				return false;
		return true;
	}
	
	public Collection<Tile> getTargets (Tile origin, Tile target, Tile directionTile) {
		ActionState state = executeKeywords(origin, target, directionTile);
		return state.getTargets();
	}
	
	public TileRegion getTilesInRangeFrom (Tile origin) {
		return new RadiatingLinesRegion(origin, _baseRange, false);
	}
	
	public boolean equals (Action other) {
		return _basePower == other._basePower
				&& Objects.deepEquals(_keywords, other._keywords);
	}
	
	public String getDescription () {
		StringBuilder builder = new StringBuilder();
		builder.append("X:"+_basePower+"\n");
		builder.append("Range:"+_baseRange+"\n");
		for (Keyword keyword : _keywords)
			builder.append(keyword.toString() + "\n");
		return builder.toString();
	}
}
