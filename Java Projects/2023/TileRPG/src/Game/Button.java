package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Button implements CanvasObject {

	private int _x, _y;
	private ButtonAction _action;
	private FilledShape _background;
	private String _text;
	private static final int BUTTON_WIDTH = 100, BUTTON_HEIGHT = 50;
	
	@FunctionalInterface
	public interface ButtonAction {
		public void performAction();
	}
	
	public Button (int x, int y, String text, Color color, ButtonAction action) {
		_x = x;
		_y = y;
		_action = action;
		_background = new FilledShape(new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT), color);
		_text = text;
	}
	
	public void setText (String text) {
		_text = text;
	}
	
	@Override
	public void draw(Graphics2D g) {
		_background.draw(g, _x, _y);
		g.setPaint(Color.black);
		g.drawString(_text, _x+(BUTTON_WIDTH/2), _y+(BUTTON_HEIGHT/2));
	}

	@Override
	public boolean leftMouseButtonReleased(int x, int y) {
		return clickButton(x, y);
	}

	@Override
	public boolean rightMouseButtonReleased(int x, int y) {
		return clickButton(x, y);
	}
	
	private boolean clickButton (int x, int y) {
		if (x >= _x && x < _x + BUTTON_WIDTH &&
			y >= _y && y < _y + BUTTON_HEIGHT) {
			_action.performAction();
			return true;
		}
		return false;
	}
	
}
