/**
 * @(#)GameOfLife.java
 *
 * GameOfLife application
 *
 *Where we left off: It works, but may become slow at high sizes. Try to optimize. (Exceptions at SIZE > 92)
 *
 * @author 
 * @version 1.00 2016/6/17
 */
 import javax.swing.*;
 import java.awt.*;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.util.concurrent.TimeUnit;
 import java.util.Scanner;
 
public class GameOfLife 
{
    public static final int SIZE = 20;
    public static JFrame GUI = new JFrame();
    public static ArrayList<ArrayList<JPanel>> pixels = new ArrayList<ArrayList<JPanel>>();
    
    public static void main(String[] args) throws InterruptedException
    {
    	Scanner input = new Scanner(System.in);
    	
    	//Create a random grid of pixels.
    	Initialize();
    	
    	GUI.setTitle("The Game of Life");
    	GUI.setSize(500, 500);
    	GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container pane = GUI.getContentPane();
    	pane.setLayout(new GridLayout(SIZE, SIZE));
    	
    	//Add the pixels to the display grid.
    	Display(pane);
    	
    	GUI.setVisible(true);
    	
    	while (true)
    	{
	    	//input.next();
	    	nextStep();
	    	Display (pane);
	    	TimeUnit.MILLISECONDS.sleep(500);
    	}
    }
    
    //initializes the pixel grid with a random assortment of pixels.
    public static void Initialize()
    {
    	for (int r = 0; r < SIZE; r++)
    	{
    		ArrayList<JPanel> tempRow = new ArrayList<JPanel>();
    		
    		for (int c = 0; c < SIZE; c++)
    		{
    			JPanel tempPixel = new JPanel();
    			
    			if (Math.random() < .5)
    			{
    				tempPixel.setBackground(Color.green);
    			}
    			
    			else
    			{
    				tempPixel.setBackground(Color.white);
    			}
    			
    			tempRow.add(tempPixel);
    		}
    		
    		pixels.add(tempRow);
    	}
    }
    
    //Displays the pixel grid.
    public static void Display(Container pane)
    {
    	//reset the pane.
    	pane.removeAll();
    	
    	for (int r = 0; r < SIZE; r++)
    	{
    		for (int c = 0; c < SIZE; c++)
    		{
    			pane.add(pixels.get(r).get(c));
    		}
    	}
    	
    	GUI.setVisible(true);
    }
    
    //Uses predetermined rules to advance the pixel grid by a step.
    public static void nextStep ()
    {
    	ArrayList<ArrayList<JPanel>> tempGrid = new ArrayList<ArrayList<JPanel>>();
    	
    	for(int r = 0; r < SIZE; r++)
    	{
    		ArrayList<JPanel> tempRow = new ArrayList<JPanel>();
    		
    		for (int c = 0; c < SIZE; c++)
    		{
    			JPanel tempPixel = new JPanel();
    			
    			//Count the number of populated pixels surrounding a pixel.
    			int count = CheckSides(r, c);
    			
    			//System.out.println(count);
    			
    			//if the pixel is green and is under or over populated, make it white.
    			if ((pixels.get(r).get(c).getBackground().equals(Color.green)) && (count < 2 || count > 3))
    			{
    				tempPixel.setBackground(Color.white);
    				//System.out.println(r + " " + c + " to white");
    			}
    			
    			//if the pixel is white and has 3 neighbors, make it green.
    			else if((pixels.get(r).get(c).getBackground().equals(Color.white)) && count == 3)
    			{
    				tempPixel.setBackground(Color.green);
    				//System.out.println(r + " " + c + " to green");
    			} 
    				
    			//otherwise, leave the color unchanged.
    			else
    			{
    				tempPixel.setBackground(pixels.get(r).get(c).getBackground());
    				//System.out.println(pixels.get(r).get(c).getBackground().toString());
    			}
    			
    			tempRow.add(tempPixel);
    		}
    		
    		tempGrid.add(tempRow);
    	}
    	
    	//replaces the existing grid with the modified one (I tried to be memory efficient with this one)
    	while (pixels.size() > 0)
    	{
    		pixels.remove(0);
    	}
    	
    	for (ArrayList<JPanel> row: tempGrid)
    	{
    		pixels.add(row);
    	}
    	
    	//pixels = tempGrid;
    }
    
    //Check the sides of a pixel for neighbors.
    public static int CheckSides(int r, int c)
    {
    	int count = 0;
    	
    	//Check the upper side if possible.
    	if(r > 0)
    	{
	    	for (int i = -1; i < 2; i++)
	    	{
	    		if (c+i > -1 && c+i < SIZE)
	    		{
		    		//if the adjacent pixel is green, add it to the count.
		    		if(pixels.get(r-1).get(c+i).getBackground().equals(Color.green))
		    		{
		    			count++;
		    		}
	    		}
	    	}
    	}
    	
    	//Check the lower side if possible.
    	if (r < SIZE - 1)
    	{
    		for (int i = -1; i < 2; i++)
	    	{
	    		if (c+i > -1 && c+i < SIZE)
	    		{
		    		//if the adjacent pixel is green, add it to the count.
		    		if(pixels.get(r+1).get(c+i).getBackground().equals(Color.green))
		    		{
		    			count++;
		    		}
	    		}
	    	}
    	}
    	
    	//Check the left pixel if possible
    	if(c > 0)
    	{
    		//if the adjacent pixel is green, add it to the count.
    		if(pixels.get(r).get(c-1).getBackground().equals(Color.green))
    		{
    			count++;
    		}
    	}
    	
    	//Check the right pixel if possible
    	if (c < SIZE - 1)
    	{
    		//if the adjacent pixel is green, add it to the count.
    		if(pixels.get(r).get(c+1).getBackground().equals(Color.green))
    		{
    			count++;
    		}
    	}
    	
    	return count;
    }
}
