package Mobs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Objects;

import Board.Tile;
import Items.Item;
import TileRegions.EmanationRegion;
import TileRegions.TileRegion;
import UI.ColoredEllipse;
import UI.ColoredShape;

public class PlayerMob extends CreatureMob {
	
	PlayerMob (ColoredShape shape, Item weapon, int speed, int maxHealth) {
		this(shape, weapon, speed, maxHealth, 0);
	}
	PlayerMob (ColoredShape shape, Item weapon, int speed, int maxHealth, int maxMana) {
		super(shape, weapon, speed, maxHealth, maxMana);
	}
	private PlayerMob (PlayerMob other) {
		super(other);
	}
	
	@Override
	public Mob getState() {
		return new PlayerMob(this);
	}
	
	@Override
	public boolean isFriendlyTo (Mob other) {
		return other instanceof PlayerMob;
	}
	
	@Override
	public void hover () {
		Tile.colorOutlineToBase(getAllPlayers());
		Collection<Tile> allEnemies = getAllEnemies();
		for (Tile enemy : allEnemies)
			if (enemy.getEnemyMob().isTargetingPlayer(getTile()))
				enemy.colorOutlineTo(Color.red);
			else
				enemy.colorOutlineToBase();	
	}
	
	@Override
	public void dehover () {
		Collection<Tile> allEnemies = getAllEnemies();
		for (Tile enemy : allEnemies)
			enemy.colorOutlineToBase();
	}
	
	@Override
	public void draw (Graphics2D g, int centerX, int centerY) {
		super.draw(g, centerX, centerY);
		drawActionFlag(g, centerX, centerY);
		drawMovementFlag(g, centerX, centerY);
	}
	
	@Override
	protected Color healthOutlineColor() {
		return Color.black;
	}
	
	@Override
	protected Color lostHealthOutlineColor() {
		return Color.white;
	}
	
	@Override
	protected Color shieldOutlineColor () {
		return Color.gray;
	}
	
	private void drawActionFlag (Graphics2D g, int centerX, int centerY) {
		if (getActionsLeft() <= 0)
			return;
		
		int diameter = (Tile.TILE_WIDTH-Tile.TILE_GAP) / 5;
		ColoredEllipse flag = new ColoredEllipse(diameter, diameter, Color.blue);
		flag.draw(g, centerX + 2*diameter, centerY-diameter);
	}
	private void drawMovementFlag (Graphics2D g, int centerX, int centerY) {
		if (getMovementLeft() <= 0)
			return;
		
		int diameter = (Tile.TILE_WIDTH-Tile.TILE_GAP) / 5;
		ColoredEllipse flag = new ColoredEllipse(diameter, diameter, Color.green);
		flag.draw(g, centerX + 2*diameter, centerY+diameter);
	}
}
