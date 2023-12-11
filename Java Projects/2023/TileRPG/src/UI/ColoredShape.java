package UI;

import java.awt.Color;
import java.awt.Graphics2D;

public interface ColoredShape {
	public void setColor (Color color);
	public void draw(Graphics2D g, int x, int y);
}
