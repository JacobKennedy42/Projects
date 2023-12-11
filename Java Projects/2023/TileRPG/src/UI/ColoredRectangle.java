package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ColoredRectangle implements ColoredShape{
	public Rectangle _shape;
	public Color _color;
	
	public ColoredRectangle (int width, int height, Color color) {
		_shape = new Rectangle (width, height);
		_color = color;
	}
	
	@Override
	public void setColor (Color color) {
		_color = color;
	}
	
	@Override
	public void draw (Graphics2D g, int x, int y) {
		g.setPaint(_color);
		_shape.setFrame(x, y, _shape.getWidth(), _shape.getHeight());
		g.fill(_shape);
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof ColoredRectangle))
			return false;
		ColoredRectangle otherShape = (ColoredRectangle) other;
		return _shape.equals(otherShape._shape)
				&& _color.equals(otherShape._color);
	}
}
