package Mobs;

//Labels and factories are in separate files to make sure static labels are initialized before static Mobs, Items, etc.
public class MobLabel {
	private String _label;
		
	private MobLabel (String label) {
		_label = label;
	}
	
	public boolean equals (MobLabel other) {
		return _label.equals(other._label);
	}
	
	public String toString() {
		return _label;
	}
	
	public static final MobLabel WARRIOR = new MobLabel("WARRIOR");
	public static final MobLabel ROGUE = new MobLabel("ROGUE");
	public static final MobLabel WIZARD = new MobLabel("WIZARD");
	public static final MobLabel DRUID = new MobLabel("DRUID");
	public static final MobLabel CLERIC = new MobLabel("CLERIC");
	public static final MobLabel ALCHEMIST = new MobLabel("ALCHEMIST");
	public static final MobLabel BARD = new MobLabel("BARD");
	public static final MobLabel MONK = new MobLabel("MONK");
	
	public static final MobLabel ZOMBIE = new MobLabel("ZOMBIE");
	public static final MobLabel SKELETON = new MobLabel("SKELETON");
	
	public static final MobLabel GIANT_WORM_HEAD = new MobLabel("GIANT WORM HEAD");
	
	public static final MobLabel WALL = new MobLabel("WALL");
}
