package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import Game.BoardHistory.BoardState;
import Items.Action;
import Mobs.Mob;
import Mobs.Mob.MobFactory;
import Mobs.MobLabel;
import TileRegions.TileRegion;
import UI.CanvasObject;
import UI.ColoredShape;
import UI.Canvas.CanvasTileCallback;
import UI.ActionSelectionOverlay;

public class Board implements CanvasObject{

	@FunctionalInterface
	public static interface ActionCallback {
		public void callback (Action action);
	}
	
	private static interface Mode {
		public void setUp();
		public void handleLeftClick(Tile clickedTile);
		public void handleRightClick(Tile clickedTile);
		public void handleMouseHover(Tile hoveredTile);
		public void cleanUp();
	}
	private class NoneMode implements Mode {
		private Tile _hoverTile;
		public void setUp () {}
		public void handleLeftClick(Tile clickedTile) {
			if (clickedTile == null
					|| !clickedTile.hasActionableMob())
				return;
			
			List<Action> availableActions = clickedTile.getMob().getAvailableActions();
			if (availableActions.size() == 1)
				switchModeTo(new TargetSelectionMode(clickedTile, availableActions.get(0)));
			else
				switchModeTo(new ActionSelectionMode(clickedTile));
		}
		public void handleRightClick(Tile clickedTile) {
			if (clickedTile == null
					|| !clickedTile.hasMovableMob())
				return;
		
			switchModeTo(new MoveMode(clickedTile));
		}
		public void handleMouseHover (Tile hoveredTile) {
			if (hoveredTile == null
					|| hoveredTile == _hoverTile)
				return;
			_hoverTile = hoveredTile;
			_mobDisplayCallback.callback(_hoverTile);
		}
		public void cleanUp() {}
	}
	private class MoveMode implements Mode {
		private Tile _selectedTile;
		private TileRegion _tilesInRange;
		private Tile _hoverTile;
		private static final Color TARGET_COLOR = Color.BLUE;
		
		public MoveMode (Tile selectedTile) {
			_selectedTile = selectedTile;
		}
		public void setUp () {
			_tilesInRange = _selectedTile.getMob().getMovementTilesInRange();
			_tilesInRange.colorTilesTo(IN_RANGE_COLOR);
		}
		public void handleLeftClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			if (_tilesInRange.contains(clickedTile))
				moveSelectedMobTo(clickedTile);
			else if (clickedTile.hasPlayerMob()){
				switchModeTo(new NoneMode());
				_currentMode.handleLeftClick(clickedTile);
			}
		}
		public void handleRightClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			if (clickedTile == _selectedTile)
				switchModeTo(new NoneMode());
			else if (clickedTile.hasMovableMob())
				switchModeTo(new MoveMode(clickedTile));
		}
		public void handleMouseHover (Tile hoveredTile) {
			if (hoveredTile == null 
					|| hoveredTile == _hoverTile)
				return;
			
			revertHoverTileColor();
				
			_hoverTile = hoveredTile;
			_mobDisplayCallback.callback(hoveredTile);
			if (_tilesInRange.contains(hoveredTile))
				_hoverTile.colorTo(TARGET_COLOR);
		}
		private void revertHoverTileColor () {
			if (_hoverTile == null)
				return;
			
			if (_tilesInRange.contains(_hoverTile))
				_hoverTile.colorTo(IN_RANGE_COLOR);
			else
				_hoverTile.colorToBase();
		}
		private void moveSelectedMobTo (Tile destinationTile) {
			int distanceFromOrigin = _tilesInRange.getDistanceFromOrigin(destinationTile);
			if (distanceFromOrigin == -1)
				return;
			
			boolean moveSuccessful = _selectedTile.moveMobToEmptyTile(destinationTile, distanceFromOrigin);
			if (moveSuccessful) {
				updateBoardHistory();
				switchModeTo(new NoneMode());
			}
		}
		public void cleanUp() {
			revertHoverTileColor();
			_tilesInRange.colorTilesToBase();
		}
	}
	private class TargetSelectionMode implements Mode {
		private Tile _selectedTile;
		private Action _action;
		private TileRegion _tilesInRange; 
		private Tile _hoverTile;
		private Collection<Tile> _targetTiles;
		private static final Color TARGET_COLOR = Color.RED;
		
		public TargetSelectionMode (Tile selectedTile, Action action) {
			_selectedTile = selectedTile;
			_action = action;
		}
		public void setUp () {
			_tilesInRange = _action.getTilesInRangeFrom(_selectedTile);
			_tilesInRange.colorTilesTo(IN_RANGE_COLOR);
		}
		public void handleLeftClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			if (_tilesInRange.contains(clickedTile))
				performActionOn(clickedTile);
			else if (clickedTile == _selectedTile)
				switchModeTo(new NoneMode());
			else if (clickedTile.hasActionableMob()) {
				switchModeTo(new NoneMode());
				_currentMode.handleLeftClick(clickedTile);
			}
		}
		public void handleRightClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			if (clickedTile.hasPlayerMob()){
				switchModeTo(new NoneMode());
				_currentMode.handleRightClick(clickedTile);
			}
		}
		public void handleMouseHover (Tile hoveredTile) {
			if (hoveredTile == null 
					|| hoveredTile == _hoverTile)
				return;
			
			revertTargetTileColor();
				
			_hoverTile = hoveredTile;
			_mobDisplayCallback.callback(hoveredTile);
			selectTargetTiles(_hoverTile);
		}
		private void selectTargetTiles (Tile targetTile) {
			if (targetTile == null
					|| !_tilesInRange.contains(targetTile))
				return;
			
			Tile directionTile = _tilesInRange.getNeighborTowardsOrigin(targetTile);
			_targetTiles = _action.getTargets(_selectedTile, targetTile, directionTile);
			for (Tile tile : _targetTiles)
				tile.colorTo(TARGET_COLOR);
		}
		private void revertTargetTileColor () {
			if (_targetTiles == null)
				return;

			for (Tile tile : _targetTiles) {
				if (_tilesInRange.contains(tile))
					tile.colorTo(IN_RANGE_COLOR);
				else
					tile.colorToBase();
			}
		}
		private void performActionOn (Tile targetTile) {
			Tile directionTile = _tilesInRange.getNeighborTowardsOrigin(targetTile);
			boolean actionSuccessful = _action.doAction(_selectedTile, targetTile, directionTile);
			if (actionSuccessful) {
				updateBoardHistory();
				switchModeTo(new NoneMode());
			}
		}
		public void cleanUp() {
			revertTargetTileColor();
			_tilesInRange.colorTilesToBase();
		}
	}
	private class ActionSelectionMode implements Mode {
		private Tile _selectedTile;
		private Tile _hoverTile;
		
		public ActionSelectionMode (Tile selectedTile) {
			_selectedTile = selectedTile;
		}
		public void setUp () {
			Point tilePoint = tileToCanvasPoint(_selectedTile);
			_overlay.setActive(tilePoint.x, tilePoint.y, _selectedTile,
								(Action action) -> switchModeTo(new TargetSelectionMode(_selectedTile, action)));
		}
		public void handleLeftClick (Tile clickedTile) {
			switchModeTo(new NoneMode());
			if (clickedTile != _selectedTile)
				_currentMode.handleLeftClick(clickedTile);
		}
		public void handleRightClick (Tile clickedTile) {
			switchModeTo(new NoneMode());
			_currentMode.handleRightClick(clickedTile);
		}
		public void handleMouseHover (Tile hoveredTile) {
			if (hoveredTile == null
					|| hoveredTile == _hoverTile)
				return;
			_hoverTile = hoveredTile;
			_mobDisplayCallback.callback(_hoverTile);
		}
		public void cleanUp () {
			_overlay.setInactive();
		}
	}
	
	private void switchModeTo (Mode newMode) {
		_currentMode.cleanUp();
		_currentMode = newMode;
		_currentMode.setUp();
	}
	
	private Tile[][] _tiles;
	private BoardHistory _history;
	private int _x, _y;
	private CanvasTileCallback _mobDisplayCallback;
	private ActionSelectionOverlay _overlay;
	private Mode _currentMode = new NoneMode();
	
	private static final Color IN_RANGE_COLOR = Color.gray;
	
	public Board (int x, int y) {
		this(x, y, 5, 5);
	}
	
	public Board (int x, int y, int rows, int cols) {
		this(x, y, rows, cols, null, null);
	}
	
	public Board (int x, int y, int rows, int cols, CanvasTileCallback mobDisplayCallback, ActionSelectionOverlay overlay){
		initializeTiles(rows, cols);
		_x = x;
		_y = y;
		placeMobsRandomly (MobFactory.get(new LinkedList<MobLabel>(Arrays.asList(
				MobLabel.SWORDSMAN,
				MobLabel.ARCHER,
				MobLabel.ROGUE,
				MobLabel.WIZARD,
				MobLabel.DRUID,
				
				MobLabel.ENEMY,
				MobLabel.ENEMY,
				MobLabel.ENEMY,
				MobLabel.ENEMY,
				MobLabel.ENEMY,
				MobLabel.ENEMY,
				MobLabel.ENEMY
				))));
		resetBoardHistory();
		_mobDisplayCallback = mobDisplayCallback;
		_overlay = overlay;
	}

	private void placeMobsRandomly (List<Mob> mobs) {
		int numRows = _tiles.length;
		int numCols = numRows > 0 ? _tiles[0].length : 0;
		int numTiles = numRows * numCols;
		
		if (mobs.size() > numTiles)
			throw new RuntimeException("Trying to place " + mobs.size() + " mobs on a board with " + numTiles + " tiles.");
		
		int rand, row, col;
		Tile nextTile;
		while (mobs.size() > 0) {
			rand = (int)(Math.random() * numTiles);
			row = rand/numRows;
			col = rand%numCols;
			nextTile = _tiles[row][col];
			if (nextTile.getMob() == null)
				placeMob(row, col, mobs.remove(0));
		}
	}
	
	private void initializeTiles (int rows, int cols) {
		_tiles = new Tile[rows][cols];
		for (int row = 0; row < _tiles.length; ++row)
			for (int col = 0; col < _tiles[row].length; ++col)
				_tiles[row][col] = new Tile();
		linkNeighboringTiles();
	}
	
	private Tile getTile (int row, int col) {
		if (row < 0
			|| row >= _tiles.length
			|| col < 0
			|| col >= _tiles[row].length)
			return null;
		return _tiles[row][col];
	}
	
	private void linkNeighboringTiles () {
		for (int row = 0; row < _tiles.length; ++row)
			for (int col = 0; col < _tiles.length; ++col)
				linkTileToNeighbors(row, col);
	}	
	private void linkTileToNeighbors (int row, int col) {
		if (row % 2 == 0)
			linkTilesToNeighborsClockwiseEvenRows(row, col);
		else
			linkTilesToNeighborsClockwiseOddRows(row, col);
	}
	private void linkTilesToNeighborsClockwiseEvenRows (int row, int col) {
		Collection<Tile> neighbors = new LinkedList<Tile>();
		neighbors.add(getTile(row-1, col-1));
		neighbors.add(getTile(row-1, col));
		neighbors.add(getTile(row,   col+1));
		neighbors.add(getTile(row+1, col));
		neighbors.add(getTile(row+1, col-1));
		neighbors.add(getTile(row,   col-1));
		_tiles[row][col].setNeighbors(neighbors);
	}
	private void linkTilesToNeighborsClockwiseOddRows (int row, int col) {
		Collection<Tile> neighbors = new LinkedList<Tile>();
		neighbors.add(getTile(row-1, col));
		neighbors.add(getTile(row-1, col+1));
		neighbors.add(getTile(row,   col+1));
		neighbors.add(getTile(row+1, col+1));
		neighbors.add(getTile(row+1, col));
		neighbors.add(getTile(row,   col-1));
		_tiles[row][col].setNeighbors(neighbors);
	}
	
	private void placeMob (int row, int col, Mob mob) {
		_tiles[row][col].placeNewMob(mob);
	}
	
	private void resetBoardHistory () {
		_history = new BoardHistory(_tiles);
	}
	
	private void updateBoardHistory () {
		_history.update(_tiles);
	}
	
	public void draw (Graphics2D g) {
		for (int row = 0; row < _tiles.length; ++row)
			for (int col = 0; col < _tiles[row].length; ++col)
				drawTile(g, _tiles[row][col], row, col);
	}
	
	private void drawTile (Graphics2D g, Tile tile, int row, int col) {
		Collection<ColoredShape> tileShapes = tile.getShapes();
		for (ColoredShape shape : tileShapes)
			shape.draw(g,
					   colToCanvasX(row, col),
					   rowToCanvasY(row));
	}
	
	public boolean leftMouseButtonReleased (int x, int y) {
		Tile tile = canvasPointToTile(x, y);
		if (tile == null)
			return false;
		
		handleTileLeftClick(tile);
		return true;
	}
	
	public boolean rightMouseButtonReleased (int x, int y) {
		Tile tile = canvasPointToTile(x, y);
		if (tile == null)
			return false;
		
		handleTileRightClick(tile);
		return true;
	}
	
	public boolean mouseHover (int x, int y) {
		Tile tile = canvasPointToTile(x, y);
		if (tile == null)
			return false;
		
		handleMouseHover(tile);
		return true;
	}
	
	private void handleTileLeftClick (Tile clickedTile) {
		_currentMode.handleLeftClick(clickedTile);
	}
	
	private void handleTileRightClick (Tile clickedTile) {
		_currentMode.handleRightClick(clickedTile);
	}
	
	private void handleMouseHover (Tile hoveredTile) {
		_currentMode.handleMouseHover(hoveredTile);
	}
	

	private Tile canvasPointToTile (int canvasX, int canvasY) {
		int boardY = canvasY - _y;
		int tileRow = boardY / Tile.TILE_HEIGHT;
		if (boardY < 0 || tileRow >= _tiles.length)
			return null;
		
		int boardX = canvasX - _x - ((tileRow % 2) * (Tile.TILE_WIDTH/2));
		int tileCol = boardX / Tile.TILE_WIDTH;
		if (boardX < 0 || tileCol >= _tiles[tileRow].length)
			return null;
		
		return _tiles[tileRow][tileCol];
	}
	
	private Pair<Integer, Integer> tileToRowAndCol (Tile soughtTile) {
		for (int row = 0; row < _tiles.length; ++row)
			for (int col = 0; col < _tiles[row].length; ++col)
				if (_tiles[row][col] == soughtTile)
					return new Pair<Integer, Integer> (row,col);
		return null;
	}
	
	private Point tileToCanvasPoint (Tile tile) {
		Pair<Integer, Integer> rowAndCol = tileToRowAndCol(tile);
		int row = rowAndCol.first, col = rowAndCol.second;
		return new Point(
				colToCanvasX(row, col),
				rowToCanvasY(row));
	}
	
	private int colToCanvasX (int row, int col) {
		return _x + col*Tile.TILE_WIDTH + (row % 2) * (Tile.TILE_WIDTH/2);
	}
	private int rowToCanvasY (int row) {
		return _y + row*Tile.TILE_HEIGHT;
	}
	
	public void nextTurn () {
		switchModeTo(new NoneMode());
		performNonPlayerActions();
		resetHeros();
//		designateNonPlayerMobActions(); TODO: Do this later
		resetBoardHistory();
	}
	
	public void undo () {
		switchModeTo(new NoneMode());
		Collection<BoardState.Entry> formerTileStates = _history.undo();
		for (BoardState.Entry entry : formerTileStates)
			revertTile(entry.r, entry.c, entry.tileState);
	}
	
	private void revertTile (int r, int c, Tile newState) {
		_tiles[r][c].setState(newState);
	}
	
	private void resetHeros () {
		for (Tile[] row : _tiles)
			for (Tile tile : row)
				tile.nextTurnReset();
	}
	
	private void performNonPlayerActions () {
		LinkedList<Tile> nonPlayerMobTiles = new LinkedList<Tile>();
		for (Tile[] row : _tiles)					//Get mobs first, to avoid double-counting moving mobs
			for (Tile tile : row)
				if (tile.hasNonPlayerMob())
					nonPlayerMobTiles.add(tile);
		for (Tile tile : nonPlayerMobTiles)
			tile.doNonPlayerAction();
	}
}
