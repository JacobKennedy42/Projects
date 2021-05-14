import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//TODO:
//add zoom feature
//	zoom into the selected point
//		make click adapter to select point to zoom into
//		periodically refind the edge point to zoom into
//		
//
//would likes:
//switch from using doubles to BigDecimal
//
//Bugs:
//clicking far away from the set won't trigger the zoom
//
//
//TODAY:
//Implement the mouse adapter


public class Mandelbrot extends JFrame
{
	private static final int PIXEL_WIDTH = 5;
	private static final int NUM_PIXELS = 1000/PIXEL_WIDTH;//1000;
	private static final short NUM_ITERATIONS = (short) Math.pow(2, 8);
	//The framerate when zooming in (given in milliseconds / frame)
	private static final int FRAME_RATE = 50; //50 ms/frame = 20 frames per second
	private static Surface surface;
	
	//TODO: refactor this into coords as distanceFrom 
	private static double calcDist (Coords c1, Coords c2)
	{
		if (c1 == null || c2 == null)
		{
			//undefined distance
			return -1;
		}
		
		double rDiff = c1.r-c2.r;
		double cDiff = c1.c-c2.c;
		return Math.sqrt(rDiff*rDiff + cDiff*cDiff);
	}
	
	static class Coords
	{
		public int r;
		public int c;
		
		public Coords (int inR, int inC)
		{
			r = inR;
			c = inC;
		}
		
		public boolean equals (Coords other)
		{
			return r==other.r && c==other.c;
		}
		
		public String toString()
		{
			return "(" + c + "," + r + ")";
		}
	}
	
	class Complex
	{
		private double _real;
		private double _imag;
		
		public Complex (double real, double imag)
		{
			_real = real;
			_imag = imag;
		}
		
		//mutating, returns this Complex, leaves other unchanged
		public Complex add (Complex other)
		{
			_real += other._real;
			_imag += other._imag;
			return this;
		}
		
		//mutating, returns this Complex, leaves other unchanged
		public Complex multiply (Complex other)
		{
			double newReal = (_real * other._real) - (_imag * other._imag);
			double newImag = (_real * other._imag) + (other._real * _imag);
			_real = newReal;
			_imag = newImag;
			return this;
		}
		
		//check if the absolute values of the Complex are in a given bounding square
		public boolean inBounds (double bound)
		{
			return Math.abs(_real) < bound && Math.abs(_imag) < bound;
		}
	}
	
	//a linked list of coordinates
	private static class CoordsList implements Iterable<Coords>
	{
		private class Node
		{
			private Node _next;
			private Coords _coords;
			private double _distance;
			
			public Node (Node next, Coords coords, double distance)
			{
				_next = next;
				_coords = coords;
				_distance = distance;
			}
			
			//return if Node has the given r and c
			public boolean matches (Coords coords)
			{
				return _coords.equals(coords);
			}
			
			public Node getNext()
			{
				return _next;
			}
			
			public void setNext (Node next)
			{
				_next = next;
			}
			
			public double getDistance ()
			{
				return _distance;
			}
			
			//return _r and _c
			public Coords getCoords ()
			{
				return _coords;
			}
		}
		
		//points to a dummy head instead of the actual front of the list,
		//to make node removal easier
		private Node _head;
		
		public CoordsList ()
		{
			//dummy node, just used to point to the front
			_head = new Node(null, null, 0.0);
		}
		
		public CoordsList add (Coords point, Coords center)
		{
			double distance = calcDist(point, center);
			Node newFront = new Node(_head.getNext(), point, distance);
			_head.setNext(newFront);
			return this;
		}
		
		public boolean isEmpty()
		{
			return _head.getNext() == null;
		}
		
		public boolean contains (Coords coords)
		{
			Node current = _head.getNext();
			while (current != null)
			{
				if (current.matches(coords))
				{
					return true;
				}
				current = current.getNext();
			}
			return false;
		}
		
		//remove the node with the lowest distance and
		//return its coordinates (row, column)
		public Coords removeClosest ()
		{
			Node prev = _head;
			Node next = _head.getNext();
			double nextDist;
			//the prev whose next has the minimum distance
			Node minPrev = _head;
			double min = Double.MAX_VALUE;
			while (next != null)
			{
				nextDist = next.getDistance();
				if (nextDist < min)
				{
					min = nextDist;
					minPrev = prev;
				}
				prev = next;
				next = prev.getNext();
			}
			
			//remove the node and return its coordinates
			Node minNext = minPrev.getNext();
			if (minNext != null)
			{
				minPrev.setNext(minNext.getNext());
				return minNext.getCoords();
			}
			return null;
		}
		
		public void removeAll()
		{
			_head.setNext(null);
		}
		
		private class CLIterator implements Iterator<Coords>
		{
			Node _cursor;
			
			public CLIterator ()
			{
				_cursor = _head.getNext();
			}
			
			public boolean hasNext ()
			{
				return _cursor != null;
			}
			
			public Coords next()
			{
				if (hasNext())
				{
					Coords output = _cursor.getCoords();
					_cursor = _cursor.getNext();
					return output;
				}
				return null;
				
			}
		}
		
		public Iterator<Coords> iterator ()
		{
			return new CLIterator();
		}
		
		//TODO: delete this
		public int dbSize()
		{
			Node current = _head;
			int output = 0;
			while (current.getNext() != null)
			{
				++output;
				current = current.getNext();
			}
			return output;
		}
	}
	
	class MBSet
	{
		private static final double THRESHOLD = 1000;
		//how precisly the findClosestEdge method finds the edge of the set
		//1 = check every pixel, 2 = check every other pixel, etc.
		private static final char EDGE_GRANULARITY = 4;
		//toggles the visuallization of the edge_finding algorithm
		private static final boolean DB_COLOR_EDGE_FIND = false;
		private short _setIterations[][];
		//bounds for the values to be inputed into f(z) = z^2 + c
		private double _rLo, _rHi, _cLo, _cHi;
		//number of iterations done before assuming convergence
		private short _iterations;
		
		public MBSet (short iterations)
		{
			_setIterations = new short[NUM_PIXELS][NUM_PIXELS];
			_rLo = -2;
			_rHi = 2;
			_cLo = -2;
			_cHi = 2;
			_iterations = iterations;
			
			initSetIterations();
		}
		
		//for each pixel of the set within bounds, find how many iterations it takes before it crosses the
		//value threshold
		private void initSetIterations ()
		{
			double rStep = (_rHi-_rLo)/NUM_PIXELS;
			double cStep = (_cHi-_cLo)/NUM_PIXELS;
	    	for (int r = 0; r < NUM_PIXELS; ++r)
	    	{
	    		for (int c = 0; c < NUM_PIXELS; ++c)
	    		{
	    			_setIterations[r][c] = findDivergence(new Complex(c*cStep+_cLo, r*rStep+_rLo), _iterations);
	    		}    		
	    	}
		}
		
		private short[][] getSetIterations()
		{
			return _setIterations;
		}
		
		//zoom the set towards a specified point
		//first, moves existing points over to their new positions, then
		//fills in the in-between points with newly calculated values
		//TODO: weird artifacting when zooming in
		public short[][] zoomTo (Coords point, double scaleFactor)
		{
			//move pixels where their values are already known
			int newR;
			int newC;
			//bounds of the already-calculated pixels
			int loRPoint = point.r - (int) (point.r/scaleFactor);
			int hiRPoint = point.r + (int)((NUM_PIXELS-point.r)/scaleFactor);
			int loCPoint = point.c - (int)(point.c/scaleFactor);
			int hiCPoint = point.c + (int)((NUM_PIXELS-point.c)/scaleFactor);
			//move the points by quadrant
			//(moved points are marked with a -1, so that they aren't replaced
			//when filling in the in-between points)
			for (int r = loRPoint; r < point.r; ++r)
			{
				newR = point.r + (int)((r-point.r) * scaleFactor);
				//1st quadrant
				for (int c = loCPoint; c <= point.c; ++c)
				{
					newC = point.c + (int)((c-point.c) * scaleFactor);
					_setIterations[newR][newC] = (short)(_setIterations[r][c] * -1);
				}
				//2nd quadrant
				for (int c = hiCPoint-1; c > point.c; --c)
				{
					newC = point.c + (int)((c-point.c) * scaleFactor);
					_setIterations[newR][newC] = (short)(_setIterations[r][c] * -1);
				}
			}
			for (int r = hiRPoint-1; r > point.r; --r)
			{
				newR = point.r + (int)((r-point.r) * scaleFactor);
				//3rd quadrant
				for (int c = loCPoint; c <= point.c; ++c)
				{
					newC = point.c + (int)((c-point.c) * scaleFactor);
					_setIterations[newR][newC] = (short)(_setIterations[r][c] * -1);
				}
				//4th quadrant
				for (int c = hiCPoint-1; c > point.c; --c)
				{
					newC = point.c + (int)((c-point.c) * scaleFactor);
					_setIterations[newR][newC] = (short)(_setIterations[r][c] * -1);
				}
			}
			
			//fill in the in-between points
			double rStep = (_rHi-_rLo)/NUM_PIXELS;
			double cStep = (_cHi-_cLo)/NUM_PIXELS;
			_rHi = (point.r + ((NUM_PIXELS-point.r)/scaleFactor))*rStep + _rLo;
			_rLo = (point.r - (point.r/scaleFactor))*rStep + _rLo;
			_cHi = (point.c + ((NUM_PIXELS-point.c)/scaleFactor))*cStep + _cLo;
			_cLo = (point.c - (point.c/scaleFactor))*cStep + _cLo;
			rStep = (_rHi-_rLo)/NUM_PIXELS;
			cStep = (_cHi-_cLo)/NUM_PIXELS;
			for (int r = 0; r < NUM_PIXELS; ++r)
			{
				for (int c = 0; c < NUM_PIXELS; ++c)
				{
					//make the moved points positive again
					if (_setIterations[r][c] < 0)
					{
						//make the value positive again
						_setIterations[r][c] *= -1;
					}
					//find the value of the in-between points
					else
					{
						_setIterations[r][c] = findDivergence(new Complex(c*cStep+_cLo, r*rStep+_rLo), _iterations);
					}
				}
			}
			
			return _setIterations;
		}
		
		//zoom the set towards a specified point
		//first, moves existing points over to their new positions, then
		//fills in the in-between points with newly calculated values
		public short[][] zoomTo2(Coords point, double scaleFactor)
		{
			short[][] output = new short[NUM_PIXELS][NUM_PIXELS]; 
			//move pixels where their values are already known
			int newR;
			int newC;
			//bounds of the already-calculated pixels
			int loRPoint = point.r - (int) (point.r/scaleFactor);
			int hiRPoint = point.r + (int)((NUM_PIXELS-point.r)/scaleFactor);
			int loCPoint = point.c - (int)(point.c/scaleFactor);
			int hiCPoint = point.c + (int)((NUM_PIXELS-point.c)/scaleFactor);
			
			//move the already existing points
			for (int r = loRPoint; r < hiRPoint; ++r)
			{
				newR = point.r + (int)((r-point.r) * scaleFactor);
				for (int c = loCPoint; c < hiCPoint; ++c)
				{
					newC = point.c + (int)((c-point.c) * scaleFactor);
					output[newR][newC] = _setIterations[r][c];
				}
			}
			
			//fill in the in-between points
			//TODO: fix/delete this
			double rStep = (_rHi-_rLo)/NUM_PIXELS;
			double cStep = (_cHi-_cLo)/NUM_PIXELS;
			_rHi = (point.r + ((NUM_PIXELS-point.r)/scaleFactor))*rStep + _rLo;
			_rLo = (point.r - (point.r/scaleFactor))*rStep + _rLo;
			_cHi = (point.c + ((NUM_PIXELS-point.c)/scaleFactor))*cStep + _cLo;
			_cLo = (point.c - (point.c/scaleFactor))*cStep + _cLo;
			rStep = (_rHi-_rLo)/NUM_PIXELS;
			cStep = (_cHi-_cLo)/NUM_PIXELS;
			for (int r = 0; r < NUM_PIXELS; ++r)
			{
				for (int c = 0; c < NUM_PIXELS; ++c)
				{
					//make the moved points positive again
					if (output[r][c] == 0)
					{
						//make the value positive again
						output[r][c] = findDivergence(new Complex(c*cStep+_cLo, r*rStep+_rLo), _iterations);
					}
				}
			}
			_setIterations = output;
			return output;
		}
		
		//zoom the set towards a specified point
		//first, move every oth point from the original setIterations to
		//every fth point of the new setIterations
		//TODO: either remove this zoom or the other zooms
		public short[][] zoomTo3(Coords point, int o, int f)
		{
			short[][] output = new short[NUM_PIXELS][NUM_PIXELS];
			
			//copy values that already exist from the original setIterations
			// to the output
			//TODO: still some weird artifacts. lines of little pixels, especially at the end
			int fRStart = point.r % f;
			int fCStart = point.c % f;
			int oRStart = (((fRStart - point.r) / f) * o) + point.r;
			int oCStart = (((fCStart - point.c) / f) * o) + point.c;
			int origR = oRStart;
			int origC;
			for (int r = fRStart; r < NUM_PIXELS; r+=f)
			{
				origC = oCStart;
				for (int c = fCStart; c < NUM_PIXELS; c+=f)
				{
					output[r][c] = _setIterations[origR][origC];
					origC += o;
				}
				origR += o;
			}
			
			//fill in the in-between points
			double scaleFactor = f / ((double) o);
			double rStep = (_rHi-_rLo)/NUM_PIXELS;
			double cStep = (_cHi-_cLo)/NUM_PIXELS;
			_rHi = (point.r + ((NUM_PIXELS-point.r)/scaleFactor))*rStep + _rLo;
			_rLo = (point.r - (point.r/scaleFactor))*rStep + _rLo;
			_cHi = (point.c + ((NUM_PIXELS-point.c)/scaleFactor))*cStep + _cLo;
			_cLo = (point.c - (point.c/scaleFactor))*cStep + _cLo;
			rStep = (_rHi-_rLo)/NUM_PIXELS;
			cStep = (_cHi-_cLo)/NUM_PIXELS;
			for (int r = 0; r < NUM_PIXELS; ++r)
			{
				for (int c = 0; c < NUM_PIXELS; ++c)
				{
					if (output[r][c] == 0)
					{
						output[r][c] = findDivergence(new Complex(c*cStep+_cLo, r*rStep+_rLo), _iterations);
					}
				}
			}
			
			_setIterations = output;
			return output;
		}
		
		//TODO: delete this, just a manual zoom
		public short[][] dbZoomTo(Coords point, double scaleFactor)
		{
			double rStep = (_rHi-_rLo)/NUM_PIXELS;
			double cStep = (_cHi-_cLo)/NUM_PIXELS;
			_rHi = (point.r + ((NUM_PIXELS-point.r)/scaleFactor))*rStep + _rLo;
			_rLo = (point.r - (point.r/scaleFactor))*rStep + _rLo;
			_cHi = (point.c + ((NUM_PIXELS-point.c)/scaleFactor))*cStep + _cLo;
			_cLo = (point.c - (point.c/scaleFactor))*cStep + _cLo;
			rStep = (_rHi-_rLo)/NUM_PIXELS;
			cStep = (_cHi-_cLo)/NUM_PIXELS;
			for (int r = 0; r < NUM_PIXELS; ++r)
			{
				for (int c = 0; c < NUM_PIXELS; ++c)
				{
					_setIterations[r][c] = findDivergence(new Complex(c*cStep+_cLo, r*rStep+_rLo), _iterations);
				}
			}
			
			return _setIterations;
		}
		
		//returns how long it takes for a point to cross a value threshold. Convergent points return iterations.
		private short findDivergence (Complex point, short iterations)
		{
			Complex z = new Complex(0, 0);
			//don't do the last iteration. If z makes it past iterations-1 while remaining in bounds,
			//then we know that z would need to be calculated at least iterations times to diverge
			for (short i = 1; i < iterations; ++i)
			{
				z.multiply(z).add(point);
				if (!z.inBounds(THRESHOLD))
				{
					return i;
				}
			}
			return iterations;
		}
		
		//find the point on the edge of the mandelbrot set that is closest
		//to another given point
		//If the given point is outside the set, a circle is created around
		//it until a point of greater iteration is found. This new point is
		//then treated as the center of a new circle. Process repeats until
		//the set is reached. A similar process is done when the given point
		//is inside the set.
		//return the coordinates of the edge point
		private Coords findClosestEdge (Coords point)
		{
			int rLo, rHi, cLo, cHi;
			Coords adjacent;
			short adjacentIter;
			
			CoordsList unvisited = new CoordsList();
			CoordsList visited = new CoordsList();
			if (DB_COLOR_EDGE_FIND)
			{
				surface.setCoordLists(visited, unvisited);
			}
			Coords center = point;
			unvisited.add(center, center);
			short centerIter = _setIterations[center.r][center.c];
			
			//given point is outside the set
			Coords current;
			if (centerIter != NUM_ITERATIONS)
			{
				while (centerIter != NUM_ITERATIONS && !unvisited.isEmpty())
				{	
					//evaluate the unevaluated point that is closest to the center
					current = unvisited.removeClosest();
					visited.add(current, null);
					
					//check the adjacent points (spaced out by the edge granularity. Use DB_COLOR_EDGE_FIND=true
					//with varying granularity to see this effect visually)
					rLo = current.r-EDGE_GRANULARITY >= 0 ? current.r-EDGE_GRANULARITY : 0;
					rHi = current.r+EDGE_GRANULARITY < _setIterations.length ? current.r+EDGE_GRANULARITY : _setIterations.length-1;
					cLo = current.c-EDGE_GRANULARITY >= 0 ? current.c-EDGE_GRANULARITY : 0;
					cHi = current.c+EDGE_GRANULARITY < _setIterations[0].length ? current.c+EDGE_GRANULARITY : _setIterations[0].length-1;
					adjLoop:
					for (int r = rLo; r <= rHi; r+=EDGE_GRANULARITY)
					{
						for (int c = cLo; c <= cHi; c+=EDGE_GRANULARITY)
						{
							//exclude the current point
							if (!(r==current.r && c==current.c))
							{
								adjacent = new Coords(r, c);
								adjacentIter = _setIterations[r][c];
								if (adjacentIter > centerIter)
								{
									//new level, create a new circle with the adjacent point
									//as the new center
									center = adjacent;
									centerIter = adjacentIter;
									unvisited.removeAll();
									visited.removeAll();
									unvisited.add(center, center);
									//stop evaluating adjacent points, we have already
									//found the next level of iteration
									break adjLoop;
								}
								if (adjacentIter==centerIter &&
										 !visited.contains(adjacent) &&
										 !unvisited.contains(adjacent))
								{
									unvisited.add(adjacent, center);
									if (DB_COLOR_EDGE_FIND) {surface.repaint();}
								}
							}
						}
					}
				}
				return center;
			}
			//given point is inside the set
			else
			{	
				while (!unvisited.isEmpty())
				{
					//evaluate the unevaluated point that is closest to the center
					current = unvisited.removeClosest();
					visited.add(current, null);
					
					//check the adjacent points (spaced out by the edge granularity. Use DB_COLOR_EDGE_FIND=true
					//with varying granularity to see this effect visually)
					rLo = current.r-EDGE_GRANULARITY >= 0 ? current.r-EDGE_GRANULARITY : 0;
					rHi = current.r+EDGE_GRANULARITY < _setIterations.length ? current.r+EDGE_GRANULARITY : _setIterations.length-1;
					cLo = current.c-EDGE_GRANULARITY >= 0 ? current.c-EDGE_GRANULARITY : 0;
					cHi = current.c+EDGE_GRANULARITY < _setIterations[0].length ? current.c+EDGE_GRANULARITY : _setIterations[0].length-1;
					for (int r = rLo; r <= rHi; r+=EDGE_GRANULARITY)
					{
						for (int c = cLo; c <= cHi; c+=EDGE_GRANULARITY)
						{
							adjacent = new Coords(r, c);
							adjacentIter = _setIterations[r][c];
							//found the edge of the set, return the adjacent point
							if (adjacentIter < centerIter)
							{
								return adjacent;
							}
							if (adjacentIter==centerIter &&
								!visited.contains(adjacent) &&
								!unvisited.contains(adjacent))
							{
								unvisited.add(adjacent, center);
								if (DB_COLOR_EDGE_FIND) {surface.repaint();}
							}
						}
					}
				}
			}
			//should not get to this point
			throw new Error("Got to end of MBSet::findClosestEdge without finding set edge");
		}
	}
	
	static class Surface extends JPanel
	{	
		//How smooth the colorization is
		private static short COLOR_GRADIENT = (short) Math.pow(2, 6);
		
		//messy way of passing set iterations to the draw method like an argument
		private short[][] _setIterations;
		//messy way of passing visited and unvisited coordinates when finding the set edge
		private CoordsList _unvisited;
		private CoordsList _visited;
		
		public Surface ()
		{
			super();
			_setIterations = new short[NUM_PIXELS][NUM_PIXELS];
		}
		
		public void setCoordLists (CoordsList inV, CoordsList inU)
		{
			_visited = inV;
			_unvisited = inU;
		}
		
		//convert a set iterations to a color on the saturated color wheel
		private Color iterToColor (short iterations)
		{
			//convergent points should be black
			if (iterations == NUM_ITERATIONS) {return new Color(0, 0, 0);}
			int[] rgb = new int[3];
			iterations = (short)(Math.abs(iterations) % (COLOR_GRADIENT*6));
			for (int i = 0; i < 3; ++i)
			{
				if (iterations < COLOR_GRADIENT) {
					rgb[i] = iterations  * (256/COLOR_GRADIENT);
				} else if (iterations >= COLOR_GRADIENT && iterations < COLOR_GRADIENT*3) {
					rgb[i] = 255;
				} else if (iterations >= COLOR_GRADIENT*3 && iterations < COLOR_GRADIENT*4) {
					rgb[i] = (COLOR_GRADIENT*4 - 1 - iterations)  * (256/COLOR_GRADIENT);
				} else {
					rgb[i] = 0;
				}
				iterations = (short)((iterations + COLOR_GRADIENT*2) % (COLOR_GRADIENT*6));
			}
			return new Color(rgb[0],rgb[1],rgb[2]);
		}
		
		//draw given set iterations
		public void drawSet (short[][] setIterations)
		{	
			//cloned to avoid concurrency errors (like how zooming will temporarily make values < 0)
			//TODO: find a more efficient way to do this
			for (int r = 0; r < _setIterations.length; ++r)
			{
				for (int c = 0; c < _setIterations[0].length; ++c)
				{
					_setIterations[r][c] = setIterations[r][c]; 
				}
			}
			repaint();
		}
		
		private void draw(Graphics g)
		{	
			Graphics2D g2d = (Graphics2D) g;
			
			RenderingHints rh = new RenderingHints (
	        		RenderingHints.KEY_RENDERING,
	        		RenderingHints.VALUE_RENDER_SPEED);
	    	g2d.setRenderingHints (rh);
	    	
	    	char colorVal;
	    	for (int r = 0; r < _setIterations.length; ++r)
	    	{
	    		for (int c = 0; c < _setIterations[0].length; ++c)
	    		{	    			
//	    			colorVal = (char) (_setIterations[r][c]*8 - 1);
//	    			g2d.setPaint(new Color(colorVal, colorVal, colorVal));
	    			g2d.setPaint(iterToColor(_setIterations[r][c]));
	    			g2d.fillRect(c*PIXEL_WIDTH, r*PIXEL_WIDTH, PIXEL_WIDTH, PIXEL_WIDTH);
	    		}    		
	    	}
	    	
	    	//draw the visited and unvisited points
	    	if (_unvisited != null && _visited != null)
	    	{
		    	g2d.setPaint(new Color(0, 255, 0));
		    	for (Coords c : _unvisited)
		    	{
		    		g2d.fillRect(c.c*PIXEL_WIDTH, c.r*PIXEL_WIDTH, PIXEL_WIDTH, PIXEL_WIDTH);
		    	}
		    	g2d.setPaint(new Color(0, 0, 255));
		    	for (Coords c : _visited)
		    	{
		    		g2d.fillRect(c.c*PIXEL_WIDTH, c.r*PIXEL_WIDTH, PIXEL_WIDTH, PIXEL_WIDTH);
		    	}
	    	}
		}
		
		public void paintComponent (Graphics g)
		{
			super.paintComponent(g);
			draw(g);
		}
	}
	
	private void InitUI ()
	{
		setTitle("Mandelbrot Set");
		setSize (NUM_PIXELS*PIXEL_WIDTH+100, NUM_PIXELS*PIXEL_WIDTH+100);
    	setLayout(null);
    	setLocationRelativeTo (null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	surface = new Surface();
    	surface.setBounds(0, 0, NUM_PIXELS*PIXEL_WIDTH, NUM_PIXELS*PIXEL_WIDTH);
    	add(surface);
	}
	
	private MBSet InitMBSet ()
	{
    	MBSet set = new MBSet(NUM_ITERATIONS);
    	surface.drawSet(set.getSetIterations());
    	return set;
	}
	
	//handles updating the surface
	static class Updater
	{
		ScheduledExecutorService _updater;
		//point which we are zooming to
		private Coords _edgePoint;
		private int _edgeTimer;
		//update the edge when this many frames pass
		private static final int EDGE_RATE = 500/FRAME_RATE; //1000/FRAME_RATE = every second
		private MBSet _set;
		
		public Updater (MBSet set)
		{
			_set = set;
		}
		
		public void zoomTo (Coords point)
		{
			_edgePoint = point;
			_edgeTimer=0;
			_updater = Executors.newSingleThreadScheduledExecutor();
			_updater.scheduleAtFixedRate(new Runnable(){
				@Override
				public void run()
				{
					update();
				}
			}, 0, FRAME_RATE, TimeUnit.MILLISECONDS);
		}
		
		
		private void update ()
		{
			if (_edgeTimer % EDGE_RATE == 0)
			{
				//refind the edge
				_edgePoint = _set.findClosestEdge(_edgePoint);
			}
			++_edgeTimer;
//			surface.drawSet(_set.dbZoomTo(_edgePoint, 1.1));
			surface.drawSet(_set.zoomTo3(_edgePoint, 11, 12));
		}
	}
	
	
	private static class Adapter extends MouseAdapter
	{
		private Updater _updater;
		private boolean _wasClicked;
		
		public Adapter (Updater updater)
		{
			_updater = updater;
			_wasClicked = false;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (!_wasClicked)
			{
				_wasClicked = true;
				_updater.zoomTo(new Coords(e.getY()/PIXEL_WIDTH, e.getX()/PIXEL_WIDTH));
			}
		}
	}
	
	public Mandelbrot ()
	{
		super();
		InitUI();
		final Adapter mouseAdapter = new Adapter(new Updater(InitMBSet()));
		addMouseListener(mouseAdapter);
	}
	
	public static void main (String[] args)
	{
		Mandelbrot window = new Mandelbrot();
		window.setVisible(true);
	}
}
