/**
 * @(#)Solitaire.java
 *
 * Solitaire application
 *
 * @author 
 * @version 1.00 2016/5/21
 */
 import java.util.Scanner;
 
public class Solitaire 
{
    public static void main(String[] args) 
    {
    	Scanner input = new Scanner(System.in);
    	
    	Board display = new Board();
    	
    	while (!display.IsWin())
    	{
	    	display.Display();
	    	display.NextMove(input.next());
    	}
    	
    	System.out.println();
    	System.out.println("YOU WIN!!!!!!");
    }
}
