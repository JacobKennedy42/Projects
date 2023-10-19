package Game;

import java.awt.Color;
import java.awt.Graphics2D;

import Mobs.Mob;

public class MobDisplay implements CanvasObject {
	
	private int _x, _y;
	private Mob _mob;
	
	public MobDisplay (int x, int y) {
		this(x, y, null);
	}
	
	public MobDisplay (int x, int y, Mob mob) {
		_x = x;
		_y = y;
		setMob(mob);
	}
	
	public void setMob (Mob mob) {
		_mob = mob;
	}
	
	public void draw (Graphics2D g) {
		if (_mob == null)
			return;
		
		_mob.getShape().draw(g, _x, _y);
		Canvas.drawString(g, _mob.toString(), _x+20, _y);
	}
	
	public boolean leftMouseButtonReleased(int x, int y) {
		return false;
	}
	
	public boolean rightMouseButtonReleased(int x, int y) {
		return false;
	}
}
