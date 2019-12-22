/**
 * @(#)PacMan.java
 *
 * PacMan application
 *
 *Where we left off: Change Player's ghost escape method (only runs from red). Dest glitches at the last moments
 *	of this run (goes from top to the bottom)
 *
 * @author 
 * @version 1.00 2016/7/11
 */
 import javax.swing.*;
 import java.awt.*;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.util.concurrent.TimeUnit;
 import java.lang.Double;
 import java.util.Arrays;
 
//PacMan Game (not perfect)
public class PacMan
{
    //size of the display grid.
    public static final int SIZEX = 28;
    public static final int SIZEY = 31;
    
    //the entire gui frame.
    public static JFrame GUI = new JFrame();
    
    //the container of the gui components
    public static Container pane = GUI.getContentPane();
    
    //a 2d list representation of the grid display.
    public static ArrayList<ArrayList<Pixel>> grid = new ArrayList<ArrayList<Pixel>>();
    
    //the map design
    public static char[][] map = {{'X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X'},
    							  {'X','.','.','.','.','.','.','.','.','.','.','.','.','X','X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','X','X','X','.','X','X','.','X','X','X','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','X','X','X','.','X','X','.','X','X','X','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','X','X','X','.','X','X','.','X','X','X','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','.','X','X','X','X','X','X','X','X','.','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','.','X','X','X','X','X','X','X','X','.','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','.','.','.','.','.','X','X','.','.','.','.','X','X','.','.','.','.','X','X','.','.','.','.','.','.','X'},
    							  {'X','X','X','X','X','X','.','X','X','X','X','X',' ','X','X',' ','X','X','X','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X','X','X','X',' ','X','X',' ','X','X','X','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ','X','X','X',' ',' ','X','X','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ','X','R',' ',' ',' ',' ','P','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {' ',' ',' ',' ',' ',' ','.',' ',' ',' ','X','B',' ',' ',' ',' ','O','X',' ',' ',' ','.',' ',' ',' ',' ',' ',' '},
    							  {'X','X','X','X','X','X','.','X','X',' ','X',' ',' ',' ',' ',' ',' ','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ','X','X','X','X','X','X','X','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ','X','X','X','X','X','X','X','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','X','X','X','X','X','.','X','X',' ','X','X','X','X','X','X','X','X',' ','X','X','.','X','X','X','X','X','X'},
    							  {'X','.','.','.','.','.','.','.','.','.','.','.','.','X','X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','X','X','X','.','X','X','.','X','X','X','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','X','X','X','X','.','X','X','X','X','X','.','X','X','.','X','X','X','X','X','.','X','X','X','X','.','X'},
    							  {'X','.','.','.','X','X','.','.','.','.','.','.','.','0','.','.','.','.','.','.','.','.','X','X','.','.','.','X'},
    							  {'X','X','X','.','X','X','.','X','X','.','X','X','X','X','X','X','X','X','.','X','X','.','X','X','.','X','X','X'},
    							  {'X','X','X','.','X','X','.','X','X','.','X','X','X','X','X','X','X','X','.','X','X','.','X','X','.','X','X','X'},
    							  {'X','.','.','.','.','.','.','X','X','.','.','.','.','X','X','.','.','.','.','X','X','.','.','.','.','.','.','X'},
    							  {'X','.','X','X','X','X','X','X','X','X','X','X','.','X','X','.','X','X','X','X','X','X','X','X','X','X','.','X'},
    							  {'X','.','X','X','X','X','X','X','X','X','X','X','.','X','X','.','X','X','X','X','X','X','X','X','X','X','.','X'},
    							  {'X','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','X'},
    							  {'X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X','X'}};
    
    //a list of the ghosts							  
   	public static ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
   	
   	//the player
   	public static Player man = new Player();
    
    public static void main(String[] args) throws InterruptedException
    {
    	//Set up the GUI grid.
    	GUI.setTitle("A*");
    	GUI.setSize(750,750);
    	GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	pane.setLayout(new GridLayout(SIZEY, SIZEX));
    	
    	//Set up the board.
    	Initialize();
    	GUI.setVisible(true);
    	
    	//make the mobs move
    	while (true)
		{
			/*
			ghost.Move();
			*/
			
			for (Ghost ghost: ghosts)
			{
				ghost.Move();
			}
			
			man.Move();
		}
    	
    }
    
    //Sets up the board.
    public static void Initialize() throws InterruptedException
    {
    	for (int r = 0; r < SIZEY; r++)
    	{
    		ArrayList<Pixel> tempRow = new ArrayList<Pixel>();
    		
    		for (int c = 0; c < SIZEX; c++)
    		{
    			JPanel tempPixel = new JPanel();
    			tempPixel.setBackground(Color.black);
    			
    			char currentSpot = map[r][c];
    			
    			//Set the board according to the map.
    			if (currentSpot == 'X')
    			{
    				tempPixel.setBackground(Color.blue);
    			}
    			
    			else if (currentSpot == '.')
    			{
    				tempPixel.setBackground(Color.white);
    			}
    			
    			else if (currentSpot == '0')
    			{
    				tempPixel.setBackground(Color.yellow);
    			}
    			
    			else if (currentSpot == 'R')
    			{
    				tempPixel.setBackground(Color.red);
    			}
    			
    			else if (currentSpot == 'P')
    			{
    				tempPixel.setBackground(Color.pink);
    			}
    			
    			else if (currentSpot == 'B')
    			{
    				tempPixel.setBackground(Color.cyan);
    			}
    			
    			else if (currentSpot == 'O')
    			{
    				tempPixel.setBackground(Color.orange);
    			}
    			
    			if (tempPixel.getBackground().equals(Color.yellow))
    			{
    				tempRow.add(new Player(c, r, tempPixel));
    			}
    			
    			else if (tempPixel.getBackground().equals(Color.red))
    			{
    				tempRow.add(new RedGhost(c, r, tempPixel));
    			}
    			
    			else if (tempPixel.getBackground().equals(Color.pink))
    			{
    				tempRow.add(new PinkGhost(c, r, tempPixel));
    			}
    			
    			else if (tempPixel.getBackground().equals(Color.cyan))
    			{
    				tempRow.add(new BlueGhost(c, r, tempPixel));
    			}
    			
    			else if (tempPixel.getBackground().equals(Color.orange))
    			{
    				tempRow.add(new OrangeGhost(c, r, tempPixel));
    			}
    			
    			else
    			{
    				tempRow.add(new Pixel (c, r, tempPixel));
    			}
    			
    			pane.add(tempPixel);
    		}
    		
    		grid.add(tempRow);
    	}
    	
    	//search for and differentiate pacmen and ghosts.
    	for (ArrayList<Pixel> row : grid)
    	{
    		for (Pixel pixel : row)
    		{
    			if (pixel instanceof Player)
    			{
    				man = (Player) pixel;
    			}
    			
    			else if (pixel instanceof Ghost)
    			{
    				Ghost ghost = (Ghost) pixel;
    				ghosts.add(ghost);
    				ghost.LockOnPlayer();
    			}
    		}
    	}
    }
}
