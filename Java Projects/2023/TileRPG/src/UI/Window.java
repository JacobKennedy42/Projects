package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Window extends JFrame{
	private Canvas canvas;
	
	public Window () {
		initializeUI();
	}
	
	private void initializeUI () {
		canvas = new Canvas();
		canvas.setBackground(Color.black);
		add(canvas);
		
		setTitle ("Tile RPG");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	double screenWidth = screenSize.getWidth();
    	double screenHeight = screenSize.getHeight();
		setSize ((int) (screenWidth/1.5), (int) (screenHeight/1.33));
		setLocationRelativeTo (null);
//    	setSize (700, 500);
//    	setLocation(350, 100);
    	setFocusable(true);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	canvas.repaint();
	}
}
