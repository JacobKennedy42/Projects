package Items;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

import Game.Tile;
import UI.ColoredEllipse;
import UI.ColoredPolygon;
import UI.ColoredShape;

class ActionImage {
	
	Collection<ColoredShape> _shapes;
	
	public static ActionImage TRIANGLE (Color color) {
		return imageFromShape(new ColoredPolygon(new int[] {0, 10, 20}, new int[] {20, 3, 20}, color));
	}
	public static ActionImage CIRCLE (Color color) {
		return imageFromShape(new ColoredEllipse(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, color));
	}
	private static ActionImage imageFromShape (ColoredShape shape) {
		Collection<ColoredShape> shapes = new LinkedList<ColoredShape>();
		shapes.add(shape);
		return new ActionImage(shapes);
	}
	
	public ActionImage () {
		_shapes = new LinkedList<ColoredShape>();
	}
	private ActionImage(Collection<ColoredShape> shapes) {
		_shapes = shapes;
	}
	
	public void addImage (ActionImage image) {
		for (ColoredShape shape : image._shapes)
			_shapes.add(shape);
	}
	
	public void draw (Graphics2D g, int x, int y) {
		for (ColoredShape shape : _shapes)
			shape.draw(g, x, y);
	}
	
}
