package UI;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface CanvasObject {
	void draw (Graphics2D g);
	default boolean leftMouseButtonReleased(int x, int y) {return false;}	//returns true if object was successfully clicked
	default boolean rightMouseButtonReleased(int x, int y) {return false;}	//returns true if object was successfully clicked
	default boolean mouseHover(int x, int y) {return false;}
}
