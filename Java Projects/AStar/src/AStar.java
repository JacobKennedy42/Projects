/**
 * @(#)AStar.java
 *
 * AStar application
 *
 *Where we left off: Problems with came from. change gscore form direct distance to total cost of the current
 *	path to the node (takes curves into account). still define fscore somehow so that open set can find the
 *	lowest one (maybe define gscore and fscore when discovering a node).
 *
 * @author 
 * @version 1.00 2016/6/28
 */
 import javax.swing.*;
 import java.awt.*;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.util.concurrent.TimeUnit;
 import java.lang.Double;
 
//A program meant to demonstrate A* path finding.
public class AStar 
{
	//size of the display grid.
    public static final int SIZE = 15;
    
    //the entire gui frame.
    public static JFrame GUI = new JFrame();
    
    //the container of the gui components
    public static Container pane = GUI.getContentPane();
    
    //a 2d list representation of the grid display using nodes.
    public static ArrayList<ArrayList<Node>> grid = new ArrayList<ArrayList<Node>>();
    
    public static void main(String[] args) throws InterruptedException
    {
    	//Set up the GUI grid.
    	GUI.setTitle("A*");
    	GUI.setSize(500, 500);
    	GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	pane.setLayout(new GridLayout(SIZE, SIZE));
    	
    	//Create the grid.
    	Initialize();
    	
    	GUI.setVisible(true);
    	
    	//Do the actual pathfinding (corner start and goal)
    	PathFinding(grid.get(SIZE-1).get(0), grid.get(0).get(SIZE-1));
    }

	//Initialize the grid.
	public static void Initialize()
	{
		for (int r = 0; r < SIZE; r++)
		{
			ArrayList<Node> tempRow = new ArrayList<Node>();
			
			for (int c = 0; c < SIZE; c++)
			{
				//random number used for maze.
				int rand = (int)(Math.random() * 3);
				
				JPanel tempPixel = new JPanel();
				
				//default color.
				tempPixel.setBackground(Color.gray);
			
				//place the goal
				if (r == 0 && c == SIZE - 1)
				{
					tempPixel.setBackground(Color.green);
				}
				
				//place the start
				else if (r == SIZE - 1 && c == 0)
				{
					tempPixel.setBackground(Color.red);
				}
				
				/*
				//place the walls (open at start)
				else if ((r == SIZE/4 && c > SIZE/4 && c < (3 * SIZE / 4) + 1) || (c == (3 * SIZE / 4) && r > SIZE/4 && r < (3 * SIZE / 4)))
				{
					tempPixel.setBackground(Color.black);
				}
				*/
				/*
				//place the walls (open at goal)
				else if ((c == SIZE/4 && r > SIZE/4 && r < (3 * SIZE / 4) + 1) || (r == (3 * SIZE / 4) && c > SIZE/4 && c < (3 * SIZE / 4)))
				{
					tempPixel.setBackground(Color.black);
				}
				*/
				//Place the walls (maze-ish)

				else if (rand == 0)
				{
					tempPixel.setBackground(Color.black);
				}
				
				
				//place the pixel into the container
				pane.add(tempPixel);
				
				tempRow.add(new Node(tempPixel, new Coord(c, r)));
			}
			
			grid.add(tempRow);
		}
	}
	
	//Pathfinding algorith.
	public static boolean PathFinding(Node start, Node goal) throws InterruptedException
	{
		//list of already-evaluated pixels
		ArrayList<Node> closedSet = new ArrayList<Node>();
		
		//Set the defualt gscore and fscore of the starting point.
		start.setGScore(0);
		start.setFScore(FindFScore(start, goal));
		
		//list of pixels discovered but not yet evaluated. Starts with the starting pixel.
		ArrayList<Node> openSet = new ArrayList<Node>();
		openSet.add(start);
		
		//While there are still nodes that can be evaluated...
		while (openSet.size() > 0)
		{
			//unevaluated node that has the lowest fScore
			Node current = LowestFScore(openSet);
			//System.out.println("C" + current.getPosString());
			
			//If the current node is the goal, create the path
			if (current == goal)
			{
				//Make the path.
				return MakePath (goal, start);
			}
			
			//otherwise
			else
			{
				//move the current node to the closed set (it has been evaluated)
				openSet.remove(current);
				closedSet.add(current);
				
				//color's the field (aethetic).
				current.setColor(new Color (255, 180, 180));
				
				//for each non-wall neighbor of the current node...
				for (int r = -1; r < 2; r++)
				{
					for (int c = -1; c < 2; c++)
					{
						if ((r != 0 || c != 0) &&
							(current.getY() + r > -1 && current.getY() + r < SIZE) &&
							(current.getX() + c > -1 && current.getX() + c < SIZE))
						{
							Node neighbor = grid.get(current.getY() + r).get(current.getX() + c);
							//System.out.println(neighbor.getPosString());
							
							//If the neighbor is not already in the closed set and is not a wall...
							if (!closedSet.contains(neighbor) && !neighbor.getColor().equals(Color.black))
							{
								//gScore of a neighbor (may not be the best)
								double tempGScore = current.getGScore() + FindDist(current, neighbor);
				
								//if the neighbor is not in the open set, put it in the open set for future evaluation.
								if (!openSet.contains(neighbor))
								{
									TimeUnit.MILLISECONDS.sleep(400);
									openSet.add(neighbor);
									neighbor.setColor(Color.white);
									
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
									TimeUnit.MILLISECONDS.sleep(400);
									neighbor.setColor(Color.white);
									
									neighbor.setCameFrom(current);
									neighbor.setGScore(tempGScore);
									neighbor.setFScore(FindFScore(neighbor, goal));
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println("No Path Found");
		
		return false;
	}
	
	//Finds fScore (distance between start and goal while passing through a given node).
	public static double FindFScore (Node inNode, Node goal)
	{
		//An estimate of how much distance is between the nod and the goal. (linear distance formula)
		double distLeft = FindDist(inNode, goal);
		
		return distLeft + inNode.getGScore();
	}
	
	//Find the linear distance between two given nodes.
	public static double FindDist(Node one, Node two)
	{
		return Math.sqrt(Math.pow(one.getX() - two.getX() , 2) + Math.pow(one.getY() - two.getY() , 2));
	}
	
	//Find the node in a given list that has the lowest fScore.
	public static Node LowestFScore (ArrayList<Node> set)
	{
		//lowest fScore so far.
		double lowest = Double.MAX_VALUE;
		
		//Node with the lowest fScore
		Node lowestNode = new Node();
		
		//for each node in a set
		for(Node node: set)
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
	
	//Make the path (from goal the start)
	public static boolean MakePath (Node goal, Node start)
	{
		Node current = goal.getCameFrom();
		
		//while the path has not reached the start...
		while (current != start)
		{
			current.setColor(Color.blue);
			current = current.getCameFrom();
		}
		
		return true;
	}
}