package Game;

import java.awt.Color;
import java.awt.geom.RectangularShape;
import java.awt.Graphics2D;

public class FilledShape {
	public Color _color;
	public RectangularShape _shape;
	
	public FilledShape (RectangularShape shape, Color color) {
		_color = color;
		_shape = shape;
	}
	
	public void draw (Graphics2D g, int x, int y) {
		g.setPaint(_color);
		_shape.setFrame(x, y, _shape.getWidth(), _shape.getHeight());
		g.fill(_shape);
	}
	
	@Override
	public boolean equals (Object other) {
		if (!(other instanceof FilledShape))
			return false;
		FilledShape otherShape = (FilledShape) other;
		return _shape.equals(otherShape._shape)
				&& _color.equals(otherShape._color);
	}

}
