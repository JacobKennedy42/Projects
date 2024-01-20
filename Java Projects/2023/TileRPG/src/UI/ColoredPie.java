package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

public class ColoredPie implements ColoredShape {
	
	private Arc2D _pie;
	private Color _color;

	public ColoredPie (int width, int height, int degrees, Color color) {
		this(width, height, degrees, 0, color);
	}
	
	public ColoredPie (int width, int height, int degrees, int offset, Color color) {
		_pie = new Arc2D.Float(0, 0, width, height, 90 + offset, degrees, Arc2D.PIE);
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
		_pie.setFrame(centerX-(_pie.getWidth()/2), centerY-(_pie.getHeight()/2), _pie.getWidth(), _pie.getHeight());
		g.fill(_pie);
	}
	
	public boolean equals (ColoredPie other) {
		return _pie.equals(other._pie)
				&& _color.equals(other._color);
	}
}
