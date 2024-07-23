package Board;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import Mobs.EnemyMob;
import Mobs.PlayerMob;

class Board implements Iterable<Tile> {

    private Tile[][] _tiles;
    public Board (int rows, int cols){
        initializeTiles(rows, cols);
    }

    private void initializeTiles (int rows, int cols) {
		_tiles = new Tile[rows][cols];
		for (int row = 0; row < _tiles.length; ++row)
			for (int col = 0; col < _tiles[row].length; ++col)
				_tiles[row][col] = new Tile();
		linkNeighboringTiles();
	}

    public int numRows() {
        return _tiles.length;
    }

    public int numCols (int row) {
        return numRows() > 0 ? _tiles[row].length : 0;
    }

    public Tile getTile (int row, int col) {
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
		List<Tile> neighbors = new LinkedList<Tile>();
		neighbors.add(getTile(row-1, col-1));	//start at the top left and go clockwise
		neighbors.add(getTile(row-1, col));
		neighbors.add(getTile(row,   col+1));
		neighbors.add(getTile(row+1, col));
		neighbors.add(getTile(row+1, col-1));
		neighbors.add(getTile(row,   col-1));
		_tiles[row][col].setNeighbors(neighbors);
	}
	private void linkTilesToNeighborsClockwiseOddRows (int row, int col) {
		List<Tile> neighbors = new LinkedList<Tile>();
		neighbors.add(getTile(row-1, col));		//start at the top left and go clockwise
		neighbors.add(getTile(row-1, col+1));
		neighbors.add(getTile(row,   col+1));
		neighbors.add(getTile(row+1, col+1));
		neighbors.add(getTile(row+1, col));
		neighbors.add(getTile(row,   col-1));
		_tiles[row][col].setNeighbors(neighbors);
	}

    private Collection<EnemyMob> getEnemies () {
		LinkedList<EnemyMob> enemies = new LinkedList<EnemyMob>();
		for (Tile tile : this)
			if (tile.hasEnemyMob())
				enemies.add(tile.getEnemyMob());
		return enemies;
	}
	private Collection<PlayerMob> getPlayers () {
		LinkedList<PlayerMob> players = new LinkedList<PlayerMob>();
		for (Tile tile : this)
			if (tile.hasPlayerMob())
				players.add(tile.getPlayerMob());
		return players;
	}

    public void startTurn () {
		for (PlayerMob player : getPlayers())
			player.startTurn();
		for (EnemyMob enemy : getEnemies())
			enemy.startTurn();
	}
	public void endTurn () {
		for (PlayerMob player : getPlayers())
			player.endTurn();
		for (EnemyMob enemy : getEnemies())
			enemy.endTurn();
	}

    public void draw(Graphics2D g, int x, int y) {
        for (int r = 0; r < numRows(); ++r)
            for (int c = 0; c < numCols(r); ++c)
                drawTile(g, x, y, r, c);
    }
    private void drawTile(Graphics2D g, int x, int y, int r, int c) {
        int tileX = x + c*Tile.TILE_WIDTH + (r % 2) * (Tile.TILE_WIDTH/2) + (Tile.TILE_WIDTH/2);
        int tileY = y + r*Tile.TILE_HEIGHT + (Tile.TILE_HEIGHT/2);
        getTile(r, c).draw(g, tileX, tileY);
    }

	public boolean checkForWin () {
		return getEnemies().size() == 0;
	}
	public boolean checkForLoss () {
		return getPlayers().size() == 0;
	}

    @Override
	public Iterator<Tile> iterator() {
		return new BoardIterator(this);
	}
    private static class BoardIterator implements Iterator<Tile> {
        private class RowIterator implements Iterator<Tile> {
            private Tile[] _row;
            private int _index;

            public RowIterator (Tile[] row) {
                _row = row;
                _index = 0;
            }

            @Override
            public boolean hasNext() {
                return _index < _row.length;
            }

            @Override
            public Tile next() {
                if (hasNext())
                    return _row[_index++];
                throw new NoSuchElementException();
            }
        }

        private int _index;
        private RowIterator _row;
        private Tile[][] _rows;

        public BoardIterator (Board board) {
            _rows = board._tiles;
            _index = 0;
        }

        public boolean hasNext() {
            while ((_row == null || !_row.hasNext())
                    && _index < _rows.length)
                _row = new RowIterator(_rows[_index++]);
            return _row.hasNext();
        }

        public Tile next() {
            if (hasNext())
                return _row.next();
            throw new NoSuchElementException();
        }
    }
}
