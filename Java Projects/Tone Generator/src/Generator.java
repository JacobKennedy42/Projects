import javax.swing.JFrame;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.*;


//TODO:
//
//would likes:
//Make ui look well-arranged
//Make wave text editable
//maybe try increasing sample size from 8 to 16, to make it less noisy. Find out why doing so doubles the pitch.
//When deleting a wave, make the waves ahead of it recalculate their waves, since they may have referred to a wave that had been deleted/shifted
//Have a way to shorthand wave references in the expression field
//save wave functions and/or byte arrays to files, and be able to load them in
//keyboard support (hot keys, arrows for selection)
//optimize series function. Maybe split it into sum and product
//Add a how-to button

public class Generator extends JFrame
{
	private static final int SAMPLE_RATE = 16 * 1024;
	private static final int SAMPLE_SIZE = 8;
	private static final float SLIDER_MAX = 1000;
	private static final float MAX_AMP = 32;
	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static ScriptEngine jsEngine = manager.getEngineByName("js");
	//initialize some code in the js engine
	static
	{
		try
		{
			jsEngine.eval("function period(x){return " + SAMPLE_RATE + "/x}");
			jsEngine.eval("function sec(x){return x/"+SAMPLE_RATE+"}");
			jsEngine.eval("function series (exp, op, n, x)"
					+ "{"
					+ " var result;"
					+ " if (n > 0)"
					+ " {"
					+ "  var i = 0;"
					+ "  result = eval(exp);"
					+ "  for (i = 1; i < n; ++i)"
					+ "  {"
					+ "   result = eval(\"result\" + op + \"(\"+exp+\")\");"
					+ "  }"
					+ " }"
					+ " return result;"
					+ "}");
			jsEngine.eval("var Wave = [];");
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}
	
	class Surface extends JPanel
	{
		private Wave _wave;
		private int _start;
		private int _length;
		
		public void drawWave (Wave wave, float start, float length)
		{
			_wave = wave;
			drawWave(start, length);
		}
		
		public void drawWave (float start, float length)
		{
			if (_wave != null)
			{
				_start = (int)(start * _wave.getBytes().length);
				_length = (int)(length * _wave.getBytes().length);
				_length = _length <= _wave.getBytes().length - _start ? _length : _wave.getBytes().length - _start;  
			}
			repaint();
		}
		
		private void draw (Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			RenderingHints rh = new RenderingHints (
	        		RenderingHints.KEY_RENDERING,
	        		RenderingHints.VALUE_RENDER_SPEED);
	    	g2d.setRenderingHints (rh);
	    	
	    	g2d.setPaint(Color.BLACK);
	    	if (_wave != null)
	    	{
	    		byte[] waveBytes = _wave.getBytes();
	    		int waveIndex;
	    		int pixelY = getBounds().height - (int)((waveBytes[_start]/MAX_AMP + 1) * (getBounds().height/2));
	    		int prevY;
		    	for (int i = 0; i < getBounds().width; ++i)
		    	{
		    		waveIndex = (int)(i/((float)getBounds().width) * (_length) + _start);
		    		prevY = pixelY;
		    		pixelY = getBounds().height - (int)((waveBytes[waveIndex]/MAX_AMP + 1) * (getBounds().height/2));
		    		g2d.fillRect(i, prevY < pixelY ? prevY : pixelY, 2, 1+Math.abs(pixelY - prevY));
		    	}
	    	}
	    	g2d.setPaint(Color.RED);
	    	g2d.fillRect(0, getBounds().height/2, getBounds().width, 2);
		}
		
		public void paintComponent (Graphics g)
	    {
	    	super.paintComponent(g);
	    	draw(g);
	    }
	}

	static class Wave
	{
		private int _id;
		private byte[] _wave;
		String _func;
		ListModel _parent;
		String _display;
		
		public Wave (String func, int ms, double amp, ListModel parent)
		{	
			try
			{
				_wave = makeFunctionWave(func, ms, amp);
			}
			catch (ScriptException e)
			{
				e.printStackTrace();
			}
			
			_parent = parent;
			_display = _func;
		}
		
		public String getFunc()
		{
			return _func;
		}
		
		public byte[] getBytes ()
		{
			return _wave;
		}
		
		private byte[] makeFunctionWave (String func, int ms, double amp) throws ScriptException 
		{
			_func = func;
			
			int numSamples = (int) ((ms * SAMPLE_RATE) / 1000);			
			double[] buffer = new double[numSamples];
			double max = 1e-8; //Not 0 to avoid divideByZero
			_id = (int) jsEngine.eval("Wave.length");
			jsEngine.eval("Wave.push(function(x){return (" + func + ") * 1.0});");
			
			JSObject wave = (JSObject) jsEngine.eval("Wave["+_id+"]");
			for (int i = 0; i < buffer.length; ++i)
			{
				buffer[i] = (double) wave.call(null, i);
				max = Math.abs(buffer[i]) > max ? Math.abs(buffer[i]) : max;
			}
			//Make sure that function gives values from -1 to 1
			byte[] output = new byte[numSamples];
			for (int i = 0; i < buffer.length; ++i)
			{
				output[i] = (byte)(buffer[i] / max * amp);
			}
			
			return output;
		}
		
		public static String sineFunc (double freq)
		{
			return "Math.sin(2 * Math.PI * x / period("+freq+"))";
		}
		
		public static String squareFunc (double freq)
		{
			return "(x%period("+freq+") >= period("+freq+")/2) * 2 - 1";
		}
		
		public static String triangleFunc (double freq)
		{
			return "Math.abs(x%period("+freq+") - period("+freq+")/2) / (period("+freq+")/4) - 1";
		}
		
		public static String sawFunc (double freq)
		{
			return "(x%period("+freq+"))/period("+freq+") * 2 - 1";
		}
		
		public void play()
		{
			try
			{
				final AudioFormat af = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
				SourceDataLine line = AudioSystem.getSourceDataLine(af);
				line.open(af, SAMPLE_RATE);
				line.start();
				line.write(_wave, 0, _wave.length);
				line.drain();
				line.close();
			}
			catch (LineUnavailableException e)
			{
				e.printStackTrace();
			}
		}
		
		public void updateIds ()
		{
			Wave wave;
			for (int i = _id; i < _parent.getSize(); ++i)
			{
				wave = (Wave) _parent.getElementAt(i);
				wave._id = i;
				//TODO: make the waves recalculate their wave. If they can't, delete them too 
			}
			//update the ids
			try
			{
				jsEngine.eval("Wave.splice("+_id+", 1)");	
			} catch (ScriptException ex) {
				ex.printStackTrace();
			}
		}
		
		public String toString()
		{
			return "Wave["+_id+"]: "+_display;
		}
	}
	
	private void setFuncField (JTextField field, String type, int freq)
	{
		String func;
		if (type.equals("Sine"))
		{
			func = Wave.sineFunc(freq);
		} else if (type.equals("Square")) {
			func = Wave.squareFunc(freq);
		} else if (type.equals("Triangle")) {
			func = Wave.triangleFunc(freq);
		} else {
			func = Wave.sawFunc(freq);
		}
		field.setText(func);
	}
	
	public void InitUI()
	{
		setTitle ("Tone Generator");
    	setSize (2400, 1000);
    	setLayout(null);
    	setLocationRelativeTo (null);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	Font buttonf = new Font("Ariel", Font.BOLD, 24);
    	Font textf = new Font("Ariel", Font.PLAIN, 24);
    	
    	//front-loaded elements to be manipulated by the wave list
    	JButton playB = new JButton("Play");;
    	JButton deleteB = new JButton("Delete");;
    	JTextField fField = new JTextField("Math.sin(2 * Math.PI * x / period(440.0))");
    	Surface waveGraph = new Surface();
    	JSlider startS = new JSlider(0, (int)SLIDER_MAX - 1, 0);
//    	JSlider endS = new JSlider(1, (int)SLIDER_MAX, (int)SLIDER_MAX);
    	SpinnerNumberModel lengthM = new SpinnerNumberModel(2, 0, 10, 1);
    	JSpinner zoomSpin = new JSpinner(lengthM);
    	
    	//wave list
    	DefaultListModel<Wave> waveLm = new DefaultListModel<Wave>();
    	JList<Wave> waveList = new JList<Wave>(waveLm);
    	waveList.setFont(textf);
    	waveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	waveList.setLayoutOrientation(JList.VERTICAL);
    	waveList.setVisibleRowCount(-1);
    	waveList.addListSelectionListener(new ListSelectionListener() {
    		public void valueChanged (ListSelectionEvent e)
    		{
    			Wave selected = waveList.getSelectedValue();
    			if (selected != null)
    			{
	    			playB.setEnabled(true);
	    			deleteB.setEnabled(true);
	    			fField.setText(waveList.getSelectedValue().getFunc());
    			} else {
    				playB.setEnabled(false);
	    			deleteB.setEnabled(false);
    			}
    			waveGraph.drawWave(selected, startS.getValue()/SLIDER_MAX, (float)Math.pow(10, -1 * (int)zoomSpin.getValue()));
    		}
    	});
    	JScrollPane sp = new JScrollPane(waveList);
    	sp.setBounds(0, 0, 1500, 400);
    	add(sp);
    	
    	//instruction label
    	JLabel instructL = new JLabel("<html>This program takes in a javascript expression and outputs a series of samples to produce a sound. The value of each sample is determined by evaluating the expression for each timestep x.<br><br>" + 
    			"<b>period(f)</b>: the number of samples per oscillation of a wave of frequency f. Calculated as SamplesPerSecond / f.<br>" + 
    			"<b>sec(x)</b>: converts number of samples to number of seconds. Calculated as x / SamplesPerSecond.<br>" + 
    			"<b>Wave[i](x)</b>: gives the value of the ith wave at timestep x.<br>" + 
    			"<b>series(exp, op, n, x)</b>: Calculates a series of n expressions connected by binomial operator op, where 0&#8804;i&#60;n.<br> Ex: series(\"Wave[i](x)\", \"+\", 3, x) = Wave[0](x) + Wave[1](x) + Wave[2](x)</html>");
    	instructL.setFont(textf);
    	instructL.setBounds(sp.getBounds().width, 0, 800, 400);
    	add(instructL);
    	
    	//freq, amp and type spinners
    	SpinnerNumberModel m = new SpinnerNumberModel();
    	m.setValue(440);
    	m.setStepSize(1);
    	JSpinner freqSpin = new JSpinner(m);
    	freqSpin.setBounds(150, sp.getBounds().height, 100, 50);
    	freqSpin.setFont(textf);
    	add(freqSpin);
    	JLabel freqLabel = new JLabel("hz");
    	freqLabel.setBounds(260, sp.getBounds().height, 150, 50);
    	freqLabel.setFont(textf);
    	add(freqLabel);
    	m = new SpinnerNumberModel(64, -127, 127, 1);
    	SpinnerListModel lm = new SpinnerListModel(new String[]{"Sine", "Square", "Triangle", "Saw"});
    	JSpinner typeSpin = new JSpinner(lm);
    	typeSpin.setBounds(0, sp.getBounds().height, 150, 50);
    	typeSpin.setFont(textf);
    	add(typeSpin);
    	freqSpin.addChangeListener(new ChangeListener() {
    		public void stateChanged(ChangeEvent e)
    		{
    			setFuncField(fField, (String)typeSpin.getValue(), (int)freqSpin.getValue());
    		}
    	});
    	typeSpin.addChangeListener(new ChangeListener() {
    		public void stateChanged(ChangeEvent e)
    		{
    			setFuncField(fField, (String)typeSpin.getValue(), (int)freqSpin.getValue());
    		}
    	});
    	
    	//function text field
    	fField.setBounds(0, sp.getBounds().height + 50, sp.getBounds().width, 50);
    	fField.setFont(textf);
    	add(fField);
    	
    	//play button
    	playB.setBounds(fField.getBounds().width + 100, fField.getBounds().y, 100, 50);
    	playB.setFont(buttonf);
    	playB.setEnabled(false);
    	playB.addActionListener(new ActionListener(){
    		public void actionPerformed (ActionEvent e)
    		{
    			Wave currentWave = waveList.getSelectedValue();
    			if (currentWave != null)
				{
    				currentWave.play();
				}
    		}
    	});
    	add(playB);
    	
    	//delete button
    	deleteB.setBounds(fField.getBounds().width + 200, fField.getBounds().y, 150, 50);
    	deleteB.setFont(buttonf);
    	deleteB.setEnabled(false);
    	deleteB.addActionListener(new ActionListener(){
    		public void actionPerformed (ActionEvent e)
    		{
    			int currentIndex = waveList.getSelectedIndex();
				Wave currentWave = waveLm.getElementAt(currentIndex);
    			waveLm.removeElementAt(currentIndex);
    			waveList.setSelectedIndex(currentIndex < waveLm.getSize()-1 ? currentIndex : waveLm.getSize()-1);
    			currentWave.updateIds();
    		}
    	});
    	add(deleteB);
    	
    	//add button
    	JButton addB = new JButton("Add");
    	addB.setBounds(fField.getBounds().width, fField.getBounds().y, 100, 50);
    	addB.setFont(buttonf);
    	addB.addActionListener(new ActionListener(){
    		public void actionPerformed (ActionEvent e)
    		{	
    			Wave wave = new Wave(fField.getText(), 5000, MAX_AMP, waveLm);
    			waveLm.addElement(wave);
    			waveList.setSelectedIndex(waveLm.getSize()-1);
    			waveList.ensureIndexIsVisible(waveLm.getSize()-1);
			}});
    	add(addB);
    	
    	//slider and spinner for the wave graph
    	startS.setBounds(0, fField.getBounds().y + 350, sp.getBounds().width, 50);
    	JLabel zoomLabel = new JLabel("Zoom:", SwingConstants.CENTER);
    	zoomLabel.setFont(textf);
    	zoomLabel.setBounds(startS.getBounds().width, startS.getBounds().y-50, 100, 50);
    	add(zoomLabel);
    	zoomSpin.setBounds(startS.getBounds().width, startS.getBounds().y, 100, 50);
    	zoomSpin.setFont(textf);
    	add(zoomSpin);
    	startS.addChangeListener(new ChangeListener (){
    		public void stateChanged(ChangeEvent e)
    		{
    			waveGraph.drawWave(startS.getValue()/SLIDER_MAX, (float)Math.pow(10, -1 * (int)zoomSpin.getValue()));
    		}});
    	zoomSpin.addChangeListener(new ChangeListener (){
    		public void stateChanged(ChangeEvent e)
    		{
    			waveGraph.drawWave(startS.getValue()/SLIDER_MAX, (float)Math.pow(10, -1 * (int)zoomSpin.getValue()));
    		}
    	});
    	add(startS);
    	
    	//wave graph
    	waveGraph.setBounds(0, fField.getBounds().y + 100, sp.getBounds().width, 256);
    	add(waveGraph);
	}
	
	public static void main(String[] args) 
	{	
		Generator window = new Generator();
		window.InitUI();
		window.setVisible(true);
	}
}