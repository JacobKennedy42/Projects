package UI;

import javax.swing.JPanel;

import Game.Tile;
import Game.Board;

import InputHandlers.MouseHandler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Canvas extends JPanel{
	
	private LinkedList<CanvasObject> _objects;
	private static final String TUTORIAL_TEXT = 
		"""
		Use the heros to defeat the red enemies.
		Hover over a hero or enemy to see their statistics and weapons.
		To move a hero, right-click the hero, then left click on a tile in range.
		To use a hero's weapon,  left-click the hero, then select a valid target/tile.
		Click the Undo button to undo previous movement and actions taken this turn.
		Click the Turn button to go to the next turn.
		
		Flanking - Applies effect when enemy is sandwiched between two heros.
		""";
	
	@FunctionalInterface
 	public interface CanvasTileCallback {
		public void callback (Tile tile);
	}
	
	public Canvas () {
		this(defaultCanvasObjects());
	}	
	private static LinkedList<CanvasObject> defaultCanvasObjects () {
		MobDisplay mobDisplay = new MobDisplay(220, 20);
		CanvasTileCallback displayCallback = (Tile tile) -> mobDisplay.setMob(tile.getMob());
		ActionSelectionOverlay overlay = new ActionSelectionOverlay(0, 0);
		Board board = new Board(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 10, 10, displayCallback, overlay);
		TurnButton nextTurnButton = new TurnButton(0, 250, "Turn: ", Color.cyan, () -> board.nextTurn());
		Button undoButton = new Button(150, 250, "Undo", Color.cyan, () -> board.undo());
		return new LinkedList<CanvasObject>(Arrays.asList(board, nextTurnButton, undoButton, mobDisplay, overlay));
	}
	
	public Canvas (LinkedList<CanvasObject> objects) {
		super();
		_objects = objects;
		MouseHandler handler = new MouseHandler(this);
		addMouseListener(handler);
		addMouseMotionListener(handler);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw((Graphics2D) g);
	}
	
	private void draw (Graphics2D g) {
		RenderingHints rh = new RenderingHints (
        		RenderingHints.KEY_RENDERING,
        		RenderingHints.VALUE_RENDER_SPEED);
    	g.setRenderingHints (rh);
    	
    	for (CanvasObject object : _objects)
    		object.draw(g);
    	
    	drawString(g, TUTORIAL_TEXT, 0, 300);
	}

	public static void drawString (Graphics2D g, String string, int x, int y) {
		g.setColor(Color.black);
		int lineHeight = g.getFontMetrics().getHeight();
		int textX = x;
		int textY = y;
		for (String line : string.split("\n"))
			g.drawString(line,
						 textX,
						 textY+=lineHeight);
	}
	
	public void leftMouseButtonReleased (int x, int y) {
		Iterator<CanvasObject> reverseIterator = _objects.descendingIterator();
		while (reverseIterator.hasNext())
			if (reverseIterator.next().leftMouseButtonReleased(x, y)) {
				repaint();
				break;
			}
	}
	
	public void rightMouseButtonReleased (int x, int y) {
		Iterator<CanvasObject> reverseIterator = _objects.descendingIterator();
		while (reverseIterator.hasNext())
			if (reverseIterator.next().rightMouseButtonReleased(x, y)) {
				repaint();
				break;
			}
	}
	
	public void mouseMovedTo (int x, int y) {
		for (CanvasObject object : _objects)
			if (object.mouseHover(x, y)) {
				repaint();
				break;
			}
	}
}
