package Game;
 import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.Polygon;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Items.Item;
import Mobs.Mob;
import Tiles.Tile;

import Items.Arrow;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

public class Surface extends JPanel
{	
	//the images that are used in this game
	public static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>(); 
	
	//UI for the player's inventory
	private InventoryUI inventoryUI;
	
	//Initializes the game by adding the Game.mobs and Game.tiles
    public Surface() 
    {   
//    	//TODO: do this in a static thing, like you do for tile and mob finals
//    	//Set up the display library
//    	DisplayLibrary.initiateLibrary();
    	
    	loadImages(new File("Images"));
    	
    	//set up the inventory UI
    	inventoryUI = new InventoryUI();
    }
    
    //recursively load the images in from a given file or directory
    private static void loadImages(File inFile)
    {
    	if (inFile.isDirectory())
    	{
    		File[] innerFiles = inFile.listFiles();
    		for (File file : innerFiles)
    		{
    			loadImages(file);
    		}
    	}
    	else
    	{
    		try
    		{
				images.put(inFile.getName(), ImageIO.read(inFile));
			}
    		catch (IOException e)
    		{
				e.printStackTrace();
			}
    	}
    }
    
//    //rotate a given image from a previous direction to a new direction
//    public static BufferedImage rotateImage (float prevDir, float newDir, BufferedImage inImage)
//    {
////    	if (inImage == null) {return null;}
//    		
//    	AffineTransform trans = new AffineTransform();
//    	trans.rotate(newDir - prevDir, inImage.getWidth()/2, inImage.getHeight()/2);
//    	AffineTransformOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_BILINEAR);
//    	return op.filter(inImage, null);
//    }
    
    //draws the Game.mobs and Game.tiles
     private void draw (Graphics g)
    {
    	Graphics2D g2d = (Graphics2D) g;
    	
//    	RenderingHints rh = new RenderingHints (
//    		RenderingHints.KEY_ANTIALIASING,
//    		RenderingHints.VALUE_ANTIALIAS_ON);
//    		
////    	rh.put(RenderingHints.KEY_RENDERING,
////    		RenderingHints.VALUE_RENDER_QUALITY);
//    		
//    	g2d.setRenderingHints (rh);
    	
    	RenderingHints rh = new RenderingHints (
        		RenderingHints.KEY_RENDERING,
        		RenderingHints.VALUE_RENDER_SPEED);
    	g2d.setRenderingHints (rh);
    	
    	//draws the Game.tiles row by column
    	for (ArrayList<Tile> row : Game.tiles)
    	{
    		for (Tile tile : row)
    		{    	
    			tile.draw(g2d);
    		}
    	}
    	
    	//draw the Game.mobs
    	for (Mob mob : Game.mobs)
    	{
    		mob.draw(g2d);
    	}
    	
    	//Draw the inventory UI and the contents of the player's inventory
    	g2d.setPaint(Color.darkGray);
    	g2d.fill(inventoryUI.getBackdrop());
    	
    	//draw the player
    	if (Game.player != null)
    	{
    		Game.player.draw(g2d);
    	}
    	
//    	Game.player.drawInventory(g2d);
    	
    	g2d.setPaint(Color.white);
//    	int currentSlot = Game.player.getSlot() + 6;
    	for (int i = 0; i < inventoryUI.getElements().size(); i++)
    	{
//    		//highlight the slot that is currently selected
//    		if (i == currentSlot)
//    		{
//    			//temp variables for the selected slot's box and a smaller version to laid on top of it
//    			Rectangle2D.Float slotRect = inventoryUI.getElements().get(i);
//    			Rectangle2D.Float smallerRect = new Rectangle2D.Float((float) slotRect.getX() + (Game.tileSize / 2f), (float) slotRect.getY() + (Game.tileSize / 2f),
//    																  (float) slotRect.getWidth() - Game.tileSize, (float) slotRect.getHeight() - Game.tileSize);
//    			
//    			g2d.setPaint(Color.red);
//    			g2d.fill(slotRect);
//    			g2d.setPaint(Color.white);
//    			g2d.fill(smallerRect);
//    		}
//    		
//    		else
//    		{
    			g2d.fill(inventoryUI.getElements().get(i));
//    		}
    	}
    	
    	//TODO: delete this
//    	//Area test stuff
//		int x = 0;
//		int y = 0;
//		int w = getSize().width - 1;
//		int h = getSize().height - 1;
//			
//		Shape line = new Line2D.Float(x, y, w, h);
//		Shape oval = new Ellipse2D.Float(x, y, w/2, h/2);
//		Shape rectangle = new Rectangle2D.Float(x, y, w/2, h/2);
//		Shape rectangle2 = new Rectangle2D.Float(w/2, h/2, w, h);
//		Shape roundRectangle = new RoundRectangle2D.Float(x, y, w, h, w/2, h/2);
//		
//		int startAngle = 45;
//		int arcAngle = -180;
//		
//		Shape arc = new Arc2D.Float(x, y, w/2, h/2, startAngle, arcAngle, Arc2D.OPEN);
//		
//		Area shape;
//		shape = new Area(rectangle2);
////		shape.add(new Area(rectangle2));
////		shape.subtract(new Area (rectangle));
////		shape.intersect(new Area(oval));
////		shape.exclusiveOr(new Area(arc));
//		
////		g2d.draw(shape);
//		//g2d.setPaint(Color.red);
//		//g2d.fill(shape.getBounds2D());
//		g2d.setPaint(Color.green);
//		g2d.fill(shape);
//		
////		g2d.setPaint(Color.green);
////		for (int i = 0; i < 1000; i++)
////		{
////			for (int j = 0; j < 1; j++)
////			{
////				g2d.fill(new Ellipse2D.Float(2*i, 2*j, 1, 1));
////			}
////		}
    }
    
    //Paints the components
    public void paintComponent (Graphics g)
    {
    	super.paintComponent(g);
    	draw(g);
    }

    
//    //Uses the library to return a list of display elements given a game element's superType, sub type, position and size.
//	public static ArrayList<DisplayElement> createDisplayList (int superType, int type, int x, int y, int width, int height)
//	{
//		return DisplayLibrary.createDisplayList(superType, type, x, y, width, height);
//	}
	
    //Zoom (change the Game.tileSize and update the game elements, as long as the tile size isn't already too big or small)
    public void zoom (int dSize)
    {	
    	Game.tileSize += dSize;
//    	int newTileSize = Game.tileSize + dSize;
//    	
//    	if (newTileSize >= 10 && newTileSize <= 30)
//    	{
//    		float scaleFactor = ((float) newTileSize) / Game.tileSize;
//
//    		Game.player.resize(scaleFactor);
//    		for (ArrayList<Tile> row : Game.tiles)
//        	{
//        		for (Tile tile : row)
//        		{    	
//        			tile.resize(scaleFactor);
//        		}
//        	}
//    		for (Mob mob : Game.mobs)
//    		{
//    			mob.resize(scaleFactor);
//    		}
//    		
//    		//resize the ui stuff
//    		Rectangle2D.Float backdrop = inventoryUI.getBackdrop();
//    		backdrop.setFrame(backdrop.getX() * scaleFactor, backdrop.getY() * scaleFactor, backdrop.getWidth() * scaleFactor, backdrop.getHeight() * scaleFactor);
//    		for (Rectangle2D.Float element : inventoryUI.getElements())
//    		{
//    			element.setFrame(element.getX() * scaleFactor, element.getY() * scaleFactor, element.getWidth() * scaleFactor, element.getHeight() * scaleFactor);
//    		}
////    		Game.player.resizeInventory(scaleFactor);
//    		
//    		Game.tileSize = newTileSize;
//    	}
    }
    
    //returns the distance between 2 points
    public static float getDist (float inX1, float inY1, float inX2, float inY2)
    {
    	return (float) (Math.sqrt((double) (Math.pow((double) (inX1 - inX2), 2) + Math.pow((double) (inY1 - inY2), 2))));
    }
    
    //class that handles the inventory UI
    class InventoryUI
    {
    	//TODO: In the future, get rid of this ui class and have the player have item slots for armor/weapons/items and have a
    	//backdrop displayElement
    	private Rectangle2D.Float backdrop;
    	private ArrayList<Rectangle2D.Float> UIElements = new ArrayList<Rectangle2D.Float>();
    	
    	public InventoryUI ()
    	{
        	int elementSize = 4 * Game.tileSize;
        	
        	//Set up the backdrop
    		backdrop = new Rectangle2D.Float((Game.BOARD_SIZE + 1) * Game.tileSize, 0, 21 * Game.tileSize, 50 * Game.tileSize);
    		//Set up the UI elements
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 7) * Game.tileSize, Game.tileSize, elementSize, elementSize));
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 2) * Game.tileSize, 6 * Game.tileSize, elementSize, elementSize));
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 7) * Game.tileSize, 6 * Game.tileSize, elementSize, elementSize));
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 12) * Game.tileSize, 6 * Game.tileSize, elementSize, elementSize));
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 7) * Game.tileSize, 11 * Game.tileSize, elementSize, elementSize));
    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 7) * Game.tileSize, 16 * Game.tileSize, elementSize, elementSize));
//    		elementSize = 3 * Game.tileSize;
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 2) * Game.tileSize, 21 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 6) * Game.tileSize, 21 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 10) * Game.tileSize, 21 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 14) * Game.tileSize, 21 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 18) * Game.tileSize, 21 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 2) * Game.tileSize, 25 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 6) * Game.tileSize, 25 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 10) * Game.tileSize, 25 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 14) * Game.tileSize, 25 * Game.tileSize, elementSize, elementSize));
//    		UIElements.add(new Rectangle2D.Float((Game.BOARD_SIZE + 18) * Game.tileSize, 25 * Game.tileSize, elementSize, elementSize));	
    	}
    	
    	public Rectangle2D.Float getBackdrop()
    	{
    		return backdrop;
    	}
    	
    	public ArrayList<Rectangle2D.Float> getElements()
    	{
    		return UIElements;
    	}
    }
    
//    //Holds what every type of game element looks like. Used when setting the display elements of a game element.
//    private static class DisplayLibrary
//    {
//    	//Stores what all the types of elements look like. The 1st level determines the superType of element (mob, tile, etc.), the 2nd level determines the type
//    	//	(air, stone, skeleton, etc.) and the last level holds all of the display elements for the game element.
//    	private static ArrayList<ArrayList<ArrayList<LibraryElement>>> library = new ArrayList<ArrayList<ArrayList<LibraryElement>>>();
//    	
////    	public DisplayLibrary ()
////    	{
////    		library = new ArrayList<ArrayList<ArrayList<LibraryElement>>>();
////    		initiateLibrary();
////    	}
//    	
//    	//Initialize the library
//    	public static void initiateLibrary ()
//    	{	
//    		ArrayList<LibraryElement> tempDisplay;
//    		
//    		//create the displays for each tile type
//    		ArrayList<ArrayList<LibraryElement>> tileDisplays = new ArrayList<ArrayList<LibraryElement>>();
//    		//Air
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, Color.lightGray, 0));
//    		tileDisplays.add(Tile.AIR_TYPE, tempDisplay);
//    		//Stone
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, Color.gray, 0));
//    		tileDisplays.add(Tile.STONE_TYPE, tempDisplay);
//    		//Glowstone
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, Color.gray, 0));
//    		tempDisplay.add(new LibraryElement(0, 0, .5f, .5f, Color.green, 0));
//    		tempDisplay.add(new LibraryElement(.5f, .5f, .5f, .5f, Color.green, 0));
//    		tileDisplays.add(Tile.GLOWSTONE_TYPE, tempDisplay);
//    		//Chest
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(0, 0, .25f, .25f, Color.yellow, 0));
//    		tempDisplay.add(new LibraryElement(.75f, 0, .25f, .25f, Color.yellow, 0));
//    		tempDisplay.add(new LibraryElement(.25f, .25f, .5f, .25f, Color.yellow, 0));
//    		tileDisplays.add(Tile.CHEST_TYPE, tempDisplay);
//    		
//    		//create the displays for each mob type (including the player)
//    		ArrayList<ArrayList<LibraryElement>> mobDisplays = new ArrayList<ArrayList<LibraryElement>>();
//    		//Player = 0
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, Color.red, 1));
//    		tempDisplay.add(new LibraryElement(.125f, .75f, .25f, .25f, Color.black, 1));
//    		tempDisplay.add(new LibraryElement(.625f, .75f, .25f, .25f, Color.black, 1));
//    		mobDisplays.add(Mob.PLAYER_TYPE, tempDisplay);
//    		//Zombie = 1
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(0, 0, 1, 1, ZOMBIE_GREEN, 1));
//    		tempDisplay.add(new LibraryElement(.125f, .75f, .25f, .25f, Color.red, 1));
//    		tempDisplay.add(new LibraryElement(.625f, .75f, .25f, .25f, Color.red, 1));
//    		mobDisplays.add(Mob.ZOMBIE_TYPE, tempDisplay);
//    		
//
//    		//The displays for all of the items (weapons, potions, etc.)
//    		ArrayList<ArrayList<LibraryElement>> itemDisplays = new ArrayList<ArrayList<LibraryElement>>();
//    		//Basic sword
//    		tempDisplay = new ArrayList<LibraryElement>();
//    		tempDisplay.add(new LibraryElement(.5f, .5f, .25f, .25f, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(.375f, .75f, .125f, .125f, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(.75f, .375f, .125f, .125f, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(.75f, .75f, .125f, .125f, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(.875f, .875f, .125f, .125f, WOOD_BROWN, 0));
//    		tempDisplay.add(new LibraryElement(.375f, .375f, .25f, .25f, Color.gray, 0));
//    		tempDisplay.add(new LibraryElement(.25f, .25f, .25f, .25f, Color.gray, 0));
//    		tempDisplay.add(new LibraryElement(.125f, .125f, .25f, .25f, Color.gray, 0));
//    		tempDisplay.add(new LibraryElement(0, 0, .25f, .25f, Color.gray, 0));
//    		itemDisplays.add(Item.SWORD1_TYPE, tempDisplay);
//    		
//    		//create a list for each type
//    		library.add(Tile.TILE_TYPE, tileDisplays);
//    		library.add(Mob.MOB_TYPE, mobDisplays);
//    		library.add(Item.ITEM_TYPE, itemDisplays);
//    	}
//    	
//    	//Uses the library to return a list of display elements given a game element's super type, type, position and size.
//    	public static ArrayList<DisplayElement> createDisplayList (int superType, int type, int x, int y, int width, int height)
//    	{
//    		//error if you input an undefined superType or subtype
//    		if (superType < 0 || superType >= library.size())
//    		{
//    			throw new Error ("trying to get undefined superType from display library.\n");
//    		}
//    		if (type < 0 || type >= library.get(superType).size())
//    		{
//    			throw new Error ("trying to get undefined superType from display library.\n");
//    		}
//    		
//    		ArrayList<DisplayElement> output = new ArrayList<DisplayElement>();
//    		ArrayList<LibraryElement> display = library.get(superType).get(type);
//    		
//    		//use the library elements to return a list of display elements
//    		for (LibraryElement element : display)
//    		{
//    			output.add(element.createDisplayElement(x, y, width, height));
//    		}
//    		
//    		return output;
//    	}
//    }
    
//    //holds the general information of a display element in the display library
//    private static class LibraryElement
//    {
//    	//the position and size of the element in terms of the game element. Ex: (1/2, 1/2, 1/2, 1/2) means place the display element's upper-left corner in the
//    	//	center of the game element, with half of the game element's width and half of its height. All of these values are between 0 and 1.
//    	private float relativeX;
//    	private float relativeY;
//    	private float relativeWidth;
//    	private float relativeHeight;    	
//    	
//    	//The color of the library element
//    	private Color color;
//    	//The shape of the library element (0 = rectangle, 1 = ellipse)
//    	private int shape;
//    	
//    	public LibraryElement (float inX, float inY, float inWidth, float inHeight, Color inColor, int inShape)
//    	{
//    		relativeX = inX;
//    		relativeY = inY;
//    		relativeWidth = inWidth;
//    		relativeHeight = inHeight;
//    		color = inColor;
//    		shape = inShape;
//    	}
//    	
//    	//creates a display element given the position and size of a game element 
//    	public DisplayElement createDisplayElement (int x, int y, int width, int height)
//    	{
//    		RectangularShape displayShape = null;
//    		if (shape == 0)
//    		{
//    			displayShape = new Rectangle2D.Float((x + relativeX * width) * Game.tileSize, (y + relativeY * height) * Game.tileSize, width * relativeWidth * Game.tileSize, height * relativeHeight * Game.tileSize);
//    		}
//    		
//    		else if (shape == 1)
//    		{
//    			displayShape = new Ellipse2D.Float((x + relativeX * width) * Game.tileSize, (y + relativeY * height) * Game.tileSize, width * relativeWidth * Game.tileSize, height * relativeHeight * Game.tileSize);
//    		}
//    		
//    		return new DisplayElement (displayShape, color);
//    	}
//    }
    
//    //holds a display shape (rectangle, circle, etc.) and its color
//    public static abstract class DisplayElement
//    {
////    	private RectangularShape shape;
//    	protected Shape shape;
//    	private Color color;
//    	
//    	private DisplayElement (){/*This is just here so that you don't have to use a super constructor in the child constructors*/}
//    	
////    	public DisplayElement (RectangularShape inShape, Color inColor)
//    	public DisplayElement (Shape inShape, Color inColor)
//    	{
//    		setShape(inShape);
//    		setColor(inColor);
//    	}
//    	
////    	public RectangularShape getShape ()
//    	public Shape getShape ()
//    	{
//    		return shape;
//    	}
//    	
////    	public void setShape (RectangularShape inShape)
//    	public void setShape (Shape inShape)
//    	{
//    		shape = inShape;
//    	}
//    	
//    	public Color getColor ()
//    	{
//    		return color;
//    	}
//    	
//    	public void setColor (Color inColor)
//    	{
//    		color = inColor;
//    	}
//    	
//    	//draw the display element
//    	public void draw (Graphics2D g2d)
//    	{
//    		g2d.setPaint(color);
//    		g2d.fill(shape);
//    	}
//    	
//    	//resize the element (used for zooming in and out)
//    	public abstract void resize (float scaleFactor);
////    	public void resize (float scaleFactor)
////    	{
////    		shape.setFrame(shape.getX() * scaleFactor, shape.getY() * scaleFactor, shape.getWidth() * scaleFactor, shape.getHeight() * scaleFactor);
////    	}
//    	
//    	//place the element at a new position
//    	public abstract void setPos (float newX, float newY);
////    	public void setPos (float newX, float newY)
////    	{	
////    		shape.setFrame(newX * Game.tileSize, newY * Game.tileSize, shape.getWidth(), shape.getHeight());
////    	}
//
//    	//move the element by a certain amount
//    	public abstract void move (int dX, int dY);
////    	public void move (int dX, int dY)
////    	{
////    		shape.setFrame(shape.getX() + (dX * Game.tileSize), shape.getY() + (dY * Game.tileSize), shape.getWidth(), shape.getHeight());
////    	}
//    }
//    
////    public static class DisplayRect extends DisplayElement
//    public static class DisplayRect extends DisplayElement
//    {
//    	public DisplayRect (RectangularShape inShape, Color inColor)
//    	{
//    		super (inShape, inColor);
//    	}
//    	
//    	public DisplayRect (float inX, float inY, float inWidth, float inHeight, Color inColor)
//    	{
//    		super(new Rectangle2D.Float(inX * Game.tileSize, inY * Game.tileSize, inWidth * Game.tileSize, inHeight * Game.tileSize), inColor);
//    	}
//    	
//    	public DisplayRect (GameObject inObj ,float relativeX, float relativeY, float relativeWidth, float relativeHeight, Color inColor)
//    	{
////    		super(new Rectangle2D.Float((inObj.getX() + relativeX * inObj.getWidth()) * Game.tileSize, (inObj.getY() + relativeY * inObj.getHeight()) * Game.tileSize, inObj.getWidth() * relativeWidth * Game.tileSize, inObj.getHeight() * relativeHeight * Game.tileSize), inColor);
//    		this(inObj.getX() + relativeX * inObj.getWidth(), inObj.getY() + relativeY * inObj.getHeight(), inObj.getWidth() * relativeWidth, inObj.getHeight() * relativeHeight, inColor);
//    	}
//    	
//    	//resize the element (used for zooming in and out)
//    	public void resize (float scaleFactor)
//    	{
//    		RectangularShape temp = (RectangularShape) shape;
//    		temp.setFrame(temp.getX() * scaleFactor, temp.getY() * scaleFactor, temp.getWidth() * scaleFactor, temp.getHeight() * scaleFactor);
////    		shape.setFrame(shape.getX() * scaleFactor, shape.getY() * scaleFactor, shape.getWidth() * scaleFactor, shape.getHeight() * scaleFactor);
//    	}
//    	
//    	//place the element at a new position
//    	public void setPos (float newX, float newY)
//    	{	
//    		RectangularShape temp = (RectangularShape) shape;
//    		temp.setFrame(newX * Game.tileSize, newY * Game.tileSize, temp.getWidth(), temp.getHeight());
//    	}
//    	
//    	//move the element by a certain amount
//    	public void move (int dX, int dY)
//    	{
//    		RectangularShape temp = (RectangularShape) shape;
//    		temp.setFrame(temp.getX() + (dX * Game.tileSize), temp.getY() + (dY * Game.tileSize), temp.getWidth(), temp.getHeight());
//    	}
//    }
//    
////    public static class DisplayEllipse extends DisplayElement
//    public static class DisplayEllipse extends DisplayRect
//    {
//    	public DisplayEllipse (float inX, float inY, float inWidth, float inHeight, Color inColor)
//    	{
//    		super(new Ellipse2D.Float(inX * Game.tileSize, inY * Game.tileSize, inWidth * Game.tileSize, inHeight * Game.tileSize), inColor);
//    	}
//    	
//    	public DisplayEllipse (GameObject inObj ,float relativeX, float relativeY, float relativeWidth, float relativeHeight, Color inColor)
//    	{
////    		super(new Ellipse2D.Float((inObj.getX() + relativeX * inObj.getWidth()) * Game.tileSize, (inObj.getY() + relativeY * inObj.getHeight()) * Game.tileSize, inObj.getWidth() * relativeWidth * Game.tileSize, inObj.getHeight() * relativeHeight * Game.tileSize), inColor);
//    		this(inObj.getX() + relativeX * inObj.getWidth(), inObj.getY() + relativeY * inObj.getHeight(), inObj.getWidth() * relativeWidth, inObj.getHeight() * relativeHeight, inColor);
//    	}
//    }
//    
//    public static class DisplayPoly extends DisplayElement
//    {	
//    	public DisplayPoly (float[] xPoints, float[] yPoints, Color inColor)
//    	{
//    		int[] tempX = new int[xPoints.length];
//    		int[] tempY = new int[yPoints.length];
//    		for (int i = 0; i < xPoints.length; i++)
//    		{
//    			tempX[i] = (int) (xPoints[i] * Game.tileSize);
//    			tempY[i] = (int) (yPoints[i] * Game.tileSize);
//    		}
//    		
//    		setShape(new Polygon(tempX, tempY, tempX.length));
//    		setColor(inColor);
//    	}
//    	
//    	//TODO: resize if buggy, use path2d.float
//    	public void resize (float scaleFactor)
//    	{
//    		Polygon temp = (Polygon) shape;
//    		for (int i = 0; i < temp.xpoints.length; i++)
//    		{
//    			temp.xpoints[i] *= scaleFactor;
//    			temp.xpoints[i] *= scaleFactor;
//    		}
//    	}
//    	
//    	public void setPos (float newX, float newY)
//    	{
//    		Polygon temp = (Polygon) shape;
//    		Rectangle2D bounds = temp.getBounds2D();
//    		temp.translate((int) (newX - bounds.getX()), (int)(newY - bounds.getY()));
//    	}
//    	
//    	public void move (int dX, int dY)
//    	{
//    		Polygon temp = (Polygon) shape;
//    		temp.translate(dX, dY);
//    	}
//    }
}