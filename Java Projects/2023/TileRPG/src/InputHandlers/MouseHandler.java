package InputHandlers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import UI.Canvas;

public class MouseHandler extends MouseAdapter {
	private int x, y;
	private Canvas _canvas;
	private static final int LEFT_MOUSE_BUTTON = 1, RIGHT_MOUSE_BUTTON = 3;
	
	public MouseHandler (Canvas canvas) {
		_canvas = canvas;
	}
	
	@Override
	public void mouseReleased (MouseEvent e) {
		int button = e.getButton();
		if (button == LEFT_MOUSE_BUTTON)
			_canvas.leftMouseButtonReleased(e.getX(), e.getY());
		else if (button == RIGHT_MOUSE_BUTTON)
			_canvas.rightMouseButtonReleased(e.getX(), e.getY());
	}
	
	@Override
	public void mouseMoved (MouseEvent e) {
		_canvas.mouseMovedTo(e.getX(), e.getY());
	}
}
