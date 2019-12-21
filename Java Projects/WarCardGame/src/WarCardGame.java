import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;
import java.awt.event.*;

public class WarCardGame extends JFrame
{
	private static Surface surface;
	
	private Rectangle2D.Float buttonBorder = new Rectangle2D.Float(210, 400, 117, 52);
	private Rectangle2D.Float button = new Rectangle2D.Float(210, 400, 115, 50);
	
	static int winner = 0;
	
	public WarCardGame ()
	{
		InitUI();
	}
	
	public void InitUI()
	{
		surface = new Surface();
		
		add(surface);
	    
    	setTitle ("War");
    	setSize (600, 600);
    	setLocationRelativeTo (null);
    	//setLocation(100, 100);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		WarCardGame window = new WarCardGame();
		window.setVisible(true);
		/*
		EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                WarCardGame ex = new WarCardGame();
                ex.setVisible(true);
            }
        });
        */
	}
	
	class Surface extends JPanel
	{
		Adapter adapter = new Adapter();
		
		ArrayList<Rectangle2D.Float> player1Tiles = new ArrayList<Rectangle2D.Float>();
		ArrayList<Rectangle2D.Float> player2Tiles = new ArrayList<Rectangle2D.Float>();
		
		Player player1 = new Player(1);
		Player player2 = new Player(2);
		
		int card1 = 0;
    	int card2 = 0;
		
		public Surface ()
		{
			super();
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			
			addMouseListener(adapter);
			
			for (int i = 0; i < 2; i++)
			{
				for (int j = 0; j < 5; j++)
				{
					Rectangle2D.Float tempRectangle = new Rectangle2D.Float(50 + (30 * i), 100 + (30 * j), 25, 25);
					player1Tiles.add(tempRectangle);
				}
			}
			
			for (int j = 0; j < 3; j++)
			{
				Rectangle2D.Float tempRectangle = new Rectangle2D.Float(110, 100 + (30 * j), 25, 25);
				player1Tiles.add(tempRectangle);
			}
			
			for (int i = 0; i < 2; i++)
			{
				for (int j = 0; j < 5; j++)
				{
					Rectangle2D.Float tempRectangle = new Rectangle2D.Float(420 - (30 * i), 100 + (30 * j), 25, 25);
					player2Tiles.add(tempRectangle);
				}
			}
			
			for (int j = 0; j < 3; j++)
			{
				Rectangle2D.Float tempRectangle = new Rectangle2D.Float(360, 100 + (30 * j), 25, 25);
				player2Tiles.add(tempRectangle);
			}
		}
		
		private void Draw (Graphics g)
	    {
	    	Graphics2D g2d = (Graphics2D) g;
	    	
	    	RenderingHints rh = new RenderingHints (
	    		RenderingHints.KEY_ANTIALIASING,
	    		RenderingHints.VALUE_ANTIALIAS_ON);
	    		
	    	rh.put(RenderingHints.KEY_RENDERING,
	    		RenderingHints.VALUE_RENDER_QUALITY);
	    		
	    	g2d.setRenderingHints (rh);
	    	
	    	g2d.setFont(new Font("Arial", Font.PLAIN, 20)); 
	    	
	    	g2d.drawString("Player1", 50, 50);
	    	g2d.drawString("Player2", 400, 50);
	    	
	    	g2d.drawString("Deck: " + player1.getDeckSize(), 50, 400);
	    	g2d.drawString("Deck: " + player2.getDeckSize(), 400, 400);
	    	
	    	g2d.drawString("Disc: " + player1.getDiscardSize(), 50, 420);
	    	g2d.drawString("Disc: " + player2.getDiscardSize(), 400, 420);
	    	
	    	g2d.drawString("Loot: " + player1.getChestSize(), 50, 440);
	    	g2d.drawString("Loot: " + player2.getChestSize(), 400, 440);
	    	
	    	g2d.setPaint(Color.black);
	    	g2d.fill(buttonBorder);
	    	
	    	g2d.setPaint(Color.white);
	    	g2d.fill(button);
	    	
	    	g2d.setPaint(Color.black);
	    	g2d.drawString("Next Turn", 225, 430);
	    	
	    	g2d.setPaint(player1.getColor());
	    	for (int i = 0; i < card1; i++)
	    	{
	    		g2d.fill(player1Tiles.get(i));
	    	}
	    	
	    	g2d.setPaint(player2.getColor());
	    	for (int i = 0; i < card2; i++)
	    	{
	    		g2d.fill(player2Tiles.get(i));
	    	}
	    }
		
		 //Paints the components
	    public void paintComponent (Graphics g)
	    {
	    	super.paintComponent(g);
	    	Draw(g);
	    }
	    
	    public void TakeTurn ()
	    {
	    	card1 = player1.PlayCard();
	    	card2 = player2.PlayCard();
	    	
	    	if (card1 != -1 && card2 != -1)
	    	{
	    		if (card1 > card2)
	    		{
	    			player1.setColor(Color.green);
	    			player2.setColor(Color.red);
	    			
	    			player1.AddToDiscard(card1);
	    			player1.AddToDiscard(card2);
	    			
	    			player1.AddToDiscard(player1.getChest());
	    			player1.AddToDiscard(player2.getChest());
	    		}
	    		
	    		else if (card1 < card2)
	    		{
	    			player2.setColor(Color.green);
	    			player1.setColor(Color.red);
	    			
	    			player2.AddToDiscard(card1);
	    			player2.AddToDiscard(card2);
	    			
	    			player2.AddToDiscard(player1.getChest());
	    			player2.AddToDiscard(player2.getChest());
	    		}
	    		
	    		else if (card1 == card2)
	    		{
	    			player2.setColor(Color.yellow);
	    			player1.setColor(Color.yellow);
	    			
	    			player1.AddToChest(card1);
	    			player2.AddToChest(card2);
	    			
	    			for (int i = 0; i < 3; i++)
	    			{
	    				if (winner == 0)
	    				{
	    					player1.AddToChest();
	    					player2.AddToChest();
	    				}
	    			}
	    		}
	    		
	    		repaint();
	    	}
	    }
	}
	
	class Adapter extends MouseAdapter
	{
		private int x;
        private int y;
		
		public void mousePressed (MouseEvent e)
		{
			 x = e.getX();
	         y = e.getY();
			
			if (button.getBounds2D().contains(x, y) && winner == 0)
			{
				surface.TakeTurn();
			}
		}
	}

	
	class Player
	{
		ArrayList<Card> deck = new ArrayList<Card>();
		ArrayList<Card> discardPile = new ArrayList<Card>();
		ArrayList<Card> warChest = new ArrayList<Card>();
		
		Color color = Color.blue;
		int playerID;
		
		public Player()
		{
			FillDeck();
			ShuffleDeck();
		}
		
		public Player(int inID)
		{
			FillDeck();
			ShuffleDeck();
			playerID = inID;
		}
		
		public Color getColor ()
		{
			return color;
		}
		
		public void setColor (Color inColor)
		{
			color = inColor;
		}
		
		public int getDeckSize()
		{
			return deck.size();
		}
		
		public int getDiscardSize()
		{
			return discardPile.size();
		}
		
		public void AddToDiscard (int inValue)
		{
			Card inCard = new Card(inValue);
			discardPile.add(inCard);
		}
		
		public void AddToDiscard (ArrayList<Card> inList)
		{
			for (int i = inList.size() - 1; i >= 0; i--)
			{
				discardPile.add(inList.remove(i));
			}
		}
		
		public void TransferDiscard ()
		{
			for (int i = discardPile.size() - 1; i >= 0; i--)
			{
				deck.add(discardPile.remove(i));
			}
			
			ShuffleDeck();
		}
		
		public ArrayList<Card> getChest ()
		{
			return warChest;
		}
		
		public void AddToChest ()
		{
			if (deck.size() != 0)
			{
				Card inCard = deck.remove(0);
				warChest.add(inCard);
			}
			
			else if (discardPile.size() != 0)
			{
				TransferDiscard ();
				
				Card inCard = deck.remove(0);
				warChest.add(inCard);
			}
			
			else
			{
				Lose();
			}
		}
		
		public void AddToChest (int inValue)
		{
			Card inCard = new Card(inValue);
			warChest.add(inCard);
		}
		
		public int getChestSize()
		{
			return warChest.size();
		}
		
		public void FillDeck ()
		{
			for (int i = 0; i < 52; i++)
			{
				deck.add(new Card((i / 4) + 1));
			}
		}
		
		public void ShuffleDeck ()
		{
			ArrayList<Card> tempDeck = new ArrayList<Card>();
			
			while (deck.size() > 0)
			{
				int randIndex = (int) (Math.random() * deck.size());
				
				tempDeck.add(deck.remove(randIndex));
			}
			
			deck = tempDeck;
		}
		
		public int PlayCard ()
		{
			if (deck.size() != 0)
			{
				Card tempCard = deck.remove(0);
				return tempCard.getValue();
			}
			
			else if (discardPile.size() != 0)
			{
				TransferDiscard ();
				
				Card tempCard = deck.remove(0);
				return tempCard.getValue();
			}
			
			Lose();
			
			return -1;
		}
		
		public void Lose()
		{
			if (playerID == 1)
			{
				winner = 2;
			}
			
			else
			{
				winner = 1;
			}
			
			System.out.println("Player " + playerID + " lost.");
			System.out.println("Player " + winner + " won.");
		}
	}
	
	class Card
	{
		int value;
		
		public Card (int inValue)
		{
			value = inValue;
		}
		
		public int getValue ()
		{
			return value;
		}
		
		public String toString ()
		{
			return "" + value;
		}
	}
}
