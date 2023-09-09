/**
 * @(#)Sticks.java
 *
 * Sticks application
 *
 * @author 
 * @version 1.00 2017/2/18
 */
 import java.io.*;
 import java.util.Scanner;
 import java.lang.Integer;
 
 //This only learns what to do on the last turn. Make it so that it augments all of its turns.
public class Sticks
{
	static File Player1Mem = new File ("Player1.txt");
	static File Player2Mem = new File ("Player2.txt");
	
    public static Player player1;
    public static Player player2;
    
    public static int cycles = 0;
    	
    public static void main(String[] args) throws IOException
    {
    	Intro();
    	
    	for (int i = 0; i < cycles; i++)
    	{
    		Player1Mem = new File ("Player1.txt");
    		Player2Mem = new File ("Player2.txt");
    		
    		player1 = new Player ();
    		player2 = new Player ();
    		
	    	player1.SetOpponent (player2);
	    	player2.SetOpponent (player1);
	    	player1.TakeInMem (Player1Mem);
	    	player2.TakeInMem (Player2Mem);
    		
	    	int turn = 1;
	    	while (!player1.IsLose() && !player2.IsLose())
	   	   {
	    		if (turn == 1)
	    		{
	    			player1.Play();
	    		}
	    		
	    		else if (turn == -1)
	    		{
	    			player2.Play();
	    		}
	    		
	    		turn *= -1;
	   	   }
	   	   
	   	   player1.IsWin();
	   	   player2.IsWin();
	   	   System.out.println("****************************************************************");
    	}
    }
    
    public static void Intro () throws IOException
    {
    	Scanner input = new Scanner (System.in);
    	
    	System.out.println("This is a game of sticks between two AI.");
    	System.out.println("Type /reset to reset the memory of the AI's.");
    	System.out.println("Type the number of games you want the AI's to play.");
    	
    	while (cycles == 0)
    	{
    		String lastInput = input.next();
    		
	    	if (lastInput.equals("/reset"))
	    	{
	    		ResetMem (Player1Mem);
	    		ResetMem (Player2Mem);
	    		System.out.println("Memory Reset. Now type the number of games you want the AI's to play.");
	    	}
	    	
	    	else if (Integer.parseInt(lastInput) > 0)
	    	{
	    		cycles = Integer.parseInt (lastInput);
	    	}
	    	
	    	else
	    	{
	    		System.out.println("Invalid. Type /reset or a whole number.");
	    	}
    	}
    }
    
    public static void ResetMem (File inFile) throws IOException
    {
    	FileWriter writer = new FileWriter (inFile);
    	
    	//(PlayerLeft, PlayerRight, OpponentLeft, OpponentRight, OptionChances)
    	for (int PL = 0; PL < 5; PL++)
    	{
    		for (int PR = 0; PR < 5; PR++)
    		{
    			for (int OL = 0; OL < 5; OL++)
    			{
    				for (int OR = 0; OR < 5; OR++)
    				{
    					writer.write (PL + " " + PR + " " + OL + " " + OR + " " + .5 + " " + .5 + " " + .5 + " " + .5 + " " + .5);
    					writer.write (System.lineSeparator());
    				}
    			}
    		}
    	}
    	
    	writer.flush();
    	writer.close();
    }
    
    public static void Display ()
    {
    	System.out.println ("========");
    	System.out.println (player1.GetLeft() + "      " + player2.GetRight());
    	System.out.println (player1.GetRight() + "      " + player2.GetLeft());
    }
}
