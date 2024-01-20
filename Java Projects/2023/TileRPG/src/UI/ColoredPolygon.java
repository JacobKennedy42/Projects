package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class ColoredPolygon implements ColoredShape {

	private Polygon _shape;
	private Color _color;
	
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
	public Color getColor() {
		return _color;
	}

	@Override
	public void draw(Graphics2D g, int centerX, int centerY) {
		g.setPaint(_color);
		movePolygonTo(centerX, centerY);
		g.fill(_shape);
	}
	
	private void movePolygonTo (int centerX, int centerY) {
		Rectangle2D bounds = _shape.getBounds2D();
		int currentCenterX = (int)(bounds.getX() + bounds.getWidth()/2);
		int currentCenterY = (int)(bounds.getY() + bounds.getHeight()/2);
		int deltaX = (int)(centerX - currentCenterX);
		int deltaY = (int)(centerY - currentCenterY);
		_shape.translate(deltaX, deltaY);
	}

	public boolean equals (ColoredPolygon other) {
		return _shape.equals(other._shape)
				&& _color.equals(other._color);
	}
	
}
