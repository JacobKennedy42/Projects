/**
 * @(#)Pixel.java
 *
 *
 * @author 
 * @version 1.00 2016/7/11
 */
 import javax.swing.*;
 import java.awt.*;

public class Pixel 
{
	//the pixel's xy position.
	protected Coord pos;
	
	//the pixel's jPanel (includes its color)
	protected JPanel myPixel = new JPanel();
	
    public Pixel () 
    {
    	pos = new Coord (0, 0);
    	myPixel.setBackground(Color.black);
    }
    
    public Pixel (int inX, int inY, JPanel inPixel)
    {
    	pos = new Coord (inX, inY);
    	myPixel = inPixel;
    }
    
    public Coord getPos()
    {
    	return pos;
    }
    
    public void setPos(Coord inPos)
    {
    	pos = inPos;
    }
    
    public Color getColor()
    {
    	return myPixel.getBackground();
    }
    
    public void setColor (Color inColor)
    {
    	myPixel.setBackground (inColor);
    }
    
    public void setPixel (JPanel inPixel)
    {
    	myPixel = inPixel;
    }
    
    public JPanel getPixel()
    {
    	return myPixel;
    }
    
    //Searches for a specified pixel (based on color).
    public Pixel Search(Color inColor, int range) throws InterruptedException
    {
    	int layer = 1;
    	
    	while (layer < range)
    	{
    		//Start in the upper-left corner.
    		int r = pos.GetY() - layer;
    		int c = pos.GetX() - layer;
    		
    		//while the current layer has not been fully evaluated...
    		do
    		{
				//if the pixel tested exists in the grid...
				if ((r > -1 && r < PacMan.SIZEY) &&
					(c > -1 && c < PacMan.SIZEX))
				{
					//TimeUnit.MILLISECONDS.sleep(200);
					
					//the pixel being tested
					Pixel currentPixel = PacMan.grid.get(r).get(c);
					
					//if the pixel tested is a pellet, return it.
					if (currentPixel.getColor().equals(inColor))
					{
						return currentPixel;
					}
					
					//currentPixel.setColor(Color.gray);
					
				}
				
				//increment based on the current side.
				//upper
				if (r == pos.GetY() - layer && c < pos.GetX() + layer)
				{
					c++;
				}
				
				//right
				else if (c == pos.GetX() + layer && r < pos.GetY() + layer)
				{
					r++;
				}
				
				//lower
				else if (r == pos.GetY() + layer && c > pos.GetX() - layer)
				{
					c--;
				}
				
				//left
				else if (c == pos.GetX() - layer && r > pos.GetY() - layer)
				{
					r--;
				}
				
    		}	 while (r != pos.GetY() - layer || c != pos.GetX() - layer);
    		
    		//increment the layer.
    		layer++;
    	}
    	
    	return null;
    }
    
    //Searches for pixels based on two colors (probably inefficient/bad method writing. Clean up if needed).
    public Pixel Search (Color colorOne, Color colorTwo, int range)
    {
    	int layer = 1;
    	
    	while (layer < range)
    	{
    		//Start in the upper-left corner.
    		int r = pos.GetY() - layer;
    		int c = pos.GetX() - layer;
    		
    		//while the current layer has not been fully evaluated...
    		do
    		{
				//if the pixel tested exists in the grid...
				if ((r > -1 && r < PacMan.SIZEY) &&
					(c > -1 && c < PacMan.SIZEX))
				{
					//TimeUnit.MILLISECONDS.sleep(200);
					
					//the pixel being tested
					Pixel currentPixel = PacMan.grid.get(r).get(c);
					
					//if the pixel tested is a pellet, return it.
					if (currentPixel.getColor().equals(colorOne) ||
						currentPixel.getColor().equals(colorTwo))
					{
						return currentPixel;
					}
					
					//currentPixel.setColor(Color.gray);
					
				}
				
				//increment based on the current side.
				//upper
				if (r == pos.GetY() - layer && c < pos.GetX() + layer)
				{
					c++;
				}
				
				//right
				else if (c == pos.GetX() + layer && r < pos.GetY() + layer)
				{
					r++;
				}
				
				//lower
				else if (r == pos.GetY() + layer && c > pos.GetX() - layer)
				{
					c--;
				}
				
				//left
				else if (c == pos.GetX() - layer && r > pos.GetY() - layer)
				{
					r--;
				}
				
    		}	 while (r != pos.GetY() - layer || c != pos.GetX() - layer);
    		
    		//increment the layer.
    		layer++;
    	}
    	
    	return null;
    }
}