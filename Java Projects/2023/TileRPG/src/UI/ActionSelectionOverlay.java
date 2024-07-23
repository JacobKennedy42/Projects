package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

import Board.Tile;
import Board.BoardManager.ActionCallback;
import Items.Action;

public class ActionSelectionOverlay implements CanvasObject {
	
	private static int OVERLAY_RADIUS = Tile.TILE_WIDTH*2;
	private static Color BACKGROUND_COLOR = new Color(125, 125, 125, 200);	//transparent grey
	private static final double SLOT_ANGLE = Math.PI;
	
	private ColoredShape _background;
	private int _x, _y;
	private boolean _isActive;
	private ActionCallback _callback;
	private Collection<ActionSlot> _slots;
	
	private class ActionSlot {
		public static final int SLOT_RADIUS = Tile.TILE_WIDTH / 2;
		
		private ColoredShape _background;
		private int _x, _y;
		private Action _action;
		
		public ActionSlot (int x, int y, Action action) {
			_background = new ColoredEllipse(SLOT_RADIUS*2, SLOT_RADIUS*2, Color.gray);
			_x = x;
			_y = y;
			_action = action;
		}
		
		public boolean leftMouseButtonReleased (int x, int y) {
			if (distanceBetween(x, y, _x, _y) > SLOT_RADIUS)
				return false;
			
			_callback.callback(_action);
			return true;
		}
		
		private double distanceBetween (int x1, int y1, int x2, int y2) {
			int deltaX = x1 - x2;
			int deltaY = y1 - y2;
			return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		}
		
		public void draw (Graphics2D g) {
			_background.draw(g, _x, _y);
			_action.draw(g, _x, _y);
		}
	}
	
	public ActionSelectionOverlay (int x, int y) {
		_background = new ColoredEllipse(OVERLAY_RADIUS*2, OVERLAY_RADIUS*2, BACKGROUND_COLOR);
		_x = x;
		_y = y;
		_isActive = false;
	}
	
	@Override
	public boolean leftMouseButtonReleased (int x, int y) {
		if (!_isActive || _slots == null)
			return false;
		
		for (ActionSlot slot : _slots)
			if (slot.leftMouseButtonReleased(x, y))
				return true;
		
		return false;
	}
	@Override
	public boolean rightMouseButtonReleased (int x, int y) {
		return false;
	}
	
	public void setActive(int tileX, int tileY, Tile actionTile, ActionCallback callback) {
		allignToTile(tileX, tileY);
		createActionSlots(actionTile, actionTile.getCreatureMob().getAllActions());
		_callback = callback;
		_isActive = true;
	}

	private void createActionSlots (Tile actionTile, Action[] actions) {
		_slots = new LinkedList<ActionSlot>();
		for (int i = 0; i < actions.length; ++i) {
			if (actions[i].costSatisfied(actionTile)) {
				ActionSlot newSlot = new ActionSlot(positionToX(i), positionToY(i), actions[i]);
				_slots.add(newSlot);
			}
		}
	}
	private int positionToX (int position) {
		return _x - (int)((OVERLAY_RADIUS/2) * Math.cos(position * SLOT_ANGLE)); 
	}
	private int positionToY (int position) {
		return _y - (int)((OVERLAY_RADIUS/2) * Math.sin(position * SLOT_ANGLE));
	}
	
	public void allignToTile (int tileCenterX, int tileCenterY) {
		_x = tileCenterX;
		_y = tileCenterY;
	}
	
	public void setInactive () {
		_isActive = false;
	}

	@Override
	public void draw(Graphics2D g) {
		if (!_isActive)
			return;
		
		_background.draw(g, _x, _y);
		for (ActionSlot slot : _slots)
			slot.draw(g);
	}
}
