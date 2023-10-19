package Game;

import java.awt.Color;
import java.awt.Graphics2D;

import Game.Button.ButtonAction;

public class TurnButton implements CanvasObject{

	private Button _button;
	private String _text;
	private int _turn;
	
	public TurnButton (int x, int y, String text, Color color, ButtonAction action) {
		_button = new Button(x, y, text, color, action);
		_text = text;
		_turn = 0;
	}
	
	@Override
	public void draw(Graphics2D g) {
		_button.setText(_text + _turn);
		_button.draw(g);
	}
	
	@Override
	public boolean leftMouseButtonReleased(int x, int y) {
		if (_button.leftMouseButtonReleased(x, y)) {
			++_turn;
			return true;
		}
		return false;
	}

	@Override
	public boolean rightMouseButtonReleased(int x, int y) {
		if (_button.rightMouseButtonReleased(x, y)) {
			++_turn;
			return true;
		}
		return false;
	}
}
