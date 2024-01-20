package UI;

import java.awt.Color;
import java.awt.Graphics2D;

public class Button implements CanvasObject {

	private int _x, _y;
	private ButtonAction _action;
	private ColoredShape _background;
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
		_background = new ColoredRectangle(BUTTON_WIDTH, BUTTON_HEIGHT, color);
		_text = text;
	}
	
	public void setText (String text) {
		_text = text;
	}
	
	@Override
	public void draw(Graphics2D g) {
		_background.draw(g, _x, _y);
		g.setPaint(Color.black);
		g.drawString(_text, _x, _y);
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
		int leftBound = _x - (BUTTON_WIDTH/2);
		int rightBound = _x + (BUTTON_WIDTH/2);
		int upBound = _y - (BUTTON_HEIGHT/2);
		int downBound = _y + (BUTTON_HEIGHT/2);
		if (x >= leftBound && x < rightBound &&
				y >= upBound && y < downBound) {
			_action.performAction();
			return true;
		}
		return false;
	}
	
}
