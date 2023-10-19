package Items;

import java.util.Collection;

import Game.Tile;
import TileRegions.RadiatingLinesRegion;
import TileRegions.TileRegion;
import Items.Action.Keyword;

public class Item {
	
	private ItemState _state;
	
	public class ItemLibrary {
		public static final ItemState SWORD = new ItemState(
				"SWORD",
				new Action (new Keyword[] {Action.DO_DAMAGE}),
				2,
				1);
		public static final ItemState FIST = new ItemState(
				"FIST",
				new Action (new Keyword[] {Action.DO_DAMAGE}),
				1,
				1);
		public static final ItemState BOW = new ItemState(
				"BOW",
				new Action (new Keyword[] {Action.DO_DAMAGE}),
				2,
				3);
		public static final ItemState DAGGER = new ItemState(
				"DAGGER",
				new Action (new Keyword[] {
						Action.FLANKING(Action.INCREMENT_POWER(2)),
						Action.DO_DAMAGE}),
				1,
				1);
		public static final ItemState FIRE_WAND = new ItemState (
				"FIRE WAND",
				new Action (new Keyword[] {Action.AREA_OF_EFFECT(Action.DO_DAMAGE, 1)}),
				2,
				3);
	}
	
	public static class ItemState {
		public String _label;
		public Action _action;
		public int _power, _range;
		
		public ItemState (String label, Action action, int power, int range) {
			_label = label; _action = action; _power = power; _range = range;
		}
		
		public ItemState (ItemState otherState) {
			this(otherState._label, otherState._action, otherState._power, otherState._range);
		}
		
		public boolean equals (ItemState other) {
			return other != null
					&& _label.equals(other._label)
					&& _action.equals(other._action)
					&& _power == other._power
					&& _range == other._range;
		}
	}
	
	public Item (ItemState state) {
		setState(state);
	}
	
	public Item (Item otherItem) {
		setState(otherItem._state);
	}
	
	private void setState (ItemState state) {
		_state = new ItemState(state);
	}
	
	public TileRegion getTilesInRangeFrom (Tile origin) {
		return new RadiatingLinesRegion(origin, _state._range, false);
	}
	
	public boolean doActionOnMob (Tile origin, Tile target) {
		return _state._action.doAction(_state._power, origin, target);
	}
	
	public Collection<Tile> getTargets (Tile origin, Tile target) {
		return _state._action.getTargets(_state._power, origin, target);
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof Item))
			return false;
		Item otherItem = (Item) other;
		return _state.equals(otherItem._state);
	}
	
	@Override
	public String toString() {
		return _state._label+":"+
			   "\nRange: "+_state._range+
			   "\nX: "+_state._power+
			   "\n"+_state._action.getDescription(_state._power);
	}
}
