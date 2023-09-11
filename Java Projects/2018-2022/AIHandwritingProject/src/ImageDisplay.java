
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageDisplay extends JFrame
{	
	private String text = "Test results:";
	
	public ImageDisplay (int inRows, int inCols, Node[] inNodes)
	{
		InitUI(inRows, inCols, inNodes);
	}
	
	private void InitUI(int inRows, int inCols, Node[] inNodes)
	{
		ImagePanel panel = new ImagePanel(inRows, inCols, inNodes);
		add(panel);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	double screenWidth = screenSize.getWidth();
    	double screenHeight = screenSize.getHeight();
		
		setTitle("imageSurface");
//		setSize(1000, 1000);
		setSize ((int) (screenWidth/1.5), (int) (screenHeight/1.33));
		setLocationRelativeTo(null);
		setFocusable(true);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setText (String newText) {
		text = newText;
	}
	
	private class ImagePanel extends JPanel
	{
		//the nodes that holds the pixel values of the image
		private Node[] imageNodes;
		private Rectangle2D.Float[][] displayGrid;
		private final int TILE_SIZE = 10;
		
		public ImagePanel (int inRows, int inCols, Node[] inNodes)
		{
			imageNodes = inNodes;
			
			displayGrid = new Rectangle2D.Float[inRows][inCols];
			for (int r = 0; r < inRows; r++)
			{
				for (int c = 0; c < inCols; c++)
				{
					displayGrid[r][c] = new Rectangle2D.Float(TILE_SIZE * c, TILE_SIZE * r, TILE_SIZE, TILE_SIZE);
				}
			}
		}

		
		//draws the components
		private void draw (Graphics g)
		{			
			
			Graphics2D g2d = (Graphics2D) g;
			
			RenderingHints rh = new RenderingHints (
		    		RenderingHints.KEY_ANTIALIASING,
		    		RenderingHints.VALUE_ANTIALIAS_ON);	
	    	rh.put(RenderingHints.KEY_RENDERING,
	    		RenderingHints.VALUE_RENDER_QUALITY);
	    	g2d.setRenderingHints (rh);
	    	
	    	float tileColor;
	    	int pixelIndex = 0;

	    	
	    	for (int r = 0; r < displayGrid.length; r++)
	    	{
	    		for (int c = 0; c < displayGrid[r].length; c++)
	    		{
	    			tileColor = imageNodes[pixelIndex].getVal();
	    			pixelIndex++;
	    			g2d.setPaint(new Color(tileColor, tileColor, tileColor));
	    			g2d.fill(displayGrid[r][c]);
	    		}
	    	}
	    	
	    	g2d.drawString(text, 0, 300);
		}
		
		//Paints the components
	    public void paintComponent (Graphics g)
	    {
	    	super.paintComponent(g);
	    	draw(g);
	    }
	}
}