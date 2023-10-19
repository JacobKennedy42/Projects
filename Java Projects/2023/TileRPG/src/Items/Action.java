package Items;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import Game.Tile;
import TileRegions.EmanationRegion;

class Action {
	
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
		public Tile _origin, _target;
		public LinkedList<Effect> _effects;
		
		public ActionState (int power, Tile origin, Tile target) {
			this (power, origin, target, false);
		}
		
		public ActionState (int power, Tile origin, Tile target, boolean colorTargetsMode) {
			_power = power;
			_origin = origin;
			_target = target;
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
	
//	@FunctionalInterface
//	public static interface Keyword {
//		public ActionState apply (ActionState state);
//	}
	
	public static class Keyword {
		@FunctionalInterface
		public static interface KeywordFunction {
			public ActionState apply (ActionState state);
		}
		@FunctionalInterface
		public static interface DescriptionFunction {
			public String getDescription();
		}
		
		private KeywordFunction _func;
		private DescriptionFunction _descriptionFunc;
		
		public Keyword (KeywordFunction func, DescriptionFunction descriptionFunc) {
			_func = func;
			_descriptionFunc = descriptionFunc;
		}
		
		public ActionState apply (ActionState state) {
			return _func.apply(state);
		}
		
		public String toString() {
			return _descriptionFunc.getDescription();
		}
	}
	
	public static final Keyword DO_DAMAGE = new Keyword (
		(ActionState state) -> {
			Effect.EffectFunction damageEffectFunc = (int power, Tile origin, Tile target) -> {
				if (target == null || target.getMob() == null)
					return false;
				target.getMob().incrementHealth(power * -1);
				return true;
			};
			
			state._effects.add(new Effect (state._power, state._origin, state._target, damageEffectFunc));
			return state;
		},
		() -> {
			return "Deal X damage";
		}
	);
	public static final Keyword INCREMENT_POWER (int delta) {
		Keyword.KeywordFunction keywordFunc = (ActionState state) -> {
			state._power += delta;
			return state;
		};
		Keyword.DescriptionFunction descriptionFunc = () -> {
			char plusOrMinus = delta > 0 ? '+' : '-';
			return ""+plusOrMinus+delta+" X";
		};
		return new Keyword(keywordFunc, descriptionFunc);
	}
	public static final Keyword FLANKING (Keyword keyword) {
		Keyword.KeywordFunction keywordFunc = (ActionState state) -> {
			if (state._origin.isFlanking(state._target))
				return keyword.apply(state);
			return state;
		};
		Keyword.DescriptionFunction descriptionFunc = () -> {
			return "Flanking: "+keyword.toString();
		};
		return new Keyword(keywordFunc, descriptionFunc);
	}
	public static final Keyword AREA_OF_EFFECT (Keyword keyword, int radius) {
		Keyword.KeywordFunction keywordFunc = (ActionState state) -> {
			EmanationRegion area = new EmanationRegion (state._target, radius, true, Tile.ALL);
			Tile oldTarget = state._target;
			for (Tile newTarget : area) {
				state._target = newTarget;
				state = keyword.apply(state);
			}
			state._target = oldTarget;
			return state;
		};
		Keyword.DescriptionFunction descriptionFunc = () -> {
			return "Radius "+radius+": "+keyword.toString();
		};
		return new Keyword(keywordFunc, descriptionFunc);
	}
	
	private Keyword[] _keywords;
	
	public Action (Keyword[] keywords) {
		_keywords = keywords;
	}
	
	private ActionState executeKeywords (int power, Tile origin, Tile target) {
		ActionState state = new ActionState(power, origin, target);
		for (Keyword keyword : _keywords)
			state = keyword.apply(state);
		return state;
	}
	
	public boolean doAction (int power, Tile origin, Tile target) {
		ActionState state = executeKeywords(power, origin, target);
		return state.applyEffects();
	}
	
	public Collection<Tile> getTargets (int power, Tile origin, Tile target) {
		ActionState state = executeKeywords(power, origin, target);
		return state.getTargets();
	}
	
	public boolean equals (Action other) {
		return Objects.deepEquals(_keywords, other._keywords);
	}
	
	public String getDescription (int power) {
		StringBuilder builder = new StringBuilder();
		for (Keyword keyword : _keywords)
			builder.append(keyword.toString() + "\n");
		return builder.toString();
	}
}
