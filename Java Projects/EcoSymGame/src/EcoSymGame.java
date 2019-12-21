 //TODO
/*
Make sure that the path area is including all the right tiles and that the mob is being set
	properly (Do math by hand).
Rework the collision system so that the mob doesn't intersect with the tiles.
	First, draw out how it is now. Then, draw out how you actually want it to work.
Mob is moved to 0, 0 when there are collision mobs/tiles, find out why
Path areas aren't making the correct rectangles, find out why
Make an isSolid variable for mobs and tiles
Maybe switch variables from floats to doubles
Make pathArea not error w/ vertical slopes
Make a GameObject class for Tile and Mob to inherit. Mob will contain an ellipse and Tile will
	contain a rectangle and surface will use these when rendering them.
Make it so that mobs only consider other mobs collision mobs when they're actually touching
	(right now it's when the mob is in one of the collision tiles)
Put collision procedures into methods so I can more easily use them for spawning children.
Make it so that overlap between tiles and mobs don't show during a collision.
Make it so that fast moving mobs can't skip over bedrock 
	(may need to look at mob's previous position and check if it is on the other side of the bedrock)
Maybe switch the movement system from velocity-based to acceleration-based
*/
 import java.awt.*;
 import javax.swing.*;
 import java.util.Scanner;
 
public class EcoSymGame extends JFrame
{
	private static Surface surface = new Surface();
	
	private static final int DELAY = 100000000;
	private static int timer = (int)(System.nanoTime() / DELAY);
	
	private static Scanner input = new Scanner(System.in);
	
	public EcoSymGame() 
    {
    	InitUI();
    }
    
    //Initiates the UI
    private void InitUI ()
    {
    	add(surface);
    
    	setTitle ("EcoSym");
    	setSize (400, 400);
    	setLocationRelativeTo (null);
    	//setLocation(100, 100);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	
    public static void main(String[] args) 
    {
    	//Not sure if i need this for action events. Probably DONT DELETE THIS
    	/*
    	EventQueue.invokeLater (new Runnable()
    	{
    		public void run()
    		{
    			EcoSymGame window = new EcoSymGame();
    			window.setVisible (true);
    			startTimer();
    		}
    	});
    	*/
    	
    	EcoSymGame window = new EcoSymGame();
		window.setVisible (true);
		startTimer();
    }
    
    //starts a timer that updates the game every second
    public static void startTimer ()
    {
    	boolean stop = false;
    	
    	while (stop == false)
    	{
    		if (timer != (int)(System.nanoTime() / DELAY))
    		{
    			//System.out.println("S");
    			surface.Update();
    			timer = (int)(System.nanoTime() / DELAY);
    		}
    		
    		//input.next();
    	}
    }
}