import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.lang.Math;
import java.io.*;
import java.awt.image.*;
import java.util.Scanner;

public class MineSweeper extends JFrame
{
	public static Surface surface;
	
	//organized row, then column
	public static ArrayList<ArrayList<Tile>> board = new ArrayList<ArrayList<Tile>>();
	
	public static final int TILE_SIZE = 50;
	public static final int BOARD_SIZE = 20;
	public static final int MINE_NUM = 100;
	
	public static boolean minesSet = false;
	public static int tilesLeft = (int) Math.pow(BOARD_SIZE, 2) - MINE_NUM;
	public static boolean isFinished = false;
	
	public static BufferedImage numbers;
	
	public Scanner input = new Scanner(System.in);
	
	public MineSweeper ()
	{
		try
		{
			numbers = ImageIO.read(new File("MineSweeperNumbers.png"));
		} catch (IOException e){}
		
		InitUI();
	}
	
	public void InitUI()
	{
		surface = new Surface();
		
		add(surface);
	    
    	setTitle ("MineSweeper");
    	setSize (1100, 1100);
    	setLocationRelativeTo (null);
    	//setLocation(100, 100);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) 
	{
		System.out.println("test");
		
		// TODO Auto-generated method stub
		MineSweeper window = new MineSweeper();
		window.setVisible(true);
	}
	
	class Surface extends JPanel
	{
		Adapter adapter = new Adapter();
		
		public Surface ()
		{
			addMouseListener(adapter);
			
			InitTiles();
		}
		
		public void InitTiles ()
		{
			for (int i = 0; i < BOARD_SIZE; i++)
			{
				ArrayList<Tile> tempRow = new ArrayList<Tile>();
				
				for (int j = 0; j < BOARD_SIZE; j++)
				{
					tempRow.add(new Tile((TILE_SIZE * i), (TILE_SIZE * j), TILE_SIZE, TILE_SIZE));
				}
				
				board.add(tempRow);
			}
			
//			SetMines();
//			SetNumbers();
		}
		
		public void SetMines ()
		{
			ArrayList<Tile> tempBoard = new ArrayList<Tile>();
			
			for (ArrayList<Tile> row : board)
			{
				for (Tile tile : row)
				{
					tempBoard.add(tile);
				}
			}
			
			for (int i = 0; i < MINE_NUM; i++)
			{
				int randIndex = (int) (Math.random() * tempBoard.size());
				Tile tempTile = tempBoard.remove(randIndex);
				tempTile.setMine(true);
			}
		}
		
		//Set mines randomly among tiles other than the tile at inX, inY
		public void SetMines (int inX, int inY)
		{
			ArrayList<Tile> tempBoard = new ArrayList<Tile>();
			
			for (int r = 0; r < board.size(); r++)
			{
				for (int c = 0; c < board.get(0).size(); c++)
				{
					if (!(r == inX && c == inY))
					{
						tempBoard.add(board.get(r).get(c));
					}
				}
			}
			
			for (int i = 0; i < MINE_NUM; i++)
			{
				int randIndex = (int) (Math.random() * tempBoard.size());
				Tile tempTile = tempBoard.remove(randIndex);
				tempTile.setMine(true);
			}
		}
		
		public void SetNumbers ()
		{
			for (int i = 0; i < BOARD_SIZE; i++)
			{
				for (int j = 0; j < BOARD_SIZE; j++)
				{
					Tile tempTile = board.get(i).get(j);
					tempTile.setNumber(FindNumber(i, j));
				}
			}
		}
		
		public int FindNumber(int inX, int inY)
		{
			//board.get(inX).get(inY).setColor(Color.blue);
			
			if (board.get(inX).get(inY).hasMine())
			{
				return -1;
			}
			
			int output = 0;
			
			for (int i = inX - 1; i <= inX + 1; i++)
			{
				for (int j = inY - 1; j <= inY + 1; j++)
				{
					if (i >= 0 && i < BOARD_SIZE &&
						j >= 0 && j < BOARD_SIZE &&
						!(i == inX && j == inY) &&
						board.get(i).get(j).hasMine())
					{
						output++;
					}
				}
			}
			
			return output;
		}
		
		public void ClearArea (int inX, int inY)
		{
			board.get(inX).get(inY).setReveal(true);
		
			if (board.get(inX).get(inY).getNumber() == 0)
			{
				for (int i = inX - 1; i <= inX + 1; i++)
				{
					for (int j = inY - 1; j <= inY + 1; j++)
					{
						if (i >= 0 && i < BOARD_SIZE &&
							j >= 0 && j < BOARD_SIZE &&
							!(i == inX && j == inY) &&
//							(i == inX || j == inY) &&
//							board.get(i).get(j).getNumber() == 0 &&
							!board.get(i).get(j).isRevealed())
						{
//							board.get(i).get(j).setReveal(true);
							ClearArea(i, j);
						}
					}
				}
			}
		}
		
		private void Draw(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			RenderingHints rh = new RenderingHints (
    		RenderingHints.KEY_ANTIALIASING,
    		RenderingHints.VALUE_ANTIALIAS_ON);
//	    		
//	    	rh.put(RenderingHints.KEY_RENDERING,
//	    		RenderingHints.VALUE_RENDER_QUALITY);
	    		
	    	g2d.setRenderingHints (rh);
	    	
	    	for (ArrayList<Tile> row : board)
	    	{
	    		for (Tile tile : row)
	    		{
	    			//g2d.setPaint(tile.getColor());
	    			
	    			//g2d.fill(tile);
	    			
	    			int tempNum = tile.getNumber();
	    			
	    			if (tempNum <= 0 || !tile.isRevealed())
	    			{
	    				g2d.setPaint(tile.getColor());
		    			g2d.fill(tile);
	    			}
	    			
	    			else if (tile.isRevealed())
	    			{
	    				//TODO: test
	    				System.out.println(numbers);
	    				
	    				g2d.drawImage(numbers, (int) tile.getX(), (int) tile.getY(), (int) tile.getX() + TILE_SIZE, (int) tile.getY() + TILE_SIZE, 
	    						((numbers.getWidth() / 8) * (tempNum - 1)), 0, ((numbers.getWidth() / 8) * tempNum), numbers.getHeight(), null);
	    			}
	    			
	    		}
	    	}
	    	
	    	//g2d.drawImage(numbers, 0, 0, 20, 20, 0, 0, numbers.getWidth() / 8, numbers.getHeight(), null);
		}
		
		//Paints the components
	    public void paintComponent (Graphics g)
	    {
	    	super.paintComponent(g);
	    	Draw(g);
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
	         
	         if (!isFinished && x / TILE_SIZE < BOARD_SIZE && y / TILE_SIZE < BOARD_SIZE)
	         {
	        	 //Set the mines if they haven't been already (1st clicked tile is always not a mine)
	        	 if (!minesSet)
	        	 {
	        		 surface.SetMines(x/TILE_SIZE, y/TILE_SIZE);
	        		 surface.SetNumbers();
	        		 minesSet = true;
	        	 }
	        	 
		         Tile tempTile = board.get(x / TILE_SIZE).get(y / TILE_SIZE);
		         
	        	 if (e.getButton() == 1)
	        	 {   
			         if (!tempTile.isRevealed() && tempTile.color != Color.yellow)
			         {   
				         if (tempTile.getNumber() == 0)
				         {
				        	 surface.ClearArea(x / TILE_SIZE, y / TILE_SIZE);
				         }
				         
				         else if (tempTile.hasMine())
				         {
				        	 tempTile.setReveal(true);
				        	 System.out.println("BOOM!");
				        	 isFinished = true;
				         }
				         
				         else
				         {
					         tempTile.setReveal(true);
				         }
				         
				         if (tilesLeft == 0)
				         {
				        	 System.out.println("You Win!");
				        	 isFinished = true;
				         }
			         }
				         //System.out.println("(" + (x / TILE_SIZE) + "," + (y / TILE_SIZE) + ")");
				         //System.out.println(surface.FindNumber(x / TILE_SIZE, y / TILE_SIZE));
	        	 }
	        	 
	        	 if (e.getButton() == 3)
	        	 {
	        		tempTile.flag(); 
	        	 }
		         repaint();
	         }	         
		}
	}
	
	class Tile extends Rectangle2D.Float
	{
		Color color;
		int number;
		boolean mine;
		boolean revealed;
		
		public Tile(float x, float y, float width, float height) 
    	{
            setFrame(x, y, width, height);
            color = Color.black;
            revealed = false;
        }
		
		public Color getColor ()
		{
			return color;
		}
		
		public void setColor(Color inColor)
		{
			color = inColor;
		}
		
		public int getNumber()
		{
			return number;
		}
		
		public void setNumber (int inNum)
		{
			number = inNum;
		}
		
		public void setMine (boolean inBool)
		{
			mine = inBool;
		}
		
		public boolean hasMine ()
		{
			return mine;
		}
		
		public boolean isRevealed ()
		{
			return revealed;
		}
		
		public void setReveal (boolean inBool)
		{
			revealed = inBool;
			
			if (inBool == false)
			{
				setColor(Color.black);
			}
			
			else
			{
				if (mine)
				{
					setColor(Color.red);
				}
				
				else
				{
					setColor(Color.white);
					tilesLeft--;
				}
			}
		}

		public void flag ()
		{
			if (!revealed)
			{
				if (color.equals(Color.black))
				{
					setColor(Color.yellow);
				}
				else
				{
					setColor(Color.black);
				}
			}
		}
	}
}
