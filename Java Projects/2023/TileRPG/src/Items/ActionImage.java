package Items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import Game.Tile;
import UI.ColoredEllipse;
import UI.ColoredPolygon;
import UI.ColoredRectangle;
import UI.ColoredShape;

class ActionImage {
	
	ColoredShape _body;
	ColoredShape _areaFlag;
	String _usesLeftFlag;

	public enum ActionImageShape {
		TRIANGLE, DIAMOND, SQUARE, HEXAGON, CIRCLE;
		
		public static ColoredShape makeShape (ActionImageShape shapeType, int width, Color color) {
			if (shapeType == ActionImageShape.CIRCLE)
				return CIRCLE(width, color);
			if (shapeType == ActionImageShape.TRIANGLE)
				return TRIANGLE(width, color);
			if (shapeType == ActionImageShape.DIAMOND)
				return DIAMOND(width, color);
			if (shapeType == ActionImageShape.SQUARE)
				return SQUARE(width, color);
			if (shapeType == ActionImageShape.HEXAGON)
				return HEXAGON(width, color);
			throw new RuntimeException("Got to end of makeShape");
		}
	} 
	
	private static ColoredShape CIRCLE (int width, Color color) {
		return new ColoredEllipse(width, width, color);
	}
	private static ColoredShape TRIANGLE (int width, Color color) {
		int triangleHeight = (int)(width*Math.sqrt(3)/2);
		return new ColoredPolygon(new int[] {0, width/2, width}, new int[] {0, triangleHeight, 0}, color);
	}
	private static ColoredShape DIAMOND (int width, Color color) {
		return new ColoredPolygon(new int[] {0, width/2, width, width/2}, new int[] {width/2, width, width/2, 0}, color);
	}
	private static ColoredShape SQUARE (int width, Color color) {
		return new ColoredRectangle(width, width, color);
	}
	private static ColoredShape HEXAGON (int width, Color color) {
		double xUnit = width/2;
		double yUnit = xUnit / Math.sqrt(3);
		int[] xPoints = new int[] {(int) xUnit, 0, 0, (int) xUnit, (int) (2*xUnit), (int) (2*xUnit)};
		int[] yPoints = new int[] {0, (int) yUnit, (int) (3*yUnit), (int) (4*yUnit), (int) (3*yUnit), (int) yUnit};
		return new ColoredPolygon(xPoints, yPoints, color);
	}
	
	public ActionImage setBodyTo (ActionImageShape shapeType, Color color) {
		_body = ActionImageShape.makeShape(shapeType, Tile.TILE_WIDTH*2/3, color);
		return this;
	}
	public ActionImage setAreaFlagTo (ActionImageShape shapeType, Color color) {
		_areaFlag = ActionImageShape.makeShape(shapeType, Tile.TILE_WIDTH/3, color);
		return this;
	}
	public ActionImage setUsesLeftFlasTo (int usesLeft) {
		_usesLeftFlag = ""+usesLeft;
		return this;
	}
	
	public void setColor (Color color) {
		_body.setColor(color);
	}
	
	public void draw (Graphics2D g, int x, int y) {
		if (_body != null)
			_body.draw(g, x, y);
		if (_areaFlag != null)
			_areaFlag.draw(g, x+Tile.TILE_WIDTH/3, y-Tile.TILE_WIDTH/3);
		if (_usesLeftFlag != null)
			drawString(g, _usesLeftFlag, x+Tile.TILE_WIDTH/3, y+Tile.TILE_WIDTH/3);
	}
	
	private void drawString (Graphics2D g, String string, int x, int y) {
		g.setPaint(Color.black);
		g.setFont(new Font("default", Font.BOLD, Tile.TILE_WIDTH/3));
		g.drawString(string, x, y);
	}
	
}
