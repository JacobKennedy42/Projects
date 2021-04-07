/**
 * @(#)Board.java
 *
 *
 * @author 
 * @version 1.00 2016/5/21
 */
import java.util.ArrayList;
import java.util.Scanner;

public class Board 
{
	Scanner input = new Scanner (System.in);
	
	public static final int boardSize = 6;
	
	Deck deck;
	ArrayList<Card> drawnCards = new ArrayList<Card>();
	ArrayList<ArrayList<Card>> playArea = new ArrayList<ArrayList<Card>>();
	Card[][] winArea = new Card[4][10];
	
    public Board() 
    {
    	NextMove("help");
    	
    	deck = new Deck();
    	
    	for (int c = 0; c < boardSize; c++)
    	{
    		ArrayList<Card> tempCol = new ArrayList<Card>();
    		
    		for (int r = 0; r < boardSize - c; r++)
    		{
    			tempCol.add(deck.DrawCard());
    		}
    		
    		playArea.add(tempCol);
    	}
    	
    	Cycle();
    }
    
    public void Display()
    {
    	
    	
    	System.out.println();
    	System.out.print(deck.GetSize());
		if (drawnCards.size() > 0)
		{
			Card currentCard = drawnCards.get(drawnCards.size() - 1);
			System.out.print(" " + currentCard.Print() + " ");
		}
		System.out.print("   ");
		for(int i = 0; i < 4; i++)
    	{
    		int k = 9;
    		
    		while (k >= 0 && winArea[i][k] == null)
    		{
    			k--;
    		}
    		
    		if (k >= 0)
    		{
    			System.out.print(winArea[i][k].GetSuit() + winArea[i][k].GetVal() + " ");
    		}
    	}
		
		System.out.println();
		boolean nextRow = true;
		for (int r = 0; nextRow; r++)
		{
			nextRow = false;
			for (int c = 0; c < boardSize; c++)
			{
				if (r > playArea.get(c).size() - 1)
				{
					System.out.print("   ");
				}
				
				else if (r == playArea.get(c).size() - 1)
				{
					System.out.print(playArea.get(c).get(r).Print() + " ");
				}
				
				else
				{
					System.out.print("*  ");
					nextRow = true;
				}
			}
			
			System.out.println();
		}
    }
    
    public void Cycle()
    {
    	if (deck.GetSize() > 0)
    	{
    		drawnCards.add(deck.DrawCard());
    	}
    	
    	else
    	{
    		while(drawnCards.size() > 0)
    		{
    			deck.AddCard(drawnCards.remove(drawnCards.size() - 1));
    		}
    		
    		drawnCards.add(deck.DrawCard());
    	}
    }
    
    public void NextMove(String inMove)
    {
    	if (inMove.equals("d"))
    	{
    		Cycle();
    	}
    	
    	else if(inMove.charAt(0) == 'x')
    	{
    		int startSpot = (int)(inMove.charAt(1)) - 49;
    		int stopSpot = (int)(inMove.charAt(2)) - 49;
    		Card startCard;
    		
    		if(startSpot == -1)
    		{
    			startCard = drawnCards.remove(drawnCards.size() - 1);
    		}
    		else
    		{
    			startCard = playArea.get(startSpot).remove(playArea.get(startSpot).size()-1);
    		}
    		
    		Card stopCard = new Card();
    		if (playArea.get(stopSpot).size() > 0)
    		{
    			stopCard = playArea.get(stopSpot).get(playArea.get(stopSpot).size()-1);
    		}
    		
    		if (stopCard.GetVal() == -1)
    		{
    			playArea.get(stopSpot).add(startCard);
    		}
    		
    		else if ((startCard.GetSuit().equals("s") || startCard.GetSuit().equals("c")) && (stopCard.GetSuit().equals("s") || stopCard.GetSuit().equals("c")))
    		{
    			if(startSpot == -1)
	    		{
	    			drawnCards.add(startCard);
	    		}
	    		else
	    		{
	    			playArea.get(startSpot).add(startCard);
	    		}
    			
    			System.out.println("Invalid Move - Suit");
    		}
    		
    		else if ((startCard.GetSuit().equals("D") || startCard.GetSuit().equals("H")) && (stopCard.GetSuit().equals("D") || stopCard.GetSuit().equals("H")))
    		{
    			if(startSpot == -1)
	    		{
	    			drawnCards.add(startCard);
	    		}
	    		else
	    		{
    				playArea.get(startSpot).add(startCard);
	    		}
	    		
    			System.out.println("Invalid Move - Suit");
    		}
    		
    		else if (startCard.GetVal() != stopCard.GetVal() - 1)
    		{
    			if(startSpot == -1)
	    		{
	    			drawnCards.add(startCard);
	    		}
	    		else
	    		{
	    			playArea.get(startSpot).add(startCard);
	    		}
	    		
    			System.out.println("Invalid Move - Value");
    		}
    		
    		else
    		{
    			playArea.get(stopSpot).add(startCard);
    			
    		}
    	}
    	
    	else if (inMove.charAt(0) == 'z')
    	{
    		int selectSpot = (int)(inMove.charAt(1)) - 49;
    		Card selectCard;
    		if(selectSpot == -1)
    		{
    			selectCard = drawnCards.remove(drawnCards.size() - 1);
    		}
    		else
    		{
    			selectCard = playArea.get(selectSpot).remove(playArea.get(selectSpot).size()-1);
    		}
    		
    		int s = 0;
    		while (!selectCard.GetSuit().equals(Deck.suits[s]))
    		{
    			s++;
    		}
    		
    		if (selectCard.GetVal() == 0 || winArea[s][selectCard.GetVal() - 1] != null)
    		{
    			winArea[s][selectCard.GetVal()] = selectCard;
    		}
    		
    		else
    		{
    			if(selectSpot == -1)
	    		{
	    			drawnCards.add(selectCard);
	    		}
	    		else
	    		{
    				playArea.get(selectSpot).add(selectCard);
	    		}
    			
    			System.out.println("Invalid Move - " + Deck.suits[s] + (selectCard.GetVal() - 1) + " needed first");
    		}
    	}
    	
    	else if (inMove.equals("help"))
    	{
    		System.out.println();
    		System.out.println();
    		System.out.println("Type \"d\" to draw a card from the deck.");
    		System.out.println();
    		System.out.println("Type \"x\" <select column> <column you want the card to go to> to move a card over to another column.");
    		System.out.println("Use 0 to select your current deck card.");
    		System.out.println("Ex: x17 x43 x05");
    		System.out.println();
    		System.out.println("Type \"z\" <select column> to move a card to the \"win spaces\" above the play area.");
    		System.out.println("Ex: z0 z4 z7");
    		System.out.println();
    		System.out.println("Type \"help\" whenever you need to see this again.");
    		System.out.println();
    		System.out.println("Press any key and then press enter to resume.");
    		System.out.println();
    		input.next();
    	}
    	
    	else 
    	{
    		System.out.println("Invalid Move - type \"help\" for help");
    	}
    }
    
    public boolean IsWin()
    {
    	for (Card[] row : winArea)
    	{
    		if (row[9] == null)
    		{
    			return false;
    		}
    	}
    	
    	return true;
    }
    
}