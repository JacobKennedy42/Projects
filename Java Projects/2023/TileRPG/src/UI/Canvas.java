package UI;

import javax.swing.JPanel;

import Board.BoardManager;
import Board.Tile;
import InputHandlers.MouseHandler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.LinkedList;

public class Canvas extends JPanel{
	
	private LinkedList<CanvasLayer> _layers;
	private CanvasLayer makeStartLayer () {
		Button startButton = new Button(100, 100, "start", Color.CYAN, () -> setLayers(makeBoardLayer()));
		return new CanvasLayer(new LinkedList<CanvasObject>(Arrays.asList(startButton)));
	}
	private CanvasLayer makeWinLayer () {
		Button winButton = new Button(100, 100, "WIN", Color.GREEN, () -> setLayers(makeStartLayer()));
		return new CanvasLayer(new LinkedList<CanvasObject>(Arrays.asList(winButton)));
	}
	private CanvasLayer makeLossLayer () {
		Button lossButton = new Button(100, 100, "LOSE", Color.RED, () -> setLayers(makeStartLayer()));
		return new CanvasLayer(new LinkedList<CanvasObject>(Arrays.asList(lossButton)));
	}
	private CanvasLayer makeBoardLayer () {
		MobDisplay mobDisplay = new MobDisplay(Tile.TILE_WIDTH*12, Tile.TILE_HEIGHT);
		CanvasTileCallback displayCallback = (Tile tile) -> mobDisplay.setMob(tile.getMob());
		ActionSelectionOverlay overlay = new ActionSelectionOverlay(0, 0);
		CanvasCallback winCallback = () -> addLayer(makeWinLayer());
		CanvasCallback lossCallback = () -> addLayer(makeLossLayer());
		BoardManager board = new BoardManager(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 10, 10, displayCallback, overlay, winCallback, lossCallback);
		TurnButton nextTurnButton = new TurnButton(150, Tile.TILE_HEIGHT*12, "Turn: ", Color.cyan, () -> board.nextTurn());
		Button undoButton = new Button(300, Tile.TILE_HEIGHT*12, "Undo", Color.cyan, () -> board.undo());

		LinkedList<CanvasObject> layerObjects = new LinkedList<CanvasObject>(Arrays.asList(board, nextTurnButton, undoButton, mobDisplay, overlay));
		return new CanvasLayer(layerObjects);
	}

	private static final String TUTORIAL_TEXT = 
		"""
		Use the heros to defeat the red enemies.
		Hover over creature to see stats and abilities.
		To move, right-click hero, then left-click on target.
		To use ability,  left-click hero, then left-click a target/ability.
		Click Turn button to start next turn.
		Click Undo to undo last action this turn.
		White outline represents health/shields lost if turn is ended.
		
		Flanking - Applies effect when enemy is sandwiched between two heros.
		Shield - Extra health. Removed at start of turn
		Push - Move target X away. Colliding creatures both take X damage.
		Slow Tile - Creature may only move 1 tile on slow tiles.
		""";
	
	@FunctionalInterface
 	public interface CanvasTileCallback {
		public void callback (Tile tile);
	}

	@FunctionalInterface
	public interface CanvasCallback {
		public void callback ();
	}
	
	public Canvas () {
		super();
		// setLayers(makeStartLayer());
		setLayers(makeBoardLayer());
		MouseHandler handler = new MouseHandler(this);
		addMouseListener(handler);
		addMouseMotionListener(handler);
	}	

	private CanvasLayer getTopLayer () {
		if (_layers.size() == 0)
			return null;
		return _layers.getLast();
	}

	private void setLayers (CanvasLayer layer) {
		LinkedList<CanvasLayer> newLayers = new LinkedList<CanvasLayer>();
		newLayers.add(layer);
		setLayers(newLayers);
	}
	private void setLayers (LinkedList<CanvasLayer> layers) {
		_layers = layers;
		repaint();
	}
	private void addLayer(CanvasLayer layer) {
		_layers.add(layer);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw((Graphics2D) g);
	}
	
	private void draw (Graphics2D g) {
		RenderingHints rh = new RenderingHints (
        		RenderingHints.KEY_RENDERING,
        		RenderingHints.VALUE_RENDER_QUALITY);
				rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g.setRenderingHints (rh);
		g.setFont(new Font("Ariel", Font.PLAIN, 18));

		drawString(g, TUTORIAL_TEXT, Tile.TILE_WIDTH * 12, Tile.TILE_HEIGHT * 9);

		for (CanvasLayer layer : _layers)
			layer.draw(g);
	}

	public static void drawString (Graphics2D g, String string, int x, int y) {
		g.setColor(Color.white);
		int lineHeight = g.getFontMetrics().getHeight();
		int textX = x;
		int textY = y;
		for (String line : string.split("\n"))
			g.drawString(line,
						 textX,
						 textY+=lineHeight);
	}
	
	public void leftMouseButtonReleased (int x, int y) {
		if (getTopLayer().leftMouseButtonReleased(x, y))
			repaint();
	}
	
	public void rightMouseButtonReleased (int x, int y) {
		if (getTopLayer().rightMouseButtonReleased(x, y))
			repaint();
	}
	
	public void mouseMovedTo (int x, int y) {
		if (getTopLayer().mouseHover(x, y))
			repaint();
	}
}
