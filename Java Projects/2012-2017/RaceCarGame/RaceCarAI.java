/**
 * @(#)RaceCarAI.java
 *
 *This object holds the AI and the methods of the game.
 *
 * @author 
 * @version 1.00 2016/6/11
 */
import java.io.*;
import java.util.Scanner;
import java.lang.Math;

public class RaceCarAI
{
	//The txt file.
	static File txtFile = new File("RaceCarGameData.txt");
	
	//writer that can modify the txt file.
	//FileWriter writer = new FileWriter (txtFile);
	
	//temporary memory extracted from the txt file (column, direction value).
	double[][] memory;
	
	//like normal memory, but for the edges of the road.
	double[][] edgeMem;
	
	//max length required for success.
	int length;
	
	//2d array that makes the road.
	char[][] road = {{' ', ' ', '|', ' ', ' '},
					 {' ', ' ', ' ', ' ', ' '},
					 {' ', ' ', ' ', ' ', ' '},
					 {' ', ' ', ' ', ' ', ' '},
					 {' ', ' ', ' ', ' ', ' '}};
					 
	//the horizontal position of the player (0 to 4).
	int playerPos = 2;
	
	//direction the player is moving;
	int dir;
	
	//stores the last adjacent row (used to analyze a hit)
	char[] lastAdjRow;
	
	//the symbol used to represent the player (changes based on whether the player is turning).
	char symbol = '|';
	
    public RaceCarAI() throws IOException
    {
    	//read in the txt file and put it in temporary memory.
    	Scanner reader = new Scanner (txtFile);
    	
    	memory = new double[reader.nextInt()][3];
		for (int c = 0; c < memory.length; c++)
		{
			for (int i = 0; i < 3; i++)
			{
				memory[c][i] = reader.nextDouble();
			}
		}
		
		edgeMem = new double[reader.nextInt()][2];
		for (int c = 0; c < edgeMem.length; c++)
		{
			for (int i = 0; i < 2; i++)
			{
				edgeMem[c][i] = reader.nextDouble();
			}
		}
		
		length = reader.nextInt();
		
		//System.out.println(length);
    }
    
    //Displays the track. (note: try to replace the X's with |, \, and / to better show the path)
    public void Display()
    {
    	//shows the road in chunks of 5. 
    	/*
    	for (char[] r: road)
    	{
    		System.out.print("|");
    		for (char item: r)
    		{
    			System.out.print(item);
    		}
    		
    		System.out.print("|\n");
    	}
    	
    	System.out.print("\n");
    	*/
    	
    	
    	//shows the road as continuous.
    	System.out.print("|");
    	for (char item: road[0])
    	{
    		System.out.print(item);
    	}
    	System.out.print("|\n");
    	
    }
    
    //Generates the road, along with the road spikes on the road.
    public void MakeRoad()
    {
    	//shift the rows up.
    	int rand = (int)(Math.random() * 5);
    	for (int i = 1; i < road.length; i++)
    	{
    		road[i-1] = road[i];
    	}
    	road[0][playerPos] = symbol;
    	
    	//create the new row.
    	char[] temp = new char[5];
    	for (int i = 0; i < road[0].length; i++)
    	{
    		if (i == rand)
    		{
    			temp[i] = '^';
    		}
    		
    		else
    		{
    			temp[i] = ' ';
    		}
    	}
    	road[road.length-1] = temp;
    }
    
    //Moves the player (if necessary) based on the data in the txt file. (note: make it so that if there
    //are not adjacent spikes, the player will look at the next row and act as if the next row is adjecent)
    public void Move()
    {
    	//looks at the row adjacent to the player.
    	//char[] adjRow = FindAdjRow();
    	lastAdjRow = FindAdjRow();
    	
    	//look for the spike in the adjacent row.
    	int spikeSpot = FindSpikes(lastAdjRow);
    	/*
    	int spikeSpot = -1;
    	for (int c = 0; c < adjRow.length; c++)
    	{
    		if (adjRow[c] == '^')
    		{
    			spikeSpot = c;
    		}
    	}
    	*/
    	
    	//look for the road's edge;
    	int edgeSpot = FindEdge(lastAdjRow);
    	/*
    	int edgeSpot = -1;
    	for (int c = 0; c < adjRow.length; c++)
    	{
    		if (adjRow[c] == '#')
    		{
    			edgeSpot = c;
    		}
    	}
    	*/
    	
    	//corrects the placement of the spike spot if the edge spot is to the left.
    	if (edgeSpot == 0)
    	{
    		spikeSpot--;
    	}
    	
    	//System.out.println(spikeSpot);
    	
    	//set the player symbol to the default
    	symbol = '|';
    	
    	//Semi-randomly decides which direction to go based on the data in the txt file
    	//(if there is actually a spike).
    	if (spikeSpot > -1)
    	{
    		//decides a direction to go based on the txt data and the random number.
    		double tempRange;
    		
    		//Add the probabilities together.
    		double probTotal = 0;
    		
    		//If the player is on the edge of the road...
    		if(edgeSpot > -1)
    		{
    			tempRange = edgeMem[spikeSpot + edgeSpot][0];
    			
    			for (int i = 0; i < 2; i++)
	    		{
	    			probTotal += edgeMem[spikeSpot + edgeSpot][i];
	    		}
	    		
	    		//make a random number in rand of the probability total.
    			double rand = Math.random() * probTotal;
    			
    			//determine the direction using the random number and the txt data.
    			for (dir = -1; tempRange < rand; dir++)
	    		{
	    			tempRange += edgeMem[spikeSpot + edgeSpot][dir+2];
	    		}
	    		
	    		//shift the direction over by 1 if the edge of the road is to the left.
	    		if (edgeSpot == 0)
	    		{
	    			dir++;
	    		}
    		}
    		//otherwise...
    		else
    		{
    			tempRange = memory[spikeSpot][0];
    			
	    		for (int i = 0; i < 3; i++)
	    		{
	    			probTotal += memory[spikeSpot][i];
	    		}
	    		
	    		//make a random number in rand of the probability total.
    			double rand = Math.random() * probTotal;
    			
    			//determine the direction using the random number and the txt data.
    			for (dir = -1; tempRange < rand; dir++)
	    		{
	    			tempRange += memory[spikeSpot][dir+2];
	    		}
    		}
    		
    		/*
    		 tempRange = memory[spikeSpot][0];
    			
    		double probTotal = 0;
    			
    		for (int i = 0; i < 3; i++)
    		{
    			probTotal += memory[spikeSpot][i];
    		}
    		
    		//make a random number in rand of the probability total.
			double rand = Math.random() * probTotal;
    			
    		for (dir = -1; tempRange < rand; dir++)
    		{
    			tempRange += memory[spikeSpot][dir+2];
    		}
    		*/
    		//System.out.println(spikeSpot);
	    	//System.out.println(dir);
    		
    		//move the player.
    		if(playerPos + dir > -1 && playerPos + dir < road[0].length)
    		{
    			playerPos += dir;
    		}
    		
    		//change the player's symbol based on their movement.
    		if (dir == -1)
    		{
    			symbol = '/';
    		}
    		else if (dir == 1)
    		{
    			symbol = '\\';
    		}
    	}
    }
    
    //finds the spikes adjacent to the player (-1 if there are no spikes).
    public int FindSpikes(char[] adjRow)
    {
    	int spikeSpot = -1;
    	for (int c = 0; c < adjRow.length; c++)
    	{
    		if (adjRow[c] == '^')
    		{
    			spikeSpot = c;
    		}
    	}
    	
    	return spikeSpot;
    }
    
    //finds the edge (0 if the edge is not near the player).
    public int FindEdge (char[] adjRow)
    {
    	int edgeSpot = -1;
    	for (int c = 0; c < adjRow.length; c++)
    	{
    		if (adjRow[c] == '#')
    		{
    			edgeSpot = c;
    		}
    	}
    	
    	return edgeSpot;
    }
    
    //finds what is adjacent to the player.
    public char[] FindAdjRow ()
    {
    	char[] adjRow = new char[3];
    	for(int i = -1; i < 2; i++)
    	{
    		if(i + playerPos < 0 || i + playerPos >= road[1].length)
    		{
    			adjRow[i+1] = '#';
    		}
    		else
    		{
    			adjRow[i+1] = road[1][i+playerPos];
    		}
    	}
    	
    	return adjRow;
    }
    
    //Checks to see if the player hit a spike.
    public boolean isHit() throws IOException
    {
    	for(int i = 0; i < road[1].length; i++)
    	{
    		if (road[1][i] == '^' && i == playerPos)
    		{
    			//show the part of the road that the player crashed into.
    			System.out.print("|");
    			for (char item: road[1])
		    	{
		    		System.out.print(item);
		    	}
		    	System.out.print("|\n");
    			System.out.println("HIT");
    			//System.out.println(dir);
    			
    			//Learn from the hit.
    			AnalyzeHit();
    			
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    //Checks to see if the player reached the goal length.
    public boolean isWin(int count) throws IOException
    {
    	if (count == length)
    	{
    		//show the last part of the road.
			System.out.print("|");
			for (char item: road[1])
	    	{
	    		System.out.print(item);
	    	}
	    	System.out.print("|\n");
    		System.out.println("WIN");
    		
    		if(FindSpikes(FindAdjRow()) != -1)
    		{
    			AnalyzeWin();
    		}
    		
    		//Upgrade the length.
    		length += 10;
    		
    		//Put the new data to memory.
    		CommitMem();
    		
    		return true;
    	}
    	
    	return false;
    }
    
    //Analyzes what went wrong and how the AI will react accordingly.
    public void AnalyzeHit() throws IOException
    {
    	int spikeSpot = FindSpikes(lastAdjRow);
    	int edgeSpot = FindEdge(lastAdjRow);
    	
    	/*
    	//corrects the placement of the spike spot and direction if the edge spot is to the left. (for array purposes).
    	if (edgeSpot == 0)
    	{
    		spikeSpot--;
    		dir--;
    	}
    	*/
    	
    	//if there was an edge...
    	//if (edgeSpot > -1)
    	if (edgeSpot == 0)
    	{
    		//discourage the direction taken during the hit in memory.
    		edgeMem[spikeSpot - 1 + edgeSpot][dir] *= .5;
    	}
    	
    	else if (edgeSpot == 2)
    	{
    		edgeMem[spikeSpot + edgeSpot][dir + 1] *= .5;
    	}
    	
    	//otherwise...
    	else
    	{
    		//discourage the direction taken during the hit in memory.
    		memory[spikeSpot][dir+1] *= .5;
    	}
    	
    	//Put the new data to memory.
    	CommitMem();
    }
    
    public void AnalyzeWin() throws IOException
    {
    	int spikeSpot = FindSpikes(lastAdjRow);
    	int edgeSpot = FindEdge(lastAdjRow);
    	
    	//if there was an edge...
    	if (edgeSpot == 0)
    	{
    		//encourage the direction taken that caused the win in memory.
    		edgeMem[spikeSpot - 1 + edgeSpot][dir] += .5 * (1 - edgeMem[spikeSpot - 1 + edgeSpot][dir]);
    	}
    	
    	else if (edgeSpot == 2)
    	{
    		edgeMem[spikeSpot + edgeSpot][dir + 1] += .5 * (1 - edgeMem[spikeSpot + edgeSpot][dir + 1]);
    	}
    	
    	//otherwise...
    	else
    	{
    		//encourage the direction taken that caused the win in memory.
    		memory[spikeSpot][dir+1] += .5 * (1 - memory[spikeSpot][dir+1]);
    	}
    }
    
    //Puts the augmented data to memory.
    public void CommitMem() throws IOException
    {
    	FileWriter writer = new FileWriter(txtFile);
    	writer.write("3 ");
    	writer.write(System.lineSeparator());
    	
    	for (double[] row: memory)
    	{
    		for(double item: row)
    		{
    			writer.write(item + " ");
    		}
    		
    		writer.write(System.lineSeparator());
    	}
    	
    	writer.write("4 ");
    	writer.write(System.lineSeparator());
    	
    	for (double[] row: edgeMem)
    	{
    		for(double item: row)
    		{
    			writer.write(item + " ");
    		}
    		
    		writer.write(System.lineSeparator());
    	}
    	
    	//System.out.println("Remembering length:" + length);
    	writer.write(length + "");
    	//writer.write(" test");
    	
    	writer.flush();
    	writer.close();
    }
    
    //Resets the memory.
    public static void ResetMem() throws IOException
    {
    	FileWriter writer = new FileWriter(txtFile);
    	writer.write("3 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write("4 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write(".5 .5 ");
    	writer.write(System.lineSeparator());
    	writer.write("10");
    	writer.flush();
    	writer.close();
    }
}