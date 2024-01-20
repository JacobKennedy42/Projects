package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ColoredRectangle implements ColoredShape{
	private Rectangle _shape;
	private Color _color;
	
	public ColoredRectangle (int width, int height, Color color) {
		_shape = new Rectangle (width, height);
		_color = color;
	}
	
	@Override
	public void setColor (Color color) {
		_color = color;
	}
	
	@Override
	public Color getColor() {
		return _color;
	}
	
	@Override
	public void draw (Graphics2D g, int centerX, int centerY) {
		g.setPaint(_color);
		_shape.setFrame(centerX-(_shape.getWidth()/2), centerY-(_shape.getHeight()/2), _shape.getWidth(), _shape.getHeight());
		g.fill(_shape);
	}
	
	public boolean equals (ColoredRectangle other) {
		return _shape.equals(other._shape)
				&& _color.equals(other._color);
	}
}
