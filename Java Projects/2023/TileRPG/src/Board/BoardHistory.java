package Board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import java.awt.Point;

class BoardHistory {
	
	private LinkedList<BoardState> _history;
	
	public static class BoardState implements Iterable<BoardState.Entry> {
		private HashMap<Point, Tile> _tileStates;
		
		public BoardState () {
			_tileStates = new HashMap<Point, Tile>();
		}
		
		public BoardState (Board initialBoard) {
			_tileStates = makeIntialTileStates(initialBoard);
		}
		
		private HashMap<Point, Tile> makeIntialTileStates (Board initialBoard) {
			HashMap<Point, Tile> initialStates = new HashMap<Point, Tile>(); 
			for (int r = 0; r < initialBoard.numRows(); ++r)
				for (int c = 0; c < initialBoard.numCols(r); ++c)
					initialStates.put(new Point(r, c), initialBoard.getTile(r, c).getState());
			return initialStates;
		}
		
		public void set (int r, int c, Tile newState) {
			_tileStates.put(new Point(r, c), newState);
		}
		
		public Tile get (int r, int c) {
			return _tileStates.get(new Point(r, c));
		}
		
		private static class BoardStateIterator implements Iterator<Entry> {

			private Iterator<Map.Entry<Point, Tile>> _tileIterator;
			
			public BoardStateIterator (HashMap<Point, Tile> tiles) {
				_tileIterator = tiles.entrySet().iterator();
			}
			
			@Override
			public boolean hasNext() {
				return _tileIterator.hasNext();
			}

			@Override
			public Entry next() {
				Map.Entry<Point, Tile> entry = _tileIterator.next();
				if (entry == null)
					throw new NoSuchElementException();
				Point coordinates = entry.getKey();
				Tile state = entry.getValue();
				return new Entry(coordinates.x, coordinates.y, state);
			}
		}
		
		public static class Entry {
			public int r;
			public int c;
			public Tile tileState;
			
			public Entry (int r, int c, Tile tileState) {
				this.r = r;
				this.c = c;
				this.tileState = tileState;
			}
		}
		
		public Iterator<Entry> iterator () {
			return new BoardStateIterator(_tileStates);
		}
	}
	
	public BoardHistory (Board initialBoard) {
		_history = new LinkedList<BoardState>();
		_history.addFirst(new BoardState(initialBoard));
	}
	
	public void update (Board newBoard) {
		BoardState newBoardState = new BoardState();
		for (int r = 0; r < newBoard.numRows(); ++r)
			for (int c = 0; c < newBoard.numCols(r); ++c) {
				Tile newTileState = newBoard.getTile(r, c).getState();
				if (!newTileState.equals(getLastStateAt(r, c)))
					newBoardState.set(r, c, newTileState);
			}
		_history.addFirst(newBoardState);
	}
	
	private Tile getLastStateAt (int r, int c) {
		for (BoardState board : _history)
			if (board.get(r, c) != null)
				return board.get(r, c);
		return null;
	}
	
	public Collection<BoardState.Entry> undo () {
		Collection<BoardState.Entry> formerTileStates = new LinkedList<BoardState.Entry>();
		
		if (_history.size() <= 1)
			return formerTileStates;
		
		BoardState lastBoard = _history.removeFirst();
		for (BoardState.Entry entry : lastBoard) {
			int r = entry.r;
			int c = entry.c;
			Tile lastTileState = getLastStateAt(r, c);
			formerTileStates.add(new BoardState.Entry(r, c, lastTileState));
		}
		return formerTileStates;	
	}
}
