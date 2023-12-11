package Items;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import Items.Action.*;
import Mobs.MobLabel;

public class Item {
	
	private String _name;
	private Action[] _actions;
	
	public static class ItemFactory {
		private static final HashMap<ItemLabel, Item> _itemCatalogue = new HashMap<ItemLabel, Item>();
		
		private static void put (ItemLabel label, Item item) {
			_itemCatalogue.put(label, item);
			item._name = label.toString();
		}
		
		public static Item get (ItemLabel label) {
			return new Item(_itemCatalogue.get(label));
		}
		
		public static List<Item> get (Iterable<ItemLabel> labels) {
			List<Item> items = new LinkedList<Item>();
			for (ItemLabel label : labels)
				items.add(get(label));
			return items;
		}
		
		static {
			put(ItemLabel.FIST,			FIST);
			put(ItemLabel.SWORD,		SWORD);
			put(ItemLabel.BOW,			BOW);
			put(ItemLabel.DAGGER,		DAGGER);
			put(ItemLabel.FIRE_WAND,	FIRE_WAND);
			put(ItemLabel.EARTH_ROD,	EARTH_ROD);
		}
		
		private ItemFactory () {}
	}
	
	private static final Item FIST = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new DO_DAMAGE()},
			1, 1)});
	private static final Item SWORD = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new DO_DAMAGE()},
			2, 1)});
	private static final Item BOW = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new DO_DAMAGE()},
			1, 3)});
	private static final Item DAGGER = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new FLANKING(new INCREMENT_POWER(2)),
				new DO_DAMAGE()},
			1, 1)});
	private static final Item FIRE_WAND = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new MANA_GAIN(1),
				new DO_DAMAGE()},
			1, 3),
			new Action (new Keyword[] {
				new MANA_COST(2, new RADIUS(1, new DO_DAMAGE()))},
			2, 3)});
	private static final Item EARTH_ROD = new Item (
		new Action[] {
			new Action (new Keyword[] {
				new MANA_GAIN(1),
				new V_LINE(1, new SUMMON(MobLabel.WALL))},
			1, 3),
			new Action (new Keyword[] {
				new MANA_COST(2, new RADIUS(2, new MAKE_SLOW_TILE()))},
			1, 3)});
	
	private Item (Action[] actions) {
		setActions(actions);
	}
	public Item (Item otherItem) {
		this(otherItem._actions);
		_name = otherItem._name;
	}
	
	private void setActions (Action[] otherActions) {
		_actions = new Action[otherActions.length];
		for (int i = 0; i < _actions.length; ++i)
			_actions[i] = otherActions[i];
	}

	public Action[] getActions () {
		return _actions;
	}
	
	public boolean equals (Item other) {
		return other != null 
				&& _name.equals(other._name)
				&& Objects.deepEquals(_actions, other._actions);
	}
	
	@Override
	public String toString() {
		String description = _name+":";
		for (Action action : _actions)
			description += "\n"+action.getDescription();
		return description;
	}
}
