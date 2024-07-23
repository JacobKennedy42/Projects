package Mobs;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Board.Tile;
import Items.Item;
import Items.ItemLabel;
import Items.Item.ItemFactory;
import TargetingAIs.Closest;
import UI.ColoredEllipse;
import UI.ColoredPie;
import UI.ColoredPolygon;
import UI.ColoredShape;

public class MobFactory {
	private static final HashMap<MobLabel, Mob> _mobCatalogue = new HashMap<MobLabel, Mob>();

	private static final Color DARK_GREEN = new Color(34, 177, 76);
	private static final Color PURPLE = new Color(163, 73, 164);
	
	private static void put (MobLabel label, Mob mob) {
		_mobCatalogue.put(label, mob);
		mob.setName(label.toString());
	}
	
	public static Mob get (MobLabel label) {
		return _mobCatalogue.get(label).getState();
	}
	
	public static List<Mob> get (Iterable<MobLabel> labels) {
		List<Mob> mobs = new LinkedList<Mob>();
		for (MobLabel label : labels)
			mobs.add(get(label));
		return mobs;
	}
	
	private static final Mob WARRIOR = new PlayerMob(
			makeCircle(Color.orange),
			new Item(ItemFactory.get(ItemLabel.SWORD)),
			2,
			5);
	private static final Mob ROGUE = new PlayerMob(
			makeCircle(Color.gray),
			new Item(ItemFactory.get(ItemLabel.DAGGER)),
			3,
			3);
	private static final Mob WIZARD = new PlayerMob(
			makeCircle(Color.cyan),
			new Item(ItemFactory.get(ItemLabel.FIRE_WAND)),
			2,
			2,
			2);
	private static final Mob DRUID = new PlayerMob(
			makeCircle(DARK_GREEN),
			new Item(ItemFactory.get(ItemLabel.EARTH_ROD)),
			2,
			3,
			2);
	private static final Mob CLERIC = new PlayerMob(
			makeCircle(Color.yellow),
			new Item(ItemFactory.get(ItemLabel.CREST)),
			2,
			2,
			2);
	private static final Mob ALCHEMIST = new PlayerMob(
			makeCircle(PURPLE),
			new Item(ItemFactory.get(ItemLabel.POTION_KIT)),
			2,
			3);
	private static final Mob BARD = new PlayerMob(
			makeCircle(Color.magenta),
			new Item(ItemFactory.get(ItemLabel.LUTE)),
			2,
			3,
			2);
	private static final Mob MONK = new PlayerMob(
			makeCircle(Color.white),
			new Item(ItemFactory.get(ItemLabel.MARTIAL_ARTS)),
			3,
			3);
	
	private static final Mob ZOMBIE = new EnemyMob (
			makeCircle(DARK_GREEN),
			new Closest(Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE)),
			new Item(ItemFactory.get(ItemLabel.FIST)),
			2,
			2);
	private static final Mob SKELETON = new EnemyMob (
			makeCircle(Color.white),
			new Closest(Tile.NON_BLOCKING.and(Tile.NON_SLOW_TILE)),
			new Item(ItemFactory.get(ItemLabel.BOW)),
			2,
			1);
	
	private static final Mob GIANT_WORM_HEAD = new GiantWorm.GiantWormHead();
	
	private static final Mob WALL = new Mob (
			makeHexagon(Color.black),
			1);
	
	static {
		put(MobLabel.WARRIOR,	WARRIOR);
		put(MobLabel.ROGUE,		ROGUE);
		put(MobLabel.WIZARD,	WIZARD);
		put(MobLabel.DRUID, 	DRUID);
		put(MobLabel.CLERIC, 	CLERIC);
		put(MobLabel.ALCHEMIST, ALCHEMIST);
		put(MobLabel.BARD,		BARD);
		put(MobLabel.MONK,		MONK);
		
		put(MobLabel.ZOMBIE,	ZOMBIE);
		put(MobLabel.SKELETON,	SKELETON);
		
		put(MobLabel.GIANT_WORM_HEAD, GIANT_WORM_HEAD);
		
		put(MobLabel.WALL,		WALL);
	}

	private MobFactory () {}
	
	static ColoredShape makeCircle (Color color) {
		int diameter = (Tile.TILE_WIDTH-Tile.TILE_GAP) / 2;
		return new ColoredEllipse(diameter, diameter, color);
	}
	
	private static ColoredPolygon makeHexagon (Color color) {
		double xUnit = (Tile.TILE_WIDTH-Tile.TILE_GAP)/4;
		double yUnit = xUnit / Math.sqrt(3);
		int[] xPoints = new int[] {(int) xUnit, 0, 0, (int) xUnit, (int) (2*xUnit), (int) (2*xUnit)};
		int[] yPoints = new int[] {0, (int) yUnit, (int) (3*yUnit), (int) (4*yUnit), (int) (3*yUnit), (int) yUnit};
		return new ColoredPolygon(xPoints, yPoints, color);
	}
}
