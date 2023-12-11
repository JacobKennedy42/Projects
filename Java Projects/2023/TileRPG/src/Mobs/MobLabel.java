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
	
	public static final MobLabel SWORDSMAN = new MobLabel("SWORDSMAN");
	public static final MobLabel ARCHER = new MobLabel("ARCHER");
	public static final MobLabel ROGUE = new MobLabel("ROGUE");
	public static final MobLabel WIZARD = new MobLabel("WIZARD");
	public static final MobLabel DRUID = new MobLabel("DRUID");
	
	public static final MobLabel ENEMY = new MobLabel("ENEMY");
	
	public static final MobLabel WALL = new MobLabel("WALL");
}
