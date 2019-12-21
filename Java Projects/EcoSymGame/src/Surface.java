import java.awt.*;
 import javax.swing.*;
 import java.awt.geom.*;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.lang.Double;
 import java.util.Scanner;
 
public class Surface extends JPanel
{
	//Stores the mobs
	static public ArrayList<Mob> mobs = new ArrayList<Mob>();
	//stores the tiles, ordered y, then x
	static public ArrayList<ArrayList<Tile>> tiles = new ArrayList<ArrayList<Tile>>();
	
	//size of the tiles
	static public final int TILE_SIZE = 10;
	
	//Max Double value (used in Mob, but I don't know why Mob isn't getting access to Double variables)
	double MAX_DOUBLE_VALUE = Double.MAX_VALUE;
	
	public Ellipse2D.Float testCircle;
	
	public Scanner input = new Scanner (System.in);
	
	//Initializes the game by adding the mobs and tiles
    public Surface() 
    {   
    	Mob sampleCircle = new Mob(150, 150, 5, 5);
    	mobs.add(sampleCircle);
    	
    	//Mob sampleCircle2 = new Mob (150, 150, 100, 100, 0);
    	//mobs.add(sampleCircle2);
    	
    	InitTiles();
    }
    
    //Sets the tiles
    public void InitTiles ()
    {
    	for (int j = 0; j < 29; j++)
    	{
    		ArrayList<Tile> tempList = new ArrayList<Tile>();
    		
    		for (int i = 0; i < 29; i++)
    		{
    			Tile tempTile = new Tile ((i*TILE_SIZE), (j*TILE_SIZE), TILE_SIZE, TILE_SIZE);
    			
    			//Make the outer edges bedrock
    			if (i == 0 || i == 28 || j == 0 || j == 28)
    			{
    				tempTile.setType("bedrock");
    			}
    			
    			tempList.add(tempTile);
    		}
    		
    		tiles.add(tempList);
    	}
    }
    
    //Draws the mobs and tiles
    private void Draw (Graphics g)
    {
    	Graphics2D g2d = (Graphics2D) g;
    	
    	RenderingHints rh = new RenderingHints (
    		RenderingHints.KEY_ANTIALIASING,
    		RenderingHints.VALUE_ANTIALIAS_ON);
    		
    	rh.put(RenderingHints.KEY_RENDERING,
    		RenderingHints.VALUE_RENDER_QUALITY);
    		
    	g2d.setRenderingHints (rh);
    	
    	//Draws the tiles row by column
    	for (ArrayList<Tile> row : tiles)
    	{
    		for (Tile tile : row)
    		{    	
    			g2d.setPaint (tile.getColor());
    			g2d.fill(tile);
    		}
    	}
    	
    	//Draws the mobs
    	for (int i = 0; i < mobs.size(); i++)
    	{
    		Mob mob = mobs.get(i);
    		
    		g2d.setPaint(mob.getColor());
    		g2d.fill(mob);
    	}
    	
    	if (testCircle != null)
    	{
	    	g2d.setPaint(Color.green);
	    	g2d.fill(testCircle);
    	}
    }
    
    //Paints the components
    public void paintComponent (Graphics g)
    {
    	super.paintComponent(g);
    	Draw(g);
    }
    
    //Updates the game by a frame when the timer pulses
	public void Update ()
	{
		//stores the current mobs in the game (makes sure that recent children wait a turn before they become parents)
		ArrayList<Mob> tempMobs = new ArrayList<Mob>();
		for (Mob mob : mobs)
		{
			tempMobs.add(mob);
		}
		
		//Updates the mobs
		for (int i = 0; i < tempMobs.size(); i++)
		{	
			Mob mob = tempMobs.get(i);
			
			//In the future, put doMove and Update borders in a Mob method named Update()
			mob.doMove();
			mob.UpdateBorders();
			//mob.SpawnChild();
			
    		//System.out.println(mobs.size() - i - 1);
		}
		
		for (Mob mob : tempMobs)
		{
			//mob.setColor(Color.green);
			//repaint();
			
			//mob.SpawnChild();
			
			//input.next();
			//mob.setColor(Color.blue);
			//repaint();
			
			//System.out.println("next mob===========================================================================================");
		}
		
		//Updates the tiles
		for (ArrayList<Tile> row : tiles)
		{
			for (Tile tile : row)
			{
	    		//In the future, make a method in Tile called Update(), probably include resource spawning in it
	    		tile.CheckMobsInside();
			}
		}
		
		
		repaint();
		//System.out.println("next Update");
		//input.next();
	}
	
	//Merges ranges (includes the span of both ranges)
	public void MergeRangesExclusive (ArrayList<Range> inRanges)
	{
		for (int i = inRanges.size() - 2; i >= 0; i--)
    	{
    		//The range that will remain
			Range tempRange = inRanges.get(i);
			
    		for (int j = inRanges.size() - 1; j > i; j--)
    		{
    			//The range that will be compared with tempRange and that will be removed if they overlap
    			Range comparedRange = inRanges.get(j);
    			
    			//System.out.println("tempmin=" + (tempRange.getMin() / Math.PI) + " tempmax=" + (tempRange.getMax() / Math.PI));
    			//System.out.println("compmin=" + (comparedRange.getMin() / Math.PI) + " compmax=" + (comparedRange.getMax() / Math.PI));
        		//input.next();
    			
    			//if the ranges overlap, merge them
    			if ((tempRange.getMin() <= comparedRange.getMax() && tempRange.getMin() >= comparedRange.getMin()) ||
    				(comparedRange.getMin() <= tempRange.getMax() && comparedRange.getMin() >= tempRange.getMin()))
    			{
    				if (tempRange.getMin() > comparedRange.getMin())
    				{
    					tempRange.setMin(comparedRange.getMin());
    				}
    				
    				if (tempRange.getMax() < comparedRange.getMax())
    				{
    					tempRange.setMax(comparedRange.getMax());
    				}
    				
    				inRanges.remove(j);
    			}
    		}
    	}
	}
	
	//Merges ranges (only includes where they overlap and cuts the rest)
	public void MergeRangesInclusive (ArrayList<Range> inRanges)
	{
		for (int i = inRanges.size() - 2; i >= 0; i--)
    	{
    		//The range that will remain
			Range tempRange = inRanges.get(i);
			
    		for (int j = inRanges.size() - 1; j > i; j--)
    		{
    			//The range that will be compared with tempRange and that will be removed if they overlap
    			Range comparedRange = inRanges.get(j);
    			
    			//System.out.println("tempmin=" + (tempRange.getMin() / Math.PI) + " tempmax=" + (tempRange.getMax() / Math.PI));
    			//System.out.println("compmin=" + (comparedRange.getMin() / Math.PI) + " compmax=" + (comparedRange.getMax() / Math.PI));
        		//input.next();
    			
    			//if the ranges overlap, merge them
    			if ((tempRange.getMin() <= comparedRange.getMax() && tempRange.getMin() >= comparedRange.getMin()) ||
    				(comparedRange.getMin() <= tempRange.getMax() && comparedRange.getMin() >= tempRange.getMin()))
    			{
    				if (tempRange.getMin() < comparedRange.getMin())
    				{
    					tempRange.setMin(comparedRange.getMin());
    				}
    				
    				if (tempRange.getMax() > comparedRange.getMax())
    				{
    					tempRange.setMax(comparedRange.getMax());
    				}
    				
    				inRanges.remove(j);
    			}
    		}
    	}
	}
	
	//Merges two sets of ranges (only includes overlap)
	public ArrayList<Range> MergeRangesInclusive (ArrayList<Range> inRanges1, ArrayList<Range> inRanges2)
	{
		ArrayList<Range> outputRanges = new ArrayList<Range>();
		
		//combine the lists of ranges
		for (Range range : inRanges1)
		{
			outputRanges.add(range);
		}
		
		for (Range range : inRanges2)
		{
			outputRanges.add(range);
		}
		
		//Merge them
		MergeRangesInclusive(outputRanges);
		
		return outputRanges;
	}

//Mob class
    class Mob extends Ellipse2D.Float
    {
    	float radius;
    	
    	float speed;
    	//angle of movement, measured from 0 to 2*pi (goes clockwise, so pi/-2 goes up)
    	double theta;
    	
    	//color of the mob
    	Color color;
    	
    	//List of tiles the mob is colliding with (used for collisions)
    	ArrayList<Tile> collisionTiles = new ArrayList<Tile>();
    	
    	//List of mobs the mob is colliding with (used for collisions)
    	ArrayList<Mob> collisionMobs = new ArrayList<Mob>();
    	
    	public double getTheta ()
    	{
    		return theta;
    	}
    	
    	public Mob(float x, float y, float width, float height) 
		{
		    setFrame(x, y, width, height);
		    radius = height/2;
		    speed = 50;
		    theta = 0;
		    color = Color.blue;
		    
		    //sets up this mob's borders
		    UpdateBorders();
		}
    	
    	public Mob(float x, float y, float width, float height, float inSpeed) 
		{
		    setFrame(x, y, width, height);
		    radius = height/2;
		    speed = inSpeed;
		    theta = 0;
		    color = Color.blue;
		    
		    //sets up this mob's borders
		    UpdateBorders();
		}
    	
    	public void addCollisionTile (Tile inTile)
    	{
    		collisionTiles.add(inTile);
    	}
    	
		public float getRadius()
		{
			return radius;
		}
		
		public void setX (float inX)
		{
			this.x = inX;
		}
		
		//Moves the mob horizontally
        public void addX(float inX) 
       	{
            this.x += inX;
        }
        
        public void setY (float inY)
		{
			this.y = inY;
		}
		
		//Moves the mob vertically
        public void addY(float inY) 
       	{
            this.y += inY;
        }

        public void addWidth(float w) 
        {
            this.width += w;
        }

        public void addHeight(float h) 
        {
            this.height += h;
        }
        
        public Color getColor()
        {
        	return color;
        }
        
        public void setColor (Color inColor)
        {
        	color = inColor;
        }
        
        //Causes the mob to move
        //The mob has a velocity magnitude and a direction theta and determines the x and y components of its movement via dx = v*cos(theta) and dy = v*sin(theta)
        public void doMove ()
        {
        	//set theta randomly
        	theta = Math.random() * (Math.PI * 2);
        	//theta = Math.PI / 2;
        	
        	//Make sure the mobs doesn't try to move through solid objects
        	//CheckCollisions();
        	/*
        	System.out.println("theta=" + (theta / Math.PI) + " x=" + this.x + " y =" + this.y);
        	System.out.println(Math.cos(theta));
        	repaint();
        	input.next();
        	*/
        	//input.next();
        	
        	//dx = v*cos(theta) (positive = right, negative = left)
        	float dx = speed * (float) Math.cos(theta);
        	//dy = v*sin(theta) (positive = down, negative = up)
            float dy = speed * (float) Math.sin(theta);
            
            
            //Predict the area that the circle passes through (used to see if things will be in the way)
            PathArea tempArea = new PathArea (this, new Ellipse2D.Float ( (float) this.x + dx, (float) this.y + dy, (float) getHeight(), (float) getWidth()));
            
            //testCircle = new Ellipse2D.Float ( (float) this.x + dx, (float) this.y + dy, (float) getHeight(), (float) getWidth());
            		
            //Find the solid tiles and mobs in that area
            ArrayList<Tile> tilesInArea = getTilesInArea (tempArea);
            
            //Add the tiles the mob will end up in to tilesInArea
            ArrayList<Tile> destinationTiles = getTilesInArea((float) this.x + dx + radius, (float) this.y + dy + radius, radius);
            for (Tile tile : destinationTiles)
            {
            	if (!tilesInArea.contains(tile))
            	{
            		tilesInArea.add(tile);
            	}
            }
            
            ArrayList<Tile> collisionTiles = new ArrayList<Tile>();
            ArrayList<Mob> collisionMobs = new ArrayList<Mob>();
            for (Tile tile : tilesInArea)
            {
            	if (tile.getType().equals("bedrock"))
            	{
            		collisionTiles.add(tile);
            	}
            	
            	ArrayList<Mob> mobs = tile.getMobsInside();
            	for (Mob mob : mobs)
            	{
            		if (!collisionMobs.contains(mob) &&
            			!mob.equals(this))
            		{
            			collisionMobs.add(mob);
            		}
            	}
            }
            /*
            for (Tile tile : tilesInArea)
            {
            	tile.setColor(color.red);
            }
            
            repaint();
            input.next();
            
            for (Tile tile : tilesInArea)
            {
            	if (tile.getType().equals("bedrock"))
            	{
            		tile.setColor(color.black);
            	}
            	
            	else
            	{
            		tile.setColor(new Color(234, 168, 47));
            	}
            }
            */
            //Make sure that if there are any solid tiles or mobs in the way, this mob
            //will stop in front of the first one it touches
            if (collisionTiles.size() > 0 || collisionMobs.size() > 0)
            {
            	Coords collisionPosition = getCollisionPosition(collisionTiles, collisionMobs);
            	setX((float) (collisionPosition.getX() - radius));
            	setY((float) (collisionPosition.getY() - radius));
            	dy = 0;
            	dx = 0;
            	/*
        		for (Tile tile : collisionTiles)
        		{
        			tile.setColor(Color.red);
        		}
        		
        		for (Mob mob : collisionMobs)
        		{
        			mob.setColor(Color.red);
        		}
        		*/
        		//repaint();
        		//input.next();
            }
            
            
            //System.out.println(collisionTiles.size());
            /*
            for (Line line : tempArea.getCircleSides())
            {
            	System.out.println(line.toString());
            }
            
            for (Line line : tempArea.getBridgeSides())
            {
            	System.out.println(line.toString());
            }
            */
            /*
            input.next();
            
            for (Tile tile : collisionTiles)
            {
            	tile.setColor(Color.blue);
            	repaint();
            	input.next();
            }
			*/
			addX(dx);
			addY(dy);
			//repaint();
        }
        
        //Sets theta so that the mob doesn't go through any solid objects
        public void CheckCollisions ()
        {
        	/*
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> restrictedRanges = new ArrayList<Range>();
        	
        	for (int i = collisionTiles.size() - 1; i >=0; i--)
        	{
        		Tile tempTile = collisionTiles.remove(i);        		
        		
        		//angle between the mob and the tile
        		//double tempTheta = Math.atan2(tempTile.getY() - this.y, tempTile.getX() - this.x);
        		double tempTheta = Math.atan2(tempTile.getCenterY() - getCenterY(), tempTile.getCenterX() - getCenterX());
        		
        		//Makes sure that min theta will be positive
        		if (tempTheta - (Math.PI/2) < 0)
        		{
        			tempTheta += (Math.PI * 2);
        		}
        		
        		//the min and max thetas given the tempTheta (covers 90 degrees to the left and right of tempTheta)
        		double tempMinTheta = tempTheta - (Math.PI/2);
        		double tempMaxTheta = tempTheta + (Math.PI/2);
        		
        		//adds the range
        		//For maxThetas above pi * 2
        		if (tempMaxTheta > Math.PI * 2)
            	{
            		
            		double min1 = tempMinTheta;
            		double max1 = Math.PI * 2;
            		double min2 = 0;
            		double max2 = tempMaxTheta - (Math.PI * 2);
            		
            		restrictedRanges.add(new Range(min1, max1));
            		restrictedRanges.add(new Range(min2, max2));
            		
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min1=" + (min1 / Math.PI) + " max1=" + (max1 / Math.PI));
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min2=" + (min2 / Math.PI) + " max2=" + (max2 / Math.PI));
            		//input.next();
            	}
        		
        		else
        		{
        			restrictedRanges.add(new Range(tempMinTheta, tempMaxTheta));
        			//System.out.println("theta=" + (tempTheta / Math.PI) + " min=" + (tempMinTheta / Math.PI) + " max=" + (tempMaxTheta / Math.PI));
            		//input.next();
        		}
        	}
        	
        	//Converge overlapping restricted ranges (each range looks at the other ranges. If they overlap, one range adjusts its min and max and the other is removed)
        	for (int i = 0; i < restrictedRanges.size(); i++)
        	{
        		//The range that will remain
    			Range tempRange = restrictedRanges.get(i);
    			
        		for (int j = restrictedRanges.size() - 1; j > i; j--)
        		{
        			//The range that will be compared with tempRange and that will be removed if they overlap
        			Range comparedRange = restrictedRanges.get(j);
        			
        			//System.out.println("tempmin=" + (tempRange.getMin() / Math.PI) + " tempmax=" + (tempRange.getMax() / Math.PI));
        			//System.out.println("compmin=" + (comparedRange.getMin() / Math.PI) + " compmax=" + (comparedRange.getMax() / Math.PI));
            		//input.next();
        			
        			//if the ranges overlap, merge them
        			if ((tempRange.getMin() <= comparedRange.getMax() && tempRange.getMin() >= comparedRange.getMin()) ||
        				(comparedRange.getMin() <= tempRange.getMax() && comparedRange.getMin() >= tempRange.getMin()))
        			{
        				if (tempRange.getMin() > comparedRange.getMin())
        				{
        					tempRange.setMin(comparedRange.getMin());
        				}
        				
        				if (tempRange.getMax() < comparedRange.getMax())
        				{
        					tempRange.setMax(comparedRange.getMax());
        				}
        				
        				restrictedRanges.remove(j);
        			}
        		}
        	}
        	
        	
        	//Adjusts theta so that it is not in the restricted range
        	for (Range range : restrictedRanges)
        	{
        		if (theta > range.getMin() &&
        			theta < range.getMax())
        		{
        			if (theta < (range.getMin() + range.getMax()) / 2)
        			{
        				theta = range.getMin();
        			}
        			
        			else
        			{
        				theta = range.getMax();
        			}
        		}
        		
        		//System.out.println(restrictedRanges.size());
        		//System.out.println("min=" + (range.getMin() / Math.PI) + " max=" + (range.getMax() / Math.PI));
        		//System.out.println(theta / Math.PI);
        		//input.next();
        	}
        	
        	//If there are restricted ranges, determine theta using a set of allowed ranges
        	if (restrictedRanges.size() != 0)
        	{
	        	//Ranges of angles that are allowed (not restricted) (found by taking the opposite of the restricted ranges)
	        	ArrayList<Range> allowedRanges = new ArrayList<Range>();
	        	
	        	//Find the allowed ranges using the restricted ranges
	        	
	        	//first make sure the restricted ranges are in order from lowest to highest (insertion sort)
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		int j = i;
	        		
	        		while (j > 0 && restrictedRanges.get(j-1).getMin() > restrictedRanges.get(j).getMin())
	        		{
	        			//swaps j and j-1
	        			Range tempRange = restrictedRanges.get(j);
	        			restrictedRanges.set(j, restrictedRanges.get(j-1));
	        			restrictedRanges.set(j-1, tempRange);
	        			
	        			j--;
	        		}
	        	}
	        	
	        	//then find the allowed ranges (may be a less messy/wordy way to do this)
	        	
	        	//for when the restricted range doesn't start at zero
	        	if (restrictedRanges.get(0).getMin() != 0)
	        	{
	        		Range tempRange = new Range(0, restrictedRanges.get(0).getMin());
	        		allowedRanges.add(tempRange);
	        	}
	        	
	        	//adds the range between restricted ranges to allowed ranges
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(i-1).getMax(), restrictedRanges.get(i).getMin());
	        		allowedRanges.add(tempRange);
	        	}
	        	
	        	//for when the restricted range doesn't end at 2pi
	        	if (restrictedRanges.get(restrictedRanges.size() - 1).getMax() != (Math.PI * 2))
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(restrictedRanges.size() - 1).getMax(), (Math.PI * 2));
	        		allowedRanges.add(tempRange);
	        	}
	        	
	        	//finally, set theta to a random angle within the allowed ranges
	        	Range randomRange = allowedRanges.get((int) (Math.random() * allowedRanges.size()));
	        	theta = (Math.random() * (randomRange.getMax() - randomRange.getMin())) + randomRange.getMin();
        	}
        	*/
        	
        	//Find the allowed ranges (angleSpan of pi/2)
        	//ArrayList<Range> allowedRanges = FindAllowedRanges(collisionTiles, Math.PI / 2);
        	ArrayList<Range> tileRanges = FindAllowedRanges(collisionTiles, Math.PI / 2);
        	ArrayList<Range> mobRanges = FindAllowedRangesMob(collisionMobs, Math.PI / 2);
        	ArrayList<Range> allowedRanges = MergeRangesInclusive(tileRanges, mobRanges);
        	
        	if (tileRanges.size() != 0)
        	{
        		System.out.println("Tiles in Range");
        		
        		for (Range range : tileRanges)
        		{
        			System.out.println((range.getMin()/ Math.PI) + " " + (range.getMax() / Math.PI));
        		}
        		
        		input.next();
        	}
        	
        	//set theta to a random angle within the allowed ranges
        	if (allowedRanges.size() != 0)
        	{
	        	Range randomRange = allowedRanges.get((int) (Math.random() * allowedRanges.size()));
	        	theta = (Math.random() * (randomRange.getMax() - randomRange.getMin())) + randomRange.getMin();
        	}
        }
        
        //tells the program which tiles contain this mob, making collisions faster and easier to spot
        //starts on the left edge of the circle and works its way to the right, scanning from bottom to top to mark which tiles contains the mob
        public void UpdateBorders ()
        {
        	/*
        	//equation of a circle: (x-a)^2 + (y-b)^2 = r^2
        	//y = +-((r^2 - (x-a)^2)^.5 + b)
        	
        	float a = (this.x + radius) / 10;
        	float b = (this.y + radius) / 10;
        		
    		//For the leftmost end
    		float leftX = a - (radius/10);
    		float yRight = b;
    		float yLeft;
    		float yTop = (float) Math.sqrt (Math.pow ((double) (radius/10), 2) - Math.pow((double) ((int)(leftX - a)), 2)) + b;
    		
			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
			{
				if (j >= 0 && j < tiles.size() &&
    				(int) leftX >= 0 && (int) leftX < tiles.get((int) (j)).size() &&
    				!tiles.get(j).get((int)(leftX)).getMobsInside().contains(this))
				{
					tiles.get(j).get((int)(leftX)).AddMob(this);
					
					repaint();
					System.out.print ("L ");
					System.out.println ("radius=" + radius/10 + " a=" + a + " b=" + b + " i=" + leftX + " j=" + j + " yTop=" + yTop);
	        		System.out.println (tiles.get(j).get((int) (leftX - 1)));
	        		//input.next();
	        		
	        		 
				}
			}
    		
    		//For the middle part
    		for (int i = (int) (a - (radius/10)) + 2; i < a + (radius/10); i++)
    		{
    			yLeft = yRight;
    			yRight = (float) Math.sqrt (Math.pow ((double) (radius/10), 2) - Math.pow((double) (i - a), 2)) + b;
    			
    			if (yLeft > yRight)
    			{
    				yTop = yLeft;
    			}
    			
    			else
    			{
    				yTop = yRight;
    			}
    			
    			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
    			{
    				if (j >= 0 && j < tiles.size() &&
    					(int) (i - 1) >= 0 && (int) (i - 1) < tiles.get((int) (j)).size() &&
    					!tiles.get(j).get((int)(i - 1)).getMobsInside().contains(this))
    				{
    					tiles.get(j).get((int) (i - 1)).AddMob(this);
    					
    					
    					repaint();
    					System.out.print ("M ");
	    				System.out.println ("radius=" + radius/10 + " a=" + a + " b=" + b + " i=" + (i - 1) + " j=" + j + " yTop=" + yTop);
		        		System.out.println (tiles.get(j).get((int) (i - 1)));
		        		//input.next();
		        		
    				}
    			}
    		}
    		
			//For the rightmost end
			yTop = yRight;
			float rightX = a + (radius/10);
			
			//for the special case in which rightX would already be an integer. (Ex: rightX = 8.0, so the border ends up 1 too far to the right).
			if (rightX == (int) rightX)
			{
				rightX -= 1;
			}
			
			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
			{
				if (j >= 0 && j < tiles.size() &&
    				(int) rightX >= 0 && (int) rightX < tiles.get((int) (j)).size() &&
    				!tiles.get(j).get((int)(rightX)).getMobsInside().contains(this))
    				
    				
				{
					tiles.get(j).get((int) (rightX)).AddMob(this);
					
					repaint();
					System.out.print ("R ");
					System.out.println ("radius=" + radius/10 + " a=" + a + " b=" + b + " i=" + rightX + " j=" + j + " yTop=" + yTop);
		    		System.out.println (tiles.get(j).get((int) (rightX)));
		    		//input.next();
		    		
				}
				
				repaint();
				System.out.print ("R ");
				System.out.println ("radius=" + radius/10 + " a=" + a + " b=" + b + " i=" + rightX + " j=" + j + " yTop=" + yTop);
	    		//System.out.println (tiles.get(j).get((int) (rightX)));
	    		//input.next();
	    		 
			}
			*/
        	
        	//clear the list of collision tiles
        	while (collisionTiles.size() != 0)
        	{
        		collisionTiles.remove(0);
        	}
        	
        	//gets all tiles that contain the mob
        	ArrayList<Tile> tempTiles = getTilesInArea((float) getCenterX(), (float) getCenterY(), radius);
        	
        	//finds the solid tiles that the mob is in contact with
        	for (Tile tile : tempTiles)
        	{
        		if (tile.getType().equals("bedrock"))
        		{
        			collisionTiles.add(tile);
        		}
        		
        		//Use the collision tile to find collision mobs
        		for (Mob mob : tile.getMobsInside())
        		{
        			if (!collisionMobs.contains(mob) && !mob.equals(this))
        			{
        				collisionMobs.add(mob);
        			}
        		}
        		
        		//Add this mob to the tile's list of mobs inside
        		tile.getMobsInside().add(this);
        	}
        }
        
        //spawns a child in an open area next to the mob 
        //(finds an open area similarly to how it finds open paths during collisions)
        private void SpawnChild ()
        {
        	//radius that the mob searches to make sure the child can fit where it spawns
        	// (Should be the parent's radius + the child's diameter)
        	float searchRadius = radius * 3;
        	
        	ArrayList<Tile> nearTiles = getTilesInArea ((float) getCenterX(), (float) getCenterY(), searchRadius);
        	ArrayList<Mob> nearMobs = new ArrayList<Mob>();
        	
        	for (int i = nearTiles.size() - 1; i >= 0; i--)
        	{
        		Tile tempTile = nearTiles.get(i);
        		
        		//makes nearTiles only consist of solid tiles and in the search radius
        		if (!tempTile.getType().equals("bedrock") ||
        			(Math.sqrt(Math.pow(getCenterX() - tempTile.getCenterX(), 2) + Math.pow(getCenterY() - tempTile.getCenterY(), 2)) > searchRadius + (tempTile.getWidth() / 2)))
        		{
        			 nearTiles.remove(tempTile);
        			 //System.out.println("distance = " + (Math.sqrt(Math.pow(getCenterX() - tempTile.getCenterX(), 2) + Math.pow(getCenterY() - tempTile.getCenterY(), 2))));
        		}
        		
        		//add the near mobs using the near tiles (don't include this mob)
        		//	(Make sure the mob is actually in the search radius.)
        		//  (The distance from the parent to the object should be < parent radius + child diameter + object radius)
    			for (Mob mob : tempTile.getMobsInside())
    			{
    				if (!nearMobs.contains(mob) && !mob.equals(this) &&
    					(Math.sqrt(Math.pow(getCenterX() - mob.getCenterX(), 2) + Math.pow(getCenterY() - mob.getCenterY(), 2)) <= searchRadius + mob.getRadius()))
    				{
    					nearMobs.add(mob);
    					//mob.setColor(Color.green);
    					//repaint();

        				//input.next();
        				//mob.setColor(Color.blue);
        				//repaint();
    				}
    			}
    			
    			//Color tempColor = tempTile.getColor();
    			//tempTile.setColor(Color.red);
	  			 //repaint();
	  			 //input.next();
	  			 //tempTile.setColor(tempColor);
	  			 //repaint();
        	}
        	
        	//System.out.println(nearMobs.size());
        	
        	//This is very similar to FindAllowedRanges, but that method can't used varying angleSpans. Try to clean this up in the future
        	
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> restrictedRanges = new ArrayList<Range>();
        	
        	//Finds the restricted ranges for the near tiles
        	for (int i = nearTiles.size() - 1; i >=0; i--)
        	{
        		Tile tempTile = nearTiles.remove(i);        		
        		
        		//angle between the mob and the tile
        		//double tempTheta = Math.atan2(tempTile.getY() - this.y, tempTile.getX() - this.x);
        		double tempTheta = Math.atan2(tempTile.getCenterY() - getCenterY(), tempTile.getCenterX() - getCenterX());
        		
        		//law of cosine
        		//cos(A) = (b^2 + c^2 - a^2) / (2bc)
        		//a = distance from object to child, b = distance from parent to child, c = distance from parent to object (pythagorean theorem)
        		double a = tempTile.getRadius() + radius;
        		double b = radius * 2;
        		double c = Math.sqrt(Math.pow(getCenterX() - tempTile.getCenterX(), 2) + Math.pow(getCenterY() - tempTile.getCenterY(), 2));
        		double angleSpan = Math.acos(((b*b) + (c*c) - (a*a)) / (2*b*c));
        		
        		//System.out.println("a: " + a + " b: " + b + " c: " + c);
        		//System.out.println("  Tile span: " + ((b*b) + (c*c) - (a*a)) / (2*b*c));
        		
        		//Makes sure that min theta will be positive
        		if (tempTheta - angleSpan < 0)
        		{
        			tempTheta += (Math.PI * 2);
        		}
        		//the min and max thetas given the tempTheta (covers 90 degrees to the left and right of tempTheta)
        		double tempMinTheta = tempTheta - angleSpan;
        		double tempMaxTheta = tempTheta + angleSpan;
        		
        		//adds the range
        		//For maxThetas above pi * 2
        		if (tempMaxTheta > Math.PI * 2)
            	{
            		
            		double min1 = tempMinTheta;
            		double max1 = Math.PI * 2;
            		double min2 = 0;
            		double max2 = tempMaxTheta - (Math.PI * 2);
            		
            		restrictedRanges.add(new Range(min1, max1));
            		restrictedRanges.add(new Range(min2, max2));
            		
            		//System.out.println("a: " + a + " b: " + b + " c: " + c);
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min1=" + (min1 / Math.PI) + " max1=" + (max1 / Math.PI));
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min2=" + (min2 / Math.PI) + " max2=" + (max2 / Math.PI));
            		//input.next();
            	}
        		
        		else
        		{
        			restrictedRanges.add(new Range(tempMinTheta, tempMaxTheta));
        			
        			//System.out.println("a: " + a + " b: " + b + " c: " + c);
        			//System.out.println("theta=" + (tempTheta / Math.PI) + " min=" + (tempMinTheta / Math.PI) + " max=" + (tempMaxTheta / Math.PI));
            		//input.next();
        		}
        	}
        	
        	//Finds the restricted ranges for all the near mobs
        	for (int i = nearMobs.size() - 1; i >=0; i--)
        	{
        		Mob tempMob = nearMobs.remove(i);        		
        		
        		//angle between the mob and the tile
        		//double tempTheta = Math.atan2(tempMob.getY() - this.y, tempMob.getX() - this.x);
        		double tempTheta = Math.atan2(tempMob.getCenterY() - getCenterY(), tempMob.getCenterX() - getCenterX());
        		
        		//law of cosine
        		//cos(A) = (b^2 + c^2 - a^2) / (2bc)
        		//a = distance from object to child, b = distance from parent to child, c = distance from parent to object (pythagorean theorem)
        		double a = (double)(tempMob.getRadius()) + radius;
        		double b = radius * 2;
        		double c = Math.sqrt(Math.pow(getCenterX() - tempMob.getCenterX(), 2) + Math.pow(getCenterY() - tempMob.getCenterY(), 2));
        		double angleSpan = Math.acos(((b*b) + (c*c) - (a*a)) / (2*b*c));
        		
        		//System.out.println("a: " + a + " b: " + b + " c: " + c);
        		//System.out.println("  Mob span: " + ((b*b) + (c*c) - (a*a)) / (2*b*c));
        		
        		//Makes sure that min theta will be positive
        		if (tempTheta - angleSpan < 0)
        		{
        			tempTheta += (Math.PI * 2);
        		}
        		//the min and max thetas given the tempTheta (covers 90 degrees to the left and right of tempTheta)
        		double tempMinTheta = tempTheta - angleSpan;
        		double tempMaxTheta = tempTheta + angleSpan;
        		
        		//adds the range
        		//For maxThetas above pi * 2
        		if (tempMaxTheta > Math.PI * 2)
            	{
            		
            		double min1 = tempMinTheta;
            		double max1 = Math.PI * 2;
            		double min2 = 0;
            		double max2 = tempMaxTheta - (Math.PI * 2);
            		
            		restrictedRanges.add(new Range(min1, max1));
            		restrictedRanges.add(new Range(min2, max2));
            		
            		//System.out.println("a: " + a + " b: " + b + " c: " + c);
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min1=" + (min1 / Math.PI) + " max1=" + (max1 / Math.PI));
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min2=" + (min2 / Math.PI) + " max2=" + (max2 / Math.PI));
            		//input.next();
            	}
        		
        		else
        		{
        			restrictedRanges.add(new Range(tempMinTheta, tempMaxTheta));
        			
        			//System.out.println("a: " + a + " b: " + b + " c: " + c);
        			//System.out.println("theta=" + (tempTheta / Math.PI) + " min=" + (tempMinTheta / Math.PI) + " max=" + (tempMaxTheta / Math.PI));
            		//input.next();
        		}
        	}
        	
        	/*
        	for (Range range : restrictedRanges)
        	{
        		System.out.println((range.getMin() / Math.PI) + " " + (range.getMax() / Math.PI));
        	}
        	*/
        	
        	//Converge overlapping restricted ranges (each range looks at the other ranges. If they overlap, one range adjusts its min and max and the other is removed)
        	MergeRangesExclusive (restrictedRanges);
        	
        	/*
        	for (Range range : restrictedRanges)
        	{
        		System.out.println((range.getMin() / Math.PI) + " " + (range.getMax() / Math.PI));
        	}
        	*/
        	//Ranges of angles that are allowed (not restricted) (found by taking the opposite of the restricted ranges)
	        ArrayList<Range> allowedRanges = new ArrayList<Range>();
        	
        	//If there are restricted ranges, determine theta using a set of allowed ranges
        	if (restrictedRanges.size() != 0)
        	{
	        	//Find the allowed ranges using the restricted ranges
	        	
	        	//first make sure the restricted ranges are in order from lowest to highest (insertion sort)
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		int j = i;
	        		
	        		while (j > 0 && restrictedRanges.get(j-1).getMin() > restrictedRanges.get(j).getMin())
	        		{
	        			//swaps j and j-1
	        			Range tempRange = restrictedRanges.get(j);
	        			restrictedRanges.set(j, restrictedRanges.get(j-1));
	        			restrictedRanges.set(j-1, tempRange);
	        			
	        			j--;
	        		}
	        	}
	        	
	        	//then find the allowed ranges (may be a less messy/wordy way to do this)
	        	
	        	//for when the restricted range doesn't start at zero
	        	if (restrictedRanges.get(0).getMin() != 0)
	        	{
	        		Range tempRange = new Range(0, restrictedRanges.get(0).getMin());
	        		allowedRanges.add(tempRange);
	        	}
	        	
	        	//adds the range between restricted ranges to allowed ranges
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(i-1).getMax(), restrictedRanges.get(i).getMin());
	        		allowedRanges.add(tempRange);
	        	}
	        	
	        	//for when the restricted range doesn't end at 2pi
	        	if (restrictedRanges.get(restrictedRanges.size() - 1).getMax() != (Math.PI * 2))
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(restrictedRanges.size() - 1).getMax(), (Math.PI * 2));
	        		allowedRanges.add(tempRange);
	        	}
        	}
        	
        	else
        	{
        		allowedRanges.add(new Range(0, Math.PI * 2));
        	}
        	/*
        	for (Range range : allowedRanges)
        	{
        		System.out.println((range.getMin() / Math.PI) + " " + (range.getMax() / Math.PI));
        	}
        	*/
        	//The theta that the child will be place relative to the parent
        	double placementTheta = 0;
        	
        	//set the placement theta to a random angle within the allowed ranges
        	if (allowedRanges.size() != 0)
        	{
	        	Range randomRange = allowedRanges.get((int) (Math.random() * allowedRanges.size()));
	        	placementTheta = (Math.random() * (randomRange.getMax() - randomRange.getMin())) + randomRange.getMin();
	        	
	        	//Place the child next to the parent using the placement theta
	        	float childX = (float) ((radius*2) * Math.cos(placementTheta) + this.x);
	        	float childY = (float) ((radius*2) * Math.sin(placementTheta) + this.y);
	        	
	        	mobs.add(new Mob (childX, childY, this.width, this.height));
	        	
	        	//color = Color.green;
	        	

				//System.out.println("Spawn");
				//System.out.println("Cx: " + childX + " Cy: " + childY);
				
	        	//input.next();
        	}
        }
        
        //Uses this mob's path and the solid tiles and mobs that are in its path and returns the position in which this mob first touches one of those mobs/tiles.
        //(Used to make the mobs stop before going through any other mobs/tiles)
        public Coords getCollisionPosition(ArrayList<Tile> inTiles, ArrayList<Mob> inMobs)
        {
        	//The path the mob takes
        	//slope = tan(theta)
        	Line pathLine = new Line (new Coords (getCenterX(), getCenterY()), Math.tan(theta));
        	
        	//Current position of the mob
        	Coords currentPosition = new Coords (getCenterX(), getCenterY());
        	
        	//The smallest distance so far between where the mob is now and where it will be if it collides.
        	double lowestDist = MAX_DOUBLE_VALUE;
        	
        	//The hypothetical mob position that is nearest to its current position.
        	Coords nearestPosition = new Coords ();
        	
        	//Find where the mob would be if it hit the tile using trigonometry
        	for (Tile tile : inTiles)
        	{
        		//The coordinates of the tile.
        		Coords tileCoords = new Coords (tile.getCenterX(), tile.getCenterY());
        		
	        	//The perpendicular line from the tile to the path line
	        	Line perpLine = new Line(tileCoords, (-1.0 / pathLine.getSlope()));
	        	
	        	//System.out.println(pathLine.toString());
	        	//System.out.println(perpLine.toString());
	        	//input.next();
	        	
	        	//The point where pathLine and perpLine intersect
	        	Coords intersect = pathLine.FindIntersect(perpLine);
	        	
	        	//Distance from tile to the pathLine
	        	double a = intersect.getDistanceFrom(tileCoords);
	        	
	        	//the hypothetical distance from the center of the tile to the center of the mob.
	        	double r = (double) (radius + tile.getRadius());
	        	
	        	//Distance from the intersect point to the mob's position if the mob were to make contact with the tile.
	        	double b = Math.sqrt(Math.pow(r, 2) - Math.pow(a, 2));
	        	
	        	//Find where the mob would be if it collided with the tile
	        	double yDist = b * Math.sin(theta);
	        	double xDist = b * Math.cos(theta);
	        	Coords mobPosition = new Coords (intersect.getX() - xDist, intersect.getY() - yDist);
	        	
	        	//Check to see if this is the nearest position to the mob so far.
	        	if (mobPosition.getDistanceFrom(currentPosition) < lowestDist)
	        	{
	        		lowestDist = mobPosition.getDistanceFrom(currentPosition);
	        		nearestPosition = mobPosition;
	        	}
	        	
	        	//System.out.println(lowestDist);
	        	//System.out.println(mobPosition.getDistanceFrom(currentPosition));
	        	//input.next();
        	}
        	
        	//Find where the mob would be if it hit the other mob using trigonometry
        	for (Mob mob : inMobs)
        	{
        		//The coordinates of the mob.
        		Coords mobCoords = new Coords (mob.getCenterX(), mob.getCenterY());
        		
	        	//The perpendicular line from the mob to the path line
	        	Line perpLine = new Line(mobCoords, pathLine.getSlope());
	        	
	        	//The point where pathLine and perpLine intersect
	        	Coords intersect = pathLine.FindIntersect(perpLine);
	        	
	        	//Distance from mob to the pathLine
	        	double a = intersect.getDistanceFrom(mobCoords);
	        	
	        	//the hypothetical distance from the center of the mob to the center of the other mob.
	        	double r = (double) (radius + mob.getRadius());
	        	
	        	//Distance from the intersect point to the mob's position if the mob were to make contact with the tile.
	        	double b = Math.sqrt(Math.pow(r, 2) - Math.pow(a, 2));
	        	
	        	//Find where the mob would be if it collided with the tile
	        	double yDist = b * Math.sin(theta);
	        	double xDist = b * Math.cos(theta);
	        	Coords mobPosition = new Coords (intersect.getX() - xDist, intersect.getY() - yDist);
	        	
	        	//Check to see if this is the nearest position to the mob so far.
	        	if (mobPosition.getDistanceFrom(currentPosition) < lowestDist)
	        	{
	        		lowestDist = mobPosition.getDistanceFrom(currentPosition);
	        		nearestPosition = mobPosition;
	        	}
        	}
        	
        	return nearestPosition;
        }
        
        //Finds all the tiles within a circular area (input is in regular units and is converted to tile units, 1 tile = 10 regular) (maybe make static) (maybe make the input a circle instead of floats)
        public ArrayList<Tile> getTilesInArea (float inX, float inY, float inR)
        {
        	//list of tiles in the circle (output)
        	ArrayList<Tile> output = new ArrayList<Tile>();
        	
        	//equation of a circle: (x-a)^2 + (y-b)^2 = r^2
        	//y = +-((r^2 - (x-a)^2)^.5 + b)
        	
        	float a = inX / TILE_SIZE;
        	float b = inY / TILE_SIZE;
        	inR = inR / TILE_SIZE;
        		
    		//For the leftmost end
    		float leftX = a - inR;
    		float yRight = b;
    		float yLeft;
    		float yTop = (float) Math.sqrt (Math.pow ((double) inR, 2) - Math.pow((double) ((int)(leftX - a)), 2)) + b;
    		
			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
			{
				if (j >= 0 && j < tiles.size() &&
    				(int) leftX >= 0 && (int) leftX < tiles.get((int) (j)).size())
				{
					output.add(tiles.get(j).get((int)(leftX)));
					/*
					repaint();
					System.out.print ("L ");
					System.out.println ("radius=" + inR + " a=" + a + " b=" + b + " i=" + leftX + " j=" + j + " yTop=" + yTop);
	        		System.out.println (output.get(j).get((int) (leftX - 1)));
	        		//input.next();
	        		*/
	        		 
				}
			}
    		
    		//For the middle part
    		for (int i = (int) (a - inR) + 2; i < a + inR; i++)
    		{
    			yLeft = yRight;
    			yRight = (float) Math.sqrt (Math.pow ((double) inR, 2) - Math.pow((double) (i - a), 2)) + b;
    			
    			if (yLeft > yRight)
    			{
    				yTop = yLeft;
    			}
    			
    			else
    			{
    				yTop = yRight;
    			}
    			
    			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
    			{
    				if (j >= 0 && j < tiles.size() &&
    					(int) (i - 1) >= 0 && (int) (i - 1) < tiles.get((int) (j)).size())
    				{
    					output.add(tiles.get(j).get((int) (i - 1)));
    					
    					/*
    					repaint();
    					System.out.print ("M ");
	    				System.out.println ("radius=" + inR + " a=" + a + " b=" + b + " i=" + (i - 1) + " j=" + j + " yTop=" + yTop);
		        		System.out.println (output.get(j).get((int) (i - 1)));
		        		//input.next();
		        		*/
    				}
    			}
    		}
    		
			//For the rightmost end
			yTop = yRight;
			float rightX = a + inR;
			
			//for the special case in which rightX would already be an integer. (Ex: rightX = 8.0, so the border ends up 1 too far to the right).
			if (rightX == (int) rightX)
			{
				rightX -= 1;
			}
			
			for (int j = (int)((2 * b) - yTop); j <= yTop; j++)
			{
				if (j >= 0 && j < tiles.size() &&
    				(int) rightX >= 0 && (int) rightX < tiles.get((int) (j)).size())
    				
    				
				{
					output.add(tiles.get(j).get((int) (rightX)));
					/*
					repaint();
					System.out.print ("R ");
					System.out.println ("radius=" + inR + " a=" + a + " b=" + b + " i=" + rightX + " j=" + j + " yTop=" + yTop);
		    		System.out.println (output.get(j).get((int) (rightX)));
		    		//input.next();
		    		*/
				}
				/*
				repaint();
				System.out.print ("R ");
				System.out.println ("radius=" + inR + " a=" + a + " b=" + b + " i=" + rightX + " j=" + j + " yTop=" + yTop);
	    		//System.out.println (output.get(j).get((int) (rightX)));
	    		//input.next();
	    		 */
			}
			
			/*
			for (Tile tile : output)
			{
				tile.setColor(Color.green);
			}
			*/
			return output;
        }
        
        //Finds the tiles in a path area (scans from left to right, lowest y to highest y)
        public ArrayList<Tile> getTilesInArea (PathArea inArea)
        {
        	//Tiles in the area that will be outputted
        	ArrayList<Tile> outputTiles = new ArrayList<Tile>();
        	
        	//Copy of the in Area (that way the original area isn't changed when this one is)
        	PathArea tempArea = new PathArea (inArea);
        	
        	//convert the inArea from grid units to tiles (10 units = 1 tile)
        	for (Line side : tempArea.getCircleSides())
        	{
        		side.setInt(side.getInt() / ((double) TILE_SIZE));
        	}
        	
        	//convert the inArea from grid units to tiles (10 units = 1 tile)
        	for (Line side : tempArea.getBridgeSides())
        	{
        		side.setInt(side.getInt() / ((double) TILE_SIZE));
        	}
        	
        	//Find the two bottom and top lines of the rectangle
        	Line lowerCircleSide = new Line();
        	Line lowerBridgeSide = new Line();
        	Line upperCircleSide = new Line();
        	Line upperBridgeSide = new Line();
        	
        	//for the lowest circle side
        	if (tempArea.getCircleSides().get(0).getInt() < tempArea.getCircleSides().get(1).getInt())
        	{
        		lowerCircleSide = tempArea.getCircleSides().get(0);
        		upperCircleSide = tempArea.getCircleSides().get(1);
        	}
        	
        	else
        	{
        		lowerCircleSide = tempArea.getCircleSides().get(1);
        		upperCircleSide = tempArea.getCircleSides().get(0);
        	}
        	
        	//for the lowest bridge side
        	if (tempArea.getBridgeSides().get(0).getInt() < tempArea.getBridgeSides().get(1).getInt())
        	{
        		lowerBridgeSide = tempArea.getBridgeSides().get(0);
        		upperBridgeSide = tempArea.getBridgeSides().get(1);
        	}
        	
        	else
        	{
        		lowerBridgeSide = tempArea.getBridgeSides().get(1);
        		upperBridgeSide = tempArea.getBridgeSides().get(0);
        	}
        	
        	//Find the upper and lower corners of the path area (used as starting and stopping points)
        	Coords lowerPoint = lowerCircleSide.FindIntersect(lowerBridgeSide);
        	Coords upperPoint = upperCircleSide.FindIntersect(upperBridgeSide);
        	
        	//The left and right edges of the area that are scanned (the lower edge with the negative slope will be the left one)
        	Line lowerLeftEdge;
        	Line upperLeftEdge;
        	Line lowerRightEdge;
        	Line upperRightEdge;
        	
        	if (lowerBridgeSide.getSlope() < lowerCircleSide.getSlope())
        	{
        		lowerLeftEdge = lowerBridgeSide;
        		upperLeftEdge = upperCircleSide;
        		
        		lowerRightEdge = lowerCircleSide;
        		upperRightEdge = upperBridgeSide;
        	}
        	
        	else
        	{
        		lowerLeftEdge = lowerCircleSide;
        		upperLeftEdge = upperBridgeSide;
        		
        		
        		lowerRightEdge = lowerBridgeSide;
        		upperRightEdge = upperCircleSide;
        	}
        	
        	//The edges that are used during the scanning (change from lower to upper when the scan passes a corner)
        	Line leftEdge = lowerLeftEdge;
        	Line rightEdge = lowerRightEdge;
        	
        	//Scan the area (j = row, i = column)
        	for (int j = (int) lowerPoint.getY(); j <= upperPoint.getY(); j++)
        	{
        		//Make sure that the correct edges are being used
        		//left should be the most rightward of the left edges and vice versa
        		
        		//for the left edges
        		if (!leftEdge.equals(upperLeftEdge) &&
        			upperLeftEdge.getXForY(j) > lowerLeftEdge.getXForY(j))
        		{
        			leftEdge = upperLeftEdge;
        		}
        		
        		//for the right edges
        		if (!rightEdge.equals(upperRightEdge) &&
        			upperRightEdge.getXForY(j) < lowerRightEdge.getXForY(j))
        		{
        			rightEdge = upperRightEdge;
        		}
        		
        		//Scan the columns
        		for (int i = (int) leftEdge.getXForY(j); i <= (int) rightEdge.getXForY(j); i++)
        		{
        			//Add the tile if it exists in tiles
        			if (j >= 0 && j < tiles.size() &&
        				i >= 0 && i < tiles.get(j).size())
        			{
        				outputTiles.add(tiles.get(j).get(i));
        			}
        			/*
        			System.out.println (j + " " + i);
                	tiles.get(j).get(i).setColor(Color.green);
                	repaint();
                	input.next();
                	*/
        		}
        	}
        	
        	return outputTiles;
        }
        
        //takes in a list of near tiles, finds the restricted angles (angle from this mob to the tile +/- the angle span (pi/2 for collisions))
        //	and inverts this to find the allowed angles
        public ArrayList<Range> FindAllowedRanges (ArrayList<Tile> inTiles, double angleSpan)
        {
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> restrictedRanges = FindRestrictedRanges (inTiles, angleSpan);
        	
        	//Ranges of angles that are allowed (not restricted) (found by taking the opposite of the restricted ranges)
	        	ArrayList<Range> outputRanges = new ArrayList<Range>();
        	
        	//If there are restricted ranges, determine theta using a set of allowed ranges
        	if (restrictedRanges.size() != 0)
        	{
	        	//Find the allowed ranges using the restricted ranges
	        	
	        	//first make sure the restricted ranges are in order from lowest to highest (insertion sort)
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		int j = i;
	        		
	        		while (j > 0 && restrictedRanges.get(j-1).getMin() > restrictedRanges.get(j).getMin())
	        		{
	        			//swaps j and j-1
	        			Range tempRange = restrictedRanges.get(j);
	        			restrictedRanges.set(j, restrictedRanges.get(j-1));
	        			restrictedRanges.set(j-1, tempRange);
	        			
	        			j--;
	        		}
	        	}
	        	
	        	//then find the allowed ranges (may be a less messy/wordy way to do this)
	        	
	        	//for when the restricted range doesn't start at zero
	        	if (restrictedRanges.get(0).getMin() != 0)
	        	{
	        		Range tempRange = new Range(0, restrictedRanges.get(0).getMin());
	        		outputRanges.add(tempRange);
	        	}
	        	
	        	//adds the range between restricted ranges to allowed ranges
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(i-1).getMax(), restrictedRanges.get(i).getMin());
	        		outputRanges.add(tempRange);
	        	}
	        	
	        	//for when the restricted range doesn't end at 2pi
	        	if (restrictedRanges.get(restrictedRanges.size() - 1).getMax() != (Math.PI * 2))
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(restrictedRanges.size() - 1).getMax(), (Math.PI * 2));
	        		outputRanges.add(tempRange);
	        	}
        	}
        	
        	else
        	{
        		outputRanges.add(new Range(0, Math.PI * 2));
        	}
        	
        	return outputRanges;
        }
        
      //takes in a list of near mobs, finds the restricted angles (angle from this mob to the tile +/- the angle span (pi/2 for collisions))
        //	and inverts this to find the allowed angles
        public ArrayList<Range> FindAllowedRangesMob (ArrayList<Mob> inMobs, double angleSpan)
        {
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> restrictedRanges = FindRestrictedRangesMob (inMobs, angleSpan);
        	
        	//Ranges of angles that are allowed (not restricted) (found by taking the opposite of the restricted ranges)
	        	ArrayList<Range> outputRanges = new ArrayList<Range>();
        	
        	//If there are restricted ranges, determine theta using a set of allowed ranges
        	if (restrictedRanges.size() != 0)
        	{
	        	//Find the allowed ranges using the restricted ranges
	        	
	        	//first make sure the restricted ranges are in order from lowest to highest (insertion sort)
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		int j = i;
	        		
	        		while (j > 0 && restrictedRanges.get(j-1).getMin() > restrictedRanges.get(j).getMin())
	        		{
	        			//swaps j and j-1
	        			Range tempRange = restrictedRanges.get(j);
	        			restrictedRanges.set(j, restrictedRanges.get(j-1));
	        			restrictedRanges.set(j-1, tempRange);
	        			
	        			j--;
	        		}
	        	}
	        	
	        	//then find the allowed ranges (may be a less messy/wordy way to do this)
	        	
	        	//for when the restricted range doesn't start at zero
	        	if (restrictedRanges.get(0).getMin() != 0)
	        	{
	        		Range tempRange = new Range(0, restrictedRanges.get(0).getMin());
	        		outputRanges.add(tempRange);
	        	}
	        	
	        	//adds the range between restricted ranges to allowed ranges
	        	for (int i = 1; i < restrictedRanges.size(); i++)
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(i-1).getMax(), restrictedRanges.get(i).getMin());
	        		outputRanges.add(tempRange);
	        	}
	        	
	        	//for when the restricted range doesn't end at 2pi
	        	if (restrictedRanges.get(restrictedRanges.size() - 1).getMax() != (Math.PI * 2))
	        	{
	        		Range tempRange = new Range(restrictedRanges.get(restrictedRanges.size() - 1).getMax(), (Math.PI * 2));
	        		outputRanges.add(tempRange);
	        	}
        	}
        	
        	else
        	{
        		outputRanges.add(new Range(0, Math.PI * 2));
        	}
        	
        	return outputRanges;
        }
        
        //Take in a set of near tiles and an angle span and returns a set of restricted ranges
        public ArrayList<Range> FindRestrictedRanges (ArrayList<Tile> inTiles, double angleSpan)
        {
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> outputRanges = new ArrayList<Range>();
        	
        	for (int i = inTiles.size() - 1; i >=0; i--)
        	{
        		Tile tempTile = inTiles.remove(i);        		
        		
        		//angle between the mob and the tile
        		//double tempTheta = Math.atan2(tempTile.getY() - this.y, tempTile.getX() - this.x);
        		double tempTheta = Math.atan2(tempTile.getCenterY() - getCenterY(), tempTile.getCenterX() - getCenterX());
        		
        		//Makes sure that min theta will be positive
        		if (tempTheta - angleSpan < 0)
        		{
        			tempTheta += (Math.PI * 2);
        		}
        		
        		//the min and max thetas given the tempTheta (covers 90 degrees to the left and right of tempTheta)
        		double tempMinTheta = tempTheta - angleSpan;
        		double tempMaxTheta = tempTheta + angleSpan;
        		
        		//adds the range
        		//For maxThetas above pi * 2
        		if (tempMaxTheta > Math.PI * 2)
            	{
            		
            		double min1 = tempMinTheta;
            		double max1 = Math.PI * 2;
            		double min2 = 0;
            		double max2 = tempMaxTheta - (Math.PI * 2);
            		
            		outputRanges.add(new Range(min1, max1));
            		outputRanges.add(new Range(min2, max2));
            		
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min1=" + (min1 / Math.PI) + " max1=" + (max1 / Math.PI));
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min2=" + (min2 / Math.PI) + " max2=" + (max2 / Math.PI));
            		//input.next();
            	}
        		
        		else
        		{
        			outputRanges.add(new Range(tempMinTheta, tempMaxTheta));
        			//System.out.println("theta=" + (tempTheta / Math.PI) + " min=" + (tempMinTheta / Math.PI) + " max=" + (tempMaxTheta / Math.PI));
            		//input.next();
        		}
        	}
        	
        	/*for (int i = outputRanges.size() - 2; i >= 0; i--)
        	{
        		//The range that will remain
    			Range tempRange = outputRanges.get(i);
    			
        		for (int j = outputRanges.size() - 1; j > i; j--)
        		{
        			//The range that will be compared with tempRange and that will be removed if they overlap
        			Range comparedRange = outputRanges.get(j);
        			
        			//System.out.println("tempmin=" + (tempRange.getMin() / Math.PI) + " tempmax=" + (tempRange.getMax() / Math.PI));
        			//System.out.println("compmin=" + (comparedRange.getMin() / Math.PI) + " compmax=" + (comparedRange.getMax() / Math.PI));
            		//input.next();
        			
        			//if the ranges overlap, merge them
        			if ((tempRange.getMin() <= comparedRange.getMax() && tempRange.getMin() >= comparedRange.getMin()) ||
        				(comparedRange.getMin() <= tempRange.getMax() && comparedRange.getMin() >= tempRange.getMin()))
        			{
        				if (tempRange.getMin() > comparedRange.getMin())
        				{
        					tempRange.setMin(comparedRange.getMin());
        				}
        				
        				if (tempRange.getMax() < comparedRange.getMax())
        				{
        					tempRange.setMax(comparedRange.getMax());
        				}
        				
        				outputRanges.remove(j);
        			}
        		}
        	}
        	
        	*/
        	
        	//Converge overlapping restricted ranges (each range looks at the other ranges. If they overlap, one range adjusts its min and max and the other is removed)
        	MergeRangesExclusive (outputRanges);
        	
        	return outputRanges;
        }
        
        //Take in a set of near tiles and an angle span and returns a set of restricted ranges
        public ArrayList<Range> FindRestrictedRangesMob (ArrayList<Mob> inMobs, double angleSpan)
        {
        	//List of all the direction ranges that the mob may not head towards (otherwise it will phase through another object)
        	ArrayList<Range> outputRanges = new ArrayList<Range>();
        	
        	for (int i = inMobs.size() - 1; i >=0; i--)
        	{
        		Mob tempMob = inMobs.remove(i);        		
        		
        		//angle between the mob and the tile
        		//double tempTheta = Math.atan2(tempTile.getY() - this.y, tempTile.getX() - this.x);
        		double tempTheta = Math.atan2(tempMob.getCenterY() - getCenterY(), tempMob.getCenterX() - getCenterX());
        		
        		//Makes sure that min theta will be positive
        		if (tempTheta - angleSpan < 0)
        		{
        			tempTheta += (Math.PI * 2);
        		}
        		
        		//the min and max thetas given the tempTheta (covers 90 degrees to the left and right of tempTheta)
        		double tempMinTheta = tempTheta - angleSpan;
        		double tempMaxTheta = tempTheta + angleSpan;
        		
        		//adds the range
        		//For maxThetas above pi * 2
        		if (tempMaxTheta > Math.PI * 2)
            	{
            		
            		double min1 = tempMinTheta;
            		double max1 = Math.PI * 2;
            		double min2 = 0;
            		double max2 = tempMaxTheta - (Math.PI * 2);
            		
            		outputRanges.add(new Range(min1, max1));
            		outputRanges.add(new Range(min2, max2));
            		
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min1=" + (min1 / Math.PI) + " max1=" + (max1 / Math.PI));
            		//System.out.println("theta=" + (tempTheta / Math.PI) + " min2=" + (min2 / Math.PI) + " max2=" + (max2 / Math.PI));
            		//input.next();
            	}
        		
        		else
        		{
        			outputRanges.add(new Range(tempMinTheta, tempMaxTheta));
        			//System.out.println("theta=" + (tempTheta / Math.PI) + " min=" + (tempMinTheta / Math.PI) + " max=" + (tempMaxTheta / Math.PI));
            		//input.next();
        		}
        	}
        	
        	/*for (int i = outputRanges.size() - 2; i >= 0; i--)
        	{
        		//The range that will remain
    			Range tempRange = outputRanges.get(i);
    			
        		for (int j = outputRanges.size() - 1; j > i; j--)
        		{
        			//The range that will be compared with tempRange and that will be removed if they overlap
        			Range comparedRange = outputRanges.get(j);
        			
        			//System.out.println("tempmin=" + (tempRange.getMin() / Math.PI) + " tempmax=" + (tempRange.getMax() / Math.PI));
        			//System.out.println("compmin=" + (comparedRange.getMin() / Math.PI) + " compmax=" + (comparedRange.getMax() / Math.PI));
            		//input.next();
        			
        			//if the ranges overlap, merge them
        			if ((tempRange.getMin() <= comparedRange.getMax() && tempRange.getMin() >= comparedRange.getMin()) ||
        				(comparedRange.getMin() <= tempRange.getMax() && comparedRange.getMin() >= tempRange.getMin()))
        			{
        				if (tempRange.getMin() > comparedRange.getMin())
        				{
        					tempRange.setMin(comparedRange.getMin());
        				}
        				
        				if (tempRange.getMax() < comparedRange.getMax())
        				{
        					tempRange.setMax(comparedRange.getMax());
        				}
        				
        				outputRanges.remove(j);
        			}
        		}
        	}
        	
        	*/
        	
        	//Converge overlapping restricted ranges (each range looks at the other ranges. If they overlap, one range adjusts its min and max and the other is removed)
        	MergeRangesExclusive (outputRanges);
        	
        	return outputRanges;
        }
    }
    
//Tile class
    class Tile extends Rectangle2D.Float
    {
    	private Color color;
    	private String type;
    	
    	//Stores what mobs are inside the tile. Used for mob on mob collisions
    	private ArrayList<Mob> mobsInside = new ArrayList<Mob>();
    	
    	public Tile(float x, float y, float width, float height) 
    	{
            setFrame(x, y, width, height);
            type = "barren";
            color = new Color(234, 168, 47);
        }
        
        public Tile (float x, float y, float width, float height, String inType)
        {
        	setFrame(x, y, width, height);
        	setType(inType);
        }
        
        public String getType ()
        {
        	return type;
        }
        
        public void setType (String inType)
        {
        	type = inType;
        	
        	if (inType.equals("barren"))
        	{
        		color = new Color(234, 168, 47);
        	}
        	
        	else if (inType.equals("bedrock"))
        	{
        		color = Color.black;
        	}
        	
        	else
        	{
        		type = "barren";
        		color = new Color(234, 168, 47);
        	}
        }
        
        public Color getColor ()
        {
        	return color;
        }
        
        public void setColor (Color inColor)
        {
        	color = inColor;
        }
        
        //returns the "radius" of the tile (make more accurate in the future)
        public double getRadius ()
        {
        	return (height / 2) * Math.sqrt(2);
        }
        
        public ArrayList<Mob> getMobsInside ()
        {
        	return mobsInside;
        }
        
        //Makes sure that all mobs considered inside are actually inside
        public void CheckMobsInside ()
        {
        	for (int i = mobsInside.size() - 1; i >= 0; i--)
        	{
        		Mob mob = mobsInside.get(i);
        		
        		if (!mob.intersects(this))
				{
					mobsInside.remove(mob);
				}
        	}
        }
    }
    
//Angle Range Class (a range of angles, with a minimum angle and a maximum angle)
    class Range
    {
    	double minTheta;
    	double maxTheta;
    	
    	public Range (double inMin, double inMax)
    	{
    		minTheta = inMin;
    		maxTheta = inMax;
    	}
    	
    	public double getMin ()
    	{
    		return minTheta;
    	}
    	
    	public void setMin (double inMin)
    	{
    		minTheta = inMin;
    	}
    	
    	public double getMax ()
    	{
    		return maxTheta;
    	}
    	
    	public void setMax (double inMax)
    	{
    		maxTheta = inMax;
    	}
    	
    	public void print ()
    	{
    		String output = "[" + minTheta + ", " + maxTheta + "]";
    		System.out.println(output);
    	}
    }

//Class for the area a circle passes through when it moves from point A to B (represented by a rectangle)
    class PathArea
    {
    	//sides that go through the circles
    	ArrayList<Line> circleSides = new ArrayList<Line>();
    	
    	//sides that bridge between the circles
    	ArrayList<Line> bridgeSides = new ArrayList<Line>();
    	
    	//constructor takes in the starting circle and the destination circle and finds to path area
    	public PathArea (Ellipse2D.Float start, Ellipse2D.Float dest)
    	{
    		//Find the slope of the line from start to dest
    		double tempSlope = (start.getCenterY() - dest.getCenterY()) / (start.getCenterX() - dest.getCenterX());
    		
    		//perpendicular to tempslope
    		double perpSlope = -1 / tempSlope;
    		
    		//lines through the start and dest circle
    		circleSides.add(new Line (new Coords(start.getCenterX(), start.getCenterY()), perpSlope));
    		circleSides.add(new Line (new Coords(dest.getCenterX(), dest.getCenterY()), perpSlope));
    		
    		//lines from start to dest
    		
    		//x = rcos(theta)
    		//y = rsin(theta)
    		double startDx = (start.getHeight() / 2) * Math.cos(Math.atan(perpSlope));
    		double startDy = (start.getHeight() / 2) * Math.sin(Math.atan(perpSlope));
    		double destDx = (dest.getHeight() / 2) * Math.cos(Math.atan(perpSlope));
    		double destDy = (dest.getHeight() / 2) * Math.sin(Math.atan(perpSlope));
    		
    		bridgeSides.add(new Line (new Coords(start.getCenterX() + startDx, start.getCenterY() + startDy), new Coords(dest.getCenterX() + destDx, dest.getCenterY() + destDy)));
    		bridgeSides.add(new Line (new Coords(start.getCenterX() - startDx, start.getCenterY() - startDy), new Coords(dest.getCenterX() - destDx, dest.getCenterY() - destDy)));
    	}
    	
    	//Takes the values of another path area, that way this one can be changed while leaving the original unchanged
    	public PathArea (PathArea inArea)
    	{
    		for (Line side : inArea.getCircleSides())
    		{
    			Line tempLine = new Line (side.getSlope(), side.getInt());
    			circleSides.add(tempLine);
    		}
    		
    		for (Line side : inArea.getBridgeSides())
    		{
    			Line tempLine = new Line (side.getSlope(), side.getInt());
    			bridgeSides.add(tempLine);
    		}
    	}
    	
    	public ArrayList<Line> getCircleSides ()
    	{
    		return circleSides;
    	}
    	
    	public ArrayList<Line> getBridgeSides ()
    	{
    		return bridgeSides;
    	}
    }
    
 //Line class (y = mx + b) 
    //For vertical lines, set m to Double.Max_value and b to the desired x-int
    class Line
    {
    	double m;
    	double b;
    	
    	public Line ()
    	{
    		setSlope(0);
    		setInt(0);
    	}
    	
    	public Line (double inSlope, double inInt)
    	{
    		setSlope(inSlope);
    		setInt(inInt);
    	}
    	
    	public Line (Coords coord1, Coords coord2)
    	{
    		//In case of vertical line
    		if (coord1.getX() - coord2.getX() == 0)
    		{
    			setSlope (Double.MAX_VALUE);

    			setInt (coord1.getX());
    		}
    		
    		else
    		{
	    		//slope = (y1 - y2) / (x1 - x2)
	    		double tempSlope = (coord1.getY() - coord2.getY()) / (coord1.getX() - coord2.getX());
	    		setSlope (tempSlope);
    			
    			//y - mx = b
	    		setInt(coord1.getY() - (m * coord1.getX()));
    		}
    	}
    	
    	public Line (Coords coord, double inSlope)
    	{
    		setSlope(inSlope);
    		
    		//in case of vertical line
    		if (inSlope == Double.MAX_VALUE)
    		{
    			setInt (coord.getX());
    		}
    		
    		else 
    		{
    			//y - mx = b
	    		setInt(coord.getY() - (inSlope * coord.getX()));
    		}
    	}
    	
    	public double getSlope ()
    	{
    		return m;
    	}
    	
    	public void setSlope (double inSlope)
    	{
    		m = inSlope;
    	}
    	
    	public double getInt ()
    	{
    		return b;
    	}
    	
    	public void setInt (double inInt)
    	{
    		b = inInt;
    	}
    	
    	public double getYForX (double inX)
    	{
    		//In case of vertical line
    		if (m == Double.MAX_VALUE)
    		{
    			System.out.println("y of vertical line");
    			return Double.MAX_VALUE;
    		}
    		
    		//y = mx + b
    		double y = (m * inX) + b;
    		return y;
    	}
    	
    	public double getXForY (double inY)
    	{
    		//In case of horizontal line
    		if (m == 0)
    		{
    			System.out.println("x of horizontal line");
    			return Double.MAX_VALUE;
    		}
    		
    		//In case of vertical line
    		else if (m == Double.MAX_VALUE)
    		{
    			return b;
    		}
    		
    		//(y - b) / m = x
    		double x = (inY - b) / m;
    		return x;
    	}
    	
    	public Coords FindIntersect (Line inLine)
    	{
    		//System.out.println("Finding Intersect");
    		
    		//m1*x + b1 = m2*x + b2
    		//x = (b2 - b1) / (m1 - m2)
    		double x = (inLine.getInt() - b) / (m - inLine.getSlope());
    		
    		//System.out.println(x);
    		//input.next();
    		
    		double y = getYForX(x);
    		
    		//System.out.println(y);
    		
    		return new Coords (x, y);
    	}
    	
    	public String toString ()
    	{
    		String output = "y = " + m + "x + " + b;
    		return output;
    	}
    }
    
//Coordinates class (x, y)
    class Coords
    {
    	double x;
    	double y;
    	
    	public Coords ()
    	{
    		setX(0);
    		setY(0);
    	}
    	
    	public Coords (double inX, double inY)
    	{
    		setX(inX);
    		setY(inY);
    	}
    	
    	public double getX ()
    	{
    		return x;
    	}
    	
    	public void setX (double inX)
    	{
    		x = inX;
    	}
    	
    	public double getY ()
    	{
    		return y;
    	}
    	
    	public void setY (double inY)
    	{
    		y = inY;
    	}
    	
    	public double getDistanceFrom (Coords inCoords)
    	{
    		//System.out.println("Getting Distance");
    		/*
    		System.out.println(x + " " + y);
    		input.next();
    		System.out.println(inCoords.getX() + " " + inCoords.getY());
    		input.next();
    		System.out.println(Math.pow((x - inCoords.getX()), 2) + Math.pow((y - inCoords.getY()), 2));
    		input.next();
    		System.out.println(Math.sqrt(Math.pow((x - inCoords.getX()), 2) + Math.pow((y - inCoords.getY()), 2)));
    		input.next();
    		*/
    		return Math.sqrt(Math.pow((x - inCoords.getX()), 2) + Math.pow((y - inCoords.getY()), 2));
    	}
    	
    	public String toString ()
    	{
    		String output = "(" + x + ", " + y + ")";
    		return output;
    	}
    }
}