/**
 * @(#)Tron.java
 *
 * Tron application
 *
 *where we left off: We made a functioning tron program. Try to add trailing paths and maybe AI.
 *
 * @author 
 * @version 1.00 2016/6/19
 */
 import javax.swing.*;
 import java.awt.*;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.util.concurrent.TimeUnit;
 
public class Tron 
{
    public static final int SIZE = 10;
    public static JFrame GUI = new JFrame();
    public static Container pane = GUI.getContentPane();
    public static ArrayList<ArrayList<JPanel>> grid = new ArrayList<ArrayList<JPanel>>();
    
    public static void main(String[] args) throws InterruptedException
    {	
    	//Set up the GUI grid.
    	GUI.setTitle("The Game of Life");
    	GUI.setSize(500, 500);
    	GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	pane.setLayout(new GridLayout(SIZE, SIZE));
    	
    	Player Hero = new Player(Color.cyan, new Coord(0, 0));
    	Player Villain = new Player(Color.orange, new Coord(SIZE - 1, SIZE - 1));
    	
    	Initialize();
    	
    	Hero.Display();
    	Villain.Display();
    	GUI.setVisible(true);
    	
    	while (Villain.Move() && Hero.Move())
    	{
    		TimeUnit.MILLISECONDS.sleep(200);
    	}
    }
    
    public static void Initialize ()
    {
    	for (int r = 0; r < SIZE; r++)
    	{
    		ArrayList<JPanel> tempRow = new ArrayList<JPanel>();
    		
    		for (int c = 0; c < SIZE; c++)
    		{
    			JPanel tempPixel = new JPanel();
	    		tempPixel.setBackground(Color.black);
	    		pane.add(tempPixel);
	    		tempRow.add(tempPixel);
    		}
    		
    		grid.add(tempRow);
    	}
    }
}
