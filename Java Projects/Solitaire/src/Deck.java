/**
 * @(#)Deck.java
 *
 *
 * @author 
 * @version 1.00 2016/5/21
 */
import java.util.ArrayList;
import java.lang.Math;

public class Deck 
{
	private ArrayList<Card> cards = new ArrayList<Card>();
	public static final String[] suits = {"s", "c", "D", "H"};
	
    public Deck() 
    {
    	for (int i = 0; i < 4; i++)
    	{
    		for (int j = 0; j <= 9; j++)
    		{
    			cards.add(new Card (suits[i], j));
    		}
    	}
    	
    	Shuffle();
    }
    
    public int GetSize()
    {
    	return cards.size();
    }
    
    public void Shuffle()
    {
    	ArrayList<Card>	temp = new ArrayList<Card>();
		
		for (int i = 0; i < 40; i++)
		{
			int rand = (int)(Math.random() * cards.size());
			temp.add(cards.remove(rand));
		}
		
		cards = temp;
    }
    
    public Card DrawCard()
    {
    	return cards.remove(cards.size() - 1);
    }
    
    public void AddCard(Card card)
    {
    	cards.add(card);
    }
}