package Items;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

import Board.Tile;
import Board.Tile.TileCondition;
import Items.ActionImage.ActionImageShape;
import Mobs.MobFactory;
import Mobs.MobLabel;
import TileRegions.ConeLineRegion;
import TileRegions.ConeRegion;
import TileRegions.EmanationRegion;
import TileRegions.RadiatingLinesRegion;
import TileRegions.TileRegion;

public class Action {
	
	private static final Color PURPLE = new Color(163, 73, 164);
	
	private static interface Effect {
		public boolean apply ();
		public default void preview() {};
		public default void revertPreview() {};
	}
	private static class DamageEffect implements Effect {
		private final int _power;
		private final Tile _target;
		
		public DamageEffect (ActionState state) {
			_power = state._power; _target = state._target;
		}
		
		public boolean apply () {
			if (_target == null || _target.getMob() == null)
				return false;
			_target.getMob().damage(_power);
			return true;
		}
		
		public void preview () {
			if (_target == null || !_target.hasCreatureMob())
				return;
//			_target.getCreatureMob().incrementIncomingAttackDamage(_power);
		}
		public void revertPreview () {
			if (_target == null || !_target.hasCreatureMob())
				return;
//			_target.getCreatureMob().incrementIncomingAttackDamage(_power * -1);
		}
	}
	private static class HealEffect implements Effect {
		private final int _power;
		private final Tile _target;
		
		public HealEffect (ActionState state) {
			_power = state._power; _target = state._target;
		}
		
		public boolean apply () {
			if (_target == null || _target.getMob() == null)
				return false;
			_target.getMob().heal(_power);
			return true;
		}
	}
	private static class ShieldEffect implements Effect {
		private final int _power;
		private final Tile _target;
		
		public ShieldEffect (ActionState state) {
			_power = state._power; _target = state._target;
		}
		
		public boolean apply () {
			if (_target == null || !_target.hasCreatureMob())
				return false;
			_target.getCreatureMob().incrementShields(_power);
			return true;
		}
	}
	private static class PoisonEffect implements Effect {
		private final int _power;
		private final Tile _target;
		
		public PoisonEffect (ActionState state) {
			_power = state._power; _target = state._target;
		}
		
		public boolean apply () {
			if (_target == null || !_target.hasCreatureMob())
				return false;
			_target.getCreatureMob().incrementPoison(_power);
			return true;
		}
	}
	private static class RegenEffect implements Effect {
		private final int _power;
		private final Tile _target;
		
		public RegenEffect (ActionState state) {
			_power = state._power; _target = state._target;
		}
		
		public boolean apply () {
			if (_target == null || !_target.hasCreatureMob())
				return false;
			_target.getCreatureMob().incrementRegen(_power);
			return true;
		}
	}
	private static class SummonEffect implements Effect {
		private final Tile _target;
		private final MobLabel _mob;
		
		public SummonEffect (ActionState state, MobLabel mob) {
			_target = state._target; _mob = mob;
		}
		
		public boolean apply () {
			if (_target == null || _target.getMob() != null)
				return false;
			_target.attachMob(MobFactory.get(_mob));
			return true;
		}
	}
	private static class SlowTileEffect implements Effect {
		private final Tile _target;
		
		public SlowTileEffect (ActionState state) {
			_target = state._target;
		}
		
		public boolean apply () {
			if (_target == null)
				return false;
			return _target.makeSlowTile();
		}
	}
	private static class PushEffect implements Effect {
		private final Tile _directionTile;
		private final Tile _target;
		private final int _distance;
		
		public PushEffect (ActionState state) {
			_directionTile = state._directionTile;
			_target = state._target;
			_distance = state._power;
		}
		
		public boolean apply () {
			if (_directionTile == null || _target == null)
				return false;
			_directionTile.pushMobAway(_target, _distance);
			return true;
		}
	}
	private static class IncrementPowerOnTargetEffect implements Effect {
		private final int _delta;
		private final Tile _target;
		
		public IncrementPowerOnTargetEffect (ActionState state, int delta) {
			_delta = delta; _target = state._target;
		}
		
		public boolean apply() {
			if (_target == null || !_target.hasCreatureMob())
				return false;
			_target.getCreatureMob().incrementPowerModifiers(_delta);
			return true;
		}
	}
	private static class ManaGainEffect implements Effect {
		private final Tile _origin;
		private final int _gain;
		
		public ManaGainEffect (ActionState state, int gain) {
			_origin = state._origin; _gain = gain;
		}
		
		public boolean apply () {
			if (_origin == null || !_origin.hasCreatureMob())
				return false;
			_origin.getCreatureMob().incrementMana(_gain);
			return true;
		}
	}
	
	private static class ManaCostEffect implements Effect {
		private final Tile _origin;
		private final int _cost;
		
		public ManaCostEffect (ActionState state, int cost) {
			_origin = state._origin; _cost = cost;
		}
		
		public boolean apply () {
			if (_origin == null || !_origin.hasCreatureMob())
				return false;
			_origin.getCreatureMob().incrementMana(_cost * -1);
			return true;
		}
	}
	private static class UseLimitEffect implements Effect {
		private final USE_LIMIT _keyword;
		
		public UseLimitEffect (USE_LIMIT keyword) {
			_keyword = keyword;
		}
		
		public boolean apply () {
			if (_keyword == null)
				return false;
			_keyword.decrementUsesLeft();
			return true;
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
		
		public boolean applyEffects () {
			boolean isSuccessful = false;
			for (Effect effect : _effects)
				if (effect.apply())
					isSuccessful = true;
			return isSuccessful;
		}
		
		public void applyPreview () {
			for (Effect effect : _effects)
				effect.preview();
		}
		public void revertPreview () {
			for (Effect effect : _effects)
				effect.revertPreview();
		}
	}
	
	public static interface Keyword {
		ActionState apply (ActionState state);
		ActionImage modifyImage (ActionImage image);
		public String toString();
		default boolean costSatisfied (Tile origin) {return true;}
		default Keyword makeCopy() {return this;}	//most Keywords are immutable. If mutable, this should be overridden
		default Iterable<Tile> getTargets (ActionState state) {return new LinkedList<Tile>(Arrays.asList(state._target));}
	}
	
	public static class DO_DAMAGE implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new DamageEffect (state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.red);
		}
		public String toString () {
			return "Deal X damage";
		}
	}
	public static class HEAL implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new HealEffect (state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.yellow);
		}
		public String toString () {
			return "Heal X";
		}
	}
	public static class SHIELD implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new ShieldEffect (state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.gray);
		}
		public String toString () {
			return "Shield X";
		}
	}
	public static class POISON implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new PoisonEffect (state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, PURPLE);
		}
		public String toString () {
			return "Poison X";
		}
	}
	public static class REGEN implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new RegenEffect (state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.pink);
		}
		public String toString () {
			return "Regen X";
		}
	}
	public static class SUMMON implements Keyword {
		private final MobLabel _mob;
		
		public SUMMON (MobLabel mob) {
			_mob = mob;
		}
		public ActionState apply (ActionState state) {
			state._effects.add(new SummonEffect (state, _mob));
			return state;	
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.HEXAGON, Color.black);
		}
		public String toString() {
			return "Summon: "+_mob.toString();
		}
	}
	public static class MAKE_SLOW_TILE implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new SlowTileEffect(state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.HEXAGON, Tile.SLOW_TILE_COLOR);
		}
		public String toString () {
			return "Make Slow Tile";
		}
	}
	public static class PUSH implements Keyword {
		public ActionState apply (ActionState state) {
			state._effects.add(new PushEffect(state));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.white);
		}
		public String toString () {
			return "Push X";
		}
	}
	public static class INCREMENT_POWER implements Keyword {
		private final int _delta;
		
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
			return ""+plusOrMinus+Math.abs(_delta)+" X";
		}
	}
	public static class INCREMENT_POWER_ON_TARGET implements Keyword {
		private final int _delta;
		
		public INCREMENT_POWER_ON_TARGET (int delta) {
			_delta = delta;
		}
		public ActionState apply (ActionState state) {
			state._effects.add(new IncrementPowerOnTargetEffect(state, _delta));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image.setBodyTo(ActionImageShape.CIRCLE, Color.magenta);
		}
		public String toString () {
			char plusOrMinus = _delta > 0 ? '+' : '-';
			return ""+plusOrMinus+Math.abs(_delta)+" X until end of turn";
		}
	}
	public static class MANA_GAIN implements Keyword {
		private final int _gain;
		
		public MANA_GAIN (int gain) {
			_gain = gain;
		}
		public ActionState apply(ActionState state) {
			state._effects.add(new ManaGainEffect(state, _gain));
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return image;
		}
		public String toString() {
			return "Mana Gain: "+_gain;
		}
	}
	
	public static class FLANKING implements Keyword {
		private final Keyword _modified;
		
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
		public Iterable<Tile> getTargets(ActionState state) {
			return _modified.getTargets(state);
		}
	}
	public static class IF_FRIENDLY implements Keyword {
		private final Keyword _modified;
		
		public IF_FRIENDLY (Keyword modified) {
			_modified = modified;
		}
		
		public ActionState apply (ActionState state) {
			if (state._origin.hasCreatureMob())
			
			if (state._origin.hasCreatureMob() && state._target.hasCreatureMob() 
					&& state._origin.getCreatureMob().isFriendlyTo(state._target.getCreatureMob()))
				return _modified.apply(state);
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString () {
			return "If Friendly: "+_modified.toString();
		}
		public Iterable<Tile> getTargets(ActionState state) {
			return _modified.getTargets(state);
		}
	}
	public static class IF_HOSTILE implements Keyword {
		private final Keyword _modified;
		
		public IF_HOSTILE (Keyword modified) {
			_modified = modified;
		}
		
		public ActionState apply (ActionState state) {
			if (state._origin.hasCreatureMob())
			
			if (state._origin.hasCreatureMob() && state._target.hasCreatureMob() 
					&& !state._origin.getCreatureMob().isFriendlyTo(state._target.getCreatureMob()))
				return _modified.apply(state);
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString () {
			return "If Hostile: "+_modified.toString();
		}
		public Iterable<Tile> getTargets(ActionState state) {
			return _modified.getTargets(state);
		}
	}
	
	public static class RADIUS implements Keyword{
		private final Keyword _modified;
		private final int _radius;
		private final TileCondition _selectionCondition, _propogationCondition;
		
		public RADIUS (int radius, Keyword modified) {
			this(radius, modified, Tile.ALL, Tile.ALL);
		}
		public RADIUS (int radius, Keyword modified, TileCondition selectionCondition, TileCondition propogationCondition) {
			_radius = radius; _modified = modified; _selectionCondition = selectionCondition; _propogationCondition = propogationCondition;
		}
		public ActionState apply (ActionState state) {
			Iterable<Tile> area = getTargets(state);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = _modified.apply(state);
			}
			state._target = oldTarget;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image).setAreaFlagTo(ActionImageShape.CIRCLE, Color.black);
		}
		public String toString() {
			return "Radius "+_radius+": "+_modified.toString();
		}
		public Iterable<Tile> getTargets (ActionState state) {
			return new EmanationRegion (state._target, 2, _radius, true, _selectionCondition, _propogationCondition);
		}
	}
	public static class CONE implements Keyword{
		private final Keyword _modified;
		private final int _distance;
		
		public CONE (int distance, Keyword modified) {
			_modified = modified; _distance = distance;
		}
		public ActionState apply (ActionState state) {
			Iterable<Tile> area = getTargets(state);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = _modified.apply(state);
			}
			state._target = oldTarget;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image).setAreaFlagTo(ActionImageShape.DIAMOND, Color.black);
		}
		public String toString() {
			return "Cone "+_distance+": "+_modified.toString();
		}
		public Iterable<Tile> getTargets (ActionState state) {
			return new ConeRegion(state._directionTile, state._target, _distance, Tile.ALL);
		}
	}
	public static class V_LINE implements Keyword{
		private final Keyword _modified;
		private final int _distance;
		
		public V_LINE (int distance, Keyword modified) {
			_modified = modified; _distance = distance;
		}
		public ActionState apply (ActionState state) {
			Iterable<Tile> area = getTargets(state);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = _modified.apply(state);
			}
			state._target = oldTarget;
			return state;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image).setAreaFlagTo(ActionImageShape.TRIANGLE, Color.black);
		}
		public String toString() {
			return "V-Line "+_distance+": "+_modified.toString();
		}
		public Iterable<Tile> getTargets (ActionState state) {
			return new ConeLineRegion(state._target, state._directionTile, _distance, true, Tile.ALL);
		}
	}

	public static class MANA_COST implements Keyword{
		private final Keyword _modified;
		private final int _cost;
		
		public MANA_COST (int cost, Keyword modified) {
			_modified = modified; _cost = cost;
		}
		public ActionState apply(ActionState state) {
			if (!costSatisfied(state._origin))
				return state;
			
			state._effects.add(new ManaCostEffect (state, _cost));
			return _modified.apply(state);
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image);
		}
		public String toString() {
			return "Mana Cost: " + _cost + "\n" + _modified.toString();
		}
		public boolean costSatisfied (Tile origin) {
			return origin.hasCreatureMob() && origin.getCreatureMob().getMana() >= _cost;
		}
		public Iterable<Tile> getTargets(ActionState state) {
			return _modified.getTargets(state);
		}
	}
	public static class USE_LIMIT implements Keyword{
		private final Keyword _modified;
		private int _usesLeft;
		
		public USE_LIMIT (int usesLeft, Keyword modified) {
			_modified = modified; _usesLeft = usesLeft;
		}
		private USE_LIMIT (USE_LIMIT other) {
			this(other._usesLeft, other._modified);
		}
		public ActionState apply(ActionState state) {
			if (!costSatisfied(state._origin))
				return state;
			state._effects.add(new UseLimitEffect(this));
			return _modified.apply(state);
		}
		public boolean costSatisfied (Tile origin) {
			return _usesLeft > 0;
		}
		public ActionImage modifyImage (ActionImage image) {
			return _modified.modifyImage(image).setUsesLeftFlasTo(_usesLeft);
		}
		public String toString() {
			return "Uses Left: " + _usesLeft + "\n" + _modified.toString();
		}
		public Keyword makeCopy () {
			return new USE_LIMIT(this);
		}
		public boolean equals (USE_LIMIT other) {
			return other != null
					&& _modified.equals(other._modified)
					&& _usesLeft == other._usesLeft;
		}
		void decrementUsesLeft () {
			--_usesLeft;
		}
		public Iterable<Tile> getTargets(ActionState state) {
			return _modified.getTargets(state);
		}
	}

	private Keyword[] _keywords;
	private final int _basePower;
	private int _powerModifier;
	private final int _baseRange;
	
	public Action (Keyword[] keywords, int basePower, int baseRange) {
		this(keywords, basePower, 0, baseRange);
	}
	private Action (Keyword[] keywords, int basePower, int powerModifier, int baseRange) {
		setKeywords(keywords);
		_basePower = basePower;
		_powerModifier = powerModifier;
		_baseRange = baseRange;
	}
	public Action (Action otherAction) {
		this(otherAction._keywords, otherAction._basePower, otherAction._powerModifier, otherAction._baseRange);
	}
	
	private void setKeywords (Keyword[] keywords) {
		Keyword[] copiedKeywords = new Keyword[keywords.length];
		for (int i = 0; i < keywords.length; ++i)
			copiedKeywords[i] = keywords[i].makeCopy();
		_keywords = copiedKeywords;
	}
	
	private ActionState executeKeywords (Tile origin, Tile target, Tile directionTile) {
		ActionState state = new ActionState(getPower(), origin, target, directionTile);
		for (Keyword keyword : _keywords)
			state = keyword.apply(state);
		return state;
	}
	
	private int getPower () {
		int power = _basePower + _powerModifier;
		power = power >= 0 ? power : 0;
		return power;
	}
	
	public void incrementPowerModifier (int delta) {
		_powerModifier += delta;
	}
	public void clearPowerModifier () {
		_powerModifier = 0;
	}
	
	private ActionImage getImage () {
		ActionImage image = new ActionImage();
		for (Keyword keyword : _keywords)
			image = keyword.modifyImage(image);
		return image;
	}
	
	public void draw (Graphics2D g, int x, int y) {
		getImage().draw(g, x, y);
	}
	
	public boolean doAction (Tile origin, Tile target) {
		return doAction(origin, target, null);
	}
	public boolean doAction (Tile origin, Tile target, Tile directionTile) {
		if (origin == null || target == null)
			return false;
		
		ActionState state = executeKeywords(origin, target, directionTile);
		boolean actionSuccessful = state.applyEffects();
		if (actionSuccessful)
			origin.getCreatureMob().decrementActionsLeft(1);
		return actionSuccessful;
	}
	
	public void applyPreview (Tile origin, Tile target) {
		applyPreview(origin, target, null);
	}
	public void applyPreview (Tile origin, Tile target, Tile directionTile) {
		if (origin == null || target == null)
			return;
		
		ActionState state = executeKeywords(origin, target, directionTile);
		state.applyPreview();
	}
	public void revertPreview (Tile origin, Tile target) {
		revertPreview(origin, target, null);
	}
	public void revertPreview (Tile origin, Tile target, Tile directionTile) {
		if (origin == null || target == null)
			return;
		
		ActionState state = executeKeywords(origin, target, directionTile);
		state.revertPreview();
	}
	
	public boolean costSatisfied (Tile origin) {
		for (Keyword keyword : _keywords)
			if (!keyword.costSatisfied(origin))
				return false;
		return true;
	}
	
	public Collection<Tile> getAreaOfInfluence (Tile origin) {
		Collection<Tile> influencedTiles = new LinkedList<Tile>();
		TileRegion inRangeTiles = getTilesInRangeFrom(origin);
		for (Tile tile : inRangeTiles) {
			Tile directionTile = inRangeTiles.getNeighborTowardsOrigin(tile);
			if (directionTile != null) {
				Iterable<Tile> targets = getTargets(origin, tile, directionTile);
				for (Tile target : targets)
					if (!influencedTiles.contains(target))
						influencedTiles.add(target);
			}
		}
		return influencedTiles;
	}
	
	public Iterable<Tile> getTargets (Tile origin, Tile target, Tile directionTile) {
		ActionState state = new ActionState(getPower(), origin, target, directionTile);
		HashSet<Tile> targets = new HashSet<Tile>();
		for (Keyword keyword : _keywords)
			for (Tile keywordTarget : keyword.getTargets(state))
				targets.add(keywordTarget);
		return targets;
	}
	
	public TileRegion getTilesInRangeFrom (Tile origin) {
		if (_baseRange == 0)
			return new RadiatingLinesRegion(origin, _baseRange, true, Tile.ALL);
		return new RadiatingLinesRegion(origin, _baseRange, false);
	}
	
	public boolean equals (Action other) {
		return _basePower == other._basePower
				&& _powerModifier == other._powerModifier
				&& _baseRange == other._baseRange
				&& Objects.deepEquals(_keywords, other._keywords);
	}
	
	public int getRange() {
		return _baseRange;
	}
	
	public String getDescription () {
		StringBuilder builder = new StringBuilder();
		builder.append("X:"+getPower()+"\n");
		builder.append("Range:"+_baseRange+"\n");
		for (Keyword keyword : _keywords)
			builder.append(keyword.toString() + "\n");
		return builder.toString();
	}
}
