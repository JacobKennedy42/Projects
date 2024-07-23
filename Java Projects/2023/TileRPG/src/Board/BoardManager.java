package Board;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import Board.BoardHistory.BoardState;
import Items.Action;
import Mobs.EnemyMob;
import Mobs.Mob;
import Mobs.MobFactory;
import Mobs.MobLabel;
import Mobs.PlayerMob;
import TileRegions.TileRegion;
import UI.CanvasObject;
import UI.Canvas.CanvasCallback;
import UI.Canvas.CanvasTileCallback;
import UI.ActionSelectionOverlay;

public class BoardManager implements CanvasObject{

	private static final boolean DEBUG_SHOW_SIMULATED_BOARD = false; //true; 

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
			
			List<Action> availableActions = clickedTile.getCreatureMob().getAvailableActions();
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
			revertHoverTileColor();
			_hoverTile = hoveredTile;
			hoveredTile.hover(_mobDisplayCallback);
		}
		private void revertHoverTileColor () {
			if (_hoverTile == null)
				return;
			_hoverTile.dehover(_mobDisplayCallback);
		}
		public void cleanUp() {
			revertHoverTileColor();
		}
	}
	private class MoveMode implements Mode {
		private Tile _selectedTile;
		private TileRegion _tilesInRange;
		private Tile _hoverTile;
		private static final Color TARGET_COLOR = Color.green;
		
		public MoveMode (Tile selectedTile) {
			_selectedTile = selectedTile;
		}
		public void setUp () {
			_tilesInRange = _selectedTile.getCreatureMob().getMovementTilesInRange();
			_tilesInRange.colorTilesTo(IN_RANGE_COLOR);
		}
		public void handleLeftClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			if (_tilesInRange.contains(clickedTile))
				moveSelectedMobTo(clickedTile);
			else {
				switchModeTo(new NoneMode());
				_currentMode.handleLeftClick(clickedTile);
			}
		}
		public void handleRightClick (Tile clickedTile) {
			if (clickedTile == null)
				return;

			if (clickedTile.hasMovableMob() && clickedTile != _selectedTile)
				switchModeTo(new MoveMode(clickedTile));
			else
				switchModeTo(new NoneMode());
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
				checkWinState();
				previewTurn();
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
		private Iterable<Tile> _hoverTargetTiles;
		private static final Color TARGET_COLOR = Color.darkGray;
		
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
			else if (clickedTile.hasActionableMob() && clickedTile != _selectedTile) {
				switchModeTo(new NoneMode());
				_currentMode.handleLeftClick(clickedTile);
			}
			else
				switchModeTo(new NoneMode());
		}
		public void handleRightClick (Tile clickedTile) {
			if (clickedTile == null)
				return;
			
			switchModeTo(new NoneMode());
			_currentMode.handleRightClick(clickedTile);
		}
		public void handleMouseHover (Tile hoveredTile) {
			if (hoveredTile == null 
					|| hoveredTile == _hoverTile)
				return;
			
			revertTargetTileColor();
				
			_hoverTile = hoveredTile;
			_mobDisplayCallback.callback(hoveredTile);
			selectHoverTargetTiles(_hoverTile);
		}
		private void selectHoverTargetTiles (Tile targetTile) {
			_hoverTargetTiles = getTargetTiles(targetTile);
			for (Tile tile : _hoverTargetTiles)
				tile.colorTo(TARGET_COLOR);
			
			if (_hoverTile != _selectedTile && getDirectionTile(targetTile) != null)
				_action.applyPreview(_selectedTile, targetTile, getDirectionTile(targetTile));
		}
		private Iterable<Tile> getTargetTiles (Tile targetTile) {
			if (targetTile == null
					|| !_tilesInRange.contains(targetTile))
				return new LinkedList<Tile>();
			
			return _action.getTargets(_selectedTile, targetTile, getDirectionTile(targetTile));
		}
		private void revertTargetTileColor () {
			if (_hoverTargetTiles == null)
				return;

			for (Tile tile : _hoverTargetTiles) {
				if (_tilesInRange.contains(tile))
					tile.colorTo(IN_RANGE_COLOR);
				else
					tile.colorToBase();
			}
			if (_hoverTile != _selectedTile && getDirectionTile(_hoverTile) != null)
				_action.revertPreview(_selectedTile, _hoverTile, getDirectionTile(_hoverTile));
		}
		private void performActionOn (Tile targetTile) {
			boolean actionSuccessful = _action.doAction(_selectedTile, targetTile, getDirectionTile(targetTile));
			if (actionSuccessful) {
				checkWinState();
				previewTurn();
				updateBoardHistory();
				switchModeTo(new NoneMode());
			}
		}
		private Tile getDirectionTile (Tile targetTile) {
			return _tilesInRange.getNeighborTowardsOrigin(targetTile);
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
			_mobDisplayCallback.callback(hoveredTile);
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
	
	private Board _board;
	private SimulatedBoard _simulatedBoard;
	private BoardHistory _history;
	private int _x, _y;
	private CanvasTileCallback _mobDisplayCallback;
	private ActionSelectionOverlay _overlay;
	private CanvasCallback _winCallback;
	private CanvasCallback _lossCallback;
	private Mode _currentMode = new NoneMode();
	
	private static final Color IN_RANGE_COLOR = Color.gray;
	
	public BoardManager (int x, int y, int rows, int cols, CanvasTileCallback mobDisplayCallback, ActionSelectionOverlay overlay, CanvasCallback winCallback, CanvasCallback lossCallback){
		_board = new Board(rows, cols);
		_simulatedBoard = new SimulatedBoard(_board);
		_x = x;
		_y = y;
		
		placeMobsRandomly (MobFactory.get(new LinkedList<MobLabel>(Arrays.asList(
				MobLabel.WARRIOR,
				MobLabel.CLERIC,
				MobLabel.MONK,
				MobLabel.ROGUE,
				MobLabel.WIZARD,
				MobLabel.DRUID,
				MobLabel.ALCHEMIST,
				MobLabel.BARD,
				
				MobLabel.ZOMBIE,
				MobLabel.ZOMBIE,
				MobLabel.SKELETON,
				MobLabel.SKELETON,
				MobLabel.ZOMBIE
				// MobLabel.GIANT_WORM_HEAD
				))));
		
		startTurn();
		_mobDisplayCallback = mobDisplayCallback;
		_overlay = overlay;
		_winCallback = winCallback;
		_lossCallback = lossCallback;
	}

	private void placeMobsRandomly (List<Mob> mobs) {
		int numRows = _board.numRows();
		int numCols = _board.numCols(0);
		int numTiles = numRows * numCols;
		
		if (mobs.size() > numTiles)
			throw new RuntimeException("Trying to place " + mobs.size() + " mobs on a board with " + numTiles + " tiles.");
		
		int rand, row, col;
		Tile nextTile;
		while (mobs.size() > 0) {
			rand = (int)(Math.random() * numTiles);
			row = rand/numRows;
			col = rand%numCols;
			nextTile = _board.getTile(row, col);
			if (mobs.get(0).canFitIn(nextTile))
				placeMob(row, col, mobs.remove(0));
		}
	}
	
	private void placeMob (int row, int col, Mob mob) {
		_board.getTile(row, col).attachMob(mob);
	}
	
	private void resetBoardHistory () {
		_history = new BoardHistory(_board);
	}
	
	private void updateBoardHistory () {
		_history.update(_board);
	}
	
	public void draw (Graphics2D g) {
		_board.draw(g, _x, _y);
		if (DEBUG_SHOW_SIMULATED_BOARD)
			_simulatedBoard.draw(g, _x+1000, _y);
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
		if (boardY < 0 || tileRow >= _board.numRows())
			return null;
		
		int boardX = canvasX - _x - ((tileRow % 2) * (Tile.TILE_WIDTH/2));
		int tileCol = boardX / Tile.TILE_WIDTH;
		if (boardX < 0 || tileCol >= _board.numCols(tileRow))
			return null;
		
		return _board.getTile(tileRow, tileCol);
	}
	
	private Point tileToRowAndCol (Tile soughtTile) {
		for (int row = 0; row < _board.numRows(); ++row)
			for (int col = 0; col < _board.numCols(row); ++col)
				if (_board.getTile(row, col) == soughtTile)
					return new Point(row,col);
		return null;
	}
	private Point tileToCanvasPoint (Tile tile) {
		Point rowAndCol = tileToRowAndCol(tile);
		int row = rowAndCol.x;
		int col = rowAndCol.y;
		return new Point(
				colToCenterX(row, col),
				rowToCenterY(row));
	}
	private int colToCenterX (int row, int col) {
		return _x + col*Tile.TILE_WIDTH + (row % 2) * (Tile.TILE_WIDTH/2) + (Tile.TILE_WIDTH/2);
	}
	private int rowToCenterY (int row) {
		return _y + row*Tile.TILE_HEIGHT + (Tile.TILE_HEIGHT/2);
	}
	
	public void nextTurn () {
		endTurn();
		startTurn();
	}
	
	public void undo () {
		switchModeTo(new NoneMode());
		Collection<BoardState.Entry> formerTileStates = _history.undo();
		for (BoardState.Entry entry : formerTileStates)
			revertTile(entry.r, entry.c, entry.tileState);
		previewTurn();
	}
	
	private void revertTile (int r, int c, Tile newState) {
		_board.getTile(r, c).setState(newState);
	}
	
	private void startTurn () {
		_board.startTurn();
		resetBoardHistory();
		previewTurn();
	}
	
	private void endTurn () {
		switchModeTo(new NoneMode());
		_board.endTurn();
		checkWinState();
		checkLossState();
	}

	private void previewTurn () {
		_simulatedBoard.simulateTurn();
	}

	private void checkWinState () {
		if (!_board.checkForWin())
			return;
		System.out.println("WIN");

		_winCallback.callback();
	}
	private void checkLossState () {
		if (_board.checkForWin() || !_board.checkForLoss())
			return;
		System.out.println("LOSE");

		_lossCallback.callback();
	}
}