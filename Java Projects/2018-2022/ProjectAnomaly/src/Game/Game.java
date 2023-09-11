package Game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import Mobs.*;
import Tiles.*;
 
 /* TODO: General
  * For each gameObject class and subclass, make a getImageDir method that calls super and appends the returned string with it's classname
  * Move tileSize into Surface, for better encapsulation
  * Use static import when importing nested classes from surface. Do so for all mobs, items, tiles, etc.
  * Get rid of the display library and store the display elements in a static arraylist for each object. This includes getting rid of types
  * Change Tile isWalkable() to isEmpty() and use that in Player placeBlock()
  * Somehow make the move methods more compact
  * Maybe make moving and digging and placing instantly responsive, but make attack speed depend on weapon (have visual
  * 	 indicator, like the player could be cyan while exhausted, and have exhaust and recharge sound effects)
  * Make ArmorSlot a child to Inventory Slot. Dissolve InventoryUI.
  * Condense constructors of GameObject, Tile, Mob, and Player
  * Put a setType method in gameElement (so that it can be used in Mob)
  * Make torches
  * Make final variables of the block and item types
  * Make sure that only items of a certain itemType or lower can be placed
  * Maybe make a "CheckNearTiles" method in Tiles that returns ArrayList<Tile>
  * Make the player turn towards where they shoot (to the nearest angle, base on center x and y)
  * Make alternate 'e' and 'space' on mouse buttons
  * add a weight system to the inventory system.
  * Optimize player.addItem()
  * Clean up constructors w/ setX(), setY(), etc.
  * Privatize and protect class variables and classes accordingly
  * Turn object arraylists into arrays if necessary (Ex: player inventory slots)
  * Maybe replace the getDist part in Tile.placeOre with something a bit more Dykstra-esk
  * Maybe zoom dynamically with changes in window size
  * If you put a robe in any slot, it goes into the torso slot and an X symbol shows up in all the other slots
  * Maybe eventually use images instead of rectangles and stuff, may be more efficient
  * eventually, make use of the fact that display elements are individual things and not single images
  * determine which variables should and shouldn't be static
  */
 

//TODO: game stalling, maybe find a way to make things faster.
//First, get rid of the trash comments, then look through the thing and see where the hangup's are. By the stuttering, it looks like it just struggles to draw everything
//Speeds hitch when the mobs are moving. Probably try to get mobs updates to be faster before doing other optimizations
//hitch due to rotation. maybe use image.createGraphics()
//Idea: directional sprite sheet. have all 4 direction images on the same image, use offset when drawing
//Idea: instead of drawing all dark blocks, just have a black background and only draw visible gameobjects
//Idea: for getDirs in Direction, have one static final arraylist and return that, so you don't have to make a new array list each time
//Idea: for simple things like blocks, may be worth it to use rectangle and circles (though not perferable) to see if that make things faster
//aslhbdsb

public class Game extends JFrame
{	
	//rotation directions
	public static enum Direction
	{	
		UP(0), RIGHT(.25), DOWN(.5), LEFT(.75);
		
		public static final float INTERVAL = .25f;
		private final float val;
		
		private Direction (double inVal)
		{
			val = (float) inVal;
		}
		
		public float val()
		{
			return val;
		}
		
		//give a list of the four directions, usually to iterate through
		public static ArrayList<Direction> getDirs()
		{
			ArrayList<Direction> output = new ArrayList<Direction>();
			output.add(Direction.LEFT);
			output.add(Direction.UP);
			output.add(Direction.RIGHT);
			output.add(Direction.DOWN);
			return output;
		}
	};
	
	private static Game window;
	
	private static Surface surface;// = new Surface();
	
	//The framerate of the game (in milliseconds / frame)
	private static final int FRAME_RATE = 31;
	
	//size of the tiles
	public static int tileSize = 10;
	//The dimensions of the board
	public static final int BOARD_SIZE = 50;
	
//	//The library that holds what all the game elements look like
//	private static DisplayLibrary displayLibrary = new DisplayLibrary();
	
	//stores the tiles, ordered y, then x
	public static ArrayList<ArrayList<Tile>> tiles = new ArrayList<ArrayList<Tile>>();
	//stores the mobs
	public static ArrayList<Mob> mobs = new ArrayList<Mob>();
	//playable character
	public static Player player;// = new Player(Game.BOARD_SIZE / 2, Game.BOARD_SIZE / 2, 1, 1);
	//the key adapter
	public static keyAdapter adapter;
	//the mouse adapter
	public static mouseAdapter adapter2;
	
	//store which buttons are currently being pressed down
	public static boolean shiftDown = false;
	public static boolean spaceDown = false;
	public static boolean eDown = false;
	//store if a button has just been pressed (used in Update() to avoid ConcurrentModificationExceptions and other nasty errors)
	public static boolean spacePressed = false;
	public static boolean ePressed = false;
	//which direction key is currently and most recently pressed (0 = none, 1 = left, 2 = up, 3 = right, 4 = down)
//	public static int direction = 0;
	public static Direction direction = null;
	//which direction button was last recently pressed
//	public static int lastDirectionReleased = 0;
	public static Direction lastDirectionReleased = null;
	
	//variables used to run the in-game clock
	public static long clock = 0;//System.nanoTime();
	
	public Game() 
    {
		//TODO
		//set up the input adapters
		adapter = new keyAdapter();
		addKeyListener(adapter);
		adapter2 = new mouseAdapter();
		addMouseListener(adapter2);
		
		//set up the UI
    	initUI();
    	
    	//set up the Game.tiles and place the ore veins and Game.mobs
    	initTiles();
    	placeVeins();
    	placeMobs();
    	
    	//Place the starting chest (should be in a lit area near the edge of the map)
    	placeChest();
    	
    	//place the player on the board
    	placeTile(new Air(), BOARD_SIZE / 2, BOARD_SIZE / 2);
    	player = new Player();
    	placePlayer (player, BOARD_SIZE / 2, BOARD_SIZE / 2);
    }
	
	 //Initiates the UI
    private void initUI ()
    {
    	surface = new Surface();
    	add(surface);
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	double screenWidth = screenSize.getWidth();
    	double screenHeight = screenSize.getHeight();
    
    	setTitle ("Project Anomaly");
    	setSize ((int) (screenWidth/1.5), (int) (screenHeight/1.33));
//    	setSize (2200, 1600);
    	setLocationRelativeTo (null);
//    	setLocation(1000, 100);
    	setFocusable(true);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    //Sets the Game.tiles
    private void initTiles ()
    {
    	for (int r = 0; r < BOARD_SIZE; r++)
    	{
    		ArrayList<Tile> tempList = new ArrayList<Tile>();
    		
    		for (int c = 0; c < BOARD_SIZE; c++)
    		{
    			double rand = Math.random();
    			Tile tempTile;
    			
    			if (rand < .5)
    			{
    				tempTile = new Air (c, r);
    			}
    			
    			else
    			{
    				tempTile = new Stone (c, r);
    			}
    			
    			tempList.add(tempTile);
    		}
    		
    		tiles.add(tempList);
    	}
    }
    
    //place a tile in tiles
    //TODO: maybe replace this with parameters Tile, Tile, as in newTile and prevTile
    public static void placeTile (Tile inTile, int inX, int inY)
    {	
    	//TODO: Bug when placing block in 1x2 stone room. end block still visible after placing
    	
    	Tile tempTile = tiles.get(inY).get(inX);
    	if (tempTile.getGlowRange() > 0)
    	{
    		tempTile.removeLightSource(tempTile);
    	}
    	
//    	inTile.setPos(inX, inY);
//    	tiles.get(inY).set(inX, inTile);
//    	if (inTile.getGlowRange() > 0)
//    	{
//    		inTile.addLightSource(inTile);
//    	}
    	
//    	inTile.updateLightSources();
    	
    	inTile.setPos(inX, inY);
//    	tiles.get(inY).set(inX, inTile);
    	
    	//remove the nearby lightsources, add the new block, then re-place the light sources
    	ArrayList<GameObject> nearbySources = inTile.getNearbyLightSources();
    	for (GameObject source : nearbySources)
    	{
//    		System.out.println(source.getX() + " " + source.getY());
    		Tile sourceTile = Game.tiles.get(source.getY()).get(source.getX());
    		sourceTile.removeLightSource(source);
//    		sourceTile.addLightSource(source);
    	}
    	
		tiles.get(inY).set(inX, inTile);
		
    	for (GameObject source : nearbySources)
    	{
//    		System.out.println(source.getX() + " " + source.getY());
    		Tile sourceTile = Game.tiles.get(source.getY()).get(source.getX());
//    		sourceTile.removeLightSource(source);
    		sourceTile.addLightSource(source);
    	}
    	
    	if (inTile.getGlowRange() > 0)
    	{
    		inTile.addLightSource(inTile);
    	}
    	
    	//If the previous tile was in front of the player, change player frontTile to the new tile
    	if (player != null && tempTile == player.getFrontTile())
    	{
    		player.setFrontTile(inTile);
    	}
    }
    
    //place a mob in a given spot
    public static void placeMob (Mob inMob, int inX, int inY)
    {	
    	Tile tempTile = tiles.get(inY).get(inX);
    	if (tempTile.getMob() != null && tempTile.getMob().getGlowRange() > 0)
    	{
    		tempTile.removeLightSource(tempTile.getMob());
    	}
    	
    	inMob.setPos(inX, inY);
    	mobs.add(inMob);
    	tempTile.setMob(inMob);
    	if (inMob.getGlowRange() > 0)
    	{
    		tempTile.addLightSource(inMob);
    	}
    	
    	tempTile.updateLightSources();
    }
    
    //Place the player (like placeMob but doesn't add the player to mobs)
    public void placePlayer (Player inPlayer, int inX, int inY)
    {
    	Tile tempTile = tiles.get(inY).get(inX);
    	if (tempTile.getMob() != null && tempTile.getMob().getGlowRange() > 0)
    	{
    		tempTile.removeLightSource(tempTile.getMob());
    	}
    	
    	inPlayer.setPos(inX, inY);
    	tempTile.setMob(inPlayer);
    	if (inPlayer.getGlowRange() > 0)
    	{
    		tempTile.addLightSource(inPlayer);
    	}
    	
    	tempTile.updateLightSources();
    }
    
    //Places the ore veins in the board
    private void placeVeins ()
    {
    	for (int r = 0; r < BOARD_SIZE; r++)
    	{
    		for (int c = 0; c < BOARD_SIZE; c++)
    		{
    			//TODO: Make a biome class and have it only use the tiles and ores of that biome. Have the biome have the
    			//placeVeins method, place structures method, etc. That way you're not looking through all tiles in existence
    			//when placing veins. Switch statements shouldn't be too bad with something like that
    			
    			Tile tempTile = tiles.get(r).get(c);
    			
    			//TODO: in Ore, have variables veinChance that determines the chance the vein will be dropped, veinGrowth that
    			//determines that when you place an ore, what is the likelihood that it recursively places an adjacent ore
    			
    			//chance of an ore vein being placed
    			if (tempTile.getClass().getSimpleName().equals("Stone") && Math.random() < .005)
    			{
    				placeOre (tempTile.getX(), tempTile.getY());
    			}
    		}
    	}
    }
    
    //Places an ore block (recursion to make a vein)
    public void placeOre (int inX, int inY)
    {
    	//TODO: eventually have a way to pick from a list of biome ores
    	Ore ore = new Glowstone(inX, inY);
//    	ArrayList<Tile> visitedTiles = new ArrayList<Tile>();
    	placeTile(ore, inX, inY);
//    	ore.makeVein(inX, inY, visitedTiles);
    	ore.makeVein();
    	
//    	//after your done placing the ore vein, clear the wasVisited's from all of the visited tiles
//    	for (Tile tile : visitedTiles)
//    	{
//    		tile.wasVisited = false;
//    	}
    	
//    	setType(inType);
//    	
//    	for (int i = x - 1; i <= x + 1; i++)
//		{
//			for (int j = y - 1; j <= y + 1; j++)
//			{
//				//Make sure the tile is in the board, non-diagonal, and in the sight range
//				if (i >= 0 && i < Game.BOARD_SIZE &&
//					j >= 0 && j < Game.BOARD_SIZE &&
//					!(i == x && j == y) &&
//					(i == x || j == y))
//				{
//					Tile tempTile = Game.tiles.get(j).get(i);
//					
//					//Chance of an ore block being placed next to this ones
//					if (tempTile.getType() == 1 &&
//						Math.random() < (veinChance / Surface.getDist(x, y, inX, inY)))
//					{
//						tempTile.placeOre(inX, inY, inType);
//					}
//				}
//			}
//		}
    }
    
    //Places the Game.mobs randomly on the map
    private void placeMobs ()
    {
    	for (int x = 0; x < BOARD_SIZE; x++)
    	{
    		for (int y = 0; y < BOARD_SIZE; y++)
    		{
    			if (!tiles.get(y).get(x).isSolid() && Math.random() < .01)
    			{
    				//place a mob (just a standard zombie for now)
    				//mobs.add(new Zombie (x, y, 1, 1));
    				placeMob(new Zombie(), x, y);
    			}
    		}
    	}
    }
    
    //places the starting chest near the edge of the map
    private void placeChest ()
    {
    	ArrayList<Tile> litTiles = new ArrayList<Tile>();
    	
    	for (ArrayList<Tile> row : tiles)
    	{
    		for (Tile tile : row)
    		{
    			if (!tile.getLightSources().isEmpty() && !tile.isSolid())
    			{
    				litTiles.add(tile);
    			}
    		}
    	}
    	
    	//TODO: probably need to make a tile wrapper class to easily change tiles to ones with different subclasses, such as
    	// placing blocks. It would have a setType that either calls the setType of the tile or changes it's class if the class
    	// is different. Game.tiles would be a list of these wrapped tiles. (implementation: do if CurrentClass != newClass, then
    	// tile = new Class(). Then, either way, setType(inType))... Or just set a new block in tiles whenever you place/break a
    	// block. KISS
    	
    	//TODO: Probably make a class for each different kind of tile. Have finals that denote numbers to certain block types, 
    	//and set those finals when initiating the library, like final1 = i++; final2 = i++; etc. Then use those finals when
    	//referring to type. Also, have slots hold actual instances of tiles, so that it is easier to place the tile, even if it
    	//might have different subclasses. change "output" to "drop"
    	
//    	int index = (int) Math.random() * litTiles.size();
//    	Tile chosenTile = litTiles.get(index);
//    	litTiles.set(index, new Chest(chosenTile.getX(), chosenTile.getY()));
    	
    	if (litTiles.size() > 0)
    	{
    		Tile chosenTile = litTiles.get((int) (Math.random() * litTiles.size()));
    		placeTile(new Chest(), chosenTile.getX(), chosenTile.getY());
    	}
    	
    	//Tile chosenTile = litTiles.get((int) Math.random() * litTiles.size());
    	//chosenTile.setType(3);
    }
    
//    //starts a timer that updates the game every second
//     public static void startTimer ()
//    {
//    	boolean stop = false;
//    	while (stop == false)
//    	{
//    			clock = System.nanoTime();
//    			Update();
//    	}
//    }
    
    //start the game.
    private static void startGame ()
    {
    	clock = 0;
    	
    	final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(new Runnable() 
	    	{
	        	@Override
	        	public void run()
	        	{
	        		Update();
	        	}
	    	}
	    	, 0, FRAME_RATE, TimeUnit.MILLISECONDS);
    }
    
    //Update the surface for each clock tick
    public static void Update()
    {	
    	//If space was just pressed, break the block in front of the player
    	if (spacePressed)
    	{
    		player.breakBlock();
    		spacePressed = false;
    	}
    	
    	//If e has just been pressed, place a block in front of the player
    	if (ePressed)
    	{
    		player.useItem();
    		ePressed = false;
    	}
    	
       	//If now is the next time for the player to move, move the player
//    	if (direction != 0 && player.getNextMoveTime() <= clock)
    	if (direction != null && player.getNextMoveTime() <= clock)
    	{	
    		//If the player is not placing blocks, rotate them to face the direction that they are facing.
			tryPlayerRotate();
			//If the player is not pressing shift, move the player
    		tryPlayerMove();
    	}
		
		//if the last direction released is the current direction, make the player stop moving
		if (lastDirectionReleased == direction)
		{
//			direction = 0;
//			lastDirectionReleased = 0;
			direction = null;
			lastDirectionReleased = null;
		}
		
		//update the mobs
    	for (Mob mob : mobs)
    	{
    		mob.update();
    	}
    	
    	//update all the items (such as arrows) that are on the tiles
    	for (ArrayList<Tile> row : tiles)
    	{
    		for (Tile tile : row)
    		{
    			tile.updateItems();
    		}
    	}
    	
//    	//If space has just been pressed, break the block in front of the player (this is done in update to prevent concurrent
//    	//	modification errors)
//    	if (spacePressed)
//    	{
//    		player.breakBlock();
//    		spacePressed = false;
//    	}
//    	
//    	//If e has just been pressed, place a block in front of the player
//    	if (ePressed)
//    	{
//    		player.useItem();
//    		ePressed = false;
//    	}
//    	
//    	//If shift is not being held down and the player has waited long enough, move the player
//    	if (!shiftDown && clock > player.getNextMoveTime())
//    	{	
//			movePlayer();
//    	}
//    	
//    	//update the mobs
//    	for (Mob mob : mobs)
//    	{
//    		mob.update();
//    	}
//    	
    	
		clock++;
		
    	surface.repaint();
    }
    
    //Move the player (takes into account if the player is holding space or e)
    public static void tryPlayerMove ()
    {
//    	System.out.println("game move called");
    	
//    	//when the player is moving...
//		if (direction != 0)
//		{	
    	
    	//don't move if shift is being held
    	if (!shiftDown)
    	{
			//if space is down, break the block in front of the player before they move (don't break a block that you're
			//	immediately going to replace)
        	if (spaceDown && !eDown)
        	{
        		player.breakBlock();
        	}
        	
			//Move the player in a given direction
			player.move (direction);
			
	    	//if e is down, place a block in front of the player
	    	if (eDown)
	    	{
	    		player.useItem();
	    	}
    	}	
//	    	System.out.println(player.getNextMoveTime() - clock);
//		}
    }
    
    //rotate the player if e is not being held down
    public static void tryPlayerRotate ()
    {
    	if (!eDown)
		{
			player.setRotation (direction);
		}
    }
	
	public static void main(String[] args) 
	{
		window = new Game();
		window.setVisible (true);
//		startTimer();
		startGame();
	}
	
	
    //class for the key adapter
    class keyAdapter extends KeyAdapter
    {	
    	@Override
    	public void keyPressed (KeyEvent e)
    	{	
       		int input = e.getKeyCode();
//    		System.out.println(input);
    		
    		//Player movement controls
    		if (input == KeyEvent.VK_UP || input == 87)
    		{
//    			
    			//move the player if shift is not down and the up key has not been pressed yet
//    			if (direction != 2)
//    			{
//    				direction = 2;
    				direction = Direction.UP;
//    				tryPlayerRotate();
//    				
//    				if (!shiftDown)
//    				{
//    					//make it so that the player can move at this moment
//    					player.setNextMoveTime(clock);
//    				}
//    			}
    		}
    		
    		else if (input == KeyEvent.VK_DOWN || input == 83)
    		{
//    			
//    			//move the player if shift is not down and the down key has not been pressed yet
//    			if (direction != 4)
//    			{
//    				direction = 4;
    				direction = Direction.DOWN;
//    				tryPlayerRotate();
//    				
//    				if (!shiftDown)
//    				{
//    					//make it so that the player can move at this moment
//    					player.setNextMoveTime(clock);
//    				}
//    			}
    		}
    		
    		else if (input == KeyEvent.VK_LEFT || input == 65)
    		{
//    			
//    			//move the player if shift is not down and the left key has not been pressed yet
//    			if (direction != 1)
//    			{
//    				direction = 1;
    				direction = Direction.LEFT;
//    				tryPlayerRotate();
//    				
//    				if (!shiftDown)
//    				{
//    					//make it so that the player can move at this moment
//    					player.setNextMoveTime(clock);
//    				}
//    			}
    		}
    		
    		else if (input == KeyEvent.VK_RIGHT || input == 68)
    		{
//    			
//    			//move the player if shift is not down and the right key has not been pressed yet
//    			if (direction != 3)
//    			{
//    				direction = 3;
    				direction = Direction.RIGHT;
//    				tryPlayerRotate();
//    				
//    				if (!shiftDown)
//    				{
//    					//make it so that the player can move at this moment
//    					player.setNextMoveTime(clock);
//    				}
//    			}
    		}
    		
    		//space (breaks a block)
    		else if (input == 32)
    		{
    			if (!spaceDown)
    			{
    				spacePressed = true;
    				spaceDown = true;
    			}
    		}
    		
    		//e (interact with the block in front of the player. If it is not interactable, use the item currently selected)
    		else if (input == 69)
    		{
    			if (!eDown)
    			{
	    			eDown = true;
	    			ePressed = true;
    			}
    		}
    		
    		//register if shift is being pressed
    		else if (input == 16)
    		{
    			shiftDown = true;
    		}
    		
    		//number keys 1-9 (to change inventory slots)
    		else if (input > 48 && input < 58)
    		{	
    			//if shift is not down or the front tile is not a chest, select through the player's inventory
    			if (!shiftDown || !(player.getFrontTile() instanceof Chest))
    			{
    				player.getInventory().setSlot(input - 49);
    			}
    			//if shift is down and the frontTile is a chest, select through that chest's inventory
    			else
    			{
    				((Chest) player.getFrontTile()).getInventory().setSlot(input - 49);
    			}
    			
				surface.repaint();
    		}
    		
    		//number key 0 (to change inventory slots)
    		else if (input == 48)
    		{
    			//if shift is not down or the front tile is not a chest, select through the player's inventory
    			if (!shiftDown || !(player.getFrontTile() instanceof Chest))
    			{
    				player.getInventory().setSlot(9);
    			}
    			//if shift is down and the frontTile is a chest, select through that chest's inventory
    			else
    			{
    				((Chest) player.getFrontTile()).getInventory().setSlot(9);
    			}
    			
				surface.repaint();
    		}
    		
    		//f: swap the items in the currently selected player and chest inventory slots
    		else if (input == 70)
    		{
    			if (player.getFrontTile() instanceof Chest)
    			{
    				player.getInventory().swap(((Chest) player.getFrontTile()).getInventory());
    			}
    		}
    		
    		//zoom (- makes the screen bigger, = makes it smaller)
    		else if (input == 45)
    		{
    			if (shiftDown)
    			{
	    			surface.zoom(-5);
	    			surface.repaint();
    			}
    		}
    		
    		else if (input == 61)
    		{
    			if (shiftDown)
    			{
	    			surface.zoom(5);
	    			surface.repaint();
    			}
    		}
    		
    		//esc
    		else if (input == 27)
    		{
    			//close the window
    			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    		}
    		
    		//System.out.println(e.paramString());
    	}
    	
    	@Override
    	public void keyReleased (KeyEvent e)
    	{
    		int input = e.getKeyCode();
    		
    		//space
    		if (input == 32)
    		{
    			spaceDown = false;
    		}
    		
    		//shift
    		else if (input == 16)
    		{
    			shiftDown = false;
    		}
    		
    		//e
    		else if (input == 69)
    		{
    			eDown = false;
    		}
    		
    		//wasd and arrow keys
    		else if (input == KeyEvent.VK_UP || input == 87)
    		{
//    			if (direction == 2)
//    			{
//    				direction = 0;
//    			}
    			
//    			lastDirectionReleased = 2;
    			lastDirectionReleased = Direction.UP;
    		}
    		else if (input == KeyEvent.VK_DOWN || input == 83)
    		{
//    			if (direction == 4)
//    			{
//    				direction = 0;
//    			}
    			
//    			lastDirectionReleased = 4;
    			lastDirectionReleased = Direction.DOWN;
    		}
    		else if (input == KeyEvent.VK_LEFT || input == 65)
    		{
//    			if (direction == 1)
//    			{
//    				direction = 0;
//    			}
    			
//    			lastDirectionReleased = 1;
    			lastDirectionReleased = Direction.LEFT;
    		}
    		else if (input == KeyEvent.VK_RIGHT || input == 68)
    		{
//    			if (direction == 3)
//    			{
//    				direction = 0;
//    			}
    			
//    			lastDirectionReleased = 3;
    			lastDirectionReleased = Direction.RIGHT;
    		}
    	}
    }
    
    //class for the mouse adapter
    class mouseAdapter extends MouseAdapter
    {
    	private int x;
        private int y;
        
        public void mousePressed (MouseEvent e)
		{
        	//x and y given in terms of tiles (I don't know why the origin is ~ (16,72))
			x = (e.getX() - 16) / tileSize;
	        y = (e.getY() - 72) / tileSize;
	        int input = e.getButton();
	        
	        //left mouse button
	        if (input == 1)
	        {
	        	//TODO: delete this
//	        	System.out.println("click:" + tiles.get(y).get(x).getClass().getSimpleName() + x + " " + y);
//	        	Tile playerTile = tiles.get(player.getY()).get(player.getX());
//	        	Tile testTile = tiles.get(y).get(x);
//	        	
//	        	for (GameObject source : testTile.getLightSources())
//	        	{
//	        		System.out.println(source.getClass().getSimpleName() + source.getX() + " " + source.getY());
//	        	}
	        	placeTile(new Glowstone(x, y), x, y);
	        	
//	        	playerTile.nextPathTile(testTile, 50);
//	        	
//	        	Tile.clearCheck();
	        }
	         
	        //right mouse button
	        else if (input == 3)
	        {
	        	 
	        }
		}
        
        public void mouseReleased (MouseEvent e)
        {
        	int input = e.getButton();
        	
    		if (input == 1)
    		{
    			spaceDown = false;
    		}
    		
    		else if (input == 3)
    		{
    			eDown = false;
    		}
        }
    }
}
