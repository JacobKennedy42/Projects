package UI;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

class CanvasLayer implements CanvasObject{
    private LinkedList<CanvasObject> _objects;

    public CanvasLayer (LinkedList<CanvasObject> objects) {
        _objects = objects;
    }

    public void draw (Graphics2D g) {
        for (CanvasObject object : _objects)
			object.draw(g);
    } 

    public boolean leftMouseButtonReleased (int x, int y) {
        Iterator<CanvasObject> reverseIterator = _objects.descendingIterator();
		while (reverseIterator.hasNext())
			if (reverseIterator.next().leftMouseButtonReleased(x, y))
				return true;
        return false;
    }

    public boolean rightMouseButtonReleased (int x, int y) {
		Iterator<CanvasObject> reverseIterator = _objects.descendingIterator();
		while (reverseIterator.hasNext())
			if (reverseIterator.next().rightMouseButtonReleased(x, y))
				return true;
        return false;
	}

    public boolean mouseHover (int x, int y) {
		Iterator<CanvasObject> reverseIterator = _objects.descendingIterator();
		while (reverseIterator.hasNext())
			if (reverseIterator.next().mouseHover(x, y))
				return true;
        return false;
	}

}
