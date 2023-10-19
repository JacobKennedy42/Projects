package Game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class BoardHistory {
	
	private LinkedList<BoardState> _history;
	
	public static class BoardState implements Iterable<BoardState.Entry> {
		private HashMap<Pair<Integer, Integer>, Tile.TileState> _tileStates;
		
		public BoardState () {
			_tileStates = new HashMap<Pair<Integer, Integer>, Tile.TileState>();
		}
		
		public BoardState (Tile[][] initialBoard) {
			_tileStates = makeIntialTileStates(initialBoard);
		}
		
		private HashMap<Pair<Integer, Integer>, Tile.TileState> makeIntialTileStates (Tile[][] initialBoard) {
			HashMap<Pair<Integer, Integer>, Tile.TileState> initialStates = new HashMap<Pair<Integer, Integer>, Tile.TileState>(); 
			for (int r = 0; r < initialBoard.length; ++r)
				for (int c = 0; c < initialBoard.length; ++c)
					initialStates.put(new Pair<Integer, Integer>(r, c), initialBoard[r][c].getState());
			return initialStates;
		}
		
		public void set (int r, int c, Tile.TileState newState) {
			_tileStates.put(new Pair<Integer, Integer>(r, c), newState);
		}
		
		public Tile.TileState get (int r, int c) {
			return _tileStates.get(new Pair<Integer, Integer>(r, c));
		}
		
		private static class BoardStateIterator implements Iterator<Entry> {

			private Iterator<Map.Entry<Pair<Integer, Integer>, Tile.TileState>> _tileIterator;
			
			public BoardStateIterator (HashMap<Pair<Integer, Integer>, Tile.TileState> tiles) {
				_tileIterator = tiles.entrySet().iterator();
			}
			
			@Override
			public boolean hasNext() {
				return _tileIterator.hasNext();
			}

			@Override
			public Entry next() {
				Map.Entry<Pair<Integer, Integer>, Tile.TileState> entry = _tileIterator.next();
				if (entry == null)
					throw new NoSuchElementException();
				Pair<Integer, Integer> coordinates = entry.getKey();
				Tile.TileState state = entry.getValue();
				return new Entry(coordinates.first, coordinates.second, state);
			}
		}
		
		public static class Entry {
			public int r;
			public int c;
			public Tile.TileState tileState;
			
			public Entry (int r, int c, Tile.TileState tileState) {
				this.r = r;
				this.c = c;
				this.tileState = tileState;
			}
		}
		
		public Iterator<Entry> iterator () {
			return new BoardStateIterator(_tileStates);
		}
	}
	
	public BoardHistory (Tile[][] initialBoard) {
		_history = new LinkedList<BoardState>();
		_history.addFirst(new BoardState(initialBoard));
	}
	
	public void update (Tile[][] newBoard) {
		BoardState newBoardState = new BoardState();
		for (int r = 0; r < newBoard.length; ++r)
			for (int c = 0; c < newBoard[r].length; ++c) {
				Tile.TileState newTileState = newBoard[r][c].getState();
				if (!newTileState.equals(getLastStateAt(r, c)))
					newBoardState.set(r, c, newTileState);
			}
		_history.addFirst(newBoardState);
	}
	
	private Tile.TileState getLastStateAt (int r, int c) {
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
			Tile.TileState lastTileState = getLastStateAt(r, c);
			formerTileStates.add(new BoardState.Entry(r, c, lastTileState));
		}
		return formerTileStates;	
	}
}
