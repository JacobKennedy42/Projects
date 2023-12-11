package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class ColoredEllipse implements ColoredShape {

	public Ellipse2D _shape;
	public Color _color;
	
	public ColoredEllipse (int width, int height, Color color) {
		_shape = new Ellipse2D.Float(0, 0, width, height);
		_color = color;
	}
	
	@Override
	public void setColor (Color color) {
		_color = color;
	}
	
	@Override
	public void draw(Graphics2D g, int x, int y) {
		g.setPaint(_color);
		_shape.setFrame(x, y, _shape.getWidth(), _shape.getHeight());
		g.fill(_shape);
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof ColoredEllipse))
			return false;
		ColoredEllipse otherShape = (ColoredEllipse) other;
		return _shape.equals(otherShape._shape)
				&& _color.equals(otherShape._color);
	}
}
