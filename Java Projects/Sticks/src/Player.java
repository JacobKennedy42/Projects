import java.lang.Math;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

class Player 
{
	private int leftHand;
	private int rightHand;
	private Player opponent;
	
	//memory ([lefthand] [righthand] [opponentLeft] [opponentRight] [option (1-5)])
	private double[][][][][] memory;
	private File myFile;
	
	/*
	private BoardState lastBoardState = new BoardState();
	private int lastChoice = -1;
	*/
	
	private ArrayList<BoardState> boardHistory = new ArrayList<BoardState>();
	
	public Player ()
	{
		leftHand = 1;
		rightHand = 1;
	}
	
	public Player (Player inPlayer)
	{
		leftHand = 1;
		rightHand = 1;
		opponent = inPlayer;
	}
	
	
	public Player (File inFile) throws IOException
	{
		leftHand = 1;
		rightHand = 1;
		TakeInMem (inFile);
	}
	
	
	public void SetOpponent (Player inPlayer)
	{
		opponent = inPlayer;
	}
	
	public int GetLeft ()
	{
		return leftHand;
	}
	
	public void AddLeft (int inNum)
	{
		leftHand = (leftHand + inNum) % 5;
	}
	
	public int GetRight()
	{
		return rightHand;
	}
	
	public void AddRight (int inNum)
	{
		rightHand = (rightHand + inNum) % 5;
	}
	
	public void TakeInMem (File inFile) throws IOException
	{
		myFile = inFile;
		
		memory = new double[5][5][5][5][5];
		
		Scanner reader = new Scanner (inFile);
		
		while (reader.hasNextDouble())
		{
			// PlayerLeft, PlayerRight, OpponentLeft, OpponentRight
			int PL = (int) (reader.nextDouble());
			int PR = (int) (reader.nextDouble());
			int OL = (int) (reader.nextDouble());
			int OR = (int) (reader.nextDouble());
			
			//5 options
			for (int option = 0; option < 5; option++)
			{
				memory[PL][PR][OL][OR][option] = reader.nextDouble();
			}
		}
		
	}
	
	public void Play() throws IOException
	{
		/*
		lastBoardState.SetState(leftHand, rightHand, opponent.GetLeft(), opponent.GetRight());
		*/
		int lastChoice = -1;
		BoardState lastBoardState = new BoardState(leftHand, rightHand, opponent.GetLeft(), opponent.GetRight());
		
		
		double [] tempMem = new double[5];
		for (int i = 0; i < 5; i++)
		{
			if (i < 2 && leftHand == 0)
			{
				tempMem[i] = 0;
			}
			
			else if ((i > 1 && i < 4) &&
					 rightHand == 0)
			{
				tempMem[i] = 0;
			}
			
			else if ((i == 0 || i == 2) &&
					 opponent.GetLeft() == 0)
			{
				tempMem[i] = 0;
			}
			
			else if ((i == 1 || i == 3) &&
					 opponent.GetRight() == 0)
			{
				tempMem[i] = 0;
			}
			
			else if (i == 4 && !(Math.abs (leftHand - rightHand) > 1))
			{
				tempMem[i] = 0;
			}
			
			else
			{
				tempMem[i] = memory[leftHand][rightHand][opponent.GetLeft()][opponent.GetRight()][i];
			}
		}
		
		double chanceTotal = 0;
		for (int i = 0; i < 5; i++)
		{
			chanceTotal += tempMem[i];
		}
		
		double playerChoice = Math.random() * chanceTotal;
		
		if ((playerChoice < tempMem[0]) && 
			(tempMem[0] != 0))
		{
			opponent.AddLeft (leftHand);
			lastChoice = 0;
		}
		
		else if ((playerChoice < tempMem[0] + tempMem[1]) && 
				 (tempMem[1] != 0))
		{
			opponent.AddRight (leftHand);
			lastChoice = 1;
		}
		
		else if ((playerChoice < tempMem[0] + tempMem[1] + tempMem[2]) && 
				 (tempMem[2] != 0))
		{
			opponent.AddLeft (rightHand);
			lastChoice = 2;
		}
		
		else if ((playerChoice < tempMem[0] + tempMem[1] + tempMem[2] + tempMem[3]) && 
				 (tempMem[3] != 0))
		{
			opponent.AddRight (rightHand);
			lastChoice = 3;
		}
		
		else if (tempMem[4] != 0)
		{
			int handTotal = leftHand + rightHand;
			leftHand = handTotal / 2;
			rightHand = handTotal - leftHand;
			lastChoice = 4;
		}
		
		lastBoardState.SetChoice (lastChoice);
		boardHistory.add (lastBoardState);
		
		Sticks.Display();
	}
	
	public boolean IsLose () throws IOException
	{
		if (leftHand + rightHand == 0)
		{
			AnalyzeLose();
			return true;
		}
		
		return false;
	}
	
	public void AnalyzeLose () throws IOException
	{
		/*
		double currentChance = memory[lastBoardState.PL][lastBoardState.PR][lastBoardState.OL][lastBoardState.OR][lastChoice];
			
			if (currentChance <= .5)
			{
				currentChance *= .5;
			}
			
			else if (currentChance > .5)
			{
				currentChance -= (1 - currentChance);
			}
			
			memory[lastBoardState.PL][lastBoardState.PR][lastBoardState.OL][lastBoardState.OR][lastChoice] = currentChance;
		*/
		
		for (BoardState state : boardHistory)
		{
			double currentChance = memory[state.PL][state.PR][state.OL][state.OR][state.choice];
			
			if (currentChance <= .5)
			{
				currentChance *= .5;
			}
			
			else if (currentChance > .5)
			{
				currentChance -= (1 - currentChance);
			}
			
			memory[state.PL][state.PR][state.OL][state.OR][state.choice] = currentChance;
		}
		
		CommitMem();
	}
	
	public boolean IsWin () throws IOException
	{
		if (opponent.GetLeft() + opponent.GetRight() == 0)
		{
			AnalyzeWin();
			return true;
		}
		
		return false;
	}
	
	public void AnalyzeWin () throws IOException
	{
		/*
		double currentChance = memory[lastBoardState.PL][lastBoardState.PR][lastBoardState.OL][lastBoardState.OR][lastChoice];
		
		if (currentChance < .5)
		{
			currentChance *= 2;
		}
		
		else if (currentChance >= .5)
		{
			currentChance += (1 - currentChance) * .5;
		}
		
		memory[lastBoardState.PL][lastBoardState.PR][lastBoardState.OL][lastBoardState.OR][lastChoice] = currentChance;
		*/
		
		for (BoardState state : boardHistory)
		{
			double currentChance = memory[state.PL][state.PR][state.OL][state.OR][state.choice];
			
			if (currentChance < .5)
			{
				currentChance *= 2;
			}
			
			else if (currentChance >= .5)
			{
				currentChance += (1 - currentChance) * .5;
			}
			
			memory[state.PL][state.PR][state.OL][state.OR][state.choice] = currentChance;
		}
		
		CommitMem();
	}
	
	public void CommitMem() throws IOException
	{
		FileWriter writer = new FileWriter(myFile);
		
		//(PlayerLeft, PlayerRight, OpponentLeft, OpponentRight, OptionChances)
    	for (int PL = 0; PL < 5; PL++)
    	{
    		for (int PR = 0; PR < 5; PR++)
    		{
    			for (int OL = 0; OL < 5; OL++)
    			{
    				for (int OR = 0; OR < 5; OR++)
    				{
    					writer.write (PL + " " + PR + " " + OL + " " + OR);
    					
    					for (int i = 0; i < 5; i++)
    					{
    						writer.write (" " + memory[PL][PR][OL][OR][i]);
    					}
    					
    					writer.write (System.lineSeparator());
    				}
    			}
    		}
    	}
    	
    	writer.flush();
    	writer.close();
	}
}

class BoardState
{
	public int PL;
	public int PR;
	public int OL;
	public int OR;
	public int choice;
	
	public BoardState ()
	{
		PL = 1;
		PR = 1;
		OL = 1;
		OR = 1;
	}
	
	public BoardState (int inPL, int inPR, int inOL, int inOR)
	{
		PL = inPL;
		PR = inPR;
		OL = inOL;
		OR = inOR;
	}
	
	public void SetState (int inPL, int inPR, int inOL, int inOR)
	{
		PL = inPL;
		PR = inPR;
		OL = inOL;
		OR = inOR;
	}
	
	public void SetChoice (int inChoice)
	{
		choice = inChoice;
	}
	
	public String toString ()
	{
		return ("(" + PL + ", " + PR + ", " + OL + ", " + OR + ")");
	}
}
