package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import Game.BoardHistory.BoardState;
import Game.Canvas.CanvasCallback;
import Mobs.Mob;
import Mobs.Mob.MobLibrary;
import TileRegions.TileRegion;

import java.awt.Point;

public class Board implements CanvasObject{
	private enum ModeValue {NONE, MOVE, ACTION}
	private class Mode {
		private ModeValue _mode = ModeValue.NONE;
		
		public ModeValue getMode () {
			return _mode;
		}
		
		public void setMode(ModeValue newMode) {
			if (_mode == newMode)
				return;
			
			deselectTile();
			_mode = newMode;
		}
	}
	
	private Tile[][] _tiles;
	private BoardHistory _history;
	private int _x, _y;
	private Tile _selectedTile;
	private Tile _hoverTile;
	private Collection<Tile> _targetTiles;
	private CanvasCallback _hoverFunc;
	private TileRegion _tilesInRange;
	private Mode _currentMode = new Mode();
	
	private static final Color IN_RANGE_COLOR = Color.gray;
	private static final Color TARGET_COLOR = Color.red;
	
	public Board (int x, int y) {
		this(x, y, 5, 5);
	}
	
	public Board (int x, int y, int rows, int cols) {
		this(x, y, rows, cols, null);
	}
	
	public Board (int x, int y, int rows, int cols, CanvasCallback hoverFunc){
		initializeTiles(rows, cols);
		_x = x;
		_y = y;
		placeMobsRandomly (new LinkedList<Mob>(Arrays.asList(
				new Mob(MobLibrary.SWORDSMAN),
				new Mob(MobLibrary.ARCHER),
				new Mob(MobLibrary.ROGUE),
				new Mob(MobLibrary.WIZARD),
				new Mob(MobLibrary.ENEMY),
				new Mob(MobLibrary.ENEMY),
				new Mob(MobLibrary.ENEMY),
				new Mob(MobLibrary.ENEMY),
				new Mob(MobLibrary.ENEMY)
			)));
		resetBoardHistory();
		_hoverFunc = hoverFunc;
	}

	private void placeMobsRandomly (LinkedList<Mob> mobs) {
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
				placeMob(row, col, mobs.removeFirst());
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
		Collection<FilledShape> tileShapes = tile.getShapes();
		for (FilledShape shape : tileShapes)
			shape.draw(g,
					   _x + col*Tile.TILE_WIDTH + (row % 2) * (Tile.TILE_WIDTH/2),
					   _y + row*Tile.TILE_HEIGHT);
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
		if (tile == null || tile == _hoverTile)
			return false;
		
		handleMouseHover(tile);
		return true;
	}
	
	private void handleTileLeftClick (Tile clickedTile) {
		if (!doPlayerAction(clickedTile)
				&& !moveSelectedMobTo(clickedTile))
			selectActionTile(clickedTile);
	}
	
	private void handleTileRightClick (Tile clickedTile) {
		selectMoveTile(clickedTile);
	}
	
	private void handleMouseHover (Tile hoverTile) {
		selectHoverTile(hoverTile);
	}
	
	private void selectMoveTile (Tile tile) {
		if (tile == null
				|| !tile.hasPlayerMob()
				|| !tile.hasMovableMob())
			return;
		
		if (tile == _selectedTile && _currentMode.getMode() == ModeValue.MOVE) {
			deselectTile();
			return;
		}
		
		deselectTile();
		_currentMode.setMode(ModeValue.MOVE);
		_selectedTile = tile;
		_tilesInRange = _selectedTile.getMob().getMovementTilesInRange();
		_tilesInRange.colorTilesTo(IN_RANGE_COLOR);
	}
	
	private boolean moveSelectedMobTo (Tile destinationTile) {
		if (_currentMode.getMode() != ModeValue.MOVE
				|| _tilesInRange == null)
			return false;
		
		int distanceFromOrigin = _tilesInRange.getDistanceFromOrigin(destinationTile);
		if (distanceFromOrigin == -1)
			return false;
		
		if (!_selectedTile.moveMobToEmptyTile(destinationTile, distanceFromOrigin))
			return false;
		
		updateBoardHistory();
		deselectTile();
		return true;
	}

	private void selectActionTile (Tile tile) {
		if (tile == null
				|| !tile.hasPlayerMob()
				|| !tile.hasMobWithActionsLeft())
			return;
		
		if (tile == _selectedTile && _currentMode.getMode() == ModeValue.ACTION) {
			deselectTile();
			return;
		}
		
		deselectTile();
		_currentMode.setMode(ModeValue.ACTION);
		_selectedTile = tile;
		_tilesInRange = _selectedTile.getMob().getActionTilesInRange();
		_tilesInRange.colorTilesTo(IN_RANGE_COLOR);
	}
	
	private void selectHoverTile (Tile tile) {
		if (tile == null
				|| tile == _hoverTile)
			return;
		
		deselectHoverTile();
		
		_hoverTile = tile;
		_hoverFunc.callback(tile);
		
		selectTargetTiles(_hoverTile);
	}
	
	private void selectTargetTiles (Tile targetTile) {
		if (targetTile == null
				|| _currentMode.getMode() != ModeValue.ACTION
				|| _selectedTile == null
				|| _selectedTile.getMob() == null
				|| _tilesInRange == null
				|| !_tilesInRange.contains(targetTile))
			return;
		
		_targetTiles = _selectedTile.getMob().getWeapon().getTargets(_selectedTile, targetTile);
		for (Tile tile : _targetTiles)
			tile.colorTo(TARGET_COLOR);
	}
	
	private void deselectHoverTile () {
		if (_hoverTile == null)
			return;
		
		deselectTargetTiles();
		
		_hoverTile = null;
	}
	
	private void deselectTargetTiles () {
		if (_targetTiles == null)
			return;
		
		for (Tile tile : _targetTiles)
			if (_currentMode.getMode() == ModeValue.ACTION && _tilesInRange.contains(tile))
				tile.colorTo(IN_RANGE_COLOR);
			else
				tile.colorToDefault();
		
		_targetTiles = null;
	}
	
	private boolean doPlayerAction (Tile targetTile) {
		if (_currentMode.getMode() != ModeValue.ACTION
				|| _tilesInRange == null
				|| !_tilesInRange.contains(targetTile)
				|| !_selectedTile.doPlayerActionOn(targetTile))	//returns false if mob is unable to do action
			return false;
		
		updateBoardHistory();
		deselectTile();
		return true;
	}
	
	private void deselectTile () {
		if (_selectedTile == null)
			return;
		
		deselectHoverTile();
		
		_selectedTile = null;
		_tilesInRange.colorTilesToDefault();
		_tilesInRange = null;
		_currentMode.setMode(ModeValue.NONE);
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
	
	public void nextTurn () {
		deselectTile();
		performNonPlayerActions();
		resetHeros();
//		designateNonPlayerMobActions(); TODO: Do this later
		resetBoardHistory();
	}
	
	public void undo () {
		deselectTile();
		Collection<BoardState.Entry> formerTileStates = _history.undo();
		for (BoardState.Entry entry : formerTileStates)
			revertTile(entry.r, entry.c, entry.tileState);
	}
	
	private void revertTile (int r, int c, Tile.TileState newState) {
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
