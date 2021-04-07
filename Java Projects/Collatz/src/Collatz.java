import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

import java.util.ArrayList;
import java.util.HashMap;

public class Collatz 
{
	private static boolean stop;
	private static long currentNum;
//	private static HashMap<Long, Node> tree;
	private static Node[] nodeCache;
	
	private static DataInputStream numIn;
	private static DataOutputStream numOut;
	private static DataInputStream treeIn;
	private static DataOutputStream treeOut;
	
//	private void growTree ()
//	{
//		Node tempNode;
//		while (!stop)
//		{
//			tempNode = new Node(currentNum);
//			tree.put(currentNum, tempNode);
//			currentNum++;
//		}
//		
//		if (currentNum % 10000 == 9999)
//		{
//			repaint();
//		}
//	}
	
	//TODO: add branches to file. Need Duplicates to indicate where the branch connects to the main tree.
	
	public static void setUpFiles () throws IOException
	{
		File numFile = new File("currentNum");
		if (!numFile.exists())
		{
			numFile.createNewFile();
		}
		numIn = new DataInputStream (new FileInputStream(numFile));
		numOut =new DataOutputStream (new FileOutputStream(numFile));

		File treeFile = new File("CollatzTree");
		if (!treeFile.exists())
		{
			treeFile.createNewFile();
		}
		treeIn = new DataInputStream (new FileInputStream(treeFile));
		treeOut =new DataOutputStream (new FileOutputStream(treeFile));
	}
	
	public static void saveToFile () throws IOException
	{	
		numOut.writeLong(currentNum);
		numOut.flush();
		numOut.close();
	}
	
	public static void main (String[] args)
	{
		currentNum = 1;
		stop = true;
//		tree = new HashMap<Long, Node>();
		nodeCache = new Node[1000];
		try
		{
			setUpFiles();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Window window = new Window();
	}
	
	private static class Window extends JFrame
	{
		Display display;
		
		private Window ()
		{
			initUI();
		}
		
		private void initUI ()
		{
			display = new Display();
			add(display);
			
//			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			addWindowListener (new WindowAdapter()
				{
					@Override
					public void windowClosing (WindowEvent e)
					{
						try
						{
							saveToFile();
						}
						catch (IOException ioe)
						{
							ioe.printStackTrace();
						}
							
						dispose();
						System.exit(0);
					}
				});
			
			setSize(500, 400);
			setLocationRelativeTo(null);
			setFocusable(true);
			setVisible(true);
		}
	}
	
	private static class Display extends JPanel
	{
		private ArrayList<Button> buttons;
		
		public Display ()
		{
//			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			Adapter adapter = new Adapter();
			addMouseListener(adapter);
			
			buttons = new ArrayList<Button>();
			buttons.add(new Button(100, 50, "START"));
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
	    	
	    	g2d.setFont(new Font("Arial", Font.PLAIN, 40));
	    	
	    	for (Button b : buttons)
	    	{
	    		b.draw(g2d);
	    	}
	    	
	    	g2d.drawString("Current number: " + currentNum, 10, 200);
		}
		
		//Paints the components
	    public void paintComponent (Graphics g)
	    {
	    	super.paintComponent(g);
	    	Draw(g);
	    }
		
		private class Button
		{	
			private final int HEIGHT = 100;
			private final int WIDTH = 200;
			private final int X_OFFSET = 10;
			private final int Y_OFFSET = 10;
			
			private Rectangle2D.Float back;
			private Rectangle2D.Float fore;
			private String text;
			private Color color;
			
			public Button (int inX, int inY, String inText)
			{
				fore = new Rectangle2D.Float(inX, inY, WIDTH, HEIGHT);
				back = new Rectangle2D.Float(inX, inY, WIDTH+X_OFFSET, HEIGHT+Y_OFFSET);
				text = inText;
				color = Color.green;
			}
			
			public boolean contains (int inX, int inY)
			{
				return fore.getBounds2D().contains(inX, inY) || back.getBounds2D().contains(inX, inY);
			}
			
			public void press()
			{
				fore.setFrame(fore.getX(), fore.getY(), WIDTH+(X_OFFSET / 2), HEIGHT+(Y_OFFSET / 2));
				repaint();
			}
			
			public void release()
			{
				fore.setFrame(fore.getX(), fore.getY(), WIDTH, HEIGHT);
				stop = !stop;
				
				if (!stop)
				{
					color = Color.red;
					text = "STOP";
					
					Node tempNode;
					for (int i = 0; i < nodeCache.length; i++)
//					while (!stop)
					{
//						tempNode = new Node(currentNum);
//						tree.put(currentNum, tempNode);
						nodeCache[i] = new Node(currentNum);
						currentNum++;
						
//						if (currentNum % 1000000 == 999999)
//						{
//							System.out.println(currentNum);
////							stop = true;
////							repaint();
//						}
					}
					
					for (int i = 0; i < nodeCache.length; i++)
					{
//						System.out.println("  " + (i+1));
						nodeCache[i].printNode();
					}
					
				}
				else
				{
					color = Color.green;
					text = "START";
				}
				
//				System.out.println(stop);
				
				repaint();
			}
			
			public void draw (Graphics2D g2d)
			{
				g2d.setPaint(Color.black);
				g2d.fill(back);
				g2d.setPaint(color);
				g2d.fill(fore);
				g2d.setPaint(Color.black);
				g2d.drawString(text, (int) (fore.getX() + (fore.getWidth()/5)), (int) (fore.getY() + (3*fore.getHeight()/5)));
			}
		}
		
		private class Adapter extends MouseAdapter
		{			
			private Button clickedButton = null;
			
			public void mousePressed (MouseEvent e)
			{
//				System.out.println(e.getX() + " " + e.getY());
				
				//check if the user clicked on any buttons
				Button tempButton;
				for (int i = 0; clickedButton == null && i < buttons.size(); i++)
				{
					tempButton = buttons.get(i); 
					if (tempButton.contains(e.getX(), e.getY()))
					{
						clickedButton = tempButton;
					}
				}
				
				if (clickedButton != null)
				{
					clickedButton.press();
				}
			}
			
			public void mouseReleased (MouseEvent e)
			{
				if (clickedButton != null)
				{
					clickedButton.release();
					clickedButton = null;
				}
			}
		}
	}

	private static class Node
	{
		private long next;
		private long odd;
		private long even;
		
		private Node (long inNum)
		{
			next = calcNext(inNum);
			odd = calcOdd(inNum);
			even = calcEven(inNum);
		}
		
		private long calcNext (long inNum)
		{
			if (inNum % 2 == 0)
			{
				return inNum / 2;
			}
			
			return (inNum * 3) + 1;
		}
		
		private long calcEven (long inNum)
		{
			return inNum * 2;
		}
		
		private long calcOdd (long inNum)
		{
			if ((inNum - 1) % 3 == 0 && ((inNum - 1) / 3) % 2 == 1)
			{
				return (inNum - 1) / 3;
			}
			
			return -1;
		}
		
		private void printNode()
		{
			System.out.println("num: " + (even / 2) + " next:" + next + " odd:" + odd + " even:" + even);
		}
	}
}
