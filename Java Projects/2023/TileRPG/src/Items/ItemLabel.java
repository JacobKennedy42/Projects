package Items;

//Labels and factories are in separate files to make sure static labels are initialized before static Mobs, Items, etc.
public class ItemLabel {
	private String _label;
	
	private ItemLabel (String label) {
		_label = label;
	}
	
	public boolean equals (ItemLabel other) {
		return _label.equals(other._label);
	}
	
	public String toString() {
		return _label;
	}
	
	public static final ItemLabel FIST = new ItemLabel("FIST");
	public static final ItemLabel SWORD = new ItemLabel("SWORD");
	public static final ItemLabel BOW = new ItemLabel("BOW");
	public static final ItemLabel DAGGER = new ItemLabel("DAGGER");
	public static final ItemLabel FIRE_WAND = new ItemLabel("FIRE WAND");
	public static final ItemLabel EARTH_ROD = new ItemLabel("EARTH ROD");
	public static final ItemLabel CREST = new ItemLabel("CREST");
	public static final ItemLabel POTION_KIT = new ItemLabel("POTION KIT");
	public static final ItemLabel LUTE = new ItemLabel("LUTE");
	public static final ItemLabel MARTIAL_ARTS = new ItemLabel("MARTIAL ARTS");
}
