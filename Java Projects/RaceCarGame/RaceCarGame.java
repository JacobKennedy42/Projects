/**
 * @(#)RaceCarGame.java
 *
 * RaceCarGame application
 *
 *This is an application meant to demonstrate simple AI learning.
 *The AI plays a car that is driving down a road. The road will
 *have randomly generated spikes. Through trial and error, the AI
 *should learn how to avoid these spikes.
 *
 *Where we left off: We rewarded successes and put in an iteration input system. Clean up the comments.
 *
 * @author 
 * @version 1.00 2016/6/11
 */
import java.io.*;
import java.util.Scanner;
import java.lang.Integer;
 
public class RaceCarGame 
{
    
    public static void main(String[] args) throws IOException
    {
    	Scanner input = new Scanner (System.in);
    	
    	
    	
    	//Intro
    	System.out.println("This is a program meant to demonstrate a simple AI.");
    	System.out.println("The Ai will play a car driving down a road with spikes in it.");
    	System.out.println("After a few runs, the AI should learn from experience how to avoid the spikes.");
    	System.out.println();
    	System.out.println("To start the program, type in a number showing through how many iterations you want the AI to drive.");
    	System.out.println("To reset the memory, type /reset");
    	System.out.println();
    	
    	int cycles = 0;
    	while (cycles == 0)
    	{
    		String lastInput = input.next();
    		
    		if (lastInput.equals("/reset"))
    		{
    			RaceCarAI.ResetMem();
    			System.out.println("Memory reset. Now type in the number of iterations.");
    		}
    		
    		else if (Integer.parseInt(lastInput) > 0)
    		{
    			cycles = Integer.parseInt(lastInput);
    		}
    		
    		else
    		{
    			System.out.println("Invalid. Try again.");
    		}
    	}
    	System.out.println();
    	
    	for (int i = 0; i < cycles; i++)
    	{
    		//The AI's name is Mitch.
    		RaceCarAI Mitch = new RaceCarAI();
    		
	    	int count = 1;
	    	
	    	//Dispays the track.
	    	Mitch.Display();
	    	
	    	//loop that creates the road and road spikes (note: make the length of the road a user input variable)
	    	while (!Mitch.isHit() && !Mitch.isWin(count))
	    	{
		    	Mitch.MakeRoad();
		    	Mitch.Display();
		    	Mitch.Move();
		    	
		    	count++;
	    	}
	    	
	    	/*
	    	if (count == cycles)
	    	{
	    		System.out.println("Success.");
	    	}
	    	*/
	    	System.out.println("Length driven: " + count + "\n");
	    	//input.next();
    	}
    }
}
