/**
 * @(#)Mob.java
 *
 *
 * @author 
 * @version 1.00 2016/7/15
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//A general mob (includes Pacman and ghosts)
public abstract class Mob extends Pixel
{
	//the mob's path towards a target pixel.
	protected ArrayList<Pixel> path = new ArrayList<Pixel>();
	
	//agro radius
	protected final int RADIUS = 4;

    public Mob() 
    {
    	super();
    }
    
    public Mob (int inX, int inY, JPanel inPixel)
    {
    	super(inX, inY, inPixel);
    }
    /*
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
    */
    
    //move the mob
    public abstract void Move() throws InterruptedException;
    
    public boolean CreatePath(Pixel target) throws InterruptedException
    {
    	//list of already-evaluated pixels
		ArrayList<PixelNode> closedSet = new ArrayList<PixelNode>();
		
		//Create the start pixel node (pacman) and the target node (the pellet)
		PixelNode start = new PixelNode(this);
		PixelNode goal = new PixelNode(target);
		
		//Set the defualt gscore and fscore of the starting point.
		start.setGScore(0);
		start.setFScore(FindFScore(start, goal));
		
		//list of pixels discovered but not yet evaluated. Starts with the starting pixel.
		ArrayList<PixelNode> openSet = new ArrayList<PixelNode>();
		openSet.add(start);
		
		//While there are still nodes that can be evaluated...
		while (openSet.size() > 0)
		{
			//unevaluated node that has the lowest fScore
			PixelNode current = LowestFScore(openSet);
			//System.out.println("C" + current.getPosString());
			
			//If the current node is the goal, create the path
			if (current.getPixel() == goal.getPixel())
			{
				//Make the path.
				return MakePath (current, start);
			}
			
			//otherwise
			else
			{
				//move the current node to the closed set (it has been evaluated)
				openSet.remove(current);
				closedSet.add(current);
				
				//color's the field (aethetic).
				//current.setColor(new Color ((int)(7 * current.getFScore()), 0, 0));
				
				//this gets rid of diagonal neighbors (so pacman only moves up, down, left, right)
				int diag = -1;
				
				//for each non-wall non-mob neighbor of the current node... (no diagonals)
				for (int r = -1; r < 2; r++)
				{
					for (int c = -1; c < 2; c++)
					{
						if ((r != 0 || c != 0) &&
							(current.getY() + r > -1 && current.getY() + r < PacMan.SIZEY) &&
							(current.getX() + c > -1 && current.getX() + c < PacMan.SIZEX) &&
							(diag == 1))
						{
							PixelNode neighbor = new PixelNode (PacMan.grid.get(current.getY() + r).get(current.getX() + c));
							//System.out.println(neighbor.getPosString());
							
							//If the neighbor is not already in the closed set and is not a wall or mob...
							if (!ContainsPixel(neighbor, closedSet) &&
								//!neighbor.getColor().equals(Color.blue)
								(neighbor.getColor().equals(Color.white) || neighbor.getColor().equals(Color.black) ||
								neighbor.getColor().equals(goal.getPixel().getColor())))
							{
								//gScore of a neighbor (may not be the best)
								double tempGScore = current.getGScore() + FindDist(current, neighbor);
				
								//if the neighbor is not in the open set, put it in the open set for future evaluation.
								if (!ContainsPixel(neighbor, openSet))
								{
									//TimeUnit.MILLISECONDS.sleep(200);
									openSet.add(neighbor);
									//neighbor.setColor(Color.gray);
									
									//Set the neighbor gscore, fscore, and camefrom if it does not already
									//have one. It'll be replaced if a more efficient path is found.
									if (neighbor.getGScore() == 0)
									{
										neighbor.setCameFrom(current);
										neighbor.setGScore(tempGScore);
										neighbor.setFScore(FindFScore(neighbor, goal));
									}
								}
								
								//If the path is the best one found so far, record it in the node's info.
								else if (tempGScore < neighbor.getGScore())
								{
									//TimeUnit.MILLISECONDS.sleep(500);
									//neighbor.setColor(Color.grey);
									
									neighbor.setCameFrom(current);
									neighbor.setGScore(tempGScore);
									neighbor.setFScore(FindFScore(neighbor, goal));
								}
							}
						}
						
						diag *= -1;
					}
				}
			}
		}
		
		System.out.println("No Path Found");
		
		return false;
    }
    
    //Finds fScore (distance between start and goal while passing through a given node).
	public static double FindFScore (PixelNode inNode, PixelNode goal)
	{
		//An estimate of how much distance is between the nod and the goal. (linear distance formula)
		double distLeft = FindDist(inNode, goal);
		
		return distLeft + inNode.getGScore();
	}
	
	//Find the linear distance between two given nodes.
	public static double FindDist(PixelNode one, PixelNode two)
	{
		return Math.sqrt(Math.pow(one.getX() - two.getX() , 2) + Math.pow(one.getY() - two.getY() , 2));
	}
	
	public static double FindDist(Pixel one, Pixel two)
	{
		return Math.sqrt(Math.pow(one.getPos().GetX() - two.getPos().GetX() , 2) + Math.pow(one.getPos().GetY() - two.getPos().GetY() , 2));
	}
    
    //Find the node in a given list that has the lowest fScore.
	public static PixelNode LowestFScore (ArrayList<PixelNode> set)
	{
		//lowest fScore so far.
		double lowest = Double.MAX_VALUE;
		
		//Node with the lowest fScore
		PixelNode lowestNode = new PixelNode();
		
		//for each node in a set
		for(PixelNode node: set)
		{
			//if the node has a lower fScore than the current lowest, make the node the lowest node.
			if (node.getFScore() < lowest)
			{
				lowest = node.getFScore();
				lowestNode = node;
			}
		}
		
		return lowestNode;
	}
	
	public static boolean ContainsPixel (PixelNode inNode, ArrayList<PixelNode> inSet)
	{
		Pixel inPixel = inNode.getPixel();
		
		for (PixelNode node : inSet)
		{
			if (node.getPixel() == inPixel)
			{
				return true;
			}
		}
		
		return false;
	}
    
    //Make the path (from goal the start)
	public boolean MakePath (PixelNode goal, PixelNode start)
	{
		PixelNode current = goal;
		
		//System.out.println(current);
		
		//while the path has not reached the start...
		while (current.getPixel() != start.getPixel())
		{
			path.add(current.getPixel());
			current = current.getCameFrom();
		}
		
		return true;
	}
	
	//Switches two adjacent pixels (used in default movement).
	public void SwitchPixels ()
	{
		//The adjacent pixel
		Pixel pathPixel = path.remove(path.size() - 1);
    	
    	//Switch the two Pixels' positions on the grid.
    	PacMan.grid.get(getPos().GetY()).set(getPos().GetX(), pathPixel);
    	PacMan.grid.get(pathPixel.getPos().GetY()).set(pathPixel.getPos().GetX(), this);
    	
    	//Switch the two pixel's JPanels
    	JPanel tempPixel = pathPixel.getPixel();
    	pathPixel.setPixel(getPixel());
    	setPixel(tempPixel);
    	setColor (Color.yellow);
    	pathPixel.setColor (Color.black);
    		    	
    	//Switch the two pixels' positions
    	Coord tempPos = pathPixel.getPos();
    	pathPixel.setPos(getPos());
    	setPos(tempPos);
	}
}