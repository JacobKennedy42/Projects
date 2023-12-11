package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class ColoredPolygon implements ColoredShape {

	Polygon _shape;
	Color _color;
	
	public ColoredPolygon (int[] xPoints, int[] yPoints, Color color) {
		if (xPoints.length != yPoints.length)
			throw new RuntimeException("For a Polygon, Number of xPoints and yPoints should be equal.");
		
		_shape = new Polygon(xPoints, yPoints, xPoints.length);
		_color = color;
	}
	
	@Override
	public void setColor(Color color) {
		_color = color;
	}

	@Override
	public void draw(Graphics2D g, int x, int y) {
		g.setPaint(_color);
		movePolygonTo(x, y);
		g.fill(_shape);
	}
	
	private void movePolygonTo (int x, int y) {
		Rectangle2D bounds = _shape.getBounds2D();
		int deltaX = (int)(x - bounds.getX());
		int deltaY = (int)(y - bounds.getY());
		_shape.translate(deltaX, deltaY);
	}

}
